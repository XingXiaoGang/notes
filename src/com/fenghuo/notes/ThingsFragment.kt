package com.fenghuo.notes

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.GridView
import com.fenghuo.notes.adapter.NoteAdapter
import com.fenghuo.notes.bean.Note
import com.mine.view.button.FloatingActionButton


class ThingsFragment : FragmentExt(), OnItemClickListener, OnItemLongClickListener {

    private var list: GridView? = null// 列表
    private var adapter: NoteAdapter? = null// 适配器
    private var mRootView: View? = null
    private var saveBtn: FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_things, null, false)
        list = mRootView!!.findViewById<GridView>(R.id.gv_list) as GridView

        mRootView?.findViewById<View>(R.id.add_new_note)?.setOnClickListener {
            val intent = Intent(activity, AddNoteActivity::class.java)
            startActivity(intent)
        }

        saveBtn = mRootView?.findViewById(R.id.save)
        saveBtn?.setOnClickListener { quiteEdit() }

        adapter = NoteAdapter(activity)

        list?.adapter = adapter

        list?.onItemClickListener = this
        list?.onItemLongClickListener = this

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

    private fun quiteEdit() {
        adapter!!.quitEdit()
        saveBtn!!.hide(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (adapter!!.isEdit) {
            quiteEdit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onItemLongClick(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long): Boolean {
        adapter!!.startEdit()
        saveBtn!!.show(true)
        return true
    }

    override fun onItemClick(arg0: AdapterView<*>, view: View, position: Int,
                             arg3: Long) {
        val note: Note? = view.getTag(R.id.tag_1) as Note
        note?.let {
            val intent = Intent(activity, EditNoteActivity::class.java)
            intent.putExtra("noteid", it.id)
            startActivity(intent)
        }
    }

}
