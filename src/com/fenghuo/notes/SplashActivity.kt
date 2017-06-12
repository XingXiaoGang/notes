package com.fenghuo.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.animation.AlphaAnimation
import com.fenghuo.notes.alarm.AlarmService
import com.fenghuo.notes.db.DBAlarmHelper
import com.fenghuo.notes.db.PreferenceHelper
import com.haibison.android.lockpattern.LockPatternActivity

class SplashActivity : Activity(), Handler.Callback {

    private var alarmHelper: DBAlarmHelper? = null
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mHandler = Handler(this)
        val sharedPreferences = getSharedPreferences("first", Context.MODE_PRIVATE)
        val isFirst = sharedPreferences.getBoolean("isfrist", true)
        alarmHelper = DBAlarmHelper(this@SplashActivity)
        if (isFirst) {
            createDeskShortCut()
        }
        val editor = sharedPreferences.edit()
        editor.putBoolean("isfrist", false)
        editor.commit()

        val view = layoutInflater.inflate(R.layout.activity_welcome, null)
        val animation = AlphaAnimation(0.8f, 1.0f)
        animation.duration = 1000
        view.animation = animation
        setContentView(view)

        mHandler!!.sendEmptyMessageDelayed(R.id.skip_splash, 1500)
    }

    override fun onDestroy() {
        alarmHelper!!.Destroy()
        super.onDestroy()
    }

    override fun handleMessage(msg: Message): Boolean {
        var res = true
        when (msg.what) {
            R.id.check_alarm -> {
                //如果有闹钟待办 则开启服务（服务中重新注册闹钟）
                //用户可能会在有闹钟任务的中途kill掉进程，这样可以重新启用闹钟
                if (alarmHelper!!.GetCount() > 0) {
                    val intent = Intent(AlarmService.Action)
                    intent.setClass(this@SplashActivity, AlarmService::class.java)
                    startService(intent)
                }
            }
            R.id.skip_splash -> {
                val preferenceHelper = PreferenceHelper(this)
                var pattenPwd: String? = preferenceHelper.patternPwd
                if (pattenPwd != null) {
                    LockPatternActivity.startToComparePattern(this, this, Values.CODE_UN_LOCK, true, pattenPwd.toCharArray())
                } else {
                    skip()
                }
            }
            else -> {
                res = false
            }
        }
        return res
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Values.CODE_UN_LOCK -> {
                if (resultCode == Activity.RESULT_OK) {
                    skip()
                } else {
                    finish()
                }
            }
        }
    }

    /**
     * 创建快捷方式
     */
    fun createDeskShortCut() {
        // 创建快捷方式的Intent
        val shortcutIntent = Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT")
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false)
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name))

        // // 快捷图片
        // Parcelable icon = Intent.ShortcutIconResource.fromContext(
        // getApplicationContext(), R.drawable.ic_launcher);
        // shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        val iconRes = Intent.ShortcutIconResource.fromContext(this.applicationContext, R.drawable.ic_launcher)
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
        // 不允许重复添加    fix: 第二个参数应传布尔值
        shortcutIntent.putExtra("duplicate", false)
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClass(this@SplashActivity, SplashActivity::class.java)
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        // 发送广播。OK
        sendBroadcast(shortcutIntent)
    }

    private fun skip() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
