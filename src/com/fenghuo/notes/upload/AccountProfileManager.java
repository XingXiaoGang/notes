package com.fenghuo.notes.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import com.fenghuo.notes.LoginActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gang on 17-2-19.
 * 账户配置管理
 */
public class AccountProfileManager {

    private static AccountProfileManager mInstance;

    //配置
    public static final String EXTRA_PREFER_OPENID = "p_openid";

    //登录
    public static final String EXTRA_REQUEST = "extra.request";
    public static final int REQUEST_BACKUP = 1;
    public static final int REQUEST_RESTORE = 2;

    public static final int LOGIN_STATE_LOGOUT = 0;
    public static final int LOGIN_STATE_LOGIN = 1;

    @IntDef({LOGIN_STATE_LOGOUT, LOGIN_STATE_LOGIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LOGIN_STATE {
    }

    private User mCurrentUser;
    private SharedPreferences mPreferences;
    private ILoginListener mLoginListener;

    public static synchronized AccountProfileManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccountProfileManager(context);
        }
        return mInstance;
    }

    public void requestLogin(Context context, ILoginListener listener, int code) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(AccountProfileManager.EXTRA_REQUEST, code);
        context.startActivity(intent);
        this.mLoginListener = listener;
    }

    private AccountProfileManager(Context context) {
        mPreferences = context.getSharedPreferences(Config.pref_Name, Context.MODE_PRIVATE);
        String mSavedOpenId = mPreferences.getString(EXTRA_PREFER_OPENID, null);
        if (mSavedOpenId != null) {
            mCurrentUser = new User();
            mCurrentUser.openId = mSavedOpenId;
        }
    }

    public void onLogin(String openID, int request) {
        if (mCurrentUser == null) {
            mCurrentUser = new User();
        }
        mCurrentUser.openId = openID;
        mPreferences.edit().putString(EXTRA_PREFER_OPENID, openID).apply();
        //处理后续任务
        if (mLoginListener != null) {
            mLoginListener.onLogin(request);
        }
    }

    public String getUserDbName() {
        if (getUserState() != LOGIN_STATE_LOGIN) {
            throw new RuntimeException("未登录，请做登录检查 ");
        }
        return getUser().openId + "-db";
    }

    public User getUser() {
        return mCurrentUser;
    }

    public int getUserState() {
        if (mCurrentUser == null) {
            return LOGIN_STATE_LOGOUT;
        }
        return mCurrentUser.state;
    }

    public static class User {

        public String userName;
        public String openId;
        @LOGIN_STATE
        public int state = LOGIN_STATE_LOGIN;
    }

    public static interface ILoginListener {
        void onLogin(int requestCode);
    }
}
