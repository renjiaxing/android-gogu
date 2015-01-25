package com.rjx.gogu02.aty;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ImageLoadTool;

public class DelPicAty extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_detail_aty);
		
		Bundle bd=getIntent().getBundleExtra("pic");
		String url=bd.getString("url");
		
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(
				R.layout.custom_picdel_actoin_bar, null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
		
		ImageView iv_back=(ImageView) findViewById(R.id.nm_iv_logo_back);
		ImageView iv_del=(ImageView) findViewById(R.id.nm_iv_picdel);
		ImageView iv_pic=(ImageView) findViewById(R.id.pic_iv_image);
		
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		ImageLoadTool imageLoadTool=new ImageLoadTool();
		imageLoadTool.loadImage(iv_pic, url);
		
		iv_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}

}
