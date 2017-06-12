package com.fenghuo.notes

import android.support.v4.app.Fragment
import android.view.KeyEvent

/**
 * @author Administrator 添加事件接口的FragMent
 */
open class FragmentExt : Fragment(), KeyEvent.Callback {

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyMultiple(keyCode: Int, count: Int, event: KeyEvent): Boolean {
        return false
    }
}
