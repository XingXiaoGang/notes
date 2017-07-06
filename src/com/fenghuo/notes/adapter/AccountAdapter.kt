package com.fenghuo.notes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.fenghuo.notes.R
import com.fenghuo.notes.bean.Account
import com.fenghuo.notes.bean.GroupAccount
import com.fenghuo.notes.bean.MonthAccount

class AccountAdapter
// GroupItem 右边箭头动画
//	private Animation mToDownAnimation;
//	private Animation mToRightAnimation;

/**
 * 本日消费明细、本月消费明细、所有消费明细

 * @param context
 * *
 * @param data_today
 * *
 * @param data_moth
 * *
 * @param data_all
 * *
 * @param data_group
 */
(context: Context, private val data_today: MutableList<Account>,
 private val data_moth: MutableList<MonthAccount>, private val data_all: MutableList<MonthAccount>,
 private val data_group: MutableList<GroupAccount>) : BaseExpandableListAdapter() {
    private val inflater: LayoutInflater

    init {

        inflater = LayoutInflater.from(context)
    }

    override fun getChild(groupposition: Int, childposition: Int): Any? {

        when (groupposition) {
            0 -> return data_today[childposition]
            1 -> return data_moth[childposition]
            2 -> return data_all[childposition]

            else -> return null
        }
    }

    override fun getChildId(groupposition: Int, childposition: Int): Long {

        return childposition.toLong()
    }

    override fun getChildView(groupposition: Int, childposition: Int,
                              islastchild: Boolean, convertview: View?, parent: ViewGroup): View {
        var context: Context = parent.context;
        val holder = ChildHolder()
        var itemView = inflater.inflate(R.layout.item_elv_child, null)
        holder.tv_name = itemView
                .findViewById(R.id.item_child_name) as TextView
        holder.tv_sumIn = itemView
                .findViewById(R.id.item_child_in) as TextView
        holder.tv_sumOut = itemView
                .findViewById(R.id.item_child_out) as TextView
        holder.tv_kinds = itemView
                .findViewById(R.id.item_child_kinds) as TextView

        // 赋值
        when (groupposition) {
            0// 本日
            -> {
                val data = data_today[childposition]
                holder.tv_kinds!!.text = context.getString(R.string.date) + data.date
                var todaymoney = if (data.kind == 1) context.getString(R.string.consume) else context.getString(R.string.income)
                todaymoney = todaymoney + data.money + context.getString(R.string.yuan)
                holder.tv_sumOut!!.text = todaymoney
                holder.tv_name!!.text = context.getString(R.string.consume_project) + data.kinds
                println(data.toString())
            }
            1 -> {
                val data2 = data_moth[childposition]
                holder.tv_name!!.text = context.getString(R.string.date) + data2.date!!.substring(5, 10)
                holder.tv_kinds!!.text = context.getString(R.string.consume_project) + data2.things
                holder.tv_sumIn!!.text = context.getString(R.string.consume) + data2.sumout + context.getString(R.string.yuan)
                holder.tv_sumOut!!.text = context.getString(R.string.income) + data2.sumin + context.getString(R.string.yuan)
            }
            2// 所有
            -> {
                val data3 = data_all[childposition]
                holder.tv_name!!.text = context.getString(R.string.date) + data3.date!!.substring(5, 10)
                holder.tv_kinds!!.text = context.getString(R.string.consume_project) + data3.things
                holder.tv_sumIn!!.text = context.getString(R.string.consume) + data3.sumout + context.getString(R.string.yuan)
                holder.tv_sumOut!!.text = context.getString(R.string.income) + data3.sumin + context.getString(R.string.yuan)
            }
            else -> {
            }
        }// 本月
        val tag = intArrayOf(groupposition, childposition)
        itemView.tag = tag
        return itemView
    }

    override fun getChildrenCount(groupposition: Int): Int {

        when (groupposition) {
            0 -> return data_today.size
            1 -> return data_moth.size

            2 -> return data_all.size
            else -> return 0
        }
    }

    override fun getGroup(position: Int): Any {

        return data_group[position]
    }

    override fun getGroupCount(): Int {

        return data_group.size
    }

    override fun getGroupId(positon: Int): Long {

        return positon.toLong()
    }

    override fun getGroupView(groupposition: Int, isexpand: Boolean,
                              convertview: View?, parent: ViewGroup): View {
        var convertview = convertview
        var holder: GroupHolder? = null
        if (convertview != null) {
            holder = convertview.getTag(R.id.tag_group) as GroupHolder
        } else {
            convertview = inflater.inflate(R.layout.item_elv_group, null)
            holder = GroupHolder()
            holder.tv_name = convertview!!
                    .findViewById(R.id.item_group_name) as TextView
            holder.tv_sumIn = convertview
                    .findViewById(R.id.item_group_in) as TextView
            holder.tv_sumOut = convertview
                    .findViewById(R.id.item_group_out) as TextView
            holder.img = convertview
                    .findViewById(R.id.item_group_img) as ImageView
        }

        if (isexpand) {
            holder.img!!.setBackgroundResource(R.drawable.ic_groupexp)
        } else {
            holder.img!!.setBackgroundResource(R.drawable.ic_group)
        }
        holder.tv_name!!.text = data_group[groupposition].name
        holder.tv_sumIn!!.text = data_group[groupposition].sumin
        holder.tv_sumOut!!.text = data_group[groupposition].sumout
        val tag = intArrayOf(groupposition, -1)
        convertview.tag = tag
        convertview.setTag(R.id.tag_group, holder)
        return convertview
    }

    override fun hasStableIds(): Boolean {

        return false
    }

    override fun isChildSelectable(arg0: Int, arg1: Int): Boolean {

        return true
    }

    internal inner class ChildHolder {
        var tv_name: TextView? = null
        var tv_sumOut: TextView? = null
        var tv_sumIn: TextView? = null
        var tv_kinds: TextView? = null
    }

    internal inner class GroupHolder {
        var tv_name: TextView? = null
        var tv_sumOut: TextView? = null
        var tv_sumIn: TextView? = null
        var img: ImageView? = null
    }

}
