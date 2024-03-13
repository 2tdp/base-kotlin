package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
        @SuppressLint("StaticFieldLeak")
        var w = 0f
    }

    override fun onCreate() {
        DataLocalManager.init(applicationContext)
        ctx = applicationContext
        w = resources.displayMetrics.widthPixels / 100f
        super.onCreate()
    }
}