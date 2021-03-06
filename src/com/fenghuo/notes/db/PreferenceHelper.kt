package com.fenghuo.notes.db

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val mPreferences: SharedPreferences

    init {
        mPreferences = context.getSharedPreferences("temp", Activity.MODE_PRIVATE)
    }

    /**
     * 读取上一次的草稿

     * @return
     */
    val last: String
        get() {
            return mPreferences.getString("last", "")
        }

    /**
     * 保存草稿

     * @param str
     */
    fun saveLast(str: String) {
        val editor = mPreferences.edit()
        editor.putString("last", str)
        editor.apply()
    }

    fun removeLastDraft() {
        val editor = mPreferences.edit()
        editor.remove("last");
        editor.apply()
    }

    fun saveLastWithID(id: Long, str: String) {
        val editor = mPreferences.edit()
        editor.putString("last_draft_with_id:$id", str);
        editor.apply()
    }

    fun getLastDraftById(id: Long): String? {
        return mPreferences.getString("last_draft_with_id:$id", null);
    }

    fun removeLastDraft(id: Long) {
        val editor = mPreferences.edit()
        editor.remove("last_draft_with_id:$id");
        editor.apply()
    }

    val patternPwd: String?
        get() = mPreferences.getString(KEY_PWD, null)

    fun setPatternpwd(pwd: String) {
        mPreferences.edit().putString(KEY_PWD, pwd).apply()
    }

    fun delPatternpwd() {
        mPreferences.edit().remove(KEY_PWD).apply()
    }

    companion object {

        private val KEY_PWD = "k_pwd"
    }
}
