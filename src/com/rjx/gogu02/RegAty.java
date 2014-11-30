package com.rjx.gogu02;

import java.io.IOException;
import java.util.regex.Pattern;

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
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegAty extends Activity {
	private EditText nameEt;
	private EditText emailEt;
	private EditText phoneEt;
	private EditText passwdEt;
	private EditText passwdcEt;
	private String value = "";
	private HttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_aty);
		
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

		nameEt = (EditText) findViewById(R.id.rg_name_et);
		emailEt = (EditText) findViewById(R.id.rg_email_et);
		phoneEt = (EditText) findViewById(R.id.rg_phone_et);
		passwdEt = (EditText) findViewById(R.id.rg_passwd_et);
		passwdcEt = (EditText) findViewById(R.id.rg_passwd_et2);

		Button reg_btn = (Button) findViewById(R.id.rg_btn);
		Button reg_back = (Button) findViewById(R.id.rg_back);

		client = new DefaultHttpClient();

		reg_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
				if (nameEt.getText().toString().equals("")) {
					showInfo("名字不能为空！");
				} else if (!(pattern.matcher(emailEt.getText().toString())
						.matches())) {
					showInfo("请输入正确的邮件地址！");
				} else if (passwdEt.getText().toString().equals("")) {
					showInfo("密码不能为空！");
				} else if (!passwdEt.getText().toString()
						.equals(passwdcEt.getText().toString())) {
					showInfo("请确认密码输入一致！");
				} else {

					RegNet("http://121.41.25.221/reg_json?name="
							+ nameEt.getText().toString() + "&&email="
							+ emailEt.getText().toString() + "&&phone="
							+ phoneEt.getText().toString() + "&&passwd="
							+ passwdEt.getText().toString(), 7);
				}
			}
		});

		reg_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent iv2 = new Intent(RegAty.this, LoginAty.class);
				startActivity(iv2);
				finish();
			}
		});

	}

	public void RegNet(String url, Integer mod) {
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
			case 7:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("checkemail").equals("ok")) {
						if (con.getString("result").equals("ok")) {
							showInfo("用户创建成功！");
							Intent it2 = new Intent(RegAty.this, LoginAty.class);
							startActivity(it2);
							finish();
						} else {
							showInfo("用户创建失败！");
						}
					} else {
							showInfo("邮件已经存在！");
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
