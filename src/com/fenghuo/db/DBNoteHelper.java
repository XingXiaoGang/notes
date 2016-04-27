package com.fenghuo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghuo.bean.Note;
import com.fenghuo.notes.Values;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DBNoteHelper {

	private DBhelper dbBhelper;
	private SQLiteDatabase database;
	private SimpleDateFormat format;
	private Random random;
	private Context context;

	public DBNoteHelper(Context context) {
		dbBhelper = new DBhelper(context);
		this.context=context;
		database = dbBhelper.getWritableDatabase();
		random=new Random();
	}

	public List<Note> Getlist() {
		List<Note> list = new ArrayList<Note>();
		String sqlstr = "select * from " + Values.TableNote;
		Cursor cursor = database.rawQuery(sqlstr, null);
		while (cursor.moveToNext()) {
			Note note = new Note();
			note.setId(cursor.getInt(0));
			note.setImg(cursor.getInt(1));
			note.setContent(cursor.getString(2));
			note.setDate(cursor.getString(3));
			note.setAlarm(cursor.getInt(4));
			list.add(note);
		}
		cursor.close();
		return list;
	}

	public Note GetSingle(int id){
		String sqlstr = "select * from " + Values.TableNote+" where id=?";
		Cursor cursor = database.rawQuery(sqlstr, new String[]{id+""});
		while (cursor.moveToNext()) {
			Note note = new Note();
			note.setId(cursor.getInt(0));
			note.setImg(cursor.getInt(1));
			note.setContent(cursor.getString(2));
			note.setDate(cursor.getString(3));
			note.setAlarm(cursor.getInt(4));
			return note;
		}
		return null;
	}

	public boolean Add(Note note) {
		String sqlstr = "insert into " + Values.TableNote + " values(null,?,?,?,?)";
		database.execSQL(sqlstr,
				new Object[] {note.getImg(), note.getContent(), GetDate() ,0});
		return true;
	}

	public void Deleteall(){
		String sqlString="delete from "+Values.TableNote;
		database.execSQL(sqlString);
		DBAlarmHelper alarmHelper=new DBAlarmHelper(context);
		alarmHelper.deleteall();
		alarmHelper.Destroy();
	}

	public boolean Delete(int id) {
		int rows = database.delete(Values.TableNote, "id=?", new String[] { id + "" });
		//同时从闹钟列表中删除 
		DBAlarmHelper alarmHelper=new DBAlarmHelper(context);
		alarmHelper.delete(id);
		alarmHelper.Destroy();
		if (rows > 0)
			return true;
		else
			return false;
	}

	public boolean Update(Note note) {
		ContentValues values = new ContentValues();
		values.put("img", note.getImg());
		values.put("content", note.getContent());
		values.put("date", note.getDate());
		values.put("alarm", note.getAlarm());
		int rows = database.update(Values.TableNote, values, "id=?",
				new String[] { note.getId() + "" });
		if (rows > 0)
			return true;
		else
			return false;
	}

	/**添加闹钟标记
	 * @param id
	 */
	public void Addalarm(int id){
		String sql="update "+Values.TableNote+" set alarm=1 where id=?";
		database.execSQL(sql,new Object[]{id});
	}

	/**删除闹钟标记 
	 * @param id
	 */
	public void deletealarm(int id){
		String sql="update "+Values.TableNote+" set alarm=0 where id=?";
		database.execSQL(sql,new Object[]{id});
	}

	public String GetDate() { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
		this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return this.format.format(new Date());// 将当前日期进行格式化操作
	}

	/**生成一个 时间+四位随机数的 文件名 
	 * @return
	 */
	public String GetFilename(){
		String str="";
		while (str.length()<4) {
			str+=random.nextInt(10)+"";
		}
		String date=GetDate();
		date=date.replaceAll(":", "");
		date=date.replaceAll(" ", "");
		return date+str+".txt";
	}

	public void Desdroy() {
		if (database != null) {
			database.close();
			database=null;
		}
		if (dbBhelper != null) {
			dbBhelper.close();
		}
	}
}
