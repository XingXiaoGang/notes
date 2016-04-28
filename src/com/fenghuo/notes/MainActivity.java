package com.fenghuo.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghuo.adapter.ContentPageAdapter;
import com.fenghuo.db.BackupRestoreUtils;
import com.fenghuo.db.DBAccountHelper;
import com.fenghuo.db.DBNoteHelper;
import com.mine.view.gesture.GestureHandleView;
import com.mine.view.menu.slide_section_menu.SlideSectionMenu;

public class MainActivity extends FragmentActivity implements View.OnClickListener, GestureHandleView.GestureCallBack, ViewPager.OnPageChangeListener,
        ViewPagerTab.OnPageChangeListener_vp {

    private ViewPager mViewPager;
    private ContentPageAdapter mAdapter;
    private SlideSectionMenu mMenu;

    private TextView mThingsTextView;
    private TextView mAccountsTextView;
    private CustomToast mToast;
    private BackupRestoreUtils mBackupRestore;

    private long mLastBackDownTime = System.currentTimeMillis();// 时间
    private static final int CLICK_DURATION = 800;
    private int mBackDownTimes = 0;// 在短时间内按下的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_setting = (Button) findViewById(R.id.btn_setting);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        ViewPagerTab pagerTab = (ViewPagerTab) findViewById(R.id.vp_tab);
        mThingsTextView = (TextView) findViewById(R.id.tv_things);
        mAccountsTextView = (TextView) findViewById(R.id.tv_accounts);
        mMenu = (SlideSectionMenu) findViewById(R.id.menu);
        GestureHandleView gestureHandleView = (GestureHandleView) findViewById(R.id.gesture_handler_view);
        gestureHandleView.setGestureCallBack(this);

        findViewById(R.id.item_about).setOnClickListener(this);
        findViewById(R.id.item_backup).setOnClickListener(this);
        findViewById(R.id.item_recovery).setOnClickListener(this);
        findViewById(R.id.item_exit).setOnClickListener(this);
        findViewById(R.id.item_clean_accounts).setOnClickListener(this);
        findViewById(R.id.item_clean_notes).setOnClickListener(this);
        findViewById(R.id.item_setpwd).setOnClickListener(this);

        mMenu.closeMenu();
        mAdapter = new ContentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        pagerTab.setViewPager(mViewPager);
        pagerTab.setOnPageChangerListener(this);
        mViewPager.setCurrentItem(0, true);

        mToast = new CustomToast(MainActivity.this);
        btn_setting.setOnClickListener(this);
        mAccountsTextView.setOnClickListener(this);
        mThingsTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting: {
                mMenu.toggleMenu(true);
                break;
            }
            case R.id.tv_things: {
                mViewPager.setCurrentItem(0);
                break;
            }
            case R.id.tv_accounts: {
                mViewPager.setCurrentItem(1);
                break;
            }
            case R.id.item_recovery: {
                if (mBackupRestore == null)
                    mBackupRestore = new BackupRestoreUtils(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确定恢复?")
                        .setMessage("所有记录将会还原到备份时的状态")
                        .setPositiveButton("恢复",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        mToast.ShowMsg("正在恢复！", CustomToast.Img_Info);
                                        Values.isRestore_database = true;
                                        int i = mBackupRestore.restore2();
                                        if (i == 1) {
                                            mToast.ShowMsg("恢复成功！",
                                                    CustomToast.Img_Ok);
                                            Intent intent = getPackageManager()
                                                    .getLaunchIntentForPackage(
                                                            getPackageName());
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        if (i == 0) {
                                            mToast.ShowMsg("未找到备份文件！",
                                                    CustomToast.Img_Info);
                                        }
                                        if (i == -1) {
                                            mToast.ShowMsg("恢复失败！",
                                                    CustomToast.Img_Ok);
                                        }
                                        Values.isRestore_database = false;
                                    }
                                }).setNegativeButton("取消", null).show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_backup: {
                if (mBackupRestore == null)
                    mBackupRestore = new BackupRestoreUtils(MainActivity.this);
                if (mBackupRestore.checkexist()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示?")
                            .setMessage("备份已存在，备份将会覆盖原来的数据，仍然备份？")
                            .setPositiveButton("继续备份",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            mToast.ShowMsg("正在备份！",
                                                    CustomToast.Img_Info);
                                            if (mBackupRestore.backup2()) {
                                                mToast.ShowMsg("备份成功！",
                                                        CustomToast.Img_Ok);
                                            }
                                        }
                                    }).setNegativeButton("取消备份", null).show();
                } else {
                    mToast.ShowMsg("正在备份！", CustomToast.Img_Info);
                    if (mBackupRestore.backup2()) {
                        mToast.ShowMsg("备份成功！", CustomToast.Img_Ok);
                    }
                }
                mMenu.closeMenu();
                break;
            }
            case R.id.item_setpwd: {
                mToast.ShowMsg("该功能暂未开放，敬请期待下次更新", CustomToast.Img_Info);
                mMenu.closeMenu();
                break;
            }
            case R.id.item_clean_notes: {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确定删除?")
                        .setMessage("建议删除前先备份数据")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        DBNoteHelper helper = new DBNoteHelper(
                                                MainActivity.this);
                                        helper.Deleteall();
                                        helper.Desdroy();
                                        mToast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                                        Intent intent = getPackageManager()
                                                .getLaunchIntentForPackage(
                                                        getPackageName());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("取消", null).show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_clean_accounts: {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确定删除?")
                        .setMessage("建议删除前先备份数据")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        DBAccountHelper helper = new DBAccountHelper(
                                                MainActivity.this);
                                        helper.Deleteall();
                                        helper.Destroy();
                                        mToast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                                        Intent intent = getPackageManager()
                                                .getLaunchIntentForPackage(
                                                        getPackageName());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("取消", null).show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_about: {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                mMenu.closeMenu();
                break;
            }
            case R.id.item_exit: {
                finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean res = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU: {
                mMenu.toggleMenu(true);
                res = true;
                break;
            }
            case KeyEvent.KEYCODE_BACK: {
                res = mAdapter.dispatchOnKeyDown(keyCode, event);
                if (!res) {
                    if (mBackDownTimes == 1 && System.currentTimeMillis() - mLastBackDownTime < CLICK_DURATION || mBackDownTimes == 0) {
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
                break;
            }
            default: {
                res = mAdapter.dispatchOnKeyDown(keyCode, event);
            }
        }
        return res || super.onKeyDown(keyCode, event);
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
                mThingsTextView.setTextColor(getResources().getColor(
                        R.color.bluedeep_text));
                mAccountsTextView
                        .setTextColor(getResources().getColor(R.color.gray_text));
                break;
            case 1:
                mAccountsTextView.setTextColor(getResources().getColor(
                        R.color.bluedeep_text));
                mThingsTextView.setTextColor(getResources().getColor(R.color.gray_text));
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onGestureClick() {
        if (mMenu.getMenuState() == SlideSectionMenu.State.OPENED) {
            mMenu.closeMenu();
        }
        return true;
    }

    @Override
    public boolean onSlideDown() {
        if (mMenu.getMenuState() == SlideSectionMenu.State.CLOSED) {
            mMenu.openMenu(true);
        }
        return true;
    }

    @Override
    public boolean onSlideUp() {
        if (mMenu.getMenuState() == SlideSectionMenu.State.OPENED) {
            mMenu.closeMenu();
        }
        return true;
    }
}
