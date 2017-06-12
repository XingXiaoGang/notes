package com.fenghuo.notes

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class CustomToast(private val context: Context) {
    private var toast: Toast? = null
    private val inflater: LayoutInflater
    private var view: View? = null
    private var toast_img: ImageView? = null
    private var toast_msg: TextView? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    fun ShowMsg(msg: String, kind: Int) {
        if (toast == null) {
            toast = Toast(context)
            view = inflater.inflate(R.layout.toast, null)
            toast_img = view!!.findViewById(R.id.toast_img) as ImageView
            toast_msg = view!!.findViewById(R.id.toast_tv) as TextView
            toast!!.view = view
        }

        when (kind) {
            Img_Ok -> toast_img!!.setImageResource(R.drawable.toast_ok)
            Img_Erro -> toast_img!!.setImageResource(R.drawable.toast_erro)
            Img_Info -> toast_img!!.setImageResource(R.drawable.toast_info)
            else -> {
            }
        }

        toast_msg!!.text = msg
        toast!!.view = view
        toast!!.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast!!.view = view
        toast!!.show()
    }

    fun cancel() {
        if (toast != null) {
            toast!!.cancel()
        }
    }

    companion object {

        val Img_Ok = 1
        val Img_Erro = 3
        val Img_Info = 2
    }
}
