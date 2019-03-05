package com.fenghuo.notes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

import com.fenghuo.notes.db.DbKindHelper
import com.fenghuo.notes.bean.Kind
import com.fenghuo.notes.R

class KindAdapter(private val context: Context, private val data: MutableList<Kind>) : BaseAdapter(), OnClickListener {
    private val inflater: LayoutInflater
    private var kindHelper: DbKindHelper? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {

        return data.size
    }

    override fun getItem(position: Int): Any {

        return data[position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup): View {
        var view = view

        var h: ViewHolder? = null
        if (view != null) {
            h = view.tag as ViewHolder
        } else {
            h = ViewHolder()
            view = inflater.inflate(R.layout.item_kind, null)
            h.item_text = view!!.findViewById<TextView>(R.id.kind_item_tv) as TextView
            h.item_btn_delete = view.findViewById<Button>(R.id.kind_item_delete) as Button
            h.item_btn_delete!!.tag = position
            h.item_btn_delete!!.setOnClickListener(this)
            view.tag = h
        }
        h.item_text!!.text = data[position].kind
        return view
    }

    private inner class ViewHolder {
        var item_text: TextView? = null
        var item_btn_delete: Button? = null
    }

    override fun onClick(view: View) {
        val position = Integer.valueOf(view.tag.toString())!!
        // 从列表中移除
        kindHelper = DbKindHelper(context)
        kindHelper!!.Delete(data[position].id)
        // 从集合中移除
        data.removeAt(position)
        notifyDataSetChanged()
    }
}
