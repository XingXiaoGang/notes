package com.fenghuo.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.fenghuo.LineEditText
import com.fenghuo.notes.adapter.KindAdapter
import com.fenghuo.notes.bean.Account
import com.fenghuo.notes.bean.Kind
import com.fenghuo.notes.db.DBAccountHelper
import com.fenghuo.notes.db.DbKindHelper

class AddAccountActivity : Activity(), OnClickListener {

    private var btn_back: Button? = null
    private var btn_save: Button? = null
    private var et_money: LineEditText? = null
    private var tv_kinds: TextView? = null
    private var ln_kinds: RelativeLayout? = null
    private var tv_time: TextView? = null
    private var tv_date: TextView? = null
    private var group: RadioGroup? = null
    private var accountHelper: DBAccountHelper? = null
    private var kindHelper: DbKindHelper? = null
    private var dialog_date: AlertDialog? = null
    private var dialog_time: AlertDialog? = null
    private var dialog_kinds: AlertDialog? = null
    private var dialog_addkinds: AlertDialog? = null
    private var kinds: MutableList<Kind>? = null
    private var datePicker: DatePicker? = null
    private var timePicker: TimePicker? = null
    private var toast: CustomToast? = null
    private var listView: ListView? = null// 类型列表
    private var adapter: KindAdapter? = null// 类型适配器

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_acc)

        findviews()
        kindHelper = DbKindHelper(this@AddAccountActivity)
        toast = CustomToast(this@AddAccountActivity)

        tv_date!!.text = kindHelper!!.GetDateTime().substring(0, 10)
        tv_time!!.text = kindHelper!!.GetDateTime().substring(10)
        btn_back!!.setOnClickListener(this)
        btn_save!!.setOnClickListener(this)
        tv_kinds!!.setOnClickListener(this)
        ln_kinds!!.setOnClickListener(this)
        tv_time!!.setOnClickListener(this)
        tv_date!!.setOnClickListener(this)
    }

    private fun findviews() {
        btn_back = findViewById(R.id.btn_back_accadd)
        btn_save = findViewById(R.id.btn_save_addacc)
        et_money = findViewById(R.id.et_money)
        tv_kinds = findViewById(R.id.tv_kinds_addacc)
        ln_kinds = findViewById(R.id.ln_kinds_addacc)
        tv_time = findViewById(R.id.tv_picktime_acc)
        tv_date = findViewById(R.id.tv_pickdate_acc)
        group = findViewById(R.id.rg_kind)
    }

    override fun onClick(view: View) = when (view.id) {
        R.id.btn_back_accadd -> finish()
        R.id.btn_save_addacc -> {
            val money = java.lang.Float.valueOf(et_money!!.text.toString())!!
            val kind = if (group!!.checkedRadioButtonId == R.id.rab_outcome)
                1
            else
                2
            val kinds = tv_kinds!!.text.toString()
            val time = tv_date!!.text.toString() + " " + tv_time!!.text.toString()
            if (money != 0f && kinds != getString(R.string.click_to_choose)) {
                accountHelper = DBAccountHelper(this@AddAccountActivity)
                val account = Account(-1, kind, kinds, money, time)
                accountHelper!!.insert(account)
                accountHelper!!.Destroy()
                toast!!.ShowMsg(getString(R.string.success), CustomToast.Img_Ok)
                finish()
            } else {
                toast!!.ShowMsg(getString(R.string.crrecot_mony_kind), CustomToast.Img_Erro)
            }
        }
        R.id.tv_kinds_addacc, R.id.ln_kinds_addacc -> showselectKinds()
        R.id.tv_picktime_acc -> showselecttime()
        R.id.tv_pickdate_acc -> showselectDate()
        else -> {
        }
    }

    /**
     * 显示选择日期对话框
     */
    private fun showselectDate() {
        if (dialog_date == null) {
            val view = layoutInflater.inflate(R.layout.dialog_selectdate, null)
            datePicker = view.findViewById<DatePicker>(R.id.datepicker) as DatePicker
            dialog_date = AlertDialog.Builder(this@AddAccountActivity)
                    .setView(view)
                    .setTitle(R.string.choose_date)
                    .setPositiveButton(R.string.confirm
                    ) { arg0, arg1 ->
                        val year = datePicker!!.year.toString() + ""
                        val moth: Int = datePicker!!.month + 1
                        val day = datePicker!!.dayOfMonth.toString() + ""
                        tv_date!!.text = "$year-" + (if (moth.toString().length == 1)
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
    }

    /**
     * 显示选择时间对话框
     */
    private fun showselecttime() {
        if (dialog_time == null) {
            val view = layoutInflater.inflate(R.layout.dialog_selecttime, null)
            timePicker = view.findViewById<TimePicker>(R.id.tiempicker) as TimePicker
            dialog_time = AlertDialog.Builder(this@AddAccountActivity)
                    .setView(view)
                    .setTitle(R.string.choose_time)
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
    }

    /**
     * 显示类型
     */
    private fun showselectKinds() {
        dialog_kinds = null
        listView = null
        val view = layoutInflater.inflate(R.layout.kind_list, null)
        listView = view.findViewById<ListView>(R.id.lv_kinds) as ListView
        kinds = kindHelper!!.Getlist()
        adapter = KindAdapter(this@AddAccountActivity, kinds!!)
        listView!!.adapter = adapter
        listView!!.onItemClickListener = OnItemClickListener { arg0, arg1, position, arg3 ->
            tv_kinds!!.text = kinds!![position].kind
            handler.sendEmptyMessage(1)
        }
        dialog_kinds = AlertDialog.Builder(this@AddAccountActivity)
                .setTitle(getString(R.string.choose_kind)).setView(view)
                .setPositiveButton(getString(R.string.add)) { arg0, arg1 -> showAddKinds() }.show()

    }

    /**
     * 显示添加类型对话框
     */
    private fun showAddKinds() {
        if (dialog_addkinds == null) {
            val view = layoutInflater.inflate(R.layout.dialog_addkinds, null)

            val editText = view
                    .findViewById<EditText>(R.id.et_addkinds) as EditText
            dialog_addkinds = AlertDialog.Builder(this@AddAccountActivity)
                    .setTitle(getString(R.string.input_name))
                    .setView(view)
                    .setPositiveButton(R.string.confirm
                    ) { arg0, arg1 ->
                        val text = editText.text.toString() + ""
                        if (text.length > 0) {
                            kindHelper!!.Add(text)
                            tv_kinds!!.text = text
                            dialog_kinds = null
                        } else {
                            toast!!.ShowMsg(getString(R.string.not_add),
                                    CustomToast.Img_Erro)
                        }
                    }.show()
        } else {
            dialog_addkinds!!.show()
        }
    }

    internal var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {

            when (msg.what) {
                1 -> dialog_kinds!!.dismiss()
                else -> {
                }
            }

            super.handleMessage(msg)
        }

    }

    override fun onDestroy() {

        kindHelper!!.destroy()
        if (accountHelper != null)
            accountHelper!!.Destroy()
        super.onDestroy()
    }

}
