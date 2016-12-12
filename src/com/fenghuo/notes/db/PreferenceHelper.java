package com.fenghuo.notes.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String KEY_PWD = "k_pwd";

    private SharedPreferences mPreferences;

    public PreferenceHelper(Context context) {
        mPreferences = context.getSharedPreferences("temp", Activity.MODE_PRIVATE);
    }

    /**
     * 读取上一次的草稿
     *
     * @return
     */
    public String getLast() {
        String str = "";
        str = mPreferences.getString("last", "");
        return str;
    }

    /**
     * 保存草稿
     *
     * @param str
     */
    public void saveLast(String str) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("last", str);
        editor.apply();
    }

    public String getPatternPwd() {
        return mPreferences.getString(KEY_PWD, null);
    }

    public void setPatternpwd(String pwd) {
        mPreferences.edit().putString(KEY_PWD, pwd).apply();
    }

    public void delPatternpwd() {
        mPreferences.edit().remove(KEY_PWD).apply();
    }
}
