package com.fenghuo.notes.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.fenghuo.notes.Values
import com.fenghuo.notes.bean.Alarm
import java.util.*

class DBAlarmHelper(context: Context) {

    private val dBhelper: DBhelper
    private val database: SQLiteDatabase

    init {
        dBhelper = DBhelper(context)
        this.database = dBhelper.writableDatabase
    }

    fun GetCount(): Int {
        val sql = "select count(*) from " + Values.TableAlarm
        val rawQuery = database.rawQuery(sql, null)
        if (rawQuery.moveToFirst()) {
            return rawQuery.getInt(0)
        }
        return 0
    }

    /**获取所有
     * @return
     */
    fun Getall(): List<Alarm> {
        val list = ArrayList<Alarm>()
        val sql = "select * from " + Values.TableAlarm
        val rawQuery = database.rawQuery(sql, null)
        while (rawQuery.moveToNext()) {
            val alarm = Alarm()
            alarm.id = rawQuery.getInt(0)
            alarm.date = rawQuery.getString(1)
            alarm.time = rawQuery.getString(2)
            alarm.repeat = rawQuery.getInt(3)
            alarm.week = rawQuery.getString(4)
            alarm.vibration = rawQuery.getInt(5)
            alarm.ringUri = rawQuery.getString(6)
            alarm.content = rawQuery.getString(7)
            list.add(alarm)
        }
        rawQuery.close()
        return list
    }

    /**根据 id 获取
     * @param thingsId
     * *
     * @return
     */
    fun Getbyid(thingsId: Int): Alarm? {

        val sql = "select * from " + Values.TableAlarm + " where id=?"
        val rawQuery = database.rawQuery(sql, arrayOf(thingsId.toString() + ""))
        while (rawQuery.moveToNext()) {
            val alarm = Alarm()
            alarm.id = rawQuery.getInt(0)
            alarm.date = rawQuery.getString(1)
            alarm.time = rawQuery.getString(2)
            alarm.repeat = rawQuery.getInt(3)
            alarm.week = rawQuery.getString(4)
            alarm.vibration = rawQuery.getInt(5)
            alarm.ringUri = rawQuery.getString(6)
            alarm.content = rawQuery.getString(7)
            return alarm
        }
        rawQuery.close()
        return null
    }

    /**添加闹钟
     * @param alarm
     */
    fun add(alarm: Alarm) {
        val sql = "insert into " + Values.TableAlarm + " values(?,?,?,?,?,?,?,?)"
        database.execSQL(
                sql,
                arrayOf(alarm.id, alarm.date, alarm.time, alarm.repeat, alarm.week, alarm.vibration, alarm.ringUri, alarm.content))
    }

    /**删除闹钟
     * @param thingsid
     */
    fun delete(thingsid: Int) {
        val sql = "delete from " + Values.TableAlarm + " where id=?"
        database.execSQL(sql, arrayOf<Any>(thingsid))
    }

    fun deleteall() {
        val sqlstr = "delete from " + Values.TableAlarm
        database.execSQL(sqlstr)
    }

    fun Destroy() {
        this.dBhelper.close()
        this.database.close()
    }
}
