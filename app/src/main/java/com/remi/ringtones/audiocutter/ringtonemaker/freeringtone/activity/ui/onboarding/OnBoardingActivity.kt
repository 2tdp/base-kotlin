package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.adapter.DepthPageTransformer
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityOnBoardingBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FIRST_INSTALL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    companion object {
        var w = 0F
    }

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#FF57AF"))
    }

    override fun setUp() {
    }
}