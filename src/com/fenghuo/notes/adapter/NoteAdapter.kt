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

class NoteAdapter(private val context: Context, private var data: MutableList<Note>?) : BaseAdapter() {
    var isEdit: Boolean = false
        private set// 是否在编辑
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    fun Update(list: MutableList<Note>) {
        data!!.clear()
        data = list
        notifyDataSetChanged()
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

    override fun getCount(): Int {

        return data!!.size
    }

    override fun getItem(arg0: Int): Any {

        return data!![arg0]
    }

    override fun getItemId(arg0: Int): Long {

        return arg0.toLong()
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup): View {
        var view = view

        var h: ViewHolder? = null
        if (view != null) {
            h = view.tag as ViewHolder
        } else {
            h = ViewHolder()
            view = inflater.inflate(R.layout.item_note, null)
            h.item_img = view!!.findViewById(R.id.item_img) as ImageView
            h.item_tv_content = view
                    .findViewById(R.id.item_tv_content) as LineTextView
            h.item_btn_delete = view.findViewById(R.id.item_btndelete) as Button
            h.item_alarm = view.findViewById(R.id.item_alarm) as ImageView
            view.tag = h
        }

        val imgIndex = data!![position].img
        if (imgIndex >= 0 && imgIndex < Values.item_bg_preview.size) {
            h.item_img!!.setBackgroundResource(Values.item_bg_preview[imgIndex])
        } else {
            h.item_img!!.setBackgroundResource(R.drawable.page_blue)
        }

        h.item_tv_content!!.text = data!![position].content
        h.item_btn_delete!!.setOnClickListener(onclicklistener())
        h.item_btn_delete!!.tag = position
        // 此方法对横线的位置进行微调(原始:getPaddingtop+lineHeigt+x). 这里setLineDown(X)
        h.item_tv_content!!.setLineDown(-h.item_tv_content!!.paddingTop - 1)
        if (data!![position].alarm == 1)
            h.item_alarm!!.visibility = View.VISIBLE
        else
            h.item_alarm!!.visibility = View.INVISIBLE
        if (isEdit)
            h.item_btn_delete!!.visibility = View.VISIBLE
        else
            h.item_btn_delete!!.visibility = View.GONE
        return view
    }

    internal inner class onclicklistener : View.OnClickListener {

        override fun onClick(arg0: View) {

            val position = Integer.valueOf(arg0.tag.toString())!!
            // 从列表中移除
            val dbNoteHelper = DBNoteHelper(context)
            dbNoteHelper.Delete(data!![position].id)
            dbNoteHelper.Desdroy()
            // 从集合中移除
            data!!.removeAt(position)
            notifyDataSetChanged()
        }

    }

    private inner class ViewHolder {
        var item_img: ImageView? = null
        // public LineEditText item_tv_content;
        var item_tv_content: LineTextView? = null
        var item_btn_delete: Button? = null
        var item_alarm: ImageView? = null
    }
}
