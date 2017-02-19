package com.fenghuo.notes.upload;

import android.os.Handler;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gang on 17-2-19.
 */
public class AccountManager {

    private static AccountManager mInstance;

    public static final int LOGIN_STATE_LOGOUT = 0;
    public static final int LOGIN_STATE_LOGIN = 1;

    @IntDef({LOGIN_STATE_LOGOUT, LOGIN_STATE_LOGIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LOGIN_STATE {
    }

    private User mCurrentUser;

    public static synchronized AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public void regist(String name, String pwd, Handler handler) {

    }

    public void login(String name, String pwd, Handler handler) {

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
        public String userPwd;
        @LOGIN_STATE
        public int state;
    }
}
