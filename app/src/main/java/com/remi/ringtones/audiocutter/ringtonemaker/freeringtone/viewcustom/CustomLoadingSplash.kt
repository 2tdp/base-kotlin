package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackCheck

class CustomLoadingSplash : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintPr = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shaderBg: LinearGradient? = null
    private var shaderPr: LinearGradient? = null
    private var w = 0f
    private var radius = 0f
    private var max = 100
    private var currentProgress = 0
    private var stroke = 0f
    private var sizeBg = 0f
    private var sizePr = 0f
    private var colorBgs = intArrayOf(
        ContextCompat.getColor(context, R.color.color_FFAD0E),
        ContextCompat.getColor(context, R.color.color_FFAD0E)
    )
    private var colorPrs = intArrayOf(
        ContextCompat.getColor(context, R.color.color_FFAD0E),
        ContextCompat.getColor(context, R.color.color_FFDF70),
        ContextCompat.getColor(context, R.color.color_FFAD0E)
    )

    var isSwipe: ICallBackCheck? = null

    init {
        w = resources.displayMetrics.widthPixels / 100f
        radius = 2.778f * w
        sizeBg = 3.0556f * w
        sizePr = 2.22f * w
        stroke = 0.8336f * w
        paint.apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        paintPr.apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw bg
        paint.apply {
            strokeWidth = stroke
            if (shaderBg == null)
                shaderBg = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), colorBgs, null, Shader.TileMode.CLAMP)
            paint.shader = shaderBg
        }
        canvas.drawRoundRect(
            radius / 2f,
            height / 2f - sizeBg / 2f,
            width - radius / 2f,
            height / 2f + sizeBg / 2f,
            radius, radius,
            paint
        )

        //draw load
        if (shaderPr == null)
            shaderPr = LinearGradient(0f, 0f, width - stroke, height.toFloat(), colorPrs, null, Shader.TileMode.CLAMP)
        paintPr.shader = shaderPr
        paintPr.strokeWidth = sizePr
        val p = (width - radius / 2) * currentProgress / max
        canvas.drawRoundRect(
            radius / 2,
            height / 2f - sizePr / 2f,
            p,
            height / 2f + sizePr / 2f,
            radius, radius,
            paintPr
        )

        if (currentProgress < 100) {
            currentProgress++
            postInvalidateDelayed(15)
        } else isSwipe?.check(true)
    }

    fun setRadius(radius: Float) {
        this.radius = radius

        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max

        invalidate()
    }

    fun setCurrentProgress(progress: Int) {
        this.currentProgress = progress

        invalidate()
    }

    fun setColorBackground(color: IntArray) {
        this.colorBgs = color

        invalidate()
    }

    fun setColorProgress(colors: IntArray) {
        this.colorPrs = colors

        invalidate()
    }
}