package com.fenghuo.notes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Handler.Callback
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.fenghuo.notes.adapter.ContentPageAdapter
import com.fenghuo.notes.db.BackupRestoreUtils
import com.fenghuo.notes.db.DBAccountHelper
import com.fenghuo.notes.db.DBNoteHelper
import com.fenghuo.notes.db.PreferenceHelper
import com.fenghuo.notes.upload.AccountProfileManager
import com.fenghuo.notes.upload.CloudUtils
import com.fenghuo.notes.upload.Config
import com.haibison.android.lockpattern.LockPatternActivity
import com.mine.view.ViewPagerTab
import com.mine.view.dialog.ConformDialog
import com.mine.view.gesture.GestureFrameLayout
import com.mine.view.gesture.GestureHandler
import com.mine.view.menu.icon.MaterialMenuDrawable
import com.mine.view.menu.icon.MaterialMenuView
import com.mine.view.menu.slide_section_menu.SlideSectionMenu

import java.io.File


class MainActivity : FragmentActivity(), View.OnClickListener, GestureHandler.GestureCallBack, ViewPager.OnPageChangeListener, ViewPagerTab.OnPageChangeListener_vp, AccountProfileManager.ILoginListener, Callback {

    private var mViewPager: ViewPager? = null
    private var mAdapter: ContentPageAdapter? = null
    private var mMenu: SlideSectionMenu? = null

    private var mThingsTextView: TextView? = null
    private var mAccountsTextView: TextView? = null
    private var mToast: CustomToast? = null
    private var mBackupRestore: BackupRestoreUtils? = null
    private var mGestureFrameLayout: GestureFrameLayout? = null
    private var mMenuView: MaterialMenuView? = null

    private var mLastBackDownTime = System.currentTimeMillis()// 时间
    private var mBackDownTimes = 0// 在短时间内按下的次数
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMenuView = findViewById(R.id.material_menu_button) as MaterialMenuView
        val pagerTab = findViewById(R.id.vp_tab) as ViewPagerTab
        mViewPager = findViewById(R.id.vp_content) as ViewPager
        mThingsTextView = findViewById(R.id.tv_things) as TextView
        mAccountsTextView = findViewById(R.id.tv_accounts) as TextView
        mMenu = findViewById(R.id.menu) as SlideSectionMenu
        mGestureFrameLayout = findViewById(R.id.container) as GestureFrameLayout
        //回调手势
        mGestureFrameLayout!!.setGestureCallBack(this)
        mHandler = Handler(this)

        findViewById(R.id.item_about).setOnClickListener(this)
        findViewById(R.id.item_backup).setOnClickListener(this)
        findViewById(R.id.item_recovery).setOnClickListener(this)
        findViewById(R.id.item_exit).setOnClickListener(this)
        findViewById(R.id.item_clean_accounts).setOnClickListener(this)
        findViewById(R.id.item_clean_notes).setOnClickListener(this)
        findViewById(R.id.item_setpwd).setOnClickListener(this)
        findViewById(R.id.item_cancel).setOnClickListener(this)

        mAdapter = ContentPageAdapter(supportFragmentManager)
        mViewPager!!.adapter = mAdapter
        pagerTab.setViewPager(mViewPager!!)
        pagerTab.setOnPageChangerListener(this)
        mViewPager!!.setCurrentItem(0, true)

