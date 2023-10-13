package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackCheck
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.UtilsBitmap
import kotlin.math.atan2

class CustomSeekbarArc: View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var w = 0f
    private val paintBm = Paint()
    private val paintColor = Paint()
    private val rectBm = RectF()

    private var padding = 0f
    private var sweepAngle = 0f
    private var sweep = 0f
    private var currentDegree = 0F
    private var moveDegree = 0F
    private var offSet = 0f

    var isActive = false
    var onSeekbarResult: OnSeekbarResult? = null
    var isSwipe: ICallBackCheck? = null

    private var arrColorPr = intArrayOf(
        Color.parseColor("#00FFA3"),
        Color.parseColor("#18A7F7"),
        Color.parseColor("#F501FA")
    )

    init {
        w = resources.displayMetrics.widthPixels / 100f
        padding = 4.44f * w

        paintBm.apply {
            flags = Paint.FILTER_BITMAP_FLAG
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        paintColor.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBg(canvas)

        drawProgress(canvas)
    }

    private fun drawBg(canvas: Canvas) {
        val bm =
            UtilsBitmap.getBitmapFromAsset(context, "theme/theme_0", "img_seekbar_arc_bg.png")

        rectBm.set(0f, 0f, width.toFloat(), height.toFloat())
        bm?.let { canvas.drawBitmap(bm, null, rectBm, paintBm) }

        paintColor.apply {
            shader = null
            color = Color.parseColor("#171717")
            strokeWidth = 1.667f * w
            style = Paint.Style.STROKE
        }
        rectBm.set(padding, padding, width - padding, height - padding)
        canvas.drawArc(rectBm, 135f, 270f, false, paintColor)
    }

    private fun drawProgress(canvas: Canvas) {
        paintColor.apply {
            shader =
                LinearGradient(0f, 0F, width.toFloat(), 0F, arrColorPr, null, Shader.TileMode.CLAMP)
            strokeWidth = 1.667f * w
            style = Paint.Style.STROKE
        }
        rectBm.set(padding, padding, width - padding, height - padding)

        canvas.drawArc(rectBm, 135f, sweep, false, paintColor)

        val bm =
            UtilsBitmap.getBitmapFromAsset(context, "theme/theme_0", "img_seekbar_arc_pr.png")
        rectBm.set(0f, 0f, width.toFloat(), height.toFloat())
        bm?.let { canvas.drawBitmap(it, null, rectBm, paintBm) }

        val bmThumb =
            UtilsBitmap.getBitmapFromAsset(context, "theme/theme_0", "img_seekbar_arc_thumb.png")
        rectBm.set(0f, 0f, width.toFloat(), height.toFloat())
        bmThumb?.let {
            canvas.rotate(sweepAngle, width / 2f, height / 2f)
            canvas.drawBitmap(it, null, rectBm, paintBm)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isActive) return false
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                isSwipe?.check(true)
                val dx = width / 2 - event.x
                val dy = height / 2 - event.y
                currentDegree = (atan2(dy, dx) * 180 / Math.PI).toFloat() - offSet

                if (currentDegree < 0) currentDegree = 360 - (currentDegree * -1)
                currentDegree -= 180f
                if (currentDegree < 0) currentDegree += 360f

                onSeekbarResult?.onDown(this)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = width / 2 - event.x
                val dy = height / 2 - event.y

                moveDegree = (atan2(dy, dx) * 180 / Math.PI).toFloat()

                if (moveDegree < 0) moveDegree = 360 - (moveDegree * -1)
                moveDegree -= 180f
                if (moveDegree < 0) moveDegree += 360f

                offSet = moveDegree - currentDegree
                sweepAngle = (offSet.toInt() + 135f) % 360

                val rotate = if (sweepAngle >= 135) sweepAngle - 225 else sweepAngle + 135

                if (rotate < 0f || moveDegree == currentDegree) return false
                else sweep = rotate

                onSeekbarResult?.onMove(this, (rotate * 100 / 270).toInt())

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isSwipe?.check(false)
                onSeekbarResult?.onUp(this, sweep.toInt())
            }
        }

        return true
    }

    fun setColorProgress (colors: IntArray) {
        this.arrColorPr = colors

        invalidate()
    }

    fun setProgress(progress: Int) {
        if (progress == 0) offSet = 0f
        this.sweep = progress * 270 / 100f
        if (sweep > 135f) this.sweepAngle = sweep - 135f else this.sweepAngle = sweep + 225f
        invalidate()
    }
}