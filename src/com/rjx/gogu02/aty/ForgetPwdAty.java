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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ForgetPwdAty extends Activity {

	private EditText emailEt;

	private String value = "";
	private HttpClient client;
	private String serUrl = ConstantValue.SERVER_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpwd_aty);

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

		emailEt = (EditText) findViewById(R.id.rg_email_et);

		Button reg_btn = (Button) findViewById(R.id.rg_btn);
		Button reg_back = (Button) findViewById(R.id.rg_back);

		client = new DefaultHttpClient();

		reg_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
				if (!(pattern.matcher(emailEt.getText().toString()).matches())) {
					showInfo("请输入正确的邮件地址");
				} else {

					RequestParams params = new RequestParams();
					params.put("email", emailEt.getText().toString());
					AsyncHttpClient client = new AsyncHttpClient();
					client.setTimeout(1000);
					client.get(ConstantValue.FORGET_PASSWORD_URL,
							params, new JsonHttpResponseHandler() {
						
								@Override
								public void onFailure(int statusCode,
										Header[] headers, String responseString,
										Throwable throwable) {
									showInfo("发送重置邮件失败，请稍后再试~");
									super.onFailure(statusCode, headers, responseString, throwable);
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONArray errorResponse) {
									showInfo("发送重置邮件失败，请稍后再试~");
									super.onFailure(statusCode, headers, throwable,
											errorResponse);
								}

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getString("result").equals(
												"ok")) {
											showInfo("请登陆邮箱重置密码！");
											finish();
										} else if (response.getString("checkemail")
												.equals("nook")) {
											showInfo("输入邮箱不存在~");
										} else {
											showInfo("发送重置邮件失败，请稍后再试~");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
				
				}
			}
		});

		reg_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent iv2 = new Intent(ForgetPwdAty.this, LoginAty.class);
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
