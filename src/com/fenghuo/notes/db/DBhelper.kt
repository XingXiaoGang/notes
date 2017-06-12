package com.fenghuo.notes.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

import com.fenghuo.notes.Values

class DBhelper @JvmOverloads constructor(context: Context, name: String = Values.DBName, factory: CursorFactory? = null,
                                         version: Int = 1) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {

        //id,img,content,date,haveAlarm	 note表
        //id,name  					 kind表
        //id,kind,kinds,money,date	 account表
        //id,date,time,repeat,week,vibration,ringuri,content alarms表
        if (!Values.isRestore_database) {
            db.execSQL("CREATE TABLE if not exists " + Values.TableNote + "(id INTEGER PRIMARY KEY AUTOINCREMENT,img int(1),content TEXT, date  DATE default (datetime('now', 'localtime')),alarm int(2) default 0)")
            db.execSQL("create table if not exists " + Values.TableKind + "(id integer primary key autoincrement,name varchar(10))")
            db.execSQL("create table if not exists " + Values.TableAccount + "(id integer primary key autoincrement,kind int(2),kinds varchar(10),money float,date datetime)")
            db.execSQL("create table if not exists " + Values.TableAlarm + "(id integer,date varchar(20),time varchar(20),repeat int(2),week varchar(10),vibration int(2),ringuri varchar(50),content text)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, arg1: Int, arg2: Int) {
        if (!Values.isRestore_database) {
            db.execSQL("drop table if exists " + Values.TableNote)
            db.execSQL("drop table if exists " + Values.TableKind)
            db.execSQL("drop table if exists " + Values.TableAccount)
            db.execSQL("drop table if exists " + Values.TableAlarm)
        }
    }


}
