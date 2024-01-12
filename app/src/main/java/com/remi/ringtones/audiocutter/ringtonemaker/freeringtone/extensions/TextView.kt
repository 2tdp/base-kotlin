package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils

fun TextView.textCustom(content: String, color: Int, textSize: Float, font: String, context: Context) {
    text = content
    setTextColor(color)
    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    typeface = try {
        Utils.getTypeFace(font.substring(0, 8), "$font.ttf", context)
    } catch (e: Exception) {
        Utils.getTypeFace(font.substring(0, 8), "$font.otf", context)
    }
}

fun TextView.setGradient(colors: IntArray) {
    paint.shader = LinearGradient(
        0f,
        0f,
        paint.measureText(text.toString()),
        textSize,
        colors, null, Shader.TileMode.CLAMP)
    invalidate()
}

fun TextView.setScrollText() {
    try {
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isFocusable = true
        isFocusableInTouchMode = true
        isSingleLine = true
        marqueeRepeatLimit = -1
        isHorizontalScrollBarEnabled = true
        isSelected = true
        requestFocus()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.setThreeDot(gravity: Gravity) {
    try {
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
    } catch (e: Exception) {
        e.printStackTrace()
    }
}