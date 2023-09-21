package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityPermissionBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.openSettingPermission
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FIRST_INSTALL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager

class PermissionActivity: BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    override fun setUp() {
        setUpLayout()
        evenClick()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.rlNotify.visibility = View.VISIBLE
            binding.scNotify.isChecked =
                ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else binding.rlNotify.visibility = View.GONE
        binding.scCamera.isChecked =
            ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        binding.scRecorder.isChecked =
            ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        binding.scDraw.isChecked = Settings.canDrawOverlays(this@PermissionActivity)
    }

    private fun evenClick() {
        binding.scNotify.setOnCheckedChangeListener { buttonView, isChecked ->
            if (ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                binding.scNotify.isChecked = true
                return@setOnCheckedChangeListener
            }
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Dexter.withContext(this)
                        .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(p0: PermissionGrantedResponse) {
                                binding.scNotify.isChecked = true
                            }

                            override fun onPermissionDenied(p0: PermissionDeniedResponse) {
                                binding.scNotify.isChecked = false
                                openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                p0: PermissionRequest,
                                p1: PermissionToken
                            ) {
                                binding.scNotify.isChecked = false
                                p1.continuePermissionRequest()
                            }

                        }).check()
            }
        }
        binding.scCamera.setOnCheckedChangeListener { buttonView, isChecked ->
            if (ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                binding.scCamera.isChecked = true
                return@setOnCheckedChangeListener
            }
            if (isChecked)
                Dexter.withContext(this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse) {
                            binding.scCamera.isChecked = true
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse) {
                            binding.scCamera.isChecked = false
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest,
                            p1: PermissionToken
                        ) {
                            binding.scCamera.isChecked = false
                            p1.continuePermissionRequest()
                        }

                    }).check()
        }
        binding.scRecorder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                binding.scRecorder.isChecked = true
                return@setOnCheckedChangeListener
            }
            if (isChecked) {
                Dexter.withContext(this)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse) {
                            binding.scRecorder.isChecked = true
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse) {
                            binding.scRecorder.isChecked = false
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest,
                            p1: PermissionToken
                        ) {
                            p1.continuePermissionRequest()
                            binding.scRecorder.isChecked = false
                        }

                    }).check()
            }
        }
        binding.scDraw.setOnCheckedChangeListener { buttonView, isChecked ->
            if (Settings.canDrawOverlays(this@PermissionActivity)) {
                binding.scDraw.isChecked = true
                return@setOnCheckedChangeListener
            }
            if (isChecked) {
                if (!Settings.canDrawOverlays(this@PermissionActivity)) {
                    requestPermission.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    })
                    binding.scDraw.isChecked = false
                } else binding.scDraw.isChecked = true
            }
        }
        binding.tvGo.setOnClickListener {
            val isPer3 = Settings.canDrawOverlays(this@PermissionActivity)
            val isPer1 =
                ((ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    && (ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            val isPer2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(this@PermissionActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            } else true

            if (isPer1 && isPer2 && isPer3) {
                DataLocalManager.setFirstInstall(FIRST_INSTALL, true)
                startIntent(MainActivity::class.java.name, true)
            } else showToast(getString(R.string.deny_pers), Gravity.CENTER)
        }
    }

    private fun setUpLayout() {
        binding.rlNotify.createBackground(
            intArrayOf(Color.WHITE), (3.5f * w).toInt(), (0.5f * w).toInt(),
            ContextCompat.getColor(this@PermissionActivity, R.color.gray_6)
        )
        binding.scNotify.apply {
            setTrackResource(R.drawable.custom_switch_track)
            setThumbResource(R.drawable.custom_switch_thumb)
        }
        binding.rlRecorder.createBackground(
            intArrayOf(Color.WHITE), (3.5f * w).toInt(), (0.5f * w).toInt(),
            ContextCompat.getColor(this@PermissionActivity, R.color.gray_6)
        )
        binding.scRecorder.apply {
            setTrackResource(R.drawable.custom_switch_track)
            setThumbResource(R.drawable.custom_switch_thumb)
        }
        binding.rlCamera.createBackground(
            intArrayOf(Color.WHITE), (3.5f * w).toInt(), (0.5f * w).toInt(),
            ContextCompat.getColor(this@PermissionActivity, R.color.gray_6)
        )
        binding.scCamera.apply {
            setTrackResource(R.drawable.custom_switch_track)
            setThumbResource(R.drawable.custom_switch_thumb)
        }
        binding.rlDraw.createBackground(
            intArrayOf(Color.WHITE), (3.5f * w).toInt(), (0.5f * w).toInt(),
            ContextCompat.getColor(this@PermissionActivity, R.color.gray_6)
        )
        binding.scDraw.apply {
            setTrackResource(R.drawable.custom_switch_track)
            setThumbResource(R.drawable.custom_switch_thumb)
        }
    }

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!Settings.canDrawOverlays(this@PermissionActivity)) {
                showMessage(resources.getString(R.string.per_overlay))
                binding.scDraw.isChecked = false
            } else binding.scDraw.isChecked = true
        }
}