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
        btn_back = findViewById(R.id.btn_back_about)
        rl_app = findViewById(R.id.rl_app)
        rl_art = findViewById(R.id.rl_art)
        rl_fuction = findViewById(R.id.rl_funvtion)
        rl_tjfh = findViewById(R.id.rl_tjfh)
        rl_update = findViewById(R.id.rl_update) 
    }

    override fun onClick(arg0: View) {
        when (arg0.id) {
            R.id.btn_back_about -> finish()
            R.id.rl_app -> toast!!.ShowMsg(getString(R.string.about), CustomToast.Img_Info)
            R.id.rl_art -> toast!!.ShowMsg(getString(R.string.about), CustomToast.Img_Info)
            R.id.rl_funvtion -> toast!!.ShowMsg(getString(R.string.about), CustomToast.Img_Info)
            R.id.rl_tjfh -> toast!!.ShowMsg(getString(R.string.about), CustomToast.Img_Info)
            R.id.rl_update -> toast!!.ShowMsg(getString(R.string.about), CustomToast.Img_Info)
            else -> {
            }
        }
    }
}