        mToast = CustomToast(this@MainActivity)
        mMenuView!!.setOnClickListener(this)
        mAccountsTextView!!.setOnClickListener(this)
        mThingsTextView!!.setOnClickListener(this)
        mMenu!!.post { mMenu!!.closeMenu() }
        val preferenceHelper = PreferenceHelper(this)
        if (preferenceHelper.patternPwd != null) {
            (findViewById(R.id.item_setpwd) as TextView).setText(R.string.del_pwd)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.material_menu_button -> {
                val isToOpen = mMenuView!!.state == MaterialMenuDrawable.IconState.BURGER
                if (isToOpen) {
                    openMenu()
                } else {
                    closeMenu()
                }
            }
            R.id.tv_things -> {
                mViewPager!!.currentItem = 0
            }
            R.id.tv_accounts -> {
                mViewPager!!.currentItem = 1
            }
            R.id.item_recovery -> {
                object : ConformDialog(this@MainActivity, getString(R.string.recovery_method)) {

                    override fun onClick(view: View) {
                        if (view.id == R.id.confirm) {
                            doRestoreCloud()
                        } else if (view.id == R.id.cancel) {
                            doRestore(null)
                        }
                        dismiss()
                    }
                }.setButtonTextColor(resources.getColor(R.color.text_444444), resources.getColor(R.color.text_444444))
                        .setButtonText(getString(R.string.local_recovery), getString(R.string.cloud_recovery)).show()
                mMenu!!.closeMenu()
            }
            R.id.item_backup -> {
                object : ConformDialog(this@MainActivity, getString(R.string.choose_backup_method)) {

                    override fun onClick(view: View) {
                        if (view.id == R.id.confirm) {
                            doBackUpCloud()
                        } else if (view.id == R.id.cancel) {
                            doBackup()
                        }
                        dismiss()
                    }
                }.setButtonTextColor(resources.getColor(R.color.text_444444), resources.getColor(R.color.text_444444))
                        .setButtonText(getString(R.string.local_backup), getString(R.string.cloud_backup)).show()
                mMenu!!.closeMenu()
            }
            R.id.item_setpwd -> {
                val text = (view as TextView).text.toString()
                if (TextUtils.equals(text, getString(R.string.del_pwd))) {
                    PreferenceHelper(this).delPatternpwd()
                    view.setText(R.string.create_pwd)
                    Toast.makeText(this, R.string.pwd_deleted, Toast.LENGTH_SHORT).show()
                } else {
                    LockPatternActivity.startToCreatePattern(this, this, Values.CODE_SET_LOCK)
                    mMenu!!.closeMenu()
                }
            }
            R.id.item_clean_notes -> {

                object : ConformDialog(this@MainActivity, getString(R.string.delete_all_note_warning)) {
                    override fun onClick(view: View) {
                        if (view.id == R.id.confirm) {
                            val helper = DBNoteHelper(this@MainActivity)
                            helper.Deleteall()
                            helper.Desdroy()
                            mToast!!.ShowMsg(getString(R.string.delete_success), CustomToast.Img_Ok)
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        dismiss()
                    }
                }.setButtonText(getString(R.string.cancel), getString(R.string.delete)).show()
                mMenu!!.closeMenu()
            }
            R.id.item_clean_accounts -> {
                object : ConformDialog(this@MainActivity, getString(R.string.delete_all_account_warning)) {
                    override fun onClick(view: View) {
                        if (view.id == R.id.confirm) {
                            val helper = DBAccountHelper(this@MainActivity)
                            helper.Deleteall()
                            helper.Destroy()
                            mToast!!.ShowMsg(getString(R.string.delete_success), CustomToast.Img_Ok)
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        dismiss()
                    }
                }.setButtonText(getString(R.string.cancel), getString(R.string.delete)).show()
                mMenu!!.closeMenu()
            }
            R.id.item_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                mMenu!!.closeMenu()
            }
            R.id.item_exit -> {
                finish()
            }
            R.id.item_cancel -> {
                closeMenu()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var res = false
        when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                toggleMenu()
                res = true
            }
            KeyEvent.KEYCODE_BACK -> {
                res = closeMenu();
                if (!res) {
                    res = mAdapter!!.dispatchOnKeyDown(keyCode, event)
                }
                if (!res) {
                    if (mBackDownTimes == 1 && System.currentTimeMillis() - mLastBackDownTime < CLICK_DURATION || mBackDownTimes == 0) {
                        mBackDownTimes++
                    } else {
                        mBackDownTimes = 0
                        res = true
                    }

                    if (mBackDownTimes == 1) {
                        Toast.makeText(applicationContext, getString(R.string.click_again_to_quit), Toast.LENGTH_SHORT).show()
                        res = true
                    } else if (mBackDownTimes == 2) {
                        res = false
                    }
                    mLastBackDownTime = System.currentTimeMillis()
                }
            }
            else -> {
                res = mAdapter!!.dispatchOnKeyDown(keyCode, event)
            }
        }
        return res || super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPageScrollStateChanged(arg0: Int) {

    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

    }

    override fun onPageSelected(i: Int) {
        when (i) {
            0 -> {
                mThingsTextView!!.setTextColor(resources.getColor(
                        R.color.bluedeep_text))
                mAccountsTextView!!
                        .setTextColor(resources.getColor(R.color.gray_text))
            }
            1 -> {
                mAccountsTextView!!.setTextColor(resources.getColor(
                        R.color.bluedeep_text))
                mThingsTextView!!.setTextColor(resources.getColor(R.color.gray_text))
            }

            else -> {
            }
        }
    }

    override fun onGestureClick(): Boolean {
        closeMenu()
        return true
    }

    override fun onSlideDown(): Boolean {
        openMenu()
        return true
    }

    override fun onSlideUp(): Boolean {
        closeMenu()
        return true
    }

    private fun openMenu(): Boolean {
        if (mMenu!!.menuState == SlideSectionMenu.State.CLOSED) {
            mMenu!!.openMenu(true)
            updateMenuState(true)
            return true;
        }
        return false;
    }

    private fun closeMenu(): Boolean {
        if (mMenu!!.menuState == SlideSectionMenu.State.OPENED) {
            mMenu!!.closeMenu()
            updateMenuState(false)
            return true
        }
        return false
    }

    private fun toggleMenu() {
        if (mMenu!!.menuState != SlideSectionMenu.State.OPENED) {
            openMenu()
        } else {
            closeMenu()
        }
    }

    private fun updateMenuState(toOpen: Boolean) {
        if (toOpen) {
            mMenuView!!.animateState(MaterialMenuDrawable.IconState.X)
        } else {
            mMenuView!!.animateState(MaterialMenuDrawable.IconState.BURGER)
        }
    }

    private fun doRestore(fileName: String?) {
        if (mBackupRestore == null)
            mBackupRestore = BackupRestoreUtils(this@MainActivity)
        object : ConformDialog(this@MainActivity, (if (fileName != null) getString(R.string.restore_from_cloud_ask) else getString(R.string.sure_restor))) {
            override fun onClick(view: View) {
                if (view.id == R.id.confirm) {
                    mToast!!.ShowMsg(getString(R.string.restoreing), CustomToast.Img_Info)
                    Values.isRestore_database = true
                    var res = -1
                    if (fileName != null) {
                        res = mBackupRestore!!.restoreEncyptFile(fileName)
                    } else {
                        res = mBackupRestore!!.restoreLocal()
                    }
                    if (res == 1) {
                        mToast!!.ShowMsg(getString(R.string.restore_success), CustomToast.Img_Ok)
                        val intent = packageManager.getLaunchIntentForPackage(packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    if (res == 0) {
                        mToast!!.ShowMsg(getString(R.string.no_backup_file), CustomToast.Img_Info)
                    }
                    if (res == -1) {
                        mToast!!.ShowMsg(getString(R.string.restore_fialed), CustomToast.Img_Ok)
                    }
                    Values.isRestore_database = false
                }
                dismiss()
            }
        }.setButtonText(getString(R.string.cancel), getString(R.string.restore)).show()
    }

    private fun doBackup() {
        if (mBackupRestore == null)
            mBackupRestore = BackupRestoreUtils(this@MainActivity)
        if (mBackupRestore!!.checkexist()) {
            object : ConformDialog(this@MainActivity, getString(R.string.backup_exist)) {
                override fun onClick(view: View) {
                    if (view.id == R.id.confirm) {
                        mToast!!.ShowMsg(getString(R.string.backuping), CustomToast.Img_Info)
                        if (mBackupRestore!!.backupLocal()) {
                            mToast!!.ShowMsg(getString(R.string.backup_success),
                                    CustomToast.Img_Ok)
                        }
                    }
                    dismiss()
                }
            }.setButtonText(getString(R.string.cancel_backup), getString(R.string.continue_backup)).show()
        } else {
            mToast!!.ShowMsg(getString(R.string.backuping), CustomToast.Img_Info)
            if (mBackupRestore!!.backupLocal()) {
                mToast!!.ShowMsg(getString(R.string.backup_success), CustomToast.Img_Ok)
            }
        }
    }

    private fun doRestoreCloud() {
        if (AccountProfileManager.getInstance(this).userState != AccountProfileManager.LOGIN_STATE_LOGIN) {
            AccountProfileManager.getInstance(this).requestLogin(this, this, AccountProfileManager.REQUEST_RESTORE)
        } else {
            //从云端下载
            if (AccountProfileManager.getInstance(this).userState == AccountProfileManager.LOGIN_STATE_LOGIN) {
                val serverFileName = Config.dbDir + File.separator + AccountProfileManager.getInstance(this).userDbName
                CloudUtils.doDownload(Config.Buckte, serverFileName, File(tempDownloadDbName), mHandler)
                mToast!!.ShowMsg(getString(R.string.downloading), CustomToast.Img_Info)
            } else {
                CustomToast(this).ShowMsg(getString(R.string.have_not_login), CustomToast.Img_Info)
            }
        }
    }

    private fun doBackUpCloud() {
        if (AccountProfileManager.getInstance(this).userState != AccountProfileManager.LOGIN_STATE_LOGIN) {
            AccountProfileManager.getInstance(this).requestLogin(this, this, AccountProfileManager.REQUEST_BACKUP)
        } else {
            if (AccountProfileManager.getInstance(this).userState == AccountProfileManager.LOGIN_STATE_LOGIN) {
                //先把最新的数据拿出来
                if (mBackupRestore == null)
                    mBackupRestore = BackupRestoreUtils(this@MainActivity)
                if (mBackupRestore!!.copyDbFileWithEncypt(tempUploadDbName)) {
                    //再上传
                    val serverFileName = Config.dbDir + File.separator + AccountProfileManager.getInstance(this).userDbName
                    CloudUtils.doUpload(Config.Buckte, serverFileName, File(tempUploadDbName), mHandler)
                    mToast!!.ShowMsg(getString(R.string.uploading), CustomToast.Img_Info)
                }
            } else {
                CustomToast(this).ShowMsg(getString(R.string.have_not_login), CustomToast.Img_Info)
            }
        }
    }

    private val tempUploadDbName: String
        get() = this.filesDir.absolutePath + File.separator + "temp_upload_db.temp"

    private val tempDownloadDbName: String
        get() = this.filesDir.absolutePath + File.separator + "temp_download_db.temp"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(Values.TAG, "onActivityResult: $requestCode , $resultCode , $data")

        when (requestCode) {
            Values.CODE_SET_LOCK -> {
                if (data != null) {
                    val pwd = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN)
                    if (pwd != null && pwd.isNotEmpty()) {
                        val savedPwd = String(pwd)
                        PreferenceHelper(this).setPatternpwd(savedPwd)
                        (findViewById(R.id.item_setpwd) as TextView).setText(R.string.del_pwd)
                        Toast.makeText(applicationContext, R.string.pwd_created, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onLogin(requestCode: Int) {
        when (requestCode) {
            AccountProfileManager.REQUEST_RESTORE -> {
                doRestoreCloud()
            }
            AccountProfileManager.REQUEST_BACKUP -> {
                doBackUpCloud()
            }
        }
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            R.id.file_download_ok -> {
                //todo 可以传回一些云端文件的信息
                mToast!!.cancel()
                doRestore(tempDownloadDbName)
            }
            R.id.file_download_error -> {
                CustomToast(applicationContext).ShowMsg(getString(R.string.file_download_failed), CustomToast.Img_Erro)
            }
            R.id.file_upload_ok -> {
                mToast!!.ShowMsg(getString(R.string.file_uplaod_success), CustomToast.Img_Ok)
            }
        }
        return false
    }

    companion object {
        private val CLICK_DURATION = 800
    }
}
