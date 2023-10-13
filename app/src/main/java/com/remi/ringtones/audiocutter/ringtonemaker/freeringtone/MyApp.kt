package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
    }

    override fun onCreate() {
        DataLocalManager.init(applicationContext)
        ctx = applicationContext
        super.onCreate()
    }
}