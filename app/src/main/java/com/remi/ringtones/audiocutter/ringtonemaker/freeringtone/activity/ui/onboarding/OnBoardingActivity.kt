package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.TipsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.PermissionActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.adapter.DepthPageTransformer
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityOnBoardingBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.setOnUnDoubleClickListener
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FIRST_INSTALL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import javax.inject.Inject

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun isVisible(): Boolean {
        return false
    }

    @Inject
    lateinit var pageAdapter: PagerOnBoardingAdapter

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(binding.viewPager.currentItem) {
                    0 -> onBackPressed(true)
                    1 -> {
                        binding.viewPager.setCurrentItem(0, true)
                        binding.tvAction.text = getString(R.string.next)
                    }
                    2 -> {
                        binding.viewPager.setCurrentItem(1, true)
                        binding.tvAction.text = getString(R.string.next)
                    }
                }
            }
        })

        binding.tvAction.setOnUnDoubleClickListener {
            when(binding.viewPager.currentItem) {
                0 -> {
                    binding.viewPager.setCurrentItem(1, true)
                    binding.tvAction.text = getString(R.string.next)
                }
                1 -> {
                    binding.viewPager.setCurrentItem(2, true)
                    binding.tvAction.text = getString(R.string.start)
                }
                2 -> {
                    if (DataLocalManager.getFirstInstall(FIRST_INSTALL))
                        startIntent(MainActivity::class.java.name, true)
                    else startIntent(PermissionActivity::class.java.name, true)
                }
            }
        }

        pageAdapter.newInstance(this@OnBoardingActivity, 0)
        pageAdapter.setData(mutableListOf<TipsModel>().apply {
//            add(TipsModel("", getString(R.string.des_onboarding_1), R.drawable.img_on_boarding_1, -1))
//            add(TipsModel("", getString(R.string.des_onboarding_2), R.drawable.img_on_boarding_2, -1))
//            add(TipsModel("", getString(R.string.des_onboarding_3), R.drawable.img_on_boarding_3, -1))
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> binding.tvAction.text = getString(R.string.next)
                    1 -> binding.tvAction.text = getString(R.string.next)
                    2 -> binding.tvAction.text = getString(R.string.start)
                }
            }
        })

        binding.viewPager.apply {
            setPageTransformer(DepthPageTransformer())
            adapter = pageAdapter
        }
        binding.indicator.attachTo(binding.viewPager)
    }
}