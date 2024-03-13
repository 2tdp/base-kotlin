package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.language

import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.LanguageModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding.OnBoardingActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityLanguageBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.setOnUnDoubleClickListener
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.CURRENT_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FINISH_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.IS_SHOW_BACK
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    @Inject
    lateinit var langAdapter: LanguageAdapter
    private val viewModel: LanguageActivityViewModel by viewModels()
    private var lang: LanguageModel? = null

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this@LanguageActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DataLocalManager.setCheck(FINISH_LANGUAGE, false)
                finish()
            }
        })

        DataLocalManager.getLanguage(CURRENT_LANGUAGE)?.let { lang = it }
        langAdapter.newInstance(this, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                lang = ob as LanguageModel
            }
        })
        binding.rcvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvLanguage.adapter = langAdapter

        binding.ivBack.visibility = if (DataLocalManager.getCheck(IS_SHOW_BACK)) View.VISIBLE else View.GONE
        binding.ivBack.setOnUnDoubleClickListener {
            DataLocalManager.setCheck(FINISH_LANGUAGE, false)
            finish()
        }
        binding.ivTick.setOnUnDoubleClickListener {
            if (lang != null) {
                DataLocalManager.setLanguage(CURRENT_LANGUAGE, lang!!)
                if (DataLocalManager.getCheck(FINISH_LANGUAGE)) {
                    startIntent(MainActivity::class.java.name, true)
                    finishAffinity()
                } else {
                    startIntent(OnBoardingActivity::class.java.name, true)
                    finish()
                }
            } else showToast(getString(R.string.you_need_pick_a_language), Gravity.CENTER)
        }

        lifecycleScope.launch {
            viewModel.getAllLanguage()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateLanguage.collect {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {}
                        is UiState.Success -> langAdapter.submitList(it.data)
                    }
                }
            }
        }
    }

}