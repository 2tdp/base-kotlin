package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewextent

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ToastCustomBinding

class CustomToast(context: Context): Toast(context) {

    companion object {
        fun makeText(context: Context, message: String, duration: Int): Toast {
            val w = context.resources.displayMetrics.widthPixels / 100f
            val binding =
                ToastCustomBinding.inflate(LayoutInflater.from(context), null, false)
            binding.root.text = message

            return Toast(context).apply {
                this.duration = duration
                view = binding.root
                setGravity(Gravity.TOP, 0, (11.11f * w).toInt())
            }
        }
    }
}