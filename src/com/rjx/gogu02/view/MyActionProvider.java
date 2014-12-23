package com.rjx.gogu02.view;

import com.rjx.gogu02.R;
import com.rjx.gogu02.R.drawable;

import android.content.Context;

import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;

public class MyActionProvider extends ActionProvider {
	
	public MyActionProvider(Context context) {
		super(context);
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add("sub item 1").setIcon(R.drawable.ic_launcher)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return true;
					}
				});
		subMenu.add("sub item 2").setIcon(R.drawable.ic_launcher)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

}
