package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogLoadingBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogUpdateAppBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.changeLanguage
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.hideKeyboardMain
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.setAnimExit
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.setStatusBarTransparent
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.setUpDialog
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.CURRENT_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.TAG
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity(), ActivityView, CoroutineScope {

    private var loadingDialog: AlertDialog? = null

    lateinit var job: Job
    var w = 0F

    override val coroutineContext: CoroutineContext
        get() = getDispatchers() + job

    val binding: B by lazy { bindingFactory(layoutInflater) }

    private lateinit var decorView: View
    private var finish = 0
    private var mIsShowLoading = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        decorView = window.decorView
        if (!isHideNavigation())
            setStatusBarTransparent(this@BaseActivity, isVisible(), getColorState()[0], getColorState()[1])
        else {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.navigationBarColor = Color.parseColor("#01ffffff")
            window.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = hideSystemBars()
        }
        job = Job()
        setContentView(binding.root)
        w = resources.displayMetrics.widthPixels / 100F

        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(changeLanguage(newBase, DataLocalManager.getLanguage(CURRENT_LANGUAGE)))
    }

    protected open fun getDispatchers(): CoroutineContext {
        return Dispatchers.IO + job
    }

    protected open fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#272727"))
    }

    protected open fun isVisible(): Boolean {
        return true
    }

    protected open fun isHideNavigation(): Boolean {
        return false
    }

    protected abstract fun setUp()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (isHideNavigation()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.navigationBarColor = Color.parseColor("#01ffffff")
            window.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = hideSystemBars()
        }
    }

    private fun hideSystemBars(): Int {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    fun checkUpdate() {
        //check update is UPDATE_AVAILABLE
        AppUpdateManagerFactory.create(this).appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                showDialogUpdate()
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
            } else Log.d(TAG, "checkUpdate: ehe")
        }
    }

    private fun showDialogUpdate() {
        val bindingDialog =
            DialogUpdateAppBinding.inflate(LayoutInflater.from(this@BaseActivity), null, false)

        val dialog = AlertDialog.Builder(this@BaseActivity, R.style.SheetDialog).create()
        dialog.setUpDialog(bindingDialog.root, false)

        bindingDialog.root.layoutParams.width = (69.96f * w).toInt()
        bindingDialog.root.layoutParams.height = (84.889f * w).toInt()

        bindingDialog.tvUpdate.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }

    override fun startIntent(nameActivity: String, isFinish: Boolean) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.component = ComponentName(this, nameActivity)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .toBundle()
        )
        if (isFinish) this.finish()
    }

    fun startIntent(intent: Intent, isFinish: Boolean) {
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .toBundle()
        )
        if (isFinish) this.finish()
    }

    protected fun openNavigation(nameActivity: String, isFinish: Boolean) {
        val intent = Intent()
        intent.component = ComponentName(this, nameActivity)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_left_small,
                R.anim.slide_out_right
            ).toBundle()
        )
        if (isFinish) finish()
    }

    protected fun onBackPressed(isAsk: Boolean) {
        if (isAsk) {
            if (finish != 0) {
                finish = 0
                finish()
                setAnimExit()
            } else {
                Toast.makeText(this, R.string.finish, Toast.LENGTH_SHORT).show()
                finish++
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        finish = 0
                    }
                }, 1000)
            }
        } else finish()
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        isAdd: Boolean,
        addBackStack: Boolean,
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    ) {
        try {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit)
            if (addBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)

            fragmentTransaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    ) {
        try {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit)
            if (addBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
            if (isAdd) fragmentTransaction.add(res, fragment, fragment.javaClass.simpleName)
            else fragmentTransaction.replace(res, fragment, fragment.javaClass.simpleName)

            fragmentTransaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        fragment: Fragment,
        layout: Int,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        replaceFragment(
            supportFragmentManager,
            fragment,
            layout,
            isAdd,
            addBackStack,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left_small,
            R.anim.slide_out_left
        )
    }

    override fun replaceFragment(fragment: Fragment, isAdd: Boolean, addBackStack: Boolean) {
        replaceFragment(
            supportFragmentManager,
            fragment,
            android.R.id.content,
            isAdd,
            addBackStack,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left_small,
            R.anim.slide_out_left
        )
    }

    override fun replaceFragment(fragment: Fragment, layout: Int, addBackStack: Boolean) {
        replaceFragment(
            supportFragmentManager,
            fragment,
            layout,
            false,
            addBackStack,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left_small,
            R.anim.slide_out_left
        )
    }

    override fun replaceFragment(fragment: Fragment, addBackStack: Boolean) {
        replaceFragment(
            supportFragmentManager, fragment, android.R.id.content, false, addBackStack,
            R.anim.slide_in_right, R.anim.slide_out_left,
            R.anim.slide_in_left_small, R.anim.slide_out_left
        )
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        replaceFragment(
            manager,
            fragment,
            android.R.id.content,
            isAdd,
            addBackStack,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left_small,
            R.anim.slide_out_left
        )
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        layout: Int,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        replaceFragment(
            manager,
            fragment,
            layout,
            isAdd,
            addBackStack,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left_small,
            R.anim.slide_out_left
        )
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        shareElement: View,
        transitionName: String
    ) {
        runOnUiThread {
            try {
                val fragmentTransaction = manager.beginTransaction()
                fragmentTransaction.addSharedElement(shareElement, transitionName)
                if (addBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
                if (isAdd) {
                    fragmentTransaction.add(res, fragment, fragment.javaClass.simpleName)
                } else fragmentTransaction.replace(res, fragment, fragment.javaClass.simpleName)
                fragmentTransaction.commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun popBackStack(manager: FragmentManager) {
        runOnUiThread { manager.popBackStack() }
    }

    override fun popBackStack() {
        runOnUiThread { supportFragmentManager.popBackStack() }
    }

    override fun popBackStack(manager: FragmentManager, tag: String, inclusive: Int) {
        runOnUiThread { manager.popBackStack(tag, inclusive) }
    }

    override fun popBackStack(tag: String, inclusive: Int) {
        runOnUiThread { supportFragmentManager.popBackStack(tag, inclusive) }
    }

    override fun clearBackStack() {
        runOnUiThread {
            val fm = supportFragmentManager
            val count = fm.backStackEntryCount
            for (i in 0 until count) {
                fm.popBackStack()
            }
        }
    }

    override fun onFragmentDetach(fragment: Fragment) {

    }

    override fun hideKeyboard() {
        if (this.currentFocus != null) hideKeyboardMain()
    }

    override fun showLoading() {
        showLoading(true)
    }

    private fun initDialog(isCancel: Boolean) {
        val bindingDialog = DialogLoadingBinding.inflate(LayoutInflater.from(this@BaseActivity))
        bindingDialog.root.createBackground(intArrayOf(Color.WHITE), 3.5f * w, -1, -1)

        loadingDialog = AlertDialog.Builder(this@BaseActivity, R.style.SheetDialog).create()
        loadingDialog?.setUpDialog(bindingDialog.root, isCancel)

        bindingDialog.root.layoutParams.width = (73.889f * w).toInt()
        bindingDialog.root.layoutParams.height = (34.556f * w).toInt()
        mIsShowLoading = true
    }

    override fun showLoading(cancelable: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            initDialog(cancelable)
        }
    }

    override fun hideLoading() {
        //cho chắc :(
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading && !isFinishing) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            mIsShowLoading = false
        }
    }

    override fun showPopupMessage(msg: String, title: String) {

    }

    override fun showPopupMessage(msg: String) {

    }

    override fun showMessage(msg: String) {
        showToast(msg, Gravity.CENTER)
    }

    override fun showMessage(@StringRes stringId: Int) {
        showToast(getString(stringId), Gravity.CENTER)
    }

    override fun refresh() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is BaseFragment<*>) fragment.refresh()
        }
    }
}