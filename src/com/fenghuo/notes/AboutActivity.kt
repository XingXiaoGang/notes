package com.fenghuo.notes

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.RelativeLayout

class AboutActivity : Activity(), OnClickListener {

    private var btn_back: Button? = null
    private var rl_app: RelativeLayout? = null
    private var rl_art: RelativeLayout? = null
    private var rl_fuction: RelativeLayout? = null
    private var rl_tjfh: RelativeLayout? = null
    private var rl_update: RelativeLayout? = null
    private var toast: CustomToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findviews()

        toast = CustomToast(this@AboutActivity)

        btn_back!!.setOnClickListener(this)
        rl_app!!.setOnClickListener(this)
        rl_art!!.setOnClickListener(this)
        rl_fuction!!.setOnClickListener(this)
        rl_tjfh!!.setOnClickListener(this)
        rl_update!!.setOnClickListener(this)
    }

    private fun findviews() {
        btn_back = findViewById(R.id.btn_back_about) as Button
        rl_app = findViewById(R.id.rl_app) as RelativeLayout
        rl_art = findViewById(R.id.rl_art) as RelativeLayout
        rl_fuction = findViewById(R.id.rl_funvtion) as RelativeLayout
        rl_tjfh = findViewById(R.id.rl_tjfh) as RelativeLayout
        rl_update = findViewById(R.id.rl_update) as RelativeLayout
    }

    override fun onClick(arg0: View) {
        when (arg0.id) {
            R.id.btn_back_about -> finish()
            R.id.rl_app -> toast!!.ShowMsg("关于", CustomToast.Img_Info)
            R.id.rl_art -> toast!!.ShowMsg("关于", CustomToast.Img_Info)
            R.id.rl_funvtion -> toast!!.ShowMsg("关于", CustomToast.Img_Info)
            R.id.rl_tjfh -> toast!!.ShowMsg("关于", CustomToast.Img_Info)
            R.id.rl_update -> toast!!.ShowMsg("关于", CustomToast.Img_Info)
            else -> {
            }
        }
    }
}
