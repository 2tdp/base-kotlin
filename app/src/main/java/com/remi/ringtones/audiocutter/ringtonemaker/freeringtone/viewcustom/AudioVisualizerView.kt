package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.volumebooster.superloud.sound.speaker.booster.R
import kotlin.math.log10
import kotlin.math.max

class AudioVisualizerView: View {

    companion object {
        private const val FFT_STEP = 2
        private const val FFT_OFFSET = 2
        private const val FFT_NEEDED_PORTION = 2 // 1/3
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var visualizer: Visualizer? = null
    private var paintLeft: Paint
    private var paintRight: Paint

    private var w = 0f
    private var magnitudes = floatArrayOf()
    private val dataLeft = mutableListOf<RectF>()
    private val dataRight = mutableListOf<RectF>()
    private val maxMagnitude = calculateMagnitude(128f, 128f)
    private val smoothingFactor = 0.3f
    private val barsCount = 40
    private var barColorsLeft = intArrayOf()
    private var barColorsRight = intArrayOf()

    init {
        w = resources.displayMetrics.widthPixels / 100f
        setBackgroundColor(Color.TRANSPARENT)

        barColorsLeft = intArrayOf(
            ContextCompat.getColor(context, R.color.linear_main_1),
            ContextCompat.getColor(context, R.color.linear_main_2),
            ContextCompat.getColor(context, R.color.linear_main_3)
        )
        barColorsRight = intArrayOf(
            ContextCompat.getColor(context, R.color.linear_main_3),
            ContextCompat.getColor(context, R.color.linear_main_2),
            ContextCompat.getColor(context, R.color.linear_main_1)
        )

        paintLeft = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader =
                LinearGradient(
                    0f, 0f, 15f * w, 0f, barColorsLeft, null, Shader.TileMode.CLAMP
                )
        }
        paintRight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader =
                LinearGradient(
                    100 * w - 15f * w, 0f, w * 100, 0f, barColorsRight, null, Shader.TileMode.CLAMP
                )
        }
        initVisualizer()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        visualizerData()
    }

    override fun onDetachedFromWindow() {
        visualizer?.release()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visualizer == null) return

        dataLeft.forEach {
            canvas.drawRoundRect(it, 25f, 25f, paintLeft)
        }

        dataRight.forEach {
            canvas.drawRoundRect(it, 25f, 25f, paintRight)
        }
    }

    fun initVisualizer() {
        if (visualizer == null) visualizer = Visualizer(0)
        try {
            visualizer?.let {
                it.enabled = false
                it.captureSize = Visualizer.getCaptureSizeRange()[0]
                it.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {

                    }

                    override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                        magnitudes = convertFFTtoMagnitudes(fft)
                        visualizerData()
                    }

                }, Visualizer.getMaxCaptureRate() * 2 / 3, false, true)
                it.enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            visualizer = null
        }
    }

    private fun visualizerData() {
        dataLeft.clear()
        dataRight.clear()

        val barWidth = height / (barsCount * 2f) // 2f is for the space between bars
        for (i in 0 until barsCount) {
            val segmentSize = (magnitudes.size / barsCount.toFloat())
            val segmentStart = i * segmentSize
            val segmentEnd = segmentStart + segmentSize

            var sum = 0f
            for (j in segmentStart.toInt() until segmentEnd.toInt()) sum += magnitudes[j]

            val amp = sum
                .run { this / segmentSize } // average
                .run { this * height - 20 } // normalize to the height of the view
                .run { max(this, barWidth) } // at least shows a circle

            val horizontalOffset = barWidth / 2 // remedy for last empty bar

            val startX = barWidth * i * 2
            val endX = startX + barWidth

            dataLeft.add(RectF(0f, startX, amp / 3, endX))
            dataRight.add(RectF(width.toFloat(), startX, width - amp / 3, endX))
        }
        invalidate()
    }

    private fun convertFFTtoMagnitudes(fft: ByteArray): FloatArray {
        if (fft.isEmpty()) return floatArrayOf()

        val n = fft.size / FFT_NEEDED_PORTION
        val curMagnitudes = FloatArray(n / 2)

        var prevMagnitudes = magnitudes
        if (prevMagnitudes.isEmpty()) prevMagnitudes = FloatArray(n)

        for (k in 0 until n / 2 - 1) {
            val index = k * FFT_STEP + FFT_OFFSET

            val curMagnitude = calculateMagnitude(fft[index].toFloat(), fft[index + 1].toFloat())
            curMagnitudes[k] = curMagnitude + (prevMagnitudes[k] - curMagnitude) * smoothingFactor
        }
        return curMagnitudes.map { it / maxMagnitude }.toFloatArray()
    }

    private fun calculateMagnitude(r: Float, i: Float) =
        if (i == 0f && r == 0f) 0f else 10 * log10(r * r + i * i)
}