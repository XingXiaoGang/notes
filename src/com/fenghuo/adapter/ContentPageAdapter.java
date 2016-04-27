package com.fenghuo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;

import com.fenghuo.notes.AccountFragment;
import com.fenghuo.notes.FragmentExt;
import com.fenghuo.notes.ThingsFragment;

public class ContentPageAdapter extends FragmentPagerAdapter {

	private FragmentExt[] fragments;

	public ContentPageAdapter(FragmentManager fm) {
		super(fm);
		fragments = new FragmentExt[2];
		fragments[0] = new ThingsFragment();
		fragments[1] = new AccountFragment();
	}

	public boolean dispatchOnKeyDown(int keyCode, KeyEvent event) {
		boolean res = false;
		for (FragmentExt fragmentExt : fragments) {
			if (fragmentExt.onKeyDown(keyCode, event)) {
				res = true;
				break;
			}
		}
		return res;
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public Fragment getItem(int position) {

		return fragments[position];
	}

}
