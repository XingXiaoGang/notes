package com.fenghuo.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by xingxiaogang on 2017/2/20.
 */

public class LoginActivity extends Activity implements View.OnClickListener, IUiListener {

    private static final String TAG = "LoginActivity";
    private static final String APP_ID = "1105989781";
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login_qq).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_qq: {
                doLogin();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.handleResultData(data, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private synchronized void doLogin() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        }
        mTencent.login(this, "123123", this);
    }

    @Override
    public void onComplete(Object o) {
        Toast.makeText(this, "omComplete:" + o, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onComplete:" + o);
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(this, "onError:code=" + uiError.errorCode + " msg=" + uiError.errorMessage + " detail=" + uiError.errorDetail, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onError: " + "onError:code=" + uiError.errorCode + " msg=" + uiError.errorMessage + " detail=" + uiError.errorDetail);
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel: ");
    }
}
