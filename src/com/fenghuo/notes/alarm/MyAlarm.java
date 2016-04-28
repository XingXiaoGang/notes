package com.fenghuo.notes.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.fenghuo.notes.db.DBAlarmHelper;
import com.fenghuo.notes.db.DBNoteHelper;
import com.fenghuo.notes.bean.Alarm;

public class MyAlarm {

	private AlarmManager manager;
	private Context context;
	private DBAlarmHelper alarmHelper;
	private DBNoteHelper noteHelper;
	private Date datenow;

	public MyAlarm(Context context) {
		this.context = context;
		manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmHelper = new DBAlarmHelper(context);
		noteHelper = new DBNoteHelper(context);
	}

	/**
	 * 设置闹钟
	 *
	 * @param alarm
	 */
	public void set(Alarm alarm) {

		// 当前的时间
		datenow = new Date();
		// 重复的星期
		String[] weekstr = null;
		int[] weekint = null;
		if (alarm.getWeek().length() > 0) {
			weekstr = alarm.getWeek().split(",");
			weekint = new int[weekstr.length];
			for (int i = 0; i < weekstr.length; i++) {
				weekint[i] = Integer.parseInt(weekstr[i]);
			}
		}

		// 闹钟时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = dateFormat.parse(alarm.getDate() + " " + alarm.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//判断是否过时(当天)
		Date datetimenow=new Date(2000, 1, 1, datenow.getHours(), datenow.getMinutes());
		Date datetimering=new Date(2000, 1, 1, date.getHours(), date.getMinutes());
		System.out.println(datetimenow.toLocaleString()+"\n"+datetimering.toLocaleString());
		if(datetimenow.after(datetimering)){
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("alarmid", alarm.getId());
		intent.setAction(AlarmReciver.Action_StartAlarm);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(),intent, PendingIntent.FLAG_UPDATE_CURRENT);


		// 有重复
		if (weekint != null && weekint.length > 0) {
			int today = datenow.getDay() + 1;
			boolean have = false;
			for (int i = 0; i < weekint.length; i++) {
				if (weekint[i] == today)
					have = true;
			}
			if (have) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(datenow);
				calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
				calendar.set(Calendar.MINUTE, date.getMinutes());
				manager.cancel(pendingIntent);
				manager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
			}
		}
		// 无重复

		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, alarm.getId(),
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		manager.cancel(pendingIntent2);
		manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntent2);
	}

	/**
	 * 删除一个闹钟
	 *
	 * @param id
	 */
	public void delete(int id) {
		// 从系统闹钟队列中删除
		Intent intent = new Intent();
		intent.setAction(AlarmReciver.Action_StartAlarm);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, 0x102 + id);
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 1,
				intent, 0x503 + id);
		manager.cancel(pendingIntent);
		manager.cancel(pendingIntent2);
	}

	/**
	 * 开机以后重新设置
	 */
	public void reset() {
		List<Alarm> alarms = alarmHelper.Getall();
		for (Alarm alarm : alarms) {
			set(alarm);
		}
	}

	/**
	 * 使用后必须调用 用来释放数据库连接
	 */
	public void destroy() {
		alarmHelper.Destroy();
		noteHelper.Desdroy();
	}

}
