package com.fenghuo.notes.alarm;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AlarmService extends Service {

	public final static String Action = "com.fenghuo.alarmservice";
	public static boolean stop = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println("AlarmService 开启_------");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// 再次动态注册接收
		IntentFilter filter = new IntentFilter("com.fenghuo.alarmstart");
		filter.setPriority(Integer.MAX_VALUE - 4);
		AlarmReciver reciver = new AlarmReciver();
		registerReceiver(reciver, filter);
		//再次注册闹钟 
		MyAlarm alarm=new MyAlarm(getApplicationContext());
		alarm.reset();
		alarm.destroy();
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// return START_STICKY_COMPATIBILITY;//服务被杀死后 系统会尝试重新启动
		flags = START_NOT_STICKY;// 服务被杀死后 系统会尝试重新启动
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// kill以后重启
		if (!stop) {
			System.out.println("AlarmService 停止_------");
			Intent service = new Intent(Action);
			service.setClass(this, AlarmService.class);
			this.startService(service);
		}
		else
			super.onDestroy();
	}

}
