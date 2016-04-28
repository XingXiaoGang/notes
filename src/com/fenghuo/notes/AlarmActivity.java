package com.fenghuo.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fenghuo.notes.alarm.MyAlarm;
import com.fenghuo.notes.db.DBAlarmHelper;
import com.fenghuo.notes.db.DBNoteHelper;
import com.fenghuo.notes.bean.Alarm;
import com.fenghuo.notes.bean.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends Activity implements OnClickListener {

	private MyAlarm alarmmanage;// 闹钟管理类
	private DBAlarmHelper alarmHelper;// 闹钟数据库类
	private DBNoteHelper noteHelper;// note 数据库类
	private CustomToast toast;
	private Button btn_save;
	private Button btn_back;
	private Button btn_cancel;
	private Button btn_ring;
	private TextView tv_date;
	private TextView tv_time;
	private TextView tv_week;
	private TextView tv_ring;
	private RelativeLayout rl_ring;
	private CheckedTextView checktext1;
	private CheckedTextView checktext2;
	private CheckedTextView checktext3;
	private CheckedTextView checktext4;
	private CheckedTextView checktext5;
	private CheckedTextView checktext6;
	private CheckedTextView checktext7;
	private CheckBox chb_vibration;
	private TextView tv_contents;
	private String ringUri = "default";
	private Note selecteNote;
	private AlertDialog dialog_date;
	private AlertDialog dialog_time;
	private DatePicker datePicker;
	private TimePicker timePicker;
	String[] weekstr = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		findVeiws();

		alarmmanage = new MyAlarm(AlarmActivity.this);
		noteHelper = new DBNoteHelper(AlarmActivity.this);
		alarmHelper = new DBAlarmHelper(AlarmActivity.this);
		// 接收
		int noteid = getIntent().getIntExtra("noteid", -1);
		selecteNote = noteHelper.GetSingle(noteid);
		initeDate();
		// 初始化
		Alarm alarm = alarmHelper.Getbyid(selecteNote.getId());
		if (selecteNote.getAlarm() == 1) {
			btn_cancel.setVisibility(View.VISIBLE);
			tv_time.setText(alarm.getTime());
			tv_date.setText(alarm.getDate());
			tv_contents.setText(alarm.getContent());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String stringweek = alarm.getWeek();
			if (stringweek != null && stringweek.length() > 0) {
				if (stringweek.contains("1"))
					checktext1.setChecked(true);
				if (stringweek.contains("2"))
					checktext2.setChecked(true);
				if (stringweek.contains("3"))
					checktext3.setChecked(true);
				if (stringweek.contains("4"))
					checktext4.setChecked(true);
				if (stringweek.contains("5"))
					checktext5.setChecked(true);
				if (stringweek.contains("6"))
					checktext6.setChecked(true);
				if (stringweek.contains("7"))
					checktext7.setChecked(true);
			}
			try {
				Date parse = format.parse(alarm.getDate());
				initeWeek(parse);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (alarm.getVibration() == 1)
				chb_vibration.setChecked(true);
			if (!alarm.getRingUri().equals("")) {
				tv_ring.setText("提示铃声(静音)");
			} else {
				try {
					int indexOf = ringUri.lastIndexOf("/");
					String index = ringUri.substring(indexOf);
					index = index.substring(1);
					Integer.parseInt(index);
					tv_ring.setText("提示铃声(自定义)");
				} catch (Exception e) {
					// 默认
					tv_ring.setText("提示铃声(默认)");
					ringUri = "default";
				}
			}
		} else {
			btn_cancel.setVisibility(View.INVISIBLE);
			tv_contents.setText(selecteNote.getContent());
		}

		toast = new CustomToast(AlarmActivity.this);

		tv_ring.setOnClickListener(this);
		rl_ring.setOnClickListener(this);
		tv_date.setOnClickListener(this);
		tv_time.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_ring.setOnClickListener(this);
		checktext1.setOnClickListener(this);
		checktext2.setOnClickListener(this);
		checktext3.setOnClickListener(this);
		checktext4.setOnClickListener(this);
		checktext5.setOnClickListener(this);
		checktext6.setOnClickListener(this);
		checktext7.setOnClickListener(this);
	}

	private void findVeiws() {
		btn_back = (Button) findViewById(R.id.btn_back_alarm);
		btn_save = (Button) findViewById(R.id.btn_savetop_alarm);
		btn_cancel = (Button) findViewById(R.id.btn_cancelalarm);
		btn_ring = (Button) findViewById(R.id.btn_selectring);
		tv_date = (TextView) findViewById(R.id.tv_alarm_date);
		tv_time = (TextView) findViewById(R.id.tv_alarm_time);
		tv_week = (TextView) findViewById(R.id.tv_alarm_week);
		checktext1 = (CheckedTextView) findViewById(R.id.week1);
		checktext2 = (CheckedTextView) findViewById(R.id.week2);
		checktext3 = (CheckedTextView) findViewById(R.id.week3);
		checktext4 = (CheckedTextView) findViewById(R.id.week4);
		checktext5 = (CheckedTextView) findViewById(R.id.week5);
		checktext6 = (CheckedTextView) findViewById(R.id.week6);
		checktext7 = (CheckedTextView) findViewById(R.id.week7);
		tv_ring = (TextView) findViewById(R.id.tv_alarm_ring);
		rl_ring = (RelativeLayout) findViewById(R.id.ln_pickrings);
		tv_contents = (TextView) findViewById(R.id.tv_alarm_contents);
		chb_vibration = (CheckBox) findViewById(R.id.chb_vibration);
	}

	/**
	 * 初始化日期
	 */
	private void initeDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		tv_date.setText(format.format(now));
		format.applyPattern("HH:mm");
		tv_time.setText(format.format(now));
		initeWeek(now);
	}

	/**
	 * 初始化星期
	 *
	 * @param now
	 */
	private void initeWeek(Date now) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		tv_week.setText(weekstr[w]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ln_pickrings:
			case R.id.tv_alarm_ring:
			case R.id.btn_selectring:
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				// 类型为来电TYPE_RINGTONE TYPE_NOTIFICATION TYPE_ALARM
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置提醒铃声");
				startActivityForResult(intent, 3);
				tv_ring.setText("提示铃声(静音)");
				ringUri = "";
				break;
			case R.id.btn_savetop_alarm:
				String date = tv_date.getText().toString();
				String time = tv_time.getText().toString();

				int isrepeat = 0;
				String repeatweeks = "";
				if (checktext1.isChecked())
					repeatweeks += "1,";
				if (checktext2.isChecked())
					repeatweeks += "2,";
				if (checktext3.isChecked())
					repeatweeks += "3,";
				if (checktext4.isChecked())
					repeatweeks += "4,";
				if (checktext5.isChecked())
					repeatweeks += "5,";
				if (checktext6.isChecked())
					repeatweeks += "6,";
				if (checktext7.isChecked())
					repeatweeks += "7,";
				if (repeatweeks.length() > 1) {
					repeatweeks = repeatweeks
							.substring(0, repeatweeks.length() - 1);
					isrepeat = 1;
				}
				int vibration = chb_vibration.isChecked() ? 1 : 0;
				String content = tv_contents.getText().toString();
				Alarm alarm = new Alarm(selecteNote.getId(), date, time, isrepeat,
						repeatweeks, vibration, ringUri, content);
				// 保存到数据库
				alarmHelper.add(alarm);
				// 更新note
				noteHelper.Addalarm(selecteNote.getId());
				// 设置闹钟
				alarmmanage.set(alarm);
				alarmmanage.destroy();
				toast.ShowMsg("设置成功！", CustomToast.Img_Ok);
				finish();
				break;
			case R.id.btn_back_alarm:
				finish();
			case R.id.tv_alarm_date:
				showdatepick();
				break;
			case R.id.tv_alarm_time:
				showtimepick();
				break;
			case R.id.week1:
				checktext1.setChecked(!checktext1.isChecked());
				break;
			case R.id.week2:
				checktext2.setChecked(!checktext2.isChecked());
				break;
			case R.id.week3:
				checktext3.setChecked(!checktext3.isChecked());
				break;
			case R.id.week4:
				checktext4.setChecked(!checktext4.isChecked());
				break;
			case R.id.week5:
				checktext5.setChecked(!checktext5.isChecked());
				break;
			case R.id.week6:
				checktext6.setChecked(!checktext6.isChecked());
				break;
			case R.id.week7:
				checktext7.setChecked(!checktext7.isChecked());
				break;
			case R.id.btn_cancelalarm:
				noteHelper.deletealarm(selecteNote.getId());
				alarmHelper.delete(selecteNote.getId());
				toast.ShowMsg("已取消", CustomToast.Img_Ok);
				btn_cancel.setVisibility(View.GONE);
				break;
			default:
				break;
		}

	}

	/**
	 * 显示选择日期对话框
	 */
	private void showdatepick() {
		if (dialog_date == null) {
			View view = getLayoutInflater().inflate(R.layout.dialog_selectdate,
					null);
			datePicker = (DatePicker) view.findViewById(R.id.datepicker);
			dialog_date = new AlertDialog.Builder(AlarmActivity.this)
					.setView(view)
					.setTitle("请选择日期")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
													int arg1) {
									String year = datePicker.getYear() + "";
									String moth = datePicker.getMonth() + 1
											+ "";
									String day = datePicker.getDayOfMonth()
											+ "";
									Calendar calendar = Calendar.getInstance();
									calendar.set(Calendar.YEAR,
											Integer.parseInt(year));
									calendar.set(Calendar.MONTH,
											Integer.parseInt(moth) - 1);
									calendar.set(Calendar.DATE,
											Integer.parseInt(day));
									initeWeek(calendar.getTime());
									tv_date.setText(year
											+ "-"
											+ (moth.length() == 1 ? "0" + moth
											: moth)
											+ "-"
											+ (day.length() == 1 ? "0" + day
											: day));
								}
							}).show();
		} else {
			dialog_date.show();
		}
	}

	/**
	 * 显示选择时间对话框
	 */
	private void showtimepick() {
		if (dialog_time == null) {
			View view = getLayoutInflater().inflate(R.layout.dialog_selecttime,
					null);
			timePicker = (TimePicker) view.findViewById(R.id.tiempicker);
			dialog_time = new AlertDialog.Builder(AlarmActivity.this)
					.setView(view)
					.setTitle("请选择时间")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
													int arg1) {
									String hour = timePicker.getCurrentHour()
											+ "";
									String minite = timePicker
											.getCurrentMinute() + "";
									hour = hour.length() == 1 ? "0" + hour
											: hour;
									minite = minite.length() == 1 ? "0"
											+ minite : minite;
									tv_time.setText(hour + ":" + minite);
								}
							}).show();
		} else {
			dialog_time.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK || requestCode != 3)
			return;
		try {
			// 得到我们选择的铃声 //EXTRA_RINGTONE_PICKED_URI
			Uri uri = data
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				System.out.println(uri.toString() + "============");
				if (uri.toString().contains("ringtone")) {
					tv_ring.setText("提示铃声(默认)");
					ringUri = "default";
				} else {
					tv_ring.setText("提示铃声(自定义)");
					ringUri = uri.toString();
				}
			}

		} catch (Exception e) {
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {

		if (noteHelper != null) {
			noteHelper.Desdroy();
		}
		if (alarmHelper != null) {
			alarmHelper.Destroy();
		}
		if (dialog_date != null) {
			dialog_date.cancel();
		}
		if (dialog_time != null) {
			dialog_time.cancel();
		}
		super.onDestroy();
	}

}
