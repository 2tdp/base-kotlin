package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewextent

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Objects

class CustomEditTextWithBackPressEvent : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var onBackPressListener: MyEditTextListener? = null

    fun setOnBackPressListener(onBackPressListener: MyEditTextListener?) {
        this.onBackPressListener = onBackPressListener
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            //back button pressed
            ViewCompat.getRootWindowInsets(rootView)?.let {
                if (it.isVisible(WindowInsetsCompat.Type.ime())) {
                    //keyboard is open
                    onBackPressListener?.callback()
                }
            }
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    interface MyEditTextListener {
        fun callback()
    }
}