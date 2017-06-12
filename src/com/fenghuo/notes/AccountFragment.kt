package com.fenghuo.notes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import com.fenghuo.notes.adapter.AccountAdapter
import com.fenghuo.notes.bean.Account
import com.fenghuo.notes.bean.GroupAccount
import com.fenghuo.notes.bean.MonthAccount
import com.fenghuo.notes.db.DBAccountHelper
import java.sql.Date
import java.util.*

class AccountFragment : FragmentExt(), OnClickListener, AdapterView.OnItemLongClickListener {

    private var btn_add: Button? = null
    private var elv_list: ExpandableListView? = null
    private var rootView: View? = null
    private var accountHelper: DBAccountHelper? = null
    private var adapter: AccountAdapter? = null
    private var list_today: MutableList<Account>? = null
    private var list_month: MutableList<MonthAccount>? = null
    private var list_all: MutableList<MonthAccount>? = null
    private var list_group: MutableList<GroupAccount>? = null
    private var tv_todayin: TextView? = null
    private var tv_todayout: TextView? = null
    private var toast: CustomToast? = null
    private var daccount: Account? = null// 当前选中的当日组的项
    private var maccount: MonthAccount? = null// 当前选中的当月组的项
    private var aaccount: MonthAccount? = null// 当前选中的所有中组的项

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_account, null, false)
        accountHelper = DBAccountHelper(activity)
        toast = CustomToast(activity)

        findviews()
        btn_add!!.setOnClickListener(this)
        elv_list!!.onItemLongClickListener = this
        return rootView
    }

    private fun inite() {
        val now = java.util.Date()
        list_today?.clear()
        list_month?.clear()
        list_group?.clear()
        list_all?.clear()
        list_today = accountHelper!!.GetToadyList(accountHelper!!.GetDate())
        list_month = accountHelper!!.GetMonthList(Date(now.year, now
                .month, now.date))
        list_all = accountHelper!!.GetListAll()
        list_group = ArrayList<GroupAccount>()

        // 统计本日
        var todayInsum = 0f
        var todayOutsum = 0f
        for (i in list_today!!.indices) {
            if (list_today!![i].kind == 2)
                todayInsum += list_today!![i].money
            else
                todayOutsum += list_today!![i].money
        }
        list_group!!.add(GroupAccount(1, "本日消费明细", "支出:" + todayOutsum, "收入:" + todayInsum))
        tv_todayin!!.text = "今日消费:" + todayOutsum + "元"
        tv_todayout!!.text = "今日收入:" + todayInsum + "元"
        // 统计本月
        var mothInsum = 0f
        var monthOutsum = 0f
        for (i in list_month!!.indices) {
            mothInsum += java.lang.Float.valueOf(list_month!![i].sumin)!!
            monthOutsum += java.lang.Float.valueOf(list_month!![i].sumout)!!
        }
        list_group!!.add(GroupAccount(2, "本月消费统计", "支出:" + monthOutsum, "收入:" + mothInsum))
        // 统计所有
        var Insum = 0f
        var Outsum = 0f
        for (i in list_all!!.indices) {
            Insum += java.lang.Float.valueOf(list_all!![i].sumin)!!
            Outsum += java.lang.Float.valueOf(list_all!![i].sumout)!!
        }
        list_group!!.add(GroupAccount(2, "全部消费", "支出:" + Outsum, "收入:" + Insum))

        adapter = AccountAdapter(activity, list_today!!, list_month!!,
                list_all!!, list_group as ArrayList<GroupAccount>)
        elv_list!!.setAdapter(adapter)
    }

    override fun onResume() {
        inite()
        super.onResume()
    }

    private fun findviews() {
        btn_add = rootView!!.findViewById(R.id.btn_add_account) as Button
        elv_list = rootView!!.findViewById(R.id.elv_list) as ExpandableListView
        tv_todayin = rootView!!.findViewById(R.id.tv_todayIn) as TextView
        tv_todayout = rootView!!.findViewById(R.id.tv_todayOut) as TextView
    }

    override fun onClick(arg0: View) {

        val intent = Intent(activity, AddAccountActivity::class.java)
        startActivity(intent)
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View,
                                 position: Int, id: Long): Boolean {

        val p = view.tag as IntArray
        if (p[1] != -1) {
            when (p[0]) {
                0 -> {
                    daccount = list_today!![p[1]]
                    AlertDialog.Builder(activity)
                            .setTitle("删除")
                            .setMessage("确认删除？将不可恢复\n内容：" + daccount!!.kinds)
                            .setPositiveButton("确定"
                            ) { arg0, arg1 ->
                                accountHelper!!.DeleteByID(daccount!!
                                        .id)
                                toast!!.ShowMsg("删除成功！",
                                        CustomToast.Img_Ok)
                                inite()
                            }.setNegativeButton("取消", null).show()
                }
                1 -> {
                    maccount = list_all!![p[1]]
                    AlertDialog.Builder(activity)
                            .setTitle("删除")
                            .setMessage(
                                    "删除将不可恢复,确认删除" + maccount!!.date + "的所有记录？")
                            .setPositiveButton("确定"
                            ) { dialog, which ->
                                accountHelper!!.DeleteDay(maccount!!.date!!)
                                toast!!.ShowMsg("删除成功！",
                                        CustomToast.Img_Ok)
                                inite()
                            }.setNegativeButton("取消", null).show()
                }
                2 -> {
                    aaccount = list_all!![p[1]]
                    AlertDialog.Builder(activity)
                            .setTitle("删除")
                            .setMessage(
                                    "删除将不可恢复,确认删除" + aaccount!!.date + "的所有记录？")
                            .setPositiveButton("确定"
                            ) { dialog, which ->
                                accountHelper!!.DeleteDay(aaccount!!.date!!)
                                toast!!.ShowMsg("删除成功！",
                                        CustomToast.Img_Ok)
                                inite()
                            }.setNegativeButton("取消", null).show()
                }
                else -> {
                }
            }

        }
        return false
    }
}
