package com.rjx.gogu02.adapter;

import com.rjx.gogu02.fragment.TabFragment;

import android.R.string;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

	public static String[] TITLES = new String[] { "全部", "我的关注" };
	
	public static String user_id = "";
	public static String token = "";

	public TabAdapter(FragmentManager fm,String user_id,String token) {
		super(fm);
		this.user_id=user_id;
		this.token=token;
	}

	@Override
	public Fragment getItem(int arg0) {
		TabFragment fragment = new TabFragment(arg0);
		return fragment;
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

}
