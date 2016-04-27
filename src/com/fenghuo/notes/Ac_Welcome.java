package com.fenghuo.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.fenghuo.alarm.AlarmService;
import com.fenghuo.db.DBAlarmHelper;

public class Ac_Welcome extends Activity {

	private SharedPreferences sharedPreferences;
	private DBAlarmHelper alarmHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		sharedPreferences = getSharedPreferences("first", Context.MODE_PRIVATE);
		boolean isFirst = sharedPreferences.getBoolean("isfrist", true);
		alarmHelper = new DBAlarmHelper(Ac_Welcome.this);
		if (isFirst) {
			createDeskShortCut();
		}
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isfrist", false);
		editor.commit();

		View view = getLayoutInflater()
				.inflate(R.layout.activity_welcome, null);
		AlphaAnimation animation = new AlphaAnimation(0.8f, 1.0f);
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {

				skip();
			}
		});
		view.setAnimation(animation);
		setContentView(view);
		thread.start();
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
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));

		// // 快捷图片
		// Parcelable icon = Intent.ShortcutIconResource.fromContext(
		// getApplicationContext(), R.drawable.ic_launcher);
		// shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		Parcelable iconRes = Intent.ShortcutIconResource.fromContext(
				this.getApplicationContext(), R.drawable.ic_launcher);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		Intent intent = new Intent(getApplicationContext(), Ac_Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(Ac_Welcome.this, Ac_Welcome.class);
		// 点击快捷图片，运行的程序主入口
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		sendBroadcast(shortcutIntent);
	}

	private void skip() {
		Intent intent = new Intent(Ac_Welcome.this, Ac_Main.class);
		startActivity(intent);
		finish();
	}

	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			//如果有闹钟待办 则开启服务（服务中重新注册闹钟）
			//用户可能会在有闹钟任务的中途kill掉进程，这样可以重新启用闹钟
			if (alarmHelper.GetCount() > 0) {
				Intent intent = new Intent(AlarmService.Action);
				intent.setClass(Ac_Welcome.this, AlarmService.class);
				startService(intent);
			}
		}
	});

	@Override
	protected void onDestroy() {
		alarmHelper.Destroy();
		super.onDestroy();
	}

}
