package com.fenghuo.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghuo.notes.adapter.ContentPageAdapter;
import com.fenghuo.notes.db.BackupRestoreUtils;
import com.fenghuo.notes.db.DBAccountHelper;
import com.fenghuo.notes.db.DBNoteHelper;
import com.fenghuo.notes.db.PreferenceHelper;
import com.fenghuo.notes.upload.AccountManager;
import com.haibison.android.lockpattern.LockPatternActivity;
import com.mine.view.ViewPagerTab;
import com.mine.view.dialog.ConformDialog;
import com.mine.view.gesture.GestureFrameLayout;
import com.mine.view.gesture.GestureHandler;
import com.mine.view.menu.icon.MaterialMenuDrawable;
import com.mine.view.menu.icon.MaterialMenuView;
import com.mine.view.menu.slide_section_menu.SlideSectionMenu;


public class MainActivity extends FragmentActivity implements View.OnClickListener, GestureHandler.GestureCallBack, ViewPager.OnPageChangeListener, ViewPagerTab.OnPageChangeListener_vp {

    private ViewPager mViewPager;
    private ContentPageAdapter mAdapter;
    private SlideSectionMenu mMenu;

    private TextView mThingsTextView;
    private TextView mAccountsTextView;
    private CustomToast mToast;
    private BackupRestoreUtils mBackupRestore;
    private GestureFrameLayout mGestureFrameLayout;
    private MaterialMenuView mMenuView;

    private long mLastBackDownTime = System.currentTimeMillis();// 时间
    private static final int CLICK_DURATION = 800;
    private int mBackDownTimes = 0;// 在短时间内按下的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMenuView = (MaterialMenuView) findViewById(R.id.material_menu_button);
        ViewPagerTab pagerTab = (ViewPagerTab) findViewById(R.id.vp_tab);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mThingsTextView = (TextView) findViewById(R.id.tv_things);
        mAccountsTextView = (TextView) findViewById(R.id.tv_accounts);
        mMenu = (SlideSectionMenu) findViewById(R.id.menu);
        mGestureFrameLayout = (GestureFrameLayout) findViewById(R.id.container);
        //回调手势
        mGestureFrameLayout.setGestureCallBack(this);

        findViewById(R.id.item_about).setOnClickListener(this);
        findViewById(R.id.item_backup).setOnClickListener(this);
        findViewById(R.id.item_recovery).setOnClickListener(this);
        findViewById(R.id.item_exit).setOnClickListener(this);
        findViewById(R.id.item_clean_accounts).setOnClickListener(this);
        findViewById(R.id.item_clean_notes).setOnClickListener(this);
        findViewById(R.id.item_setpwd).setOnClickListener(this);

        mAdapter = new ContentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        pagerTab.setViewPager(mViewPager);
        pagerTab.setOnPageChangerListener(this);
        mViewPager.setCurrentItem(0, true);

