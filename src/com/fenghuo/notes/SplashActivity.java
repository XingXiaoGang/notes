package com.fenghuo.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.fenghuo.notes.alarm.AlarmService;
import com.fenghuo.notes.db.DBAlarmHelper;
import com.fenghuo.notes.db.PreferenceHelper;
import com.haibison.android.lockpattern.LockPatternActivity;

public class SplashActivity extends Activity implements Handler.Callback {

    private DBAlarmHelper alarmHelper;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mHandler = new Handler(this);
        SharedPreferences sharedPreferences = getSharedPreferences("first", Context.MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isfrist", true);
        alarmHelper = new DBAlarmHelper(SplashActivity.this);
        if (isFirst) {
            createDeskShortCut();
        }
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("isfrist", false);
        editor.commit();

        View view = getLayoutInflater().inflate(R.layout.activity_welcome, null);
        AlphaAnimation animation = new AlphaAnimation(0.8f, 1.0f);
        animation.setDuration(1000);
        view.setAnimation(animation);
        setContentView(view);

        mHandler.sendEmptyMessageDelayed(R.id.skip_splash, 1500);
    }

    @Override
    protected void onDestroy() {
        alarmHelper.Destroy();
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean res = true;
        switch (msg.what) {
            case R.id.check_alarm: {
                //如果有闹钟待办 则开启服务（服务中重新注册闹钟）
                //用户可能会在有闹钟任务的中途kill掉进程，这样可以重新启用闹钟
                if (alarmHelper.GetCount() > 0) {
                    Intent intent = new Intent(AlarmService.Action);
                    intent.setClass(SplashActivity.this, AlarmService.class);
                    startService(intent);
                }
                break;
            }
            case R.id.skip_splash: {
                PreferenceHelper preferenceHelper = new PreferenceHelper(this);
                String pattenPwd = null;
                if ((pattenPwd = preferenceHelper.getPatternPwd()) != null) {
                    LockPatternActivity.startToComparePattern(this, this, Values.CODE_UN_LOCK, true, pattenPwd.toCharArray());
                } else {
                    skip();
                }
                break;
            }
            default: {
                res = false;
            }
        }
        return res;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Values.CODE_UN_LOCK: {
                if (resultCode == LockPatternActivity.RESULT_OK) {
                    skip();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    /**
     * 创建快捷方式
     */
    public void createDeskShortCut() {
        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

        // // 快捷图片
        // Parcelable icon = Intent.ShortcutIconResource.fromContext(
        // getApplicationContext(), R.drawable.ic_launcher);
        // shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Parcelable iconRes = Intent.ShortcutIconResource.fromContext(this.getApplicationContext(), R.drawable.ic_launcher);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // 不允许重复添加    fix: 第二个参数应传布尔值
        shortcutIntent.putExtra("duplicate", false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(SplashActivity.this, SplashActivity.class);
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutIntent);
    }

    private void skip() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
