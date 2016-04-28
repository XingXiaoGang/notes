package com.fenghuo.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.fenghuo.notes.adapter.AccountAdapter;
import com.fenghuo.notes.db.DBAccountHelper;
import com.fenghuo.notes.bean.Account;
import com.fenghuo.notes.bean.GroupAccount;
import com.fenghuo.notes.bean.MonthAccount;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends FragmentExt implements OnClickListener,
		AdapterView.OnItemLongClickListener {

	private Button btn_add;
	private ExpandableListView elv_list;
	private View view;
	private DBAccountHelper accountHelper;
	private AccountAdapter adapter;
	private List<Account> list_today;
	private List<MonthAccount> list_month;
	private List<MonthAccount> list_all;
	private List<GroupAccount> list_group;
	private TextView tv_todayin;
	private TextView tv_todayout;
	private CustomToast toast;
	private Account daccount;// 当前选中的当日组的项
	private MonthAccount maccount;// 当前选中的当月组的项
	private MonthAccount aaccount;// 当前选中的所有中组的项

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_account, null, false);
		accountHelper = new DBAccountHelper(getActivity());
		toast = new CustomToast(getActivity());

		findviews();
		btn_add.setOnClickListener(this);
		elv_list.setOnItemLongClickListener(this);
		return view;
	}

	private void inite() {
		java.util.Date now = new java.util.Date();
		if (list_today != null)
			list_today.clear();
		if (list_month != null)
			list_month.clear();
		if (list_group != null)
			list_group.clear();
		if (list_all != null)
			list_all.clear();

		list_today = accountHelper.GetToadyList(accountHelper.GetDate());
		list_month = accountHelper.GetMonthList(new Date(now.getYear(), now
				.getMonth(), now.getDate()));
		list_all = accountHelper.GetListAll();
		list_group = new ArrayList<GroupAccount>();

		// 统计本日
		float todayInsum = 0;
		float todayOutsum = 0;
		for (int i = 0; i < list_today.size(); i++) {
			if (list_today.get(i).getKind() == 2)
				todayInsum += list_today.get(i).getMoney();
			else
				todayOutsum += list_today.get(i).getMoney();
		}
		list_group.add(new GroupAccount(1, "本日消费明细", "支出:" + todayOutsum, "收入:"
				+ todayInsum));
		tv_todayin.setText("今日消费:" + todayOutsum + "元");
		tv_todayout.setText("今日收入:" + todayInsum + "元");
		// 统计本月
		float mothInsum = 0;
		float monthOutsum = 0;
		for (int i = 0; i < list_month.size(); i++) {
			mothInsum += Float.valueOf(list_month.get(i).getSumin());
			monthOutsum += Float.valueOf(list_month.get(i).getSumout());
		}
		list_group.add(new GroupAccount(2, "本月消费统计", "支出:" + monthOutsum, "收入:"
				+ mothInsum));
		// 统计所有
		float Insum = 0;
		float Outsum = 0;
		for (int i = 0; i < list_all.size(); i++) {
			Insum += Float.valueOf(list_all.get(i).getSumin());
			Outsum += Float.valueOf(list_all.get(i).getSumout());
		}
		list_group.add(new GroupAccount(2, "全部消费", "支出:" + Outsum, "收入:"
				+ Insum));

		adapter = new AccountAdapter(getActivity(), list_today, list_month,
				list_all, list_group);
		elv_list.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		inite();
		super.onResume();
	}

	private void findviews() {
		btn_add = (Button) view.findViewById(R.id.btn_add_account);
		elv_list = (ExpandableListView) view.findViewById(R.id.elv_list);
		tv_todayin = (TextView) view.findViewById(R.id.tv_todayIn);
		tv_todayout = (TextView) view.findViewById(R.id.tv_todayOut);
	}

	@Override
	public void onClick(View arg0) {

		Intent intent = new Intent(getActivity(), AddAccountActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
								   int position, long id) {

		int[] p = (int[]) view.getTag();
		if (p[1] != -1) {
			switch (p[0]) {
				case 0:
					daccount = list_today.get(p[1]);
					new AlertDialog.Builder(getActivity())
							.setTitle("删除")
							.setMessage("确认删除？将不可恢复\n内容：" + daccount.getKinds())
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0,
															int arg1) {
											accountHelper.DeleteByID(daccount
													.getId());
											toast.ShowMsg("删除成功！",
													CustomToast.Img_Ok);
											inite();
										}
									}).setNegativeButton("取消", null).show();
					break;
				case 1:
					maccount = list_all.get(p[1]);
					new AlertDialog.Builder(getActivity())
							.setTitle("删除")
							.setMessage(
									"删除将不可恢复,确认删除" + maccount.getDate() + "的所有记录？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
															int which) {
											accountHelper.DeleteDay(maccount
													.getDate());
											toast.ShowMsg("删除成功！",
													CustomToast.Img_Ok);
											inite();
										}
									}).setNegativeButton("取消", null).show();
					break;
				case 2:
					aaccount = list_all.get(p[1]);
					new AlertDialog.Builder(getActivity())
							.setTitle("删除")
							.setMessage(
									"删除将不可恢复,确认删除" + aaccount.getDate() + "的所有记录？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
															int which) {
											accountHelper.DeleteDay(aaccount
													.getDate());
											toast.ShowMsg("删除成功！",
													CustomToast.Img_Ok);
											inite();
										}
									}).setNegativeButton("取消", null).show();
					break;
				default:
					break;
			}

		}
		return false;
	}
}
