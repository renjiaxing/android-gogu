package com.rjx.gogu02.aty;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

public class RegAty extends Activity {
//	private EditText codeEt;
	private EditText emailEt;
	private EditText phoneEt;
	private EditText passwdEt;
	private EditText passwdcEt;
	private String value = "";
	private HttpClient client;
	private String serUrl = ConstantValue.SERVER_URL;

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

//		codeEt = (EditText) findViewById(R.id.rg_code_et);
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
//				if (codeEt.getText().toString().equals("")) {
//					showInfo("验证码不能为空");
			   if (!(pattern.matcher(emailEt.getText().toString())
						.matches())) {
					showInfo("请输入正确的邮件地址");
				} else if (passwdEt.getText().toString().equals("")) {
					showInfo("密码不能为空");
				} else if (!passwdEt.getText().toString()
						.equals(passwdcEt.getText().toString())) {
					showInfo("请确认密码输入一致");
				} else {
					RequestParams params = new RequestParams();
//					params.put("code", codeEt.getText().toString());
					params.put("email", emailEt.getText().toString());
					params.put("phone", phoneEt.getText().toString());
					params.put("passwd", passwdEt.getText().toString());
					AsyncHttpClient client = new AsyncHttpClient();
					client.setTimeout(1000);
					client.post(ConstantValue.REG_URL, params,
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
//										if(!(response.getString("checkcode").equals("ok"))){
//											showInfo("邀请码错误~");
										 if(response.getString("checkemail")
												.equals("ok")) {
											if (response.getString("result")
													.equals("ok")) {
												showInfo("用户创建成功！");
												Intent it2 = new Intent(
														RegAty.this,
														LoginAty.class);
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
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONObject errorResponse) {
									showInfo("无法注册,请稍后再试~");
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									showInfo("无法注册,请稍后再试~");
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});

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

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
