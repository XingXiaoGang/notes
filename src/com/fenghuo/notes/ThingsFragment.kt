package com.fenghuo.notes

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.GridView
import com.fenghuo.notes.adapter.NoteAdapter
import com.fenghuo.notes.bean.Note


class ThingsFragment : FragmentExt(), OnClickListener, OnItemClickListener, OnItemLongClickListener {

    private var gv_list: GridView? = null// 列表
    private var adapter: NoteAdapter? = null// 适配器
    private var mRootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_things, null, false)
        gv_list = mRootView!!.findViewById(R.id.gv_list) as GridView
        mRootView!!.findViewById(R.id.add_new_note).setOnClickListener(this)
        adapter = NoteAdapter(activity)
        gv_list!!.adapter = adapter

        gv_list!!.onItemClickListener = this
        gv_list!!.onItemLongClickListener = this

        return mRootView
    }

    override fun onResume() {
        super.onResume()
        adapter?.Update();
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.destory()
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
        }

    }

    override fun onItemLongClick(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long): Boolean {
        adapter!!.startEdit()
        return true
    }

    override fun onItemClick(arg0: AdapterView<*>, view: View, position: Int,
                             arg3: Long) {
        val note: Note? = view.getTag(R.id.tag_1) as Note
        note?.let {
            val intent_edit = Intent(activity, EditNoteActivity::class.java)
            intent_edit.putExtra("noteid", it.id)
            startActivity(intent_edit)
        }
    }

}