        mToast = new CustomToast(MainActivity.this);
        mMenuView.setOnClickListener(this);
        mAccountsTextView.setOnClickListener(this);
        mThingsTextView.setOnClickListener(this);
        mMenu.post(new Runnable() {
            @Override
            public void run() {
                mMenu.closeMenu();
            }
        });
        PreferenceHelper preferenceHelper = new PreferenceHelper(this);
        if (preferenceHelper.getPatternPwd() != null) {
            ((TextView) findViewById(R.id.item_setpwd)).setText(R.string.del_pwd);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.material_menu_button: {
                mMenu.toggleMenu(true);
                boolean isToOpen = mMenuView.getState() == MaterialMenuDrawable.IconState.BURGER;
                updateMenuState(isToOpen);
                if (isToOpen) {
                    mMenu.openMenu(true);
                } else {
                    mMenu.closeMenu();
                }
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
                new ConformDialog(MainActivity.this, "请选择恢复方式!") {

                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.confirm) {
                            doRestoreCloud();
                        } else if (view.getId() == R.id.cancel) {
                            doRestoreLocal();
                        }
                        dismiss();
                    }
                }.setButtonTextColor(getResources().getColor(R.color.text_444444), getResources().getColor(R.color.text_444444))
                        .setButtonText("本地恢复", "云恢复").show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_backup: {
                new ConformDialog(MainActivity.this, "请选择备份方式!") {

                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.confirm) {
                            doBackUpCloud();
                        } else if (view.getId() == R.id.cancel) {
                            doBackupLocal();
                        }
                        dismiss();
                    }
                }.setButtonTextColor(getResources().getColor(R.color.text_444444), getResources().getColor(R.color.text_444444))
                        .setButtonText("本地备份", "云备份").show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_setpwd: {
                String text = ((TextView) view).getText().toString();
                if (TextUtils.equals(text, getString(R.string.del_pwd))) {
                    new PreferenceHelper(this).delPatternpwd();
                    ((TextView) view).setText(R.string.create_pwd);
                    Toast.makeText(this, R.string.pwd_deleted, Toast.LENGTH_SHORT).show();
                } else {
                    LockPatternActivity.startToCreatePattern(this, this, Values.CODE_SET_LOCK);
                    mMenu.closeMenu();
                }
                break;
            }
            case R.id.item_clean_notes: {

                new ConformDialog(MainActivity.this, "确定删除所有记事?建议删除前先备份数据") {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.confirm) {
                            DBNoteHelper helper = new DBNoteHelper(MainActivity.this);
                            helper.Deleteall();
                            helper.Desdroy();
                            mToast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        dismiss();
                    }
                }.setButtonText("取消", "删除").show();
                mMenu.closeMenu();
                break;
            }
            case R.id.item_clean_accounts: {
                new ConformDialog(MainActivity.this, "确定删除所有记账?建议删除前先备份数据") {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.confirm) {
                            DBAccountHelper helper = new DBAccountHelper(MainActivity.this);
                            helper.Deleteall();
                            helper.Destroy();
                            mToast.ShowMsg("删除成功！", CustomToast.Img_Ok);
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        dismiss();
                    }
                }.setButtonText("取消", "删除").show();
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
                if (mMenu.getMenuState() == SlideSectionMenu.State.OPENED) {
                    mMenu.closeMenu();
                    res = true;
                }
                if (!res) {
                    res = mAdapter.dispatchOnKeyDown(keyCode, event);
                }
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
    protected void onDestroy() {
        super.onDestroy();
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
            updateMenuState(false);
        }
        return true;
    }

    @Override
    public boolean onSlideDown() {
        if (mMenu.getMenuState() == SlideSectionMenu.State.CLOSED) {
            mMenu.openMenu(true);
            updateMenuState(true);
        }
        return true;
    }

    @Override
    public boolean onSlideUp() {
        if (mMenu.getMenuState() == SlideSectionMenu.State.OPENED) {
            mMenu.closeMenu();
            updateMenuState(false);
        }
        return true;
    }

    private void updateMenuState(boolean toOpen) {
        if (toOpen) {
            mMenuView.animateState(MaterialMenuDrawable.IconState.X);
        } else {
            mMenuView.animateState(MaterialMenuDrawable.IconState.BURGER);
        }
    }

    private void doRestoreLocal() {
        if (mBackupRestore == null)
            mBackupRestore = new BackupRestoreUtils(MainActivity.this);
        new ConformDialog(MainActivity.this, "确定恢复?所有记录将会还原到备份时的状态") {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.confirm) {
                    mToast.ShowMsg("正在恢复！", CustomToast.Img_Info);
                    Values.isRestore_database = true;
                    int i = mBackupRestore.restore2();
                    if (i == 1) {
                        mToast.ShowMsg("恢复成功！", CustomToast.Img_Ok);
                        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    if (i == 0) {
                        mToast.ShowMsg("未找到备份文件！", CustomToast.Img_Info);
                    }
                    if (i == -1) {
                        mToast.ShowMsg("恢复失败！", CustomToast.Img_Ok);
                    }
                    Values.isRestore_database = false;
                }
                dismiss();
            }
        }.setButtonText("取消", "恢复").show();
    }

    private void doRestoreCloud() {
        if (AccountManager.getInstance().getUserState() != AccountManager.LOGIN_STATE_LOGIN) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            //todo 下载
        }
    }

    private void doBackupLocal() {
        if (mBackupRestore == null)
            mBackupRestore = new BackupRestoreUtils(MainActivity.this);
        if (mBackupRestore.checkexist()) {
            new ConformDialog(MainActivity.this, "备份已存在，备份将会覆盖原来的数据，仍然备份？") {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.confirm) {
                        mToast.ShowMsg("正在备份！", CustomToast.Img_Info);
                        if (mBackupRestore.backup2()) {
                            mToast.ShowMsg("备份成功！",
                                    CustomToast.Img_Ok);
                        }
                    }
                    dismiss();
                }
            }.setButtonText("取消备份", "继续备份").show();
        } else {
            mToast.ShowMsg("正在备份！", CustomToast.Img_Info);
            if (mBackupRestore.backup2()) {
                mToast.ShowMsg("备份成功！", CustomToast.Img_Ok);
            }
        }
    }

    private void doBackUpCloud() {
        if (AccountManager.getInstance().getUserState() != AccountManager.LOGIN_STATE_LOGIN) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            //todo 上传
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Values.TAG, "onActivityResult: " + requestCode + " , " + resultCode + " , " + data);

        switch (requestCode) {
            case Values.CODE_SET_LOCK: {
                if (data != null) {
                    char[] pwd = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    if (pwd != null && pwd.length > 0) {
                        String savedPwd = new String(pwd);
                        new PreferenceHelper(this).setPatternpwd(savedPwd);
                        ((TextView) findViewById(R.id.item_setpwd)).setText(R.string.del_pwd);
                        Toast.makeText(getApplicationContext(), R.string.pwd_created, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }
}
