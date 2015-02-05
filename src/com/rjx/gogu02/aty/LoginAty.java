package com.rjx.gogu02.aty;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.domain.Comments;
import com.rjx.gogu02.service.NotificationService;
import com.rjx.gogu02.update.UpdateManager;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.NetworkResources;

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
import android.widget.TextView;
import android.widget.Toast;

public class LoginAty extends Activity {

	private String username = "";
	private String passwd = "";
	private EditText et1;
	private EditText et2;
	private TextView tv_forgetpwd;
	private SharedPreferences sp;
	private CheckBox cb1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginaty);
		
		Intent serIntent=new Intent(this, NotificationService.class);
		startService(serIntent);
		
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
		tv_forgetpwd=(TextView) findViewById(R.id.lg_forgetpwd);

		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkUpdateInfo();
		cb1.setChecked(true);

		sp = getSharedPreferences("login1", MODE_PRIVATE);

		lg_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				username = et1.getText().toString();
				passwd = et2.getText().toString();
				
				RequestParams params = new RequestParams();
				params.put("username", username);
				params.put("passwd", passwd);
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(1000);
				client.get(ConstantValue.LOGIN_URL, params,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response.getString("result").equals("ok")) {
										if (cb1.isChecked()) {
											Editor et = sp.edit();
											et.putString("user_id", response.getString("user_id"));
											et.putString("token", response.getString("token"));
											et.putString("username", username);
											et.commit();
										}
										Intent it2 = new Intent(LoginAty.this,
												MainActivity.class);
										startActivity(it2);
										finish();
									} else {
										showInfo("用户名或者密码错误");
										et1.setText("");
										et2.setText("");
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONObject errorResponse) {
								showInfo("无法登陆,请稍后再试~");
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers,
									String responseString,
									Throwable throwable) {
								showInfo("无法登陆,请稍后再试~");
								super.onFailure(statusCode, headers,
										responseString, throwable);
							}
						});
			}
		});
		
		reg_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it2=new Intent(LoginAty.this,RegAty.class);
				startActivity(it2);
			}
		});
		
		tv_forgetpwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it3=new Intent(LoginAty.this,ForgetPwdAty.class);
				startActivity(it3);
			}
		});

	}

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
