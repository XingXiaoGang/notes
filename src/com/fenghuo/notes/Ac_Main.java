package com.fenghuo.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghuo.adapter.ContentPageAdapter;
import com.fenghuo.db.BackupRestore;
import com.fenghuo.db.DBAccountHelper;
import com.fenghuo.db.DBNoteHelper;
import com.mine.view.menu.slide_section_menu.SlideSectionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ac_Main extends FragmentActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, OnPageChangeListener,
        ViewPagerTab.OnPageChangeListener_vp {

    private PopupWindow pop_Menu;
    private ListView lv_setting;
    private ViewPager viewPager;
    private ContentPageAdapter adapter;
    private SlideSectionMenu mSatelliteMenu;

    private TextView tv_things;
    private TextView tv_accounts;
    private CustomToast toast;
    private BackupRestore backupRestore;

    private long mLastBackDownTime = System.currentTimeMillis();// 时间
    private static final int CLICK_DURATION = 800;
    private int mBackDownTimes = 0;// 在短时间内按下的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_setting = (Button) findViewById(R.id.btn_setting);
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        ViewPagerTab pagerTab = (ViewPagerTab) findViewById(R.id.vp_tab);
        tv_things = (TextView) findViewById(R.id.tv_things);
        tv_accounts = (TextView) findViewById(R.id.tv_accounts);
        mSatelliteMenu = (SlideSectionMenu) findViewById(R.id.menu);

        adapter = new ContentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
        pagerTab.setOnPageChangerListener(this);
        viewPager.setCurrentItem(0, true);

        toast = new CustomToast(Ac_Main.this);
        btn_setting.setOnClickListener(this);
        tv_accounts.setOnClickListener(this);
        tv_things.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_setting:
//                showMenu(id);
                mSatelliteMenu.toggleMenu();
                break;
            case R.id.tv_things:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_accounts:
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean res = false;
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showMenu(findViewById(R.id.btn_setting));
            res = true;
        }
        if (adapter.dispatchOnKeyDown(keyCode, event)) {
            res = true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBackDownTimes == 1
                    && System.currentTimeMillis() - mLastBackDownTime < CLICK_DURATION || mBackDownTimes == 0) {
                mBackDownTimes++;
            } else {
                mBackDownTimes = 0;
                res = true;
            }

            if (mBackDownTimes == 1) {
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                res = true;
            } else if (mBackDownTimes == 2) {
                res = false;
            }
            mLastBackDownTime = System.currentTimeMillis();
        }
        return res ? res : super.onKeyDown(keyCode, event);
    }

    private void showMenu(View anchor) {
        if (pop_Menu != null && pop_Menu.isShowing()) {
            pop_Menu.dismiss();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.pop_setting, null);
        if (lv_setting == null) {
            lv_setting = (ListView) view.findViewById(R.id.setting_lv);
            SimpleAdapter adapter = new SimpleAdapter(Ac_Main.this, getsettinglist(), R.layout.item_setting, new String[]{"img", "content"}, new int[]{R.id.set_item_img, R.id.set_item_tv});
            lv_setting.setAdapter(adapter);
            lv_setting.setOnItemClickListener(this);
        }
        if (pop_Menu == null) {
            pop_Menu = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            pop_Menu.setBackgroundDrawable(dw);
            pop_Menu.setOutsideTouchable(true);
            pop_Menu.setFocusable(true);// 加上此句代码后 dissmis()方法失效
        }
        pop_Menu.showAsDropDown(anchor, 0, 0);
        pop_Menu.update();
    }

    @Override
    protected void onStop() {
        if (pop_Menu != null && pop_Menu.isShowing()) {
            pop_Menu.dismiss();
        }
        super.onStop();
    }

    /**
     * 填充设置列表
     *
     * @return
     */
    private List<HashMap<String, String>> getsettinglist() {
        List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map4 = new HashMap<String, String>();
        map4.put("img", R.drawable.pop_restore + "");
        map4.put("content", "恢复");
        maps.add(map4);
        HashMap<String, String> map5 = new HashMap<String, String>();
        map5.put("img", R.drawable.pop_backup + "");
        map5.put("content", "备份");
        maps.add(map5);
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("img", R.drawable.pop_setsecrit + "");
        map1.put("content", "设置密码");
        maps.add(map1);
        HashMap<String, String> map6 = new HashMap<String, String>();
        map6.put("img", R.drawable.pop_backup + "");
        map6.put("content", "清理记事");
        maps.add(map6);
        HashMap<String, String> map7 = new HashMap<String, String>();
        map7.put("img", R.drawable.pop_backup + "");
        map7.put("content", "清理记账");
        maps.add(map7);
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("img", R.drawable.pop_about + "");
        map2.put("content", "关于");
        maps.add(map2);
        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put("img", R.drawable.pop_close + "");
        map3.put("content", "退出");
        maps.add(map3);
        return maps;
    }

    /**
     * 设置菜单事件处理
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        HashMap<String, String> map = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
        String str = map.get("content");
        if (str.equals("退出")) {
            pop_Menu.dismiss();
            finish();
        } else if (str.equals("关于")) {
            pop_Menu.dismiss();
            Intent intent = new Intent(Ac_Main.this, Ac_About.class);
            startActivity(intent);
        } else if (str.equals("备份")) {
            pop_Menu.dismiss();
            if (backupRestore == null)
                backupRestore = new BackupRestore(Ac_Main.this);
            if (backupRestore.checkexist()) {
                new AlertDialog.Builder(Ac_Main.this)
                        .setTitle("提示?")
                        .setMessage("备份已存在，备份将会覆盖原来的数据，仍然备份？")
                        .setPositiveButton("继续备份",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        toast.ShowMsg("正在备份！",
                                                CustomToast.Img_Info);
                                        if (backupRestore.backup2()) {
                                            toast.ShowMsg("备份成功！",
                                                    CustomToast.Img_Ok);
                                        }
                                    }
                                }).setNegativeButton("取消备份", null).show();
            } else {
                toast.ShowMsg("正在备份！", CustomToast.Img_Info);
                if (backupRestore.backup2()) {
                    toast.ShowMsg("备份成功！", CustomToast.Img_Ok);
                }
            }

        } else if (str.equals("恢复")) {
            pop_Menu.dismiss();
            if (backupRestore == null)
                backupRestore = new BackupRestore(Ac_Main.this);
            new AlertDialog.Builder(Ac_Main.this)
                    .setTitle("确定恢复?")
                    .setMessage("所有记录将会还原到备份时的状态")
                    .setPositiveButton("恢复",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    toast.ShowMsg("正在恢复！", CustomToast.Img_Info);
                                    Values.isRestore_database = true;
                                    int i = backupRestore.restore2();
                                    pop_Menu.dismiss();
                                    if (i == 1) {
                                        toast.ShowMsg("恢复成功！",
                                                CustomToast.Img_Ok);
                                        Intent intent = getPackageManager()
                                                .getLaunchIntentForPackage(
                                                        getPackageName());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    if (i == 0) {
                                        toast.ShowMsg("未找到备份文件！",
                                                CustomToast.Img_Info);
                                    }
                                    if (i == -1) {
                                        toast.ShowMsg("恢复失败！",
                                                CustomToast.Img_Ok);
                                    }
                                    Values.isRestore_database = false;
                                }
                            }).setNegativeButton("取消", null).show();
        } else if (str.equals("清理记事")) {
            pop_Menu.dismiss();
            new AlertDialog.Builder(Ac_Main.this)
                    .setTitle("确定删除?")
                    .setMessage("建议删除前先备份数据")
                    .setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    DBNoteHelper helper = new DBNoteHelper(
                                            Ac_Main.this);
                                    helper.Deleteall();
                                    helper.Desdroy();
                                    toast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                                    Intent intent = getPackageManager()
                                            .getLaunchIntentForPackage(
                                                    getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("取消", null).show();
        } else if (str.equals("清理记账")) {
            pop_Menu.dismiss();
            new AlertDialog.Builder(Ac_Main.this)
                    .setTitle("确定删除?")
                    .setMessage("建议删除前先备份数据")
                    .setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    DBAccountHelper helper = new DBAccountHelper(
                                            Ac_Main.this);
                                    helper.Deleteall();
                                    helper.Destroy();
                                    toast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                                    Intent intent = getPackageManager()
                                            .getLaunchIntentForPackage(
                                                    getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("取消", null).show();
        } else {
            toast.ShowMsg("该功能暂未开放，敬请期待下次更新", CustomToast.Img_Info);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                tv_things.setTextColor(getResources().getColor(
                        R.color.bluedeep_text));
                tv_accounts
                        .setTextColor(getResources().getColor(R.color.gray_text));
                break;
            case 1:
                tv_accounts.setTextColor(getResources().getColor(
                        R.color.bluedeep_text));
                tv_things.setTextColor(getResources().getColor(R.color.gray_text));
                break;

            default:
                break;
        }
    }

}
