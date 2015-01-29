package com.rjx.gogu02.aty;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rjx.gogu02.R;

public class SettingsAty extends Activity {
	private SharedPreferences sp;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_aty);

		sp = getSharedPreferences("login1", MODE_PRIVATE);

		editor = sp.edit();

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_back_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

		Integer sys_status = sp.getInt("sys_status", 1);
		Integer reply_status = sp.getInt("reply_status", 1);
		Integer msg_status = sp.getInt("msg_status", 1);

		ImageView back_iv = (ImageView) findViewById(R.id.common_logo_back);
		
		TextView tv_advice=(TextView) findViewById(R.id.setting_advice);
		
		tv_advice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(SettingsAty.this,AdviceAty.class);
				startActivity(it);
			}
		});

		back_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ToggleButton sys_tb = (ToggleButton) findViewById(R.id.set_sys_switch);
		if (sys_status == 1) {
			sys_tb.setChecked(true);
		} else {
			sys_tb.setChecked(false);
		}

		sys_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					// mod=2;
					// Intent it=new Intent();
					// it.setAction("com.rjx.gogu.PUSHINFO");
					// it.putExtra("mod", mod);
					// sendBroadcast(it);
					editor.putInt("sys_status", 1);
					editor.commit();
				} else {
					// mod=1;
					// Intent it=new Intent();
					// it.setAction("com.rjx.gogu.PUSHINFO");
					// it.putExtra("mod", mod);
					// sendBroadcast(it);
					editor.putInt("sys_status", 0);
					editor.commit();
				}
			}
		});

		ToggleButton reply_tb = (ToggleButton) findViewById(R.id.set_reply_switch);
		if (reply_status == 1) {
			reply_tb.setChecked(true);
		} else {
			reply_tb.setChecked(false);
		}

		reply_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					editor.putInt("reply_status", 1);
					editor.commit();
				} else {

					editor.putInt("reply_status", 0);
					editor.commit();
				}
			}
		});

		ToggleButton msg_tb = (ToggleButton) findViewById(R.id.set_msg_switch);
		if (msg_status == 1) {
			msg_tb.setChecked(true);
		} else {
			msg_tb.setChecked(false);
		}

		msg_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					editor.putInt("msg_status", 1);
					editor.commit();
				} else {

					editor.putInt("msg_status", 0);
					editor.commit();
				}
			}
		});

		try {

			TextView version_tv = (TextView) findViewById(R.id.setting_sys_version);
			version_tv.setText(this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
