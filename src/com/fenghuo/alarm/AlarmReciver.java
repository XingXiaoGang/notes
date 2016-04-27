package com.fenghuo.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Audio;

import com.fenghuo.db.DBAlarmHelper;
import com.fenghuo.db.DBNoteHelper;
import com.fenghuo.bean.Alarm;
import com.fenghuo.notes.Ac_EditThing;
import com.fenghuo.notes.R;

public class AlarmReciver extends BroadcastReceiver {

	public final static String Action_StartAlarm = "com.fenghuo.alarmstart";
	private Vibrator vibrator;
	private Alarm alarm;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// 开启闹钟
		if (action.equals(Action_StartAlarm)) {

			DBAlarmHelper alarmHelper = new DBAlarmHelper(context);
			DBNoteHelper noteHelper = new DBNoteHelper(context);

			// 获取信息
			int intExtraid = intent.getIntExtra("alarmid", -1);
			alarm=alarmHelper.Getbyid(intExtraid);
			if(alarm==null)
				return;

			// 如果数据库没有对应note 说明该记事已被删除
			if (noteHelper.GetSingle(alarm.getId()) == null) {
				noteHelper.deletealarm(alarm.getId());
				alarmHelper.delete(alarm.getId());
				alarmHelper.Destroy();
				noteHelper.Desdroy();
				return;
			}

			// 振动
			if (alarm.getVibration() == 1) {
				vibrator = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				long[] pattern = { 300, 500, 300, 500 }; // OFF/ON/OFF/ON...
				vibrator.vibrate(pattern, -1);
			}

			// 通知栏
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.tickerText = alarm.getContent();// 在通知栏上的滚动信息
			notification.flags = Notification.FLAG_AUTO_CANCEL;

			// 铃声
			if (alarm.getRingUri().equals("")) {
				;// 静音
			} else if (alarm.getRingUri().equals("default")) {
				notification.defaults = Notification.DEFAULT_SOUND;
			} else {
				try {
					// 自定义
					int indexOf = alarm.getRingUri().lastIndexOf("/");
					String index = alarm.getRingUri().substring(indexOf);
					notification.sound = Uri.withAppendedPath(
							Audio.Media.INTERNAL_CONTENT_URI, index);
				} catch (Exception e) {
					// 默认
					notification.defaults = Notification.DEFAULT_SOUND;
				}
			}

			// 点击跳转动作
			Intent intent2 = new Intent(context, Ac_EditThing.class);
			intent2.putExtra("noteid", alarm.getId());
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 3,intent2, PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, "随心记提醒",alarm.getContent(), pendingIntent);
			NotificationManager maneger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			maneger.notify(0x103, notification);

			// 点亮屏幕
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank");
			wl.acquire();

			// 如果没重复 从数据库中删除
			if (alarm.getWeek().length() == 0) {
				noteHelper.deletealarm(alarm.getId());
				alarmHelper.delete(alarm.getId());
			}
			alarmHelper.Destroy();
			noteHelper.Desdroy();
		}
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			// 重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
			// 这里是在服务里启动因为开机广播发生的次数<服务开启的次数。在服务里设置，确保闹钟可以正常开启
			Intent service = new Intent(AlarmService.Action);
			service.setClass(context, AlarmService.class);
			context.startService(service);
		}
	}
}
