package com.rjx.gogu02;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginAty extends Activity {

	private String username = "";
	private String passwd = "";
	private HttpClient client;
	private String value = "";
	private EditText et1;
	private EditText et2;
	private SharedPreferences sp;
	private String token = "";
	private CheckBox cb1;
	private String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginaty);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_login_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
		
		Button lg_btn = (Button) findViewById(R.id.lg_btn);
		Button reg_btn = (Button) findViewById(R.id.req_btn);
		et1 = (EditText) findViewById(R.id.lg_username);
		et2 = (EditText) findViewById(R.id.lg_password);
		cb1 = (CheckBox) findViewById(R.id.lg_checkBox1);

		cb1.setChecked(true);

		client = new DefaultHttpClient();
		sp = getSharedPreferences("login1", MODE_PRIVATE);
//		username = sp.getString("username", "");
//		token = sp.getString("token", "");
//		uid = sp.getString("user_id", "");
//		if (uid != "") {
//			et1.setText(username);
//			et2.setText(token);
//			LoginNet("http://192.168.110.128/login_token_json?uid="
//					+ uid + "&&token=" + token, 6);
//		}

		lg_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				username = et1.getText().toString();
				passwd = et2.getText().toString();

				LoginNet("http://192.168.110.128/login_json?username="
						+ username + "&&passwd=" + passwd, 5);
			}
		});
		
		reg_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it2=new Intent(LoginAty.this,RegAty.class);
				startActivity(it2);
			}
		});

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
			case 5:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						if (cb1.isChecked()) {
							Editor et = sp.edit();
							et.putString("user_id", con.getString("user_id"));
							et.putString("token", con.getString("token"));
							et.commit();
						}
						Intent it2 = new Intent(LoginAty.this,
								MainActivity.class);
						startActivity(it2);
						finish();
					} else {
						showInfo("ÎÞ·¨µÇÂ½");
						et1.setText("");
						et2.setText("");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 6:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						Intent it2 = new Intent(LoginAty.this,
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

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
