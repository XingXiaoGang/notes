package com.fenghuo.notes.alarm

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class AlarmService : Service() {

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        println("AlarmService 开启_------")
        super.onCreate()
    }

    override fun onStart(intent: Intent, startId: Int) {
        // 再次动态注册接收
        val filter = IntentFilter("com.fenghuo.alarmstart")
        filter.priority = Integer.MAX_VALUE - 4
        val reciver = AlarmReciver()
        registerReceiver(reciver, filter)
        //再次注册闹钟
        val alarm = AlarmHelper(applicationContext)
        alarm.reset()
        alarm.destroy()
        super.onStart(intent, startId)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var flags = flags
        // return START_STICKY_COMPATIBILITY;//服务被杀死后 系统会尝试重新启动
        flags = Service.START_NOT_STICKY// 服务被杀死后 系统会尝试重新启动
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        // kill以后重启
        if (!stop) {
            println("AlarmService 停止_------")
            val service = Intent(Action)
            service.setClass(this, AlarmService::class.java)
            this.startService(service)
        } else
            super.onDestroy()
    }

    companion object {

        val Action = "com.fenghuo.alarmservice"
        var stop = false
    }

}
