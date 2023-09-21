package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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


class CustomSeekbarVertical : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var w = 0f

    private var paint: Paint
    private var paintBg: Paint
    private var paintProgress: Paint
    private var progress = 0
    private var max = 0
    private var sizeThumb = 0f
    private var sizeBg = 0f
    private var sizePos = 0f
    private var bitmap: Bitmap? = null
    private var rectBm = RectF()
    private var isFirstShader = false
    private var isTouch = false

    var isActive = true
    private var colorBg = intArrayOf()
    private var colorPr = intArrayOf()

    var onSeekbarResult: OnSeekbarResult? = null
    var isSwipe: ICallBackCheck? = null

    init {
        w = resources.displayMetrics.widthPixels / 100f
        sizeBg = 3.33f * w
        sizePos = 1.11f * w

        paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paintBg.apply {
            color = colorBg[0]
            strokeWidth = sizeBg
        }
        canvas.drawLine(width / 2f, sizeThumb / 2f, width / 2f, height - sizeThumb / 2f, paintBg)
        paintBg.apply {
            color = colorBg[1]
            strokeWidth = 2 * sizeBg / 3
        }
        canvas.drawLine(width / 2f, sizeThumb / 2f, width / 2f, height - sizeThumb / 2f, paintBg)

        if (!isFirstShader) {
            paintProgress.shader = LinearGradient(0f, height.toFloat(),0f, 0f, colorPr, null, Shader.TileMode.CLAMP)
            isFirstShader = true
        }
        paintProgress.strokeWidth = sizePos
        val p = (height - sizeThumb) * progress / max + sizeThumb / 2f
        canvas.drawLine(width / 2f, height - sizeThumb / 2f, width / 2f, p, paintProgress)

        bitmap?.let {
            rectBm.set(
                width / 2f - it.width / 2f,
                p - it.height / 2f,
                width / 2f + it.width / 2f,
                p + it.width / 2f
            )
            paint.setShadowLayer(sizeThumb / 8, 0f, 0f, Color.WHITE)
            canvas.drawBitmap(it, null, rectBm, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isActive) return false
        if (event.x in rectBm.left..rectBm.right && event.y in rectBm.top..rectBm.bottom)
            isTouch = true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouch) isSwipe?.check(true)

                onSeekbarResult?.onDown(this)
            }

            MotionEvent.ACTION_MOVE -> {
                progress = ((event.y - sizeThumb / 2) * max / (height - sizeThumb)).toInt()

                if (progress < 0) progress = 0
                else if (progress > max) progress = max
                if (isTouch) {
                    invalidate()
                    onSeekbarResult?.onMove(this, progress)
                }
            }

            MotionEvent.ACTION_UP -> {
                isTouch = false
                isSwipe?.check(false)
                onSeekbarResult?.onUp(this, progress)
            }
        }
        return true
    }

    fun setBitmapThumb(folder: String, name: String) {
        UtilsBitmap.getBitmapFromAsset(context, folder, name)?.let {
            bitmap = Bitmap.createScaledBitmap(
                UtilsBitmap.rotateBitmap(it, 90f),
                (7.778f * w).toInt(), (5.556f * w).toInt(), false
            )
            sizeThumb = it.height.toFloat()
        }
        invalidate()
    }

    fun setColorProgress(colors: IntArray) {
        this.colorPr = colors
        invalidate()
    }

    fun setColorBg(colors: IntArray) {
        this.colorBg = colors

        invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress = progress

        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
    }
}