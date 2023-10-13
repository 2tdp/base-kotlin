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
    private var rectF = RectF()
    private var path = Path()
    private var w = 0f
    private var radius = 0f
    private var isCreate = true
    private var isFirstShader = false
    private var max = 100
    private var currentProgress = 0
    private var sizeBg = 0f
    private var sizePos = 0f
    private var colorBg = ContextCompat.getColor(context, R.color.black_background)
    private var colorPrs = intArrayOf()

    var isSwipe: ICallBackCheck? = null

    init {
        w = resources.displayMetrics.widthPixels / 100f
        radius = 1.11f * w
        sizeBg = 1.667f * w
        sizePos = 1.667f * w
        paint.apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        paintPr.apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreate) {
            isCreate = false
            rectF.set(radius, (height - radius) / 2f, width - radius, (height + radius) / 2f)
            path.addRoundRect(rectF, radius / 2f, radius / 2f, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipPath(path)

        //draw bg
        paint.apply {
            strokeWidth = sizeBg
            color = colorBg
        }
        canvas.drawLine(radius / 2f, height / 2f, width - radius / 2f, height / 2f, paint)

        //draw load
        if (colorPrs.size == 1)
            paintPr.apply {
                shader = null
                color = colorPrs[0]
            }
        else {
            if (!isFirstShader) {
                paintPr.shader =
                    LinearGradient(0f, 0f, width - radius, 0f, colorPrs, null, Shader.TileMode.CLAMP)
                isFirstShader = true
            }
        }
        paintPr.strokeWidth = sizePos
        val p = (width - radius / 2f) * currentProgress / max + radius / 2f
        canvas.drawLine(radius / 2f, height / 2f, p, height / 2f, paintPr)

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

    fun setColorBackground(color: Int) {
        this.colorBg = color

        invalidate()
    }

    fun setColorProgress(colors: IntArray) {
        this.colorPrs = colors

        invalidate()
    }
}