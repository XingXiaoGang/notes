package com.fenghuo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fenghuo.bean.Account;
import com.fenghuo.bean.GroupAccount;
import com.fenghuo.bean.MonthAccount;
import com.fenghuo.notes.R;

import java.util.List;

public class AccountAdapter extends BaseExpandableListAdapter {

    private List<Account> data_today;
    private List<MonthAccount> data_moth;
    private List<MonthAccount> data_all;
    private List<GroupAccount> data_group;
    private LayoutInflater inflater;
    // GroupItem 右边箭头动画
//	private Animation mToDownAnimation;
//	private Animation mToRightAnimation;

    /**
     * 本日消费明细、本月消费明细、所有消费明细
     *
     * @param context
     * @param data_today
     * @param data_moth
     * @param data_all
     * @param data_group
     */
    public AccountAdapter(Context context, List<Account> data_today,
                          List<MonthAccount> data_moth, List<MonthAccount> data_all,
                          List<GroupAccount> data_group) {

        inflater = LayoutInflater.from(context);
        this.data_today = data_today;
        this.data_moth = data_moth;
        this.data_all = data_all;
        this.data_group = data_group;
    }

    @Override
    public Object getChild(int groupposition, int childposition) {

        switch (groupposition) {
            case 0:
                return data_today.get(childposition);
            case 1:
                return data_moth.get(childposition);
            case 2:
                return data_all.get(childposition);

            default:
                return null;
        }
    }

    @Override
    public long getChildId(int groupposition, int childposition) {

        return childposition;
    }

    @Override
    public View getChildView(int groupposition, int childposition,
                             boolean islastchild, View convertview, ViewGroup parent) {
        ChildHolder holder = new ChildHolder();
        convertview = inflater.inflate(R.layout.item_elv_child, null);
        holder.tv_name = (TextView) convertview
                .findViewById(R.id.item_child_name);
        holder.tv_sumIn = (TextView) convertview
                .findViewById(R.id.item_child_in);
        holder.tv_sumOut = (TextView) convertview
                .findViewById(R.id.item_child_out);
        holder.tv_kinds = (TextView) convertview
                .findViewById(R.id.item_child_kinds);

        // 赋值
        switch (groupposition) {
            case 0:// 本日
                Account data = data_today.get(childposition);
                holder.tv_kinds.setText("日期:" + data.getDate());
                String todaymoney = data.getKind() == 1 ? "支出:" : "收入:";
                todaymoney = todaymoney + data.getMoney() + "元";
                holder.tv_sumOut.setText(todaymoney);
                holder.tv_name.setText("消费内容:" + data.getKinds());
                System.out.println(data.toString());
                break; // 本月
            case 1:
                MonthAccount data2 = data_moth.get(childposition);
                holder.tv_name.setText("日期:" + data2.getDate().substring(5, 10));
                holder.tv_kinds.setText("消费内容:" + data2.getThings());
                holder.tv_sumIn.setText("支出:" + data2.getSumout() + "元");
                holder.tv_sumOut.setText("收入:" + data2.getSumin() + "元");
                break;
            case 2:// 所有
                MonthAccount data3 = data_all.get(childposition);
                holder.tv_name.setText("日期:" + data3.getDate().substring(5, 10));
                holder.tv_kinds.setText("消费内容:" + data3.getThings());
                holder.tv_sumIn.setText("支出:" + data3.getSumout() + "元");
                holder.tv_sumOut.setText("收入:" + data3.getSumin() + "元");
                break;
            default:
                break;
        }
        int[] tag = new int[]{groupposition, childposition};
        convertview.setTag(tag);
        return convertview;
    }

    @Override
    public int getChildrenCount(int groupposition) {

        switch (groupposition) {
            case 0:
                return data_today.size();
            case 1:
                return data_moth.size();

            case 2:
                return data_all.size();
            default:
                return 0;
        }
    }

    @Override
    public Object getGroup(int position) {

        return data_group.get(position);
    }

    @Override
    public int getGroupCount() {

        return data_group.size();
    }

    @Override
    public long getGroupId(int positon) {

        return positon;
    }

    @Override
    public View getGroupView(int groupposition, boolean isexpand,
                             View convertview, ViewGroup parent) {
        GroupHolder holder = null;
        if (convertview != null) {
            holder = (GroupHolder) convertview.getTag(R.id.tag_group);
        } else {
            convertview = inflater.inflate(R.layout.item_elv_group, null);
            holder = new GroupHolder();
            holder.tv_name = (TextView) convertview
                    .findViewById(R.id.item_group_name);
            holder.tv_sumIn = (TextView) convertview
                    .findViewById(R.id.item_group_in);
            holder.tv_sumOut = (TextView) convertview
                    .findViewById(R.id.item_group_out);
            holder.img = (ImageView) convertview
                    .findViewById(R.id.item_group_img);
        }

        if (isexpand) {
            holder.img.setBackgroundResource(R.drawable.ic_groupexp);
        } else {
            holder.img.setBackgroundResource(R.drawable.ic_group);
        }
        holder.tv_name.setText(data_group.get(groupposition).getName());
        holder.tv_sumIn.setText(data_group.get(groupposition).getSumin());
        holder.tv_sumOut.setText(data_group.get(groupposition).getSumout());
        int[] tag = new int[]{groupposition, -1};
        convertview.setTag(tag);
        convertview.setTag(R.id.tag_group, holder);
        return convertview;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {

        return true;
    }

    class ChildHolder {
        TextView tv_name;
        TextView tv_sumOut;
        TextView tv_sumIn;
        TextView tv_kinds;
    }

    class GroupHolder {
        TextView tv_name;
        TextView tv_sumOut;
        TextView tv_sumIn;
        ImageView img;
    }

}
