package com.rjx.gogu02.aty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ImageLoadTool;

public class PicDetailAty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.pic_detail_aty);

		Bundle bd = getIntent().getBundleExtra("pic");
		String url = bd.getString("url");
		ImageView pic_iv = (ImageView) findViewById(R.id.pic_iv_image);

		ImageLoadTool imageLoadTool=new ImageLoadTool();
		
		imageLoadTool.loadImage(pic_iv, url);
		
		pic_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
