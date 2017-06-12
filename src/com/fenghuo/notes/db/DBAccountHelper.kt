package com.fenghuo.notes.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.fenghuo.notes.Values
import com.fenghuo.notes.bean.Account
import com.fenghuo.notes.bean.MonthAccount
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class DBAccountHelper(context: Context) {

    private val dBhelper: DBhelper?
    private val database: SQLiteDatabase?
    private var dateFormat: SimpleDateFormat? = null

    init {
        dBhelper = DBhelper(context)
        database = dBhelper.writableDatabase
    }

    fun insert(account: Account) {
        val sql = "insert into " + Values.TableAccount + " values(null,?,?,?,?)"
        database!!.execSQL(sql,
                arrayOf(account.kind.toString() + "", account.kinds, account.money.toString() + "", account.date))
    }

    fun Update(account: Account) {
        val sql = "update " + Values.TableAccount + " set kind=?,kinds=?,money=?,date=? where id=?"
        database!!.rawQuery(
                sql,
                arrayOf(account.kind.toString() + "", account.kinds, account.money.toString() + "", account.date, account.id.toString() + ""))
    }

    /**
     * 得到某天的消费明细

     * @param date
     * *
     * @return
     */
    fun GetToadyList(day: String): MutableList<Account> {
        val list = ArrayList<Account>()
        val sql = "select * from " + Values.TableAccount + " where date like '" + day + "%'"
        val rawQuery = database!!.rawQuery(sql, null)
        while (rawQuery.moveToNext()) {
            val account = Account(rawQuery.getInt(0),
                    rawQuery.getInt(1), rawQuery.getString(2),
                    rawQuery.getFloat(3), rawQuery.getString(4))
            list.add(account)
        }
        rawQuery.close()
        return list
    }

    /**
     * 得到某天的消费统计

     * @param day
     * *
     * @return
     */
    private fun GetDaySum(day: String): MonthAccount {
        val sql = "select sum(money) from " + Values.TableAccount + " where date like '" + day + "%' and kind=1"
        // 计算支出总计
        val rawQuery = database!!.rawQuery(sql, null)
        var sumout = "0.0"
        if (rawQuery.moveToNext()) {
            sumout = rawQuery.getFloat(0).toString() + ""
        }
        // 计算收入总计
        val sql2 = "select sum(money) from " + Values.TableAccount + " where date like '" + day + "%' and kind=2"
        val rawQuery2 = database.rawQuery(sql2, null)
        var sumin = "0.0"
        if (rawQuery2.moveToNext()) {
            sumin = rawQuery2.getFloat(0).toString() + ""
        }
        // 得到消费种类
        val sql3 = "select kinds from " + Values.TableAccount + " where date like '" + day + "%'"
        val rawQuery3 = database.rawQuery(sql3, null)
        var things = ""
        while (rawQuery3.moveToNext()) {
            things += rawQuery3.getString(0) + " "
        }
        return MonthAccount(-1, things, day, sumin, sumout)
    }

    /**
     * 等到月统计

     * @param date
     * *
     * @return
     */
    fun GetMonthList(date: Date): MutableList<MonthAccount> {
        val start = Date(date.year, date.month, 1)
        val end = Date(date.year, date.month, date.date + 1)
        val list = ArrayList<MonthAccount>()
        val sql = "select distinct substr(date,0,11) from " + Values.TableAccount + " where date between '" + start.toString() + "' and '" + end.toString() + "' group by date"

        val rawQuery = database!!.rawQuery(sql, null)
        val listdate = ArrayList<String>()
        while (rawQuery.moveToNext()) {
            listdate.add(rawQuery.getString(0).trim { it <= ' ' })
        }
        rawQuery.close()
        for (i in listdate.indices) {
            list.add(GetDaySum(listdate[i]))
        }
        return list
    }

    /**
     * 得到所有的统计

     * @return
     */
    fun GetListAll(): MutableList<MonthAccount> {
        val list = ArrayList<MonthAccount>()
        val sql = "select distinct substr(date,0,11) from " + Values.TableAccount + " group by date"
        val rawQuery = database!!.rawQuery(sql, null)
        val listdate = ArrayList<String>()
        while (rawQuery.moveToNext()) {
            listdate.add(rawQuery.getString(0).trim { it <= ' ' })
        }
        rawQuery.close()
        for (i in listdate.indices) {
            list.add(GetDaySum(listdate[i]))
        }
        return list
    }

    /**
     * 根据id删除

     * @param id
     */
    fun DeleteByID(id: Int) {
        val sql = "delete from " + Values.TableAccount + " where id=?"
        database!!.execSQL(sql, arrayOf<Any>(id))
    }

    /**
     * 删除某天的所有记录

     * @param date
     */
    fun DeleteDay(date: String) {
        val sql = "delete from " + Values.TableAccount + " where date like '" + date + "%'"
        database!!.execSQL(sql)
    }

    /**
     * 删除所有的记录
     */
    fun Deleteall() {
        val sqlstr = "delete from " + Values.TableAccount
        database!!.execSQL(sqlstr)
    }

    fun GetDateTime(): String {
        this.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return this.dateFormat!!.format(java.util.Date())// 将当前日期进行格式化操作
    }

    fun GetDate(): String { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
        this.dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return this.dateFormat!!.format(java.util.Date())// 将当前日期进行格式化操作
    }

    fun Destroy() {
        database?.close()
        dBhelper?.close()
    }
}
