package com.rjx.gogu02.aty;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.NetworkResources;

public class LogoPicAty extends Activity {
	private String username = "";
	private HttpClient client;
	private String value = "";
	private SharedPreferences sp;
	private String token = "";
	private String uid;
	private String serUrl=ConstantValue.SERVER_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo_login_aty);
		client = new DefaultHttpClient();
		sp = getSharedPreferences("login1", MODE_PRIVATE);
		username = sp.getString("username", "");
		token = sp.getString("token", "");
		uid = sp.getString("user_id", "");		

		
		ImageView iv=(ImageView) findViewById(R.id.logo_login_iv);
		
		AlphaAnimation animation=new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(2000);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if (uid != "") {
					LoginNet(serUrl+"login_token_json?uid="
							+ uid + "&&token=" + token, 6);
				}else {
					Intent it2=new Intent(getApplicationContext(), LoginAty.class);
					startActivity(it2);
					finish();
				}
			}
		});
		iv.setAnimation(animation);
		
	}
	
	public void LoginNet(String url, Integer mod) {
		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];
				Integer mod = (Integer) params[1];

				HttpGet get = new HttpGet(urlString);

				Message msg = new Message();
				msg.what = mod;

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());
					handler.sendMessage(msg);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;

			}

		}.execute(url, mod);

	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case 6:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						Intent it2 = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(it2);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		};
	};

}
