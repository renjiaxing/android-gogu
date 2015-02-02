package com.rjx.gogu02.aty;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.service.NotificationService;
import com.rjx.gogu02.utils.ConstantValue;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

public class LogoPicAty extends Activity {
	private String username = "";
	private HttpClient client;
	private String value = "";
	private SharedPreferences sp;
	private String token = "";
	private String uid;
	private String serUrl = ConstantValue.SERVER_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo_login_aty);
		client = new DefaultHttpClient();
		sp = getSharedPreferences("login1", MODE_PRIVATE);
		username = sp.getString("username", "");
		token = sp.getString("token", "");
		uid = sp.getString("user_id", "");

		// 初始化bugly
		Context appContext = this.getApplicationContext();
		String appId = "900001517"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
		boolean isDebug = true; // true代表App处于调试阶段，false代表App发布阶段

		UserStrategy strategy = new UserStrategy(getApplicationContext()); // App的策略Bean
		if ("".equals(uid)) {
			strategy.setAppChannel("unkown"); // 设置渠道
		} else {
			strategy.setAppChannel(uid); // 设置渠道
		}
		strategy.setAppVersion("BuglyDemo_1.0"); // App的版本
		strategy.setAppReportDelay(5000); // 设置SDK处理延时，毫秒

		CrashReport.initCrashReport(appContext, appId, isDebug, strategy); // 初始化SDK

		ImageView iv = (ImageView) findViewById(R.id.logo_login_iv);

		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
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
					RequestParams params = new RequestParams();
					params.put("uid", uid);
					params.put("token", token);
					AsyncHttpClient client = new AsyncHttpClient();
					client.setTimeout(1000);
					client.get(ConstantValue.LOGIN_TOKEN_URL, params,
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getString("result")
												.equals("ok")) {
											Intent it2 = new Intent(
													getApplicationContext(),
													MainActivity.class);
											startActivity(it2);
											finish();
										} else {
											Intent it2 = new Intent(
													getApplicationContext(),
													LoginAty.class);
											startActivity(it2);
											finish();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONObject errorResponse) {
									Intent it2 = new Intent(
											getApplicationContext(),
											LoginAty.class);
									startActivity(it2);
									finish();
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									Intent it2 = new Intent(
											getApplicationContext(),
											LoginAty.class);
									startActivity(it2);
									finish();
								}
							});
				} else {
					Intent it2 = new Intent(getApplicationContext(),
							LoginAty.class);
					startActivity(it2);
					finish();
				}
			}
		});
		iv.setAnimation(animation);

	}

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}

}
