package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.util.Util.isOnMainThread
import com.google.gson.Gson
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.language.LanguageModel

import java.io.File
import java.util.Locale

fun Any?.toJson(): String = Gson().toJson(this)

fun AppCompatActivity.changeLanguage(context: Context, language: LanguageModel?): Context {
    language?.let {
        try {
            Locale.setDefault(language.locale)
            return context.createConfigurationContext(Configuration(context.resources.configuration).apply {
                this.setLocale(language.locale)
            })
        } catch (e: Exception) {
            e.printStackTrace()
            return context.createConfigurationContext(Configuration(context.resources.configuration).apply {
                this.setLocale(Locale.ENGLISH)
            })
        }
    }
    return context.createConfigurationContext(Configuration(context.resources.configuration).apply {
        this.setLocale(Locale.ENGLISH)
    })
}

fun AppCompatActivity.getTempFile(child: String): File? {
    val folder = File(cacheDir, child)
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            showToast(getString(R.string.unknown_error_occurred), Gravity.CENTER)
            return null
        }
    }

    return folder
}

fun AppCompatActivity.setAnimExit() {
    overridePendingTransition(R.anim.slide_in_left_small, R.anim.slide_out_right)
}

fun AppCompatActivity.showToast(msg: String, gravity: Int) {
    val toast: Toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast.setGravity(gravity, 0, 0)
    toast.show()
}

fun AppCompatActivity.hideKeyboardMain() {
    if (isOnMainThread()) hideKeyboardSync()
    else {
        Handler(Looper.getMainLooper()).post {
            hideKeyboardSync()
        }
    }
}

fun AppCompatActivity.hideKeyboardSync() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    currentFocus?.clearFocus()
}

fun AppCompatActivity.showKeyboard(et: EditText) {
    et.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}

fun AppCompatActivity.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.takePersistableUriPermission(uri: Uri, flag: Int) {
    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val flags = flag and Intent.FLAG_GRANT_READ_URI_PERMISSION
    contentResolver.takePersistableUriPermission(uri, flags)
}

fun createDocumentIntent(types: Array<String>, allowedMultiple: Boolean): Intent {
    return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = if (types.isNotEmpty()) {
            putExtra(Intent.EXTRA_MIME_TYPES, types)
            types[0]
        } else "*/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowedMultiple)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

fun AppCompatActivity.openSettingPermission(action: String) {
    val intent = Intent(action)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun AppCompatActivity.actionNotificationListenerSettingPermission(c: Context): Boolean {
    return NotificationManagerCompat.getEnabledListenerPackages(c).contains(c.packageName)
}

fun AppCompatActivity.openNotificationSetting(
    context: Context, nameService: String,
    activityResultLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
    val showArgs = context.packageName + "/" + nameService
    intent.apply {
        putExtra(":settings:fragment_args_key", showArgs)
        putExtra(
            ":settings:show_fragment_args",
            Bundle().apply { putString(":settings:fragment_args_key", showArgs) })
    }
    try {
        activityResultLauncher.launch(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Please open Setting", Toast.LENGTH_SHORT).show()
    }
}

@Suppress("DEPRECATION")
fun makeVibrate(context: Context) {
    val vibrator: Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        //deprecated in API 26
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }
}

fun AppCompatActivity.setStatusBarTransparent(context: Context, isVisible: Boolean, colorStatus: Int, colorNavi: Int) {
    val decorView = window.decorView
    window.statusBarColor = colorStatus
    window.navigationBarColor = colorNavi

    var flags = (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    if (isVisible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        decorView.systemUiVisibility = flags
    } else {
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT
//        if (Build.VERSION.SDK_INT >= 30)
//            WindowCompat.setDecorFitsSystemWindows(window, false)
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//            decorView.systemUiVisibility = flags
//        } else if (Build.VERSION.SDK_INT in 21..26) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            actionBar?.hide()
//        }

        /**
         * display above lock screen
         */
        var flag = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        val manager =
            applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            manager.requestDismissKeyguard(this, null)
        } else {
            flag = flag or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        }
        /**
         * set navigation bar transparent
         */
        decorView.systemUiVisibility = flags
        window.addFlags(flag)
        window.navigationBarColor = Color.parseColor("#01ffffff")
        window.statusBarColor = Color.TRANSPARENT

        (context.getSystemService(Context.POWER_SERVICE) as PowerManager).let {
            @SuppressLint("InvalidWakeLockTag")
            val wOn = it.newWakeLock(PowerManager.FULL_WAKE_LOCK, "tag")
            wOn.acquire(5 * 1000L)
        }
    }
}