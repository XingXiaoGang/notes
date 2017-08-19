package com.fenghuo.notes.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import com.fenghuo.notes.AccountFragment
import com.fenghuo.notes.FragmentExt
import com.fenghuo.notes.RecordFragment
import com.fenghuo.notes.ThingsFragment

class ContentPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fm: FragmentManager = fm;

    fun dispatchOnKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var res = false
        kotlin.run out@ {
            fm.fragments.forEach {
                if ((it as FragmentExt).onKeyDown(keyCode, event)) {
                    res = true;
                    return@out
                }
            }
        }
        return res
    }

    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return ThingsFragment();
            1 -> return AccountFragment();
            2 -> return RecordFragment();
            else -> throw RuntimeException("please check getCount")
        }
    }
}
