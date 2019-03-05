package com.fenghuo.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.fenghuo.notes.upload.AccountProfileManager
import com.fenghuo.notes.upload.Config
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by xingxiaogang on 2017/2/20.
 */

class LoginActivity : Activity(), View.OnClickListener, IUiListener {
    private var mTencent: Tencent? = null
    private var mRequestCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.btn_login_qq).setOnClickListener(this)

        mRequestCode = intent.getIntExtra(AccountProfileManager.EXTRA_REQUEST, 0)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login_qq -> {
                doLogin()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.handleResultData(data, this)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Synchronized private fun doLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Config.APP_ID, this.applicationContext)
        }
        //
        mTencent!!.login(this, "", this)
    }

    override fun onComplete(o: Any) {
        Log.d(TAG, "onComplete:" + o)
        //{"ret":0,"openid":"4BE3F4095635AA28A6F72B583ACE879C","access_token":"34ADF58B8834158A64148B4D5C07ECBD","pay_token":"3AAD510728DAAC4FF8B88D1C33E5DBA2","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"e4218a9315037c22abd28aa151b2bb68","msg":"","login_cost":433,"query_authority_cost":433,"authority_cost":-132092761}
        try {
            val jsonObject = JSONObject(o.toString())
            var rec = -1
            var openId: String? = null
            if (jsonObject.has("ret")) {
                rec = jsonObject.getInt("ret")
            }
            if (jsonObject.has("openid")) {
                openId = jsonObject.getString("openid")
            }
            if (rec == 0) {
                Log.d(LoginActivity.TAG, "onLogin: 登录成功")
                AccountProfileManager.getInstance(this).onLogin(openId!!, mRequestCode)
                finish()
            } else {
                Toast.makeText(this, "登录失败 ret:$rec\no=$o", Toast.LENGTH_LONG).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "解析登录结果失败 :${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onError(uiError: UiError) {
        Toast.makeText(this, "登录失败:code=" + uiError.errorCode + " msg=" + uiError.errorMessage + " detail=" + uiError.errorDetail, Toast.LENGTH_LONG).show()
        Log.d(TAG, "onError: " + "onError:code=" + uiError.errorCode + " msg=" + uiError.errorMessage + " detail=" + uiError.errorDetail)
    }

    override fun onCancel() {
        Log.d(TAG, "onCancel: ")
    }

    companion object {

        val TAG = "LoginActivity"
    }
}
