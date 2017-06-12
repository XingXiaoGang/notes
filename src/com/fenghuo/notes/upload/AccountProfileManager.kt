package com.fenghuo.notes.upload

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.annotation.IntDef

import com.fenghuo.notes.LoginActivity

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by gang on 17-2-19.
 * 账户配置管理
 */
class AccountProfileManager private constructor(context: Context) {

    @IntDef(LOGIN_STATE_LOGOUT.toLong(), LOGIN_STATE_LOGIN.toLong())
    @Retention(RetentionPolicy.SOURCE)
    annotation class LOGIN_STATE

    var user: User? = null
        private set
    private val mPreferences: SharedPreferences
    private var mLoginListener: ILoginListener? = null

    fun requestLogin(context: Context, listener: ILoginListener, code: Int) {
        val intent = Intent(context, LoginActivity::class.java)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(AccountProfileManager.EXTRA_REQUEST, code)
        context.startActivity(intent)
        this.mLoginListener = listener
    }

    init {
        mPreferences = context.getSharedPreferences(Config.pref_Name, Context.MODE_PRIVATE)
        val mSavedOpenId = mPreferences.getString(EXTRA_PREFER_OPENID, null)
        if (mSavedOpenId != null) {
            user = User()
            user!!.openId = mSavedOpenId
        }
    }

    fun onLogin(openID: String, request: Int) {
        if (user == null) {
            user = User()
        }
        user!!.openId = openID
        mPreferences.edit().putString(EXTRA_PREFER_OPENID, openID).apply()
        //处理后续任务
        if (mLoginListener != null) {
            mLoginListener!!.onLogin(request)
        }
    }

    val userDbName: String
        get() {
            if (userState != LOGIN_STATE_LOGIN) {
                throw RuntimeException("未登录，请做登录检查 ")
            }
            return user!!.openId!! + "-db"
        }

    val userState: Int
        get() {
            if (user == null) {
                return LOGIN_STATE_LOGOUT
            }
            return user!!.state
        }

    class User {

        var userName: String? = null
        var openId: String? = null
        @LOGIN_STATE
        var state = LOGIN_STATE_LOGIN
    }

    interface ILoginListener {
        fun onLogin(requestCode: Int)
    }

    companion object {

        private var mInstance: AccountProfileManager? = null

        //配置
        const val EXTRA_PREFER_OPENID = "p_openid"

        //登录
        const val EXTRA_REQUEST = "extra.request"
        const val REQUEST_BACKUP = 1
        const val REQUEST_RESTORE = 2

        const val LOGIN_STATE_LOGOUT = 0
        const val LOGIN_STATE_LOGIN = 1

        @Synchronized fun getInstance(context: Context): AccountProfileManager {
            if (mInstance == null) {
                mInstance = AccountProfileManager(context)
            }
            return mInstance as AccountProfileManager
        }
    }
}
