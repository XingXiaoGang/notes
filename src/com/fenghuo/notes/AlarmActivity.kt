package com.fenghuo.notes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.fenghuo.notes.alarm.MyAlarm
import com.fenghuo.notes.bean.Alarm
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.db.DBAlarmHelper
import com.fenghuo.notes.db.DBNoteHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : Activity(), OnClickListener {

    private var alarmmanage: MyAlarm? = null// 闹钟管理类
    private var alarmHelper: DBAlarmHelper? = null// 闹钟数据库类
    private var noteHelper: DBNoteHelper? = null// note 数据库类
    private var toast: CustomToast? = null
    private var btn_save: Button? = null
    private var btn_back: Button? = null
    private var btn_cancel: Button? = null
    private var btn_ring: Button? = null
    private var tv_date: TextView? = null
    private var tv_time: TextView? = null
    private var tv_week: TextView? = null
    private var tv_ring: TextView? = null
    private var rl_ring: RelativeLayout? = null
    private var checktext1: CheckedTextView? = null
    private var checktext2: CheckedTextView? = null
    private var checktext3: CheckedTextView? = null
    private var checktext4: CheckedTextView? = null
    private var checktext5: CheckedTextView? = null
    private var checktext6: CheckedTextView? = null
    private var checktext7: CheckedTextView? = null
    private var chb_vibration: CheckBox? = null
    private var tv_contents: TextView? = null
    private var ringUri = "default"
    private var selecteNote: Note? = null
    private var dialog_date: AlertDialog? = null
    private var dialog_time: AlertDialog? = null
    private var datePicker: DatePicker? = null
    private var timePicker: TimePicker? = null
    internal var weekstr = arrayOf(getString(R.string.sunday), getString(R.string.monday), getString(R.string.tusday), getString(R.string.thirsday), getString(R.string.wensday), getString(R.string.friday), getString(R.string.saturday))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        findVeiws()

        alarmmanage = MyAlarm(this@AlarmActivity)
        noteHelper = DBNoteHelper(this@AlarmActivity)
        alarmHelper = DBAlarmHelper(this@AlarmActivity)
        // 接收
        val noteid = intent.getIntExtra("noteid", -1)
        selecteNote = noteHelper!!.GetSingle(noteid)
        initeDate()
        // 初始化
        val alarm = alarmHelper!!.Getbyid(selecteNote!!.id)
        if (selecteNote!!.alarm == 1) {
            btn_cancel!!.visibility = View.VISIBLE
            tv_time!!.text = alarm!!.time
            tv_date!!.text = alarm.date
            tv_contents!!.text = alarm.content
            val format = SimpleDateFormat("yyyy-MM-dd")
            val stringweek = alarm.week
            if (stringweek != null && stringweek.length > 0) {
                if (stringweek.contains("1"))
                    checktext1!!.isChecked = true
                if (stringweek.contains("2"))
                    checktext2!!.isChecked = true
                if (stringweek.contains("3"))
                    checktext3!!.isChecked = true
                if (stringweek.contains("4"))
                    checktext4!!.isChecked = true
                if (stringweek.contains("5"))
                    checktext5!!.isChecked = true
                if (stringweek.contains("6"))
                    checktext6!!.isChecked = true
                if (stringweek.contains("7"))
                    checktext7!!.isChecked = true
            }
            try {
                val parse = format.parse(alarm.date)
                initeWeek(parse)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (alarm.vibration == 1)
                chb_vibration!!.isChecked = true
            if (alarm.ringUri != "") {
                tv_ring!!.text = getString(R.string.tips_silence)
            } else {
                try {
                    val indexOf = ringUri.lastIndexOf("/")
                    var index = ringUri.substring(indexOf)
                    index = index.substring(1)
                    Integer.parseInt(index)
                    tv_ring!!.text = getString(R.string.custom_ring)
                } catch (e: Exception) {
                    // 默认
                    tv_ring!!.text = getString(R.string.default_ring)
                    ringUri = "default"
                }

            }
        } else {
            btn_cancel!!.visibility = View.INVISIBLE
            tv_contents!!.text = selecteNote!!.content
        }

        toast = CustomToast(this@AlarmActivity)

        tv_ring!!.setOnClickListener(this)
        rl_ring!!.setOnClickListener(this)
        tv_date!!.setOnClickListener(this)
        tv_time!!.setOnClickListener(this)
        btn_save!!.setOnClickListener(this)
        btn_back!!.setOnClickListener(this)
        btn_cancel!!.setOnClickListener(this)
        btn_ring!!.setOnClickListener(this)
        checktext1!!.setOnClickListener(this)
        checktext2!!.setOnClickListener(this)
        checktext3!!.setOnClickListener(this)
        checktext4!!.setOnClickListener(this)
        checktext5!!.setOnClickListener(this)
        checktext6!!.setOnClickListener(this)
        checktext7!!.setOnClickListener(this)
    }

    private fun findVeiws() {
        btn_back = findViewById(R.id.btn_back_alarm) as Button
        btn_save = findViewById(R.id.btn_savetop_alarm) as Button
        btn_cancel = findViewById(R.id.btn_cancelalarm) as Button
        btn_ring = findViewById(R.id.btn_selectring) as Button
        tv_date = findViewById(R.id.tv_alarm_date) as TextView
        tv_time = findViewById(R.id.tv_alarm_time) as TextView
        tv_week = findViewById(R.id.tv_alarm_week) as TextView
        checktext1 = findViewById(R.id.week1) as CheckedTextView
        checktext2 = findViewById(R.id.week2) as CheckedTextView
        checktext3 = findViewById(R.id.week3) as CheckedTextView
        checktext4 = findViewById(R.id.week4) as CheckedTextView
        checktext5 = findViewById(R.id.week5) as CheckedTextView
        checktext6 = findViewById(R.id.week6) as CheckedTextView
        checktext7 = findViewById(R.id.week7) as CheckedTextView
        tv_ring = findViewById(R.id.tv_alarm_ring) as TextView
        rl_ring = findViewById(R.id.ln_pickrings) as RelativeLayout
        tv_contents = findViewById(R.id.tv_alarm_contents) as TextView
        chb_vibration = findViewById(R.id.chb_vibration) as CheckBox
    }

    /**
     * 初始化日期
     */
    private fun initeDate() {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val now = Date()
        tv_date!!.text = format.format(now)
        format.applyPattern("HH:mm")
        tv_time!!.text = format.format(now)
        initeWeek(now)
    }

    /**
     * 初始化星期

     * @param now
     */
    private fun initeWeek(now: Date) {
        val cal = Calendar.getInstance()
        cal.time = now
        var w = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0)
            w = 0
        tv_week!!.text = weekstr[w]
    }

    override fun onClick(v: View) = when (v.id) {
        R.id.ln_pickrings, R.id.tv_alarm_ring, R.id.btn_selectring -> {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                // 类型为来电TYPE_RINGTONE TYPE_NOTIFICATION TYPE_ALARM
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.set_tip_rings))
            }
            startActivityForResult(intent, 3)
            tv_ring!!.text = getString(R.string.tips_silence)
            ringUri = ""
        }
        R.id.btn_savetop_alarm -> {
            val date = tv_date!!.text.toString()
            val time = tv_time!!.text.toString()

            var isrepeat = 0
            var repeatweeks = ""
            if (checktext1!!.isChecked)
                repeatweeks += "1,"
            if (checktext2!!.isChecked)
                repeatweeks += "2,"
            if (checktext3!!.isChecked)
                repeatweeks += "3,"
            if (checktext4!!.isChecked)
                repeatweeks += "4,"
            if (checktext5!!.isChecked)
                repeatweeks += "5,"
            if (checktext6!!.isChecked)
                repeatweeks += "6,"
            if (checktext7!!.isChecked)
                repeatweeks += "7,"
            if (repeatweeks.length > 1) {
                repeatweeks = repeatweeks
                        .substring(0, repeatweeks.length - 1)
                isrepeat = 1
            }
            val vibration = if (chb_vibration!!.isChecked) 1 else 0
            val content = tv_contents!!.text.toString()
            val alarm = Alarm(selecteNote!!.id, date, time, isrepeat,
                    repeatweeks, vibration, ringUri, content)
            // 保存到数据库
            alarmHelper!!.add(alarm)
            // 更新note
            noteHelper!!.Addalarm(selecteNote!!.id)
            // 设置闹钟
            alarmmanage!!.set(alarm)
            alarmmanage!!.destroy()
            toast!!.ShowMsg(getString(R.string.setted_success), CustomToast.Img_Ok)
            finish()
        }
        R.id.btn_back_alarm -> {
            finish()
            showdatepick()
        }
        R.id.tv_alarm_date -> showdatepick()
        R.id.tv_alarm_time -> showtimepick()
        R.id.week1 -> checktext1!!.isChecked = !checktext1!!.isChecked
        R.id.week2 -> checktext2!!.isChecked = !checktext2!!.isChecked
        R.id.week3 -> checktext3!!.isChecked = !checktext3!!.isChecked
        R.id.week4 -> checktext4!!.isChecked = !checktext4!!.isChecked
        R.id.week5 -> checktext5!!.isChecked = !checktext5!!.isChecked
        R.id.week6 -> checktext6!!.isChecked = !checktext6!!.isChecked
        R.id.week7 -> checktext7!!.isChecked = !checktext7!!.isChecked
        R.id.btn_cancelalarm -> {
            noteHelper!!.deletealarm(selecteNote!!.id)
            alarmHelper!!.delete(selecteNote!!.id)
            toast!!.ShowMsg(getString(R.string.canceld), CustomToast.Img_Ok)
            btn_cancel!!.visibility = View.GONE
        }
        else -> {
        }
    }

    /**
     * 显示选择日期对话框
     */
    private fun showdatepick() = if (dialog_date == null) {
        val view = layoutInflater.inflate(R.layout.dialog_selectdate, null)
        datePicker = view.findViewById(R.id.datepicker) as DatePicker
        dialog_date = AlertDialog.Builder(this@AlarmActivity)
                .setView(view)
                .setTitle(getString(R.string.choose_date))
                .setPositiveButton(R.string.confirm
                ) { arg0, arg1 ->
                    val year = datePicker!!.year.toString() + ""
                    val moth: Int = datePicker!!.month + 1
                    val day = datePicker!!.dayOfMonth.toString() + ""
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR,
                            Integer.parseInt(year))
                    calendar.set(Calendar.MONTH, moth - 1)
                    calendar.set(Calendar.DATE,
                            Integer.parseInt(day))
                    initeWeek(calendar.time)
                    tv_date!!.text = "$year-" + (if ("$moth".length == 1)
                        "0" + moth
                    else
                        moth) + "-" + if (day.length == 1)
                        "0" + day
                    else
                        day
                }.show()
    } else {
        dialog_date!!.show()
    }

    /**
     * 显示选择时间对话框
     */
    private fun showtimepick() = if (dialog_time == null) {
        val view = layoutInflater.inflate(R.layout.dialog_selecttime, null)
        timePicker = view.findViewById(R.id.tiempicker) as TimePicker
        dialog_time = AlertDialog.Builder(this@AlarmActivity)
                .setView(view)
                .setTitle(getString(R.string.choose_time))
                .setPositiveButton(R.string.confirm
                ) { arg0, arg1 ->
                    var hour = timePicker!!.currentHour.toString() + ""
                    var minite = timePicker!!
                            .currentMinute.toString() + ""
                    hour = if (hour.length == 1)
                        "0" + hour
                    else
                        hour
                    minite = if (minite.length == 1)
                        "0" + minite
                    else
                        minite
                    tv_time!!.text = hour + ":" + minite
                }.show()
    } else {
        dialog_time!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (resultCode != Activity.RESULT_OK || requestCode != 3)
            return
        try {
            // 得到我们选择的铃声 //EXTRA_RINGTONE_PICKED_URI
            val uri = data
                    .getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                println(uri.toString() + "============")
                if (uri.toString().contains("ringtone")) {
                    tv_ring!!.text = getString(R.string.default_ring)
                    ringUri = "default"
                } else {
                    tv_ring!!.text = getString(R.string.custom_ring)
                    ringUri = uri.toString()
                }
            }

        } catch (e: Exception) {
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {

        if (noteHelper != null) {
            noteHelper!!.Desdroy()
        }
        if (alarmHelper != null) {
            alarmHelper!!.Destroy()
        }
        if (dialog_date != null) {
            dialog_date!!.cancel()
        }
        if (dialog_time != null) {
            dialog_time!!.cancel()
        }
        super.onDestroy()
    }

}
