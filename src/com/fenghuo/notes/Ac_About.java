package com.fenghuo.notes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Ac_About extends Activity implements OnClickListener {

    private Button btn_back;
    private RelativeLayout rl_app;
    private RelativeLayout rl_art;
    private RelativeLayout rl_fuction;
    private RelativeLayout rl_tjfh;
    private RelativeLayout rl_update;
    private CustomToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findviews();

        toast = new CustomToast(Ac_About.this);

        btn_back.setOnClickListener(this);
        rl_app.setOnClickListener(this);
        rl_art.setOnClickListener(this);
        rl_fuction.setOnClickListener(this);
        rl_tjfh.setOnClickListener(this);
        rl_update.setOnClickListener(this);
    }

    private void findviews() {
        btn_back = (Button) findViewById(R.id.btn_back_about);
        rl_app = (RelativeLayout) findViewById(R.id.rl_app);
        rl_art = (RelativeLayout) findViewById(R.id.rl_art);
        rl_fuction = (RelativeLayout) findViewById(R.id.rl_funvtion);
        rl_tjfh = (RelativeLayout) findViewById(R.id.rl_tjfh);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_back_about:
                finish();
                break;
            case R.id.rl_app:
                toast.ShowMsg("关于", CustomToast.Img_Info);
                break;
            case R.id.rl_art:
                toast.ShowMsg("关于", CustomToast.Img_Info);
                break;
            case R.id.rl_funvtion:
                toast.ShowMsg("关于", CustomToast.Img_Info);
                break;
            case R.id.rl_tjfh:
                toast.ShowMsg("关于", CustomToast.Img_Info);
                break;
            case R.id.rl_update:
                toast.ShowMsg("关于", CustomToast.Img_Info);
                break;
            default:
                break;
        }
    }
}
