package com.fenghuo.notes

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.fenghuo.LineEditText
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.db.DBNoteHelper
import com.fenghuo.notes.db.PreferenceHelper
import com.mine.view.dialog.ConformDialog
import java.util.*

class EditNoteActivity : Activity(), View.OnClickListener {

    private var btn_back: Button? = null
    private var btn_save: Button? = null
    private var btn_alarm: Button? = null
    private var btn_delete: Button? = null
    private var btn_edit: Button? = null
    private var tv_date: TextView? = null
    private var mScrollView: ViewGroup? = null
    private var et_content: LineEditText? = null
    private var noteHelper: DBNoteHelper? = null// 数据库
    private var currentNote: Note? = null
    private var toast: CustomToast? = null
    private var contentChagned: Boolean = false
    private var prefferHelper: PreferenceHelper? = null
    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_thing)

        initViews()

        noteHelper = DBNoteHelper(this@EditNoteActivity)
        toast = CustomToast(this@EditNoteActivity)
        prefferHelper = PreferenceHelper(this@EditNoteActivity)

        val noteId = intent.getIntExtra("noteid", -1)
        if (noteId != -1) {
            currentNote = noteHelper!!.GetSingle(noteId)
        }

        btn_back!!.setOnClickListener(this)
        btn_save!!.setOnClickListener(this)
        btn_alarm!!.setOnClickListener(this)
        btn_delete!!.setOnClickListener(this)
        btn_edit!!.setOnClickListener(this)
        btn_save!!.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        // 编辑、添加逻辑处理
        currentNote?.let {
            currentNote = noteHelper!!.GetSingle(currentNote!!.id)
        }
        et_content!!.setText(currentNote!!.content)
        et_content!!.inputType = InputType.TYPE_NULL
        et_content!!.setSingleLine(false)
        et_content!!.setLineDown(et_content!!.paddingTop - 2)
        tv_date!!.text = currentNote!!.date

        val imgIndex = currentNote!!.img
        if (imgIndex >= 0 && imgIndex < Values.item_bg_big.size) {
            et_content!!.setBackgroundResource(Values.item_bg_big[imgIndex])
        } else {
            et_content!!.setBackgroundResource(R.drawable.page_bg_blue)
        }

        //从草稿中恢复
        val saved = prefferHelper?.getLastDraftById(currentNote!!.id.toLong())
        if (!TextUtils.equals(saved, currentNote?.content)) {
            saved?.let {
                val confirm = object : ConformDialog(this@EditNoteActivity, getString(R.string.restore_from_saved)) {
                    override fun onClick(v: View?) {
                        if (v?.id == R.id.confirm) {
                            et_content?.setText(saved)
                            startEdit()
                            dismiss()
                        } else {
                            dismiss()
                        }
                    }
                }
                confirm.setButtonText(getString(R.string.cancel), getString(R.string.ok))
                confirm.show()
            }
        }
    }

    private fun initViews() {
        btn_back = findViewById(R.id.btn_back_editthi) as Button
        btn_save = findViewById(R.id.btn_save_editthi) as Button
        et_content = findViewById(R.id.et_text_editthi) as LineEditText
        btn_alarm = findViewById(R.id.btn_alarm_editthi) as Button
        btn_delete = findViewById(R.id.btn_delete_editthi) as Button
        btn_edit = findViewById(R.id.btn_edit_editthi) as Button
        tv_date = findViewById(R.id.tv_date_editthi) as TextView
        mScrollView = findViewById(R.id.scroll_view) as ViewGroup
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("saved_txt", et_content?.text.toString())
        outState?.putBoolean("isedit", isEditing)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getString("saved_txt")?.let { et_content?.setText(it) }
        savedInstanceState?.getBoolean("isedit", false).let {
            if (isEditing) {
                startEdit()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (contentChagned) {
            currentNote?.id?.let {
                prefferHelper?.saveLastWithID(it.toLong(), et_content?.text.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        noteHelper!!.Desdroy()
    }

    private fun startEdit() {
        isEditing = true
        contentChagned = true
        et_content!!.isEnabled = true
        et_content!!.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        et_content!!.setSingleLine(false)
        btn_save!!.visibility = View.VISIBLE
        et_content?.setSelection(et_content!!.text.length)
        showKeyboard()
    }

    private fun endEdit() {
        currentNote!!.content = et_content!!.text.toString().trim()
        noteHelper!!.Update(currentNote!!)
        noteHelper!!.Desdroy()
        isEditing = false;
        prefferHelper?.removeLastDraft(currentNote!!.id.toLong())
    }

    private fun showKeyboard() {
        // 自动弹出键盘
        val timer = Timer()
        timer.schedule(object : TimerTask() {

            override fun run() {
                // TODO Auto-generated method stub
                val manager = et_content!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(et_content, 0)
            }
        }, 500)
    }

    private fun deleteNote() {
        AlertDialog.Builder(this@EditNoteActivity)
                .setTitle(getString(R.string.confirm_delete))
                .setPositiveButton(getString(R.string.confirm)
                ) { arg0, arg1 ->
                    noteHelper!!.Delete(currentNote!!.id)
                    toast!!.ShowMsg(getString(R.string.delete_success), CustomToast.Img_Ok)
                    finish()
                }.setNegativeButton(getString(R.string.cancel), null).show()
    }

    // 返回 /保存 按钮事件
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_alarm_editthi -> {
                val intent_alarm = Intent(this@EditNoteActivity, AlarmActivity::class.java)
                intent_alarm.putExtra("noteid", currentNote!!.id)
                startActivity(intent_alarm)
            }
            R.id.btn_delete_editthi -> deleteNote()
            R.id.btn_edit_editthi -> startEdit()
            R.id.btn_save_editthi -> {
                endEdit()
                finish()
            }
            R.id.btn_back_editthi -> finish()
        }
    }

}
