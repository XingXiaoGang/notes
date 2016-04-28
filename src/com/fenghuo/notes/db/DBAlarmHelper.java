package com.fenghuo.notes.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghuo.notes.bean.Alarm;
import com.fenghuo.notes.Values;

import java.util.ArrayList;
import java.util.List;

public class DBAlarmHelper {

	private DBhelper dBhelper;
	private SQLiteDatabase database;

	public DBAlarmHelper(Context context) {
		dBhelper = new DBhelper(context);
		this.database = dBhelper.getWritableDatabase();
	}

	public int GetCount(){
		String sql="select count(*) from "+Values.TableAlarm;
		Cursor rawQuery = database.rawQuery(sql, null);
		if(rawQuery.moveToFirst()){
			return rawQuery.getInt(0);
		}
		return 0;
	}

	/**获取所有
	 * @return
	 */
	public List<Alarm> Getall() {
		List<Alarm> list = new ArrayList<Alarm>();
		String sql = "select * from " + Values.TableAlarm;
		Cursor rawQuery = database.rawQuery(sql, null);
		while (rawQuery.moveToNext()) {
			Alarm alarm = new Alarm();
			alarm.setId(rawQuery.getInt(0));
			alarm.setDate(rawQuery.getString(1));
			alarm.setTime(rawQuery.getString(2));
			alarm.setRepeat(rawQuery.getInt(3));
			alarm.setWeek(rawQuery.getString(4));
			alarm.setVibration(rawQuery.getInt(5));
			alarm.setRingUri(rawQuery.getString(6));
			alarm.setContent(rawQuery.getString(7));
			list.add(alarm);
		}
		rawQuery.close();
		return list;
	}

	/**根据 id 获取
	 * @param thingsId
	 * @return
	 */
	public Alarm Getbyid(int thingsId) {

		String sql = "select * from " + Values.TableAlarm+" where id=?";
		Cursor rawQuery = database.rawQuery(sql, new String[]{thingsId+""});
		while (rawQuery.moveToNext()) {
			Alarm alarm = new Alarm();
			alarm.setId(rawQuery.getInt(0));
			alarm.setDate(rawQuery.getString(1));
			alarm.setTime(rawQuery.getString(2));
			alarm.setRepeat(rawQuery.getInt(3));
			alarm.setWeek(rawQuery.getString(4));
			alarm.setVibration(rawQuery.getInt(5));
			alarm.setRingUri(rawQuery.getString(6));
			alarm.setContent(rawQuery.getString(7));
			return alarm;
		}
		rawQuery.close();
		return null;
	}

	/**添加闹钟
	 * @param alarm
	 */
	public void add(Alarm alarm) {
		String sql = "insert into " + Values.TableAlarm
				+ " values(?,?,?,?,?,?,?,?)";
		database.execSQL(
				sql,
				new Object[] { alarm.getId(), alarm.getDate(), alarm.getTime(),
						alarm.getRepeat(), alarm.getWeek(),
						alarm.getVibration(),alarm.getRingUri(), alarm.getContent() });
	}

	/**删除闹钟
	 * @param thingsid
	 */
	public void delete(int thingsid) {
		String sql="delete from "+Values.TableAlarm+" where id=?";
		database.execSQL(sql, new Object[]{thingsid});
	}

	public void deleteall(){
		String sqlstr="delete from "+Values.TableAlarm;
		database.execSQL(sqlstr);
	}

	public void Destroy() {
		this.dBhelper.close();
		this.database.close();
	}
}
