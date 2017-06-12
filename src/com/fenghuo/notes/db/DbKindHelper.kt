package com.fenghuo.notes.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.fenghuo.notes.Values
import com.fenghuo.notes.bean.Kind
import java.text.SimpleDateFormat
import java.util.*

class DbKindHelper(context: Context) {

    private val dBhelper: DBhelper
    private val database: SQLiteDatabase
    private val dateFormat: SimpleDateFormat

    init {
        dBhelper = DBhelper(context)
        database = dBhelper.writableDatabase
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    }

    fun Getlist(): MutableList<Kind> {
        val list = ArrayList<Kind>()
        val sql = "select * from " + Values.TableKind
        val rawQuery = database.rawQuery(sql, null)
        while (rawQuery.moveToNext()) {
            list.add(Kind(rawQuery.getInt(0), rawQuery.getString(1)))
        }
        rawQuery.close()
        return list
    }

    fun Add(Name: String) {
        val sql = "insert into " + Values.TableKind + " values(null,?)"
        database.execSQL(sql, arrayOf(Name))
    }

    fun Delete(id: Int) {
        val sql = "delete from " + Values.TableKind + " where id=?"
        database.execSQL(sql, arrayOf(id.toString() + ""))
    }

    fun GetDateTime(): String { // 得到的是一个日期：格式为：yyyy-MM-dd HH:mm:ss.SSS
        return this.dateFormat.format(Date())// 将当前日期进行格式化操作
    }


    fun destroy() {
        dBhelper.close()
        database.close()
    }
}
