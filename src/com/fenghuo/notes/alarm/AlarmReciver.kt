package com.fenghuo.notes.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.Vibrator
import android.provider.MediaStore.Audio

import com.fenghuo.notes.db.DBAlarmHelper
import com.fenghuo.notes.db.DBNoteHelper
import com.fenghuo.notes.bean.Alarm
import com.fenghuo.notes.EditNoteActivity
import com.fenghuo.notes.R

class AlarmReciver : BroadcastReceiver() {
    private var vibrator: Vibrator? = null
    private var alarm: Alarm? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        // 开启闹钟
        if (action == Action_StartAlarm) {

            val alarmHelper = DBAlarmHelper(context)
            val noteHelper = DBNoteHelper(context)

            // 获取信息
            val intExtraid = intent.getIntExtra("alarmid", -1)
            alarm = alarmHelper.Getbyid(intExtraid)
            if (alarm == null)
                return

            // 如果数据库没有对应note 说明该记事已被删除
            if (noteHelper.GetSingle(alarm!!.id) == null) {
                noteHelper.deletealarm(alarm!!.id)
                alarmHelper.delete(alarm!!.id)
                alarmHelper.Destroy()
                noteHelper.Desdroy()
                return
            }

            // 振动
            if (alarm!!.vibration == 1) {
                vibrator = context
                        .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val pattern = longArrayOf(300, 500, 300, 500) // OFF/ON/OFF/ON...
                vibrator!!.vibrate(pattern, -1)
            }

            // 通知栏
            val notification = Notification()
            notification.icon = R.drawable.ic_launcher
            notification.tickerText = alarm!!.content// 在通知栏上的滚动信息
            notification.flags = Notification.FLAG_AUTO_CANCEL

            // 铃声
            if (alarm!!.ringUri == "") {
            }// 静音
            else if (alarm!!.ringUri == "default") {
                notification.defaults = Notification.DEFAULT_SOUND
            } else {
                try {
                    // 自定义
                    val indexOf = alarm!!.ringUri!!.lastIndexOf("/")
                    val index = alarm!!.ringUri!!.substring(indexOf)
                    notification.sound = Uri.withAppendedPath(
                            Audio.Media.INTERNAL_CONTENT_URI, index)
                } catch (e: Exception) {
                    // 默认
                    notification.defaults = Notification.DEFAULT_SOUND
                }

            }

            // 点击跳转动作
            val intent2 = Intent(context, EditNoteActivity::class.java)
            intent2.putExtra("noteid", alarm!!.id)
            intent2.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val pendingIntent = PendingIntent.getActivity(context, 3, intent2, PendingIntent.FLAG_UPDATE_CURRENT)

            notification.setLatestEventInfo(context, "随心记提醒", alarm!!.content, pendingIntent)
            val maneger = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            maneger.notify(0x103, notification)

            // 点亮屏幕
            val pm = context
                    .getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank")
            wl.acquire()

            // 如果没重复 从数据库中删除
            if (alarm!!.week!!.length == 0) {
                noteHelper.deletealarm(alarm!!.id)
                alarmHelper.delete(alarm!!.id)
            }
            alarmHelper.Destroy()
            noteHelper.Desdroy()
        }
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // 重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
            // 这里是在服务里启动因为开机广播发生的次数<服务开启的次数。在服务里设置，确保闹钟可以正常开启
            val service = Intent(AlarmService.Action)
            service.setClass(context, AlarmService::class.java)
            context.startService(service)
        }
    }

    companion object {

        val Action_StartAlarm = "com.fenghuo.alarmstart"
    }
}
