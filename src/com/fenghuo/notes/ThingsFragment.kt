package com.fenghuo.notes

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.GridView
import android.widget.PopupWindow

import com.fenghuo.notes.adapter.NoteAdapter
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.db.DBNoteHelper
import com.fenghuo.notes.upload.CloudUtils


class ThingsFragment : FragmentExt(), OnClickListener, OnItemClickListener, OnItemLongClickListener {

    private var btn_complete: Button? = null// 结束编辑
    private var gv_list: GridView? = null// 列表
    private var noteHelper: DBNoteHelper? = null// 数据库
    private var list: MutableList<Note>? = null// 数据集合
    private var adapter: NoteAdapter? = null// 适配器
    private var pop_button: PopupWindow? = null
    private var mPopBtnView: View? = null// 弹出
    // 按钮
    private var mCurrentNote: Note? = null// 当前的note
    private var mRootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_things, null, false)
        gv_list = mRootView!!.findViewById(R.id.gv_list) as GridView
        mRootView!!.findViewById(R.id.add_new_note).setOnClickListener(this)
        noteHelper = DBNoteHelper(activity)
        list = noteHelper!!.Getlist()
        adapter = NoteAdapter(activity, list)
        gv_list!!.adapter = adapter

        gv_list!!.onItemClickListener = this
        gv_list!!.onItemLongClickListener = this

        return mRootView
    }

    override fun onDestroy() {
        noteHelper!!.Desdroy()
        super.onDestroy()
    }

    override fun onResume() {
        list!!.clear()
        list = noteHelper!!.Getlist()
        adapter!!.Update(list!!)
        super.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (adapter!!.isEdit) {
            adapter!!.quitEdit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(arg0: View) {
        when (arg0.id) {
        // 跳转界面
            R.id.add_new_note -> {
                val intent = Intent(activity, AddNoteActivity::class.java)
                startActivity(intent)
            }
        // 结束
            R.id.btn_compelete -> {
                btn_complete!!.visibility = View.GONE
                adapter!!.quitEdit()
                pop_button!!.dismiss()
            }
            else -> {
            }
        }

    }

    override fun onItemLongClick(arg0: AdapterView<*>, arg1: View, arg2: Int,
                                 arg3: Long): Boolean {
        adapter!!.startEdit()
        showPopButton()
        return true
    }

    override fun onItemClick(arg0: AdapterView<*>, arg1: View, position: Int,
                             arg3: Long) {
        mCurrentNote = list!![position]
        val intent_edit = Intent(activity, EditNoteActivity::class.java)
        intent_edit.putExtra("noteid", mCurrentNote!!.id)
        startActivity(intent_edit)
    }

    private fun showPopButton() {

        mPopBtnView = activity.layoutInflater.inflate(
                R.layout.pop_button, null)
        btn_complete = mPopBtnView!!.findViewById(R.id.btn_compelete) as Button
        btn_complete!!.setOnClickListener(this)
        pop_button = PopupWindow(mPopBtnView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT)
        pop_button!!.showAtLocation(activity.findViewById(R.id.vp_content), Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        pop_button!!.update()
    }

}
