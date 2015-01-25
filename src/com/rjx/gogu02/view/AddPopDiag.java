package com.rjx.gogu02.view;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.LoginAty;
import com.rjx.gogu02.aty.MyMicropostAty;
import com.rjx.gogu02.aty.SettingsAty;

public class AddPopDiag extends PopupWindow {

	private View contentView;
	private SharedPreferences sp;

	public AddPopDiag(final Activity context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.popup_main_more_diag, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		sp=context.getSharedPreferences("login1", context.MODE_PRIVATE);

		this.setContentView(contentView);
		this.setWidth(w / 2 + 50);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		ColorDrawable dw = new ColorDrawable(00000000);
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.AnimationPreview);
//		TextView mn_diag_1 = (TextView) contentView
//				.findViewById(R.id.mn_diag_1);
		TextView mn_diag_2 = (TextView) contentView
				.findViewById(R.id.mn_diag_logout);
		TextView mn_bs=(TextView) contentView.findViewById(R.id.mn_settings);
		
//		BadgeView badge= new BadgeView(context);
//		badge.setTargetView(mn_diag_1);
		
		
		mn_bs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(context,SettingsAty.class);
				context.startActivity(it);
				AddPopDiag.this.dismiss();
			}
		});
		
//		mn_diag_1.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Intent it1 = new Intent(context, MyMicropostAty.class);
//				context.startActivity(it1);
//				AddPopDiag.this.dismiss();
//			}
//		});
		mn_diag_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor ed=sp.edit();
				ed.clear();
				ed.commit();
				Intent it2 = new Intent(context, LoginAty.class);
				context.startActivity(it2);
				AddPopDiag.this.dismiss();
				context.finish();
			}
		});
	}
	
	 public void showPopupWindow(View parent) {
	        if (!this.isShowing()) {
	            this.showAsDropDown(parent, parent.getLayoutParams().width / 3, 18);
	        } else {
	            this.dismiss();
	        }
	    }

}
