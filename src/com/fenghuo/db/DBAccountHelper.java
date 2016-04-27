package com.fenghuo.db;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghuo.bean.Account;
import com.fenghuo.bean.MonthAccount;
import com.fenghuo.notes.Values;

public class DBAccountHelper {

	private DBhelper dBhelper;
	private SQLiteDatabase database;
	private SimpleDateFormat dateFormat;

	public DBAccountHelper(Context context) {
		dBhelper = new DBhelper(context);
		database = dBhelper.getWritableDatabase();
	}

	public void insert(Account account) {
		String sql = "insert into " + Values.TableAccount
				+ " values(null,?,?,?,?)";
		database.execSQL(sql,
				new String[] { account.getKind() + "", account.getKinds(),
						account.getMoney() + "", account.getDate() });
	}

	public void Update(Account account) {
		String sql = "update " + Values.TableAccount
				+ " set kind=?,kinds=?,money=?,date=? where id=?";
		database.rawQuery(
				sql,
				new String[] { account.getKind() + "", account.getKinds(),
						account.getMoney() + "", account.getDate(),
						account.getId() + "" });
	}

	/**
	 * 得到某天的消费明细
	 *
	 * @param date
	 * @return
	 */
	public List<Account> GetToadyList(String day) {
		List<Account> list = new ArrayList<Account>();
		String sql = "select * from " + Values.TableAccount
				+ " where date like '" + day + "%'";
		Cursor rawQuery = database.rawQuery(sql, null);
		while (rawQuery.moveToNext()) {
			Account account = new Account(rawQuery.getInt(0),
					rawQuery.getInt(1), rawQuery.getString(2),
					rawQuery.getFloat(3), rawQuery.getString(4));
			list.add(account);
		}
		rawQuery.close();
		return list;
	}

	/**
	 * 得到某天的消费统计
	 *
	 * @param day
	 * @return
	 */
	private MonthAccount GetDaySum(String day) {
		String sql = "select sum(money) from " + Values.TableAccount
				+ " where date like '" + day + "%' and kind=1";
		// 计算支出总计
		Cursor rawQuery = database.rawQuery(sql, null);
		String sumout = "0.0";
		if (rawQuery.moveToNext()) {
			sumout = rawQuery.getFloat(0) + "";
		}
		// 计算收入总计
		String sql2 = "select sum(money) from " + Values.TableAccount
				+ " where date like '" + day + "%' and kind=2";
		Cursor rawQuery2 = database.rawQuery(sql2, null);
		String sumin = "0.0";
		if (rawQuery2.moveToNext()) {
			sumin = rawQuery2.getFloat(0) + "";
		}
		// 得到消费种类
		String sql3 = "select kinds from " + Values.TableAccount
				+ " where date like '" + day + "%'";
		Cursor rawQuery3 = database.rawQuery(sql3, null);
		String things = "";
		while (rawQuery3.moveToNext()) {
			things += (rawQuery3.getString(0) + " ");
		}
		return new MonthAccount(-1, things, day, sumin, sumout);
	}

	/**
	 * 等到月统计
	 *
	 * @param date
	 * @return
	 */
	public List<MonthAccount> GetMonthList(Date date) {
		Date start = new Date(date.getYear(), date.getMonth(), 1);
		Date end = new Date(date.getYear(), date.getMonth(), date.getDate() + 1);
		List<MonthAccount> list = new ArrayList<MonthAccount>();
		String sql = "select distinct substr(date,0,11) from "
				+ Values.TableAccount + " where date between '"
				+ start.toString() + "' and '" + end.toString()
				+ "' group by date";

		Cursor rawQuery = database.rawQuery(sql, null);
		List<String> listdate = new ArrayList<String>();
		while (rawQuery.moveToNext()) {
			listdate.add(rawQuery.getString(0).trim());
		}
		rawQuery.close();
		for (int i = 0; i < listdate.size(); i++) {
			list.add(GetDaySum(listdate.get(i)));
		}
		return list;
	}

	/**
	 * 得到所有的统计
	 *
	 * @return
	 */
	public List<MonthAccount> GetListAll() {
		List<MonthAccount> list = new ArrayList<MonthAccount>();
		String sql = "select distinct substr(date,0,11) from "
				+ Values.TableAccount + " group by date";
		Cursor rawQuery = database.rawQuery(sql, null);
		List<String> listdate = new ArrayList<String>();
		while (rawQuery.moveToNext()) {
			listdate.add(rawQuery.getString(0).trim());
		}
		rawQuery.close();
		for (int i = 0; i < listdate.size(); i++) {
			list.add(GetDaySum(listdate.get(i)));
		}
		return list;
	}

	/**
	 * 根据id删除
	 *
	 * @param id
	 */
	public void DeleteByID(int id) {
		String sql = "delete from " + Values.TableAccount + " where id=?";
		database.execSQL(sql, new Object[] { id });
	}

	/**
	 * 删除某天的所有记录
	 *
	 * @param date
	 */
	public void DeleteDay(String date) {
		String sql = "delete from " + Values.TableAccount
				+ " where date like '" + date + "%'";
		database.execSQL(sql);
	}

	/**
	 * 删除所有的记录
	 */
	public void Deleteall(){
		String sqlstr="delete from "+Values.TableAccount;
		database.execSQL(sqlstr);
	}

	public String GetDateTime() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return this.dateFormat.format(new java.util.Date());// 将当前日期进行格式化操作
	}

	public String GetDate() { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return this.dateFormat.format(new java.util.Date());// 将当前日期进行格式化操作
	}

	public void Destroy() {
		if (database != null) {
			database.close();
		}
		if (dBhelper != null) {
			dBhelper.close();
		}
	}
}
