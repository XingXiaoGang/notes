package com.fenghuo.notes.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghuo.notes.bean.Kind;
import com.fenghuo.notes.Values;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbKindHelper {

	private DBhelper dBhelper;
	private SQLiteDatabase database;
	private SimpleDateFormat dateFormat;

	public DbKindHelper(Context context){
		dBhelper=new  DBhelper(context);
		database=dBhelper.getWritableDatabase();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	public List<Kind> Getlist(){
		List<Kind> list=new ArrayList<Kind>();
		String sql="select * from "+Values.TableKind;
		Cursor rawQuery = database.rawQuery(sql, null);
		while(rawQuery.moveToNext()){
			list.add(new Kind(rawQuery.getInt(0), rawQuery.getString(1)));
		}
		rawQuery.close();
		return list;
	}

	public void Add(String Name){
		String sql="insert into "+Values.TableKind +" values(null,?)";
		database.execSQL(sql, new String[]{Name});
	}

	public void Delete(int id){
		String sql="delete from "+Values.TableKind +" where id=?";
		database.execSQL(sql, new String[]{id+""});
	}

	public String GetDateTime() { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
		return this.dateFormat.format(new Date());// 将当前日期进行格式化操作
	}


	public void destroy(){
		dBhelper.close();
		database.close();
	}
}
