package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewextent

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackCheck


class CustomImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var isSwipe: ICallBackCheck? = null

    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            isSwipe?.check(true)
        }
    })

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) true
        else {
            if (event.action == MotionEvent.ACTION_UP) isSwipe?.check(false)

            super.onTouchEvent(event)
        }
    }
}