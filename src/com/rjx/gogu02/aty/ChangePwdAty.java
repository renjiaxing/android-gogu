package com.rjx.gogu02.aty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

public class ChangePwdAty extends Activity {

	private SharedPreferences sp;
	private String user_id = "";
	private String token = "";
	private String value = "";
	private EditText oldpwd_et, newpwd_et, newpwd_repeat_et;
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepwd_aty);

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

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");
		
		title_tv=(TextView) findViewById(R.id.custom_actionbar_title);
		title_tv.setText("修改密码");

		oldpwd_et = (EditText) findViewById(R.id.changepwd_oldpwd_et);
		newpwd_et = (EditText) findViewById(R.id.changepwd_newpwd_et);
		newpwd_repeat_et = (EditText) findViewById(R.id.changepwd_newpwd_repeat_et);

		ImageView back_iv = (ImageView) findViewById(R.id.common_logo_back);
		Button send_bt = (Button) findViewById(R.id.changepwd_send);
		Button back_bt = (Button) findViewById(R.id.changepwd_back);

		send_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (newpwd_et.getText().toString().equals("")) {
					showInfo("密码不能为空");
				} else if (!newpwd_et.getText().toString()
						.equals(newpwd_repeat_et.getText().toString())) {
					showInfo("请确认密码输入一致");
				}

				String oldpwd = oldpwd_et.getText().toString();
				String newpwd = newpwd_et.getText().toString();
				RequestParams params = new RequestParams();
				params.put("uid", user_id);
				params.put("token", token);
				params.put("oldpwd", oldpwd);
				params.put("newpwd", newpwd);
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(1000);
				client.post(ConstantValue.CHANGE_PASSWORD_URL,
						params, new JsonHttpResponseHandler() {
					
							@Override
							public void onFailure(int statusCode,
									Header[] headers, String responseString,
									Throwable throwable) {
								showInfo("密码修改失败");
								super.onFailure(statusCode, headers, responseString, throwable);
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONArray errorResponse) {
								showInfo("密码修改失败");
								super.onFailure(statusCode, headers, throwable,
										errorResponse);
							}

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response.getString("result").equals(
											"ok")) {
										finish();
									} else if (response.getString("result")
											.equals("pwdnook")) {
										showInfo("旧密码输入错误");
									} else {
										showInfo("密码修改失败");
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

			}
		});

		back_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		back_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
