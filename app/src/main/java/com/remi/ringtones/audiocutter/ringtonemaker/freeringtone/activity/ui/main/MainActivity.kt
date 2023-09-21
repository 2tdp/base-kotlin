package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main

import android.graphics.Color
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun getDispatchers(): CoroutineContext {
        return Dispatchers.IO + job
    }

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, ContextCompat.getColor(this, R.color.black_background))
    }

    @Inject
    lateinit var repository: DataRepository
    private val viewModel: MainActivityViewModel by viewModels()

    override fun setUp() {
    }
}