package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityPremiumBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#FF49CC"))
    }

    override fun setUp() {

    }
}