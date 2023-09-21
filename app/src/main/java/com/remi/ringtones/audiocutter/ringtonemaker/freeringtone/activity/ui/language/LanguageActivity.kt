package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.language

import android.view.Gravity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.language.LanguageModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding.OnBoardingActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityLanguageBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.CHECK_STATE_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.CURRENT_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FINISH_LANGUAGE
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
        DataLocalManager.getLanguage(CURRENT_LANGUAGE)?.let { lang = it }
        langAdapter.newInstance(this, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                lang = ob as LanguageModel
            }
        })
        binding.rcvLanguage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvLanguage.adapter = langAdapter

        binding.ivTick.setOnClickListener {
            if (lang != null) {
                DataLocalManager.setLanguage(CURRENT_LANGUAGE, lang!!)
                DataLocalManager.setCheck(CHECK_STATE_LANGUAGE, false)
                if (DataLocalManager.getCheck(FINISH_LANGUAGE)) finish()
                else startIntent(OnBoardingActivity::class.java.name, true)
            } else showToast(getString(R.string.you_need_pick_a_language), Gravity.CENTER)
        }

        lifecycleScope.launch {
            viewModel.getAllLanguage()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateLanguage.collect {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {}
                        is UiState.Success -> {
                            if (it.data.isNotEmpty()) langAdapter.setData(it.data)
                        }
                    }
                }
            }
        }
    }

}