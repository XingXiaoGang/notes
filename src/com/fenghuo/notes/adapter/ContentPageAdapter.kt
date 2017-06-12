package com.fenghuo.notes.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent

import com.fenghuo.notes.AccountFragment
import com.fenghuo.notes.FragmentExt
import com.fenghuo.notes.ThingsFragment

class ContentPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments: Array<FragmentExt?>

    init {
        fragments = arrayOfNulls<FragmentExt>(2)
        fragments[0] = ThingsFragment()
        fragments[1] = AccountFragment()
    }

    fun dispatchOnKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var res = false
        for (fragmentExt in fragments) {
            if (fragmentExt!!.onKeyDown(keyCode, event)) {
                res = true
                break
            }
        }
        return res
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment? {

        return fragments!![position]
    }

}
