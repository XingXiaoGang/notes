package com.fenghuo.notes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import com.fenghuo.LineTextView
import com.fenghuo.notes.R
import com.fenghuo.notes.Values
import com.fenghuo.notes.bean.Note
import com.fenghuo.notes.db.DBNoteHelper

class NoteAdapter() : BaseAdapter(), View.OnClickListener {


    var isEdit: Boolean = false
    private var inflater: LayoutInflater? = null
    private var noteDbHelper: DBNoteHelper? = null
    private val data: MutableList<Note> = arrayListOf()

    constructor(context: Context) : this() {
        this.inflater = LayoutInflater.from(context)
        this.noteDbHelper = DBNoteHelper(context)
        this.data.clear()
    }

    /**
     * 刷新数据
     * **/
    fun Update() {
        data.clear()
        data.let { this.data.addAll(noteDbHelper!!.Getlist()) }
    }

    /**
     * 进入编辑状态
     */
    fun startEdit() {
        isEdit = true
        notifyDataSetChanged()
    }

    /**
     * 退出编辑状态
     */
    fun quitEdit() {
        isEdit = false
        notifyDataSetChanged()
    }

    fun destory() {
        noteDbHelper?.Desdroy()
        noteDbHelper = null
    }

    override fun getCount(): Int = data.size
    override fun getItem(arg0: Int): Any = data[arg0]
    override fun getItemId(arg0: Int): Long {
        return arg0.toLong()
    }

    override fun onClick(view: View) {
        val item: Note = view.getTag(R.id.tag_1) as Note
        // 从列表中移除
        val dbNoteHelper = DBNoteHelper(view.context)
        dbNoteHelper.Delete(item.id)
        dbNoteHelper.Desdroy()
        // 从集合中移除
        data.remove(item)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup): View {
        var view = view
        var h: ViewHolder? = null
        val item: Note = data[position]
        if (view != null) {
            h = view.tag as ViewHolder
        } else {
            h = ViewHolder()
            view = inflater?.inflate(R.layout.item_note, null)
            h.item_img = view!!.findViewById(R.id.item_img)
            h.item_tv_content = view.findViewById(R.id.item_tv_content)
            h.item_btn_delete = view.findViewById(R.id.item_btndelete)
            h.item_alarm = view.findViewById(R.id.item_alarm)
            view.tag = h
        }

        val imgIndex = item.img
        if (imgIndex >= 0 && imgIndex < Values.item_bg_preview.size) {
            h.item_img!!.setBackgroundResource(Values.item_bg_preview[imgIndex])
        } else {
            h.item_img!!.setBackgroundResource(R.drawable.page_blue)
        }

        h.item_tv_content!!.text = item.content
        h.item_btn_delete!!.setOnClickListener(this)
        h.item_btn_delete!!.setTag(R.id.tag_1, item)
        // 此方法对横线的位置进行微调(原始:getPaddingtop+lineHeigt+x). 这里setLineDown(X)
        h.item_tv_content!!.setLineDown(-h.item_tv_content!!.paddingTop - 1)
        if (item.alarm == 1)
            h.item_alarm!!.visibility = View.VISIBLE
        else
            h.item_alarm!!.visibility = View.INVISIBLE
        if (isEdit)
            h.item_btn_delete!!.visibility = View.VISIBLE
        else
            h.item_btn_delete!!.visibility = View.GONE
        view.setTag(R.id.tag_1, item)
        return view
    }

    private inner class ViewHolder {
        var item_img: ImageView? = null
        var item_tv_content: LineTextView? = null
        var item_btn_delete: Button? = null
        var item_alarm: ImageView? = null
    }

}
