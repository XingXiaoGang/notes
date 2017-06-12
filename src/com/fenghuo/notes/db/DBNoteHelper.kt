package com.fenghuo.notes.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import com.fenghuo.notes.Values
import com.fenghuo.notes.bean.Note

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Random

class DBNoteHelper(private val context: Context) {

    private val dbBhelper: DBhelper?
    private var database: SQLiteDatabase? = null
    private var format: SimpleDateFormat? = null
    private val random: Random

    init {
        dbBhelper = DBhelper(context)
        database = dbBhelper.writableDatabase
        random = Random()
    }

    fun Getlist(): MutableList<Note> {
        val list = ArrayList<Note>()
        val sqlstr = "select * from " + Values.TableNote
        val cursor = database!!.rawQuery(sqlstr, null)
        while (cursor.moveToNext()) {
            val note = Note()
            note.id = cursor.getInt(0)
            note.img = cursor.getInt(1)
            note.content = cursor.getString(2)
            note.date = cursor.getString(3)
            note.alarm = cursor.getInt(4)
            list.add(note)
        }
        cursor.close()
        return list
    }

    fun GetSingle(id: Int): Note? {
        val sqlstr = "select * from " + Values.TableNote + " where id=?"
        val cursor = database!!.rawQuery(sqlstr, arrayOf(id.toString() + ""))
        while (cursor.moveToNext()) {
            val note = Note()
            note.id = cursor.getInt(0)
            note.img = cursor.getInt(1)
            note.content = cursor.getString(2)
            note.date = cursor.getString(3)
            note.alarm = cursor.getInt(4)
            return note
        }
        return null
    }

    fun Add(note: Note): Boolean {
        val sqlstr = "insert into " + Values.TableNote + " values(null,?,?,?,?)"
        database!!.execSQL(sqlstr,
                arrayOf(note.img, note.content, GetDate(), 0))
        return true
    }

    fun Deleteall() {
        val sqlString = "delete from " + Values.TableNote
        database!!.execSQL(sqlString)
        val alarmHelper = DBAlarmHelper(context)
        alarmHelper.deleteall()
        alarmHelper.Destroy()
    }

    fun Delete(id: Int): Boolean {
        val rows = database!!.delete(Values.TableNote, "id=?", arrayOf(id.toString() + ""))
        //同时从闹钟列表中删除
        val alarmHelper = DBAlarmHelper(context)
        alarmHelper.delete(id)
        alarmHelper.Destroy()
        if (rows > 0)
            return true
        else
            return false
    }

    fun Update(note: Note): Boolean {
        val values = ContentValues()
        values.put("img", note.img)
        values.put("content", note.content)
        values.put("date", note.date)
        values.put("alarm", note.alarm)
        val rows = database!!.update(Values.TableNote, values, "id=?",
                arrayOf(note.id.toString() + ""))
        if (rows > 0)
            return true
        else
            return false
    }

    /**
     * 添加闹钟标记

     * @param id
     */
    fun Addalarm(id: Int) {
        val sql = "update " + Values.TableNote + " set alarm=1 where id=?"
        database!!.execSQL(sql, arrayOf<Any>(id))
    }

    /**
     * 删除闹钟标记

     * @param id
     */
    fun deletealarm(id: Int) {
        val sql = "update " + Values.TableNote + " set alarm=0 where id=?"
        database!!.execSQL(sql, arrayOf<Any>(id))
    }

    fun GetDate(): String { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
        this.format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return this.format!!.format(Date())// 将当前日期进行格式化操作
    }

    /**
     * 生成一个 时间+四位随机数的 文件名

     * @return
     */
    fun GetFilename(): String {
        var str = ""
        while (str.length < 4) {
            str += random.nextInt(10).toString() + ""
        }
        var date = GetDate()
        date = date.replace(":".toRegex(), "")
        date = date.replace(" ".toRegex(), "")
        return date + str + ".txt"
    }

    fun Desdroy() {
        if (database != null) {
            database!!.close()
            database = null
        }
        dbBhelper?.close()
    }
}
