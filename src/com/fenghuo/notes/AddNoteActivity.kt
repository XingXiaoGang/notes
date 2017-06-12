package com.fenghuo.notes

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button

import com.fenghuo.LineEditText
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.db.DBNoteHelper
import com.fenghuo.notes.db.PreferenceHelper

import java.util.Random

class AddNoteActivity : Activity(), View.OnClickListener {

    private var btn_back: Button? = null
    private var btn_save: Button? = null
    private var mScrollView: ViewGroup? = null
    private var et_content: LineEditText? = null
    private var noteHelper: DBNoteHelper? = null// 数据库
    private var helper: PreferenceHelper? = null// 用于存储草稿
    private var random: Random? = null// 用于分配随机图片
    private var isSaved = false
    private var currentNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thing)

        btn_back = findViewById(R.id.btn_back_addthi) as Button
        btn_save = findViewById(R.id.btn_save_addthi) as Button
        mScrollView = findViewById(R.id.scroll_view) as ViewGroup
        et_content = findViewById(R.id.et_text_addthi) as LineEditText

        random = Random()
        helper = PreferenceHelper(this@AddNoteActivity)
        noteHelper = DBNoteHelper(this@AddNoteActivity)

        btn_back!!.setOnClickListener(this)
        btn_save!!.setOnClickListener(this)
        // 上次没有保存的草稿
        et_content!!.setText(helper!!.last)
        et_content!!.setLineDown(et_content!!.paddingTop - 2)
        // 自动弹出键盘
        et_content!!.postDelayed({
            val manager = et_content!!
                    .context.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(et_content, 0)
        }, 500)
    }

    override fun onStop() {
        super.onStop()
        if (!isSaved) {
            helper!!.saveLast(et_content!!.text.toString() + "")
        }
    }

    override fun onDestroy() {

        if (!isSaved)
            helper!!.saveLast(et_content!!.text.toString() + "")
        super.onDestroy()
    }

    // 返回 /保存 按钮事件
    override fun onClick(arg0: View) {

        val content = et_content!!.text.toString().trim { it <= ' ' }

        when (arg0.id) {
            R.id.btn_back_addthi -> {
                if (content.length > 0)
                    helper!!.saveLast(content)
                finish()
            }
            R.id.btn_save_addthi -> {
                currentNote = Note(-1, random!!.nextInt(5), content, noteHelper!!.GetDate(), 0)
                noteHelper!!.Add(currentNote!!)
                helper!!.saveLast("")// 如果是插入一条新记事 则需要清空last
                noteHelper!!.Desdroy()
                isSaved = true
                finish()
            }
            else -> {
            }
        }
    }

}
