package com.fenghuo.notes.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.fenghuo.notes.bean.Alarm
import com.fenghuo.notes.db.DBAlarmHelper
import com.fenghuo.notes.db.DBNoteHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyAlarm(private val context: Context) {

    private val manager: AlarmManager
    private val alarmHelper: DBAlarmHelper
    private val noteHelper: DBNoteHelper
    private var datenow: Date? = null

    init {
        manager = context
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmHelper = DBAlarmHelper(context)
        noteHelper = DBNoteHelper(context)
    }

    /**
     * 设置闹钟

     * @param alarm
     */
    fun set(alarm: Alarm) {

        // 当前的时间
        datenow = Date()
        // 重复的星期
        var weekstr: Array<String>? = null
        var weekint: IntArray? = null
        var week: String? = alarm.week
        if (week != null && week.isNotEmpty()) {
            weekstr = alarm.week!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            weekint = IntArray(weekstr.size)
            for (i in weekstr.indices) {
                weekint[i] = Integer.parseInt(weekstr[i])
            }
        }

        // 闹钟时间
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var date: Date? = null
        try {
            date = dateFormat.parse(alarm.date + " " + alarm.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        //判断是否过时(当天)
        val datetimenow = Date(2000, 1, 1, datenow!!.hours, datenow!!.minutes)
        val datetimering = Date(2000, 1, 1, date!!.hours, date.minutes)
        println(datetimenow.toLocaleString() + "\n" + datetimering.toLocaleString())
        if (datetimenow.after(datetimering)) {
            return
        }

        val intent = Intent()
        intent.putExtra("alarmid", alarm.id)
        intent.action = AlarmReciver.Action_StartAlarm
        val pendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        // 有重复
        if (weekint != null && weekint.size > 0) {
            val today = datenow!!.day + 1
            var have = false
            for (i in weekint.indices) {
                if (weekint[i] == today)
                    have = true
            }
            if (have) {
                val calendar = Calendar.getInstance()
                calendar.time = datenow
                calendar.set(Calendar.HOUR_OF_DAY, date.hours)
                calendar.set(Calendar.MINUTE, date.minutes)
                manager.cancel(pendingIntent)
                manager.set(AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis, pendingIntent)
            }
        }
        // 无重复

        val pendingIntent2 = PendingIntent.getBroadcast(context, alarm.id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.time = date
        manager.cancel(pendingIntent2)
        manager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                pendingIntent2)
    }

    /**
     * 删除一个闹钟

     * @param id
     */
    fun delete(id: Int) {
        // 从系统闹钟队列中删除
        val intent = Intent()
        intent.action = AlarmReciver.Action_StartAlarm
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0x102 + id)
        val pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent, 0x503 + id)
        manager.cancel(pendingIntent)
        manager.cancel(pendingIntent2)
    }

    /**
     * 开机以后重新设置
     */
    fun reset() {
        val alarms = alarmHelper.Getall()
        for (alarm in alarms) {
            set(alarm)
        }
    }

    /**
     * 使用后必须调用 用来释放数据库连接
     */
    fun destroy() {
        alarmHelper.Destroy()
        noteHelper.Desdroy()
    }

}
