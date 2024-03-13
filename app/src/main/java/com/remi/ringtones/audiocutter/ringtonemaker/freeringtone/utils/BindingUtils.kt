package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.URL_ASSETS

object BindingUtils {

    @BindingAdapter("2tdp:imgAssets")
    fun AppCompatImageView.setImgAssets(path: String) {
        try {
            Glide.with(this.context)
                .asBitmap()
                .load(URL_ASSETS + path)
                .into(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}