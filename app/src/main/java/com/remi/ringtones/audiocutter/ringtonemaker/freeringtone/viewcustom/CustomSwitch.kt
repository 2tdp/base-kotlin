package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R


class CustomSwitch : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val rectF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private var isCallback = true
    private var isChecked = false
    private var isReset = false
    private var thumbX = 0f
    private var margin = 0f
    private var padding = 0f
    private var sizeThumb = 0f
    private var valueAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 334
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            thumbX = animation.animatedValue as Float
            invalidate()
        }
    }
    var onResult: StatusResultSwitch? = null

    init {
        setOnClickListener { setChecked(!isChecked) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        padding = 0.084f * width
        margin = 0.055f * width
        sizeThumb = 0.52f * width - padding

        paint.color = if (isChecked) ContextCompat.getColor(context, R.color.main_color)
        else ContextCompat.getColor(context, R.color.gray_main2)
        rectF.set(padding, padding / 2f, width.toFloat() - padding, height.toFloat() - padding / 2f)
        canvas.drawRoundRect(rectF, height / 2f, height / 2f, paint)

        paint.color = if (isChecked) ContextCompat.getColor(context, R.color.white)
        else ContextCompat.getColor(context, R.color.white)
        canvas.drawCircle( padding + margin + thumbX + sizeThumb / 2f, height / 2f, sizeThumb / 2f, paint)

        if (isReset) {
            val startValue = if (this.isChecked) 0f else width - 2 * padding - sizeThumb - 2 * margin
            val endValue = if (this.isChecked) width - 2 * padding - sizeThumb - 2 * margin else 0f
            valueAnimator.apply {
                setFloatValues(startValue, endValue)
                start()
            }
            isReset = false
        }
    }

    fun setChecked(isChecked: Boolean) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked
            if (width == 0) isReset = true
            val startValue = if (this.isChecked) 0f else width - 2 * padding - sizeThumb - 2 * margin
            val endValue = if (this.isChecked) width - 2 * padding - sizeThumb - 2 * margin else 0f
            valueAnimator.apply {
                setFloatValues(startValue, endValue)
                start()
            }
            if (isCallback) onResult?.onResult(this.isChecked)
        }
    }

    fun getChecked(): Boolean = isChecked

    fun setCallback(isCallback: Boolean) {
        this.isCallback = isCallback

        invalidate()
    }
}