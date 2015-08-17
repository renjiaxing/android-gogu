package com.rjx.gogu02.aty;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsAty extends Activity {
	private SharedPreferences sp;
	SharedPreferences.Editor editor;
	private String uid="";
	private String token="";
	private ToggleButton sys_tb;
	private ToggleButton msg_tb;
	private ToggleButton reply_tb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_aty);

		sp = getSharedPreferences("login1", MODE_PRIVATE);

		editor = sp.edit();
		String username=sp.getString("username", "");

		uid = sp.getString("user_id", "");
		token = sp.getString("token", "");

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

		String micro_status = sp.getString("android_micro_push", "true");
		String reply_status = sp.getString("android_reply_push", "true");
		String msg_status = sp.getString("android_chat_push", "true");

		ImageView back_iv = (ImageView) findViewById(R.id.common_logo_back);
		
//		TextView tv_advice=(TextView) findViewById(R.id.setting_advice);
//		TextView tv_changepwd=(TextView) findViewById(R.id.setting_changepwd);
		
		TextView title_tv=(TextView) findViewById(R.id.custom_actionbar_title);
		title_tv.setText("配置");
		
		TextView settings_username_tv=(TextView) findViewById(R.id.settings_username);
		settings_username_tv.setText(username);
		
		RelativeLayout lo_changepwd=(RelativeLayout) findViewById(R.id.setting_changepwd_relativelayout);
		RelativeLayout lo_advice=(RelativeLayout) findViewById(R.id.setting_advice_relativelayout);
		
		lo_changepwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(SettingsAty.this,ChangePwdAty.class);
				startActivity(it);
			}
		});
		
		lo_advice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(SettingsAty.this,AdviceAty.class);
				startActivity(it);
			}
		});

		back_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		sys_tb = (ToggleButton) findViewById(R.id.set_sys_switch);
		if (micro_status.equals("true")) {
			sys_tb.setChecked(true);
		} else {
			sys_tb.setChecked(false);
		}

		sys_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				AsyncHttpClient httpClient = new AsyncHttpClient();

				RequestParams params = new RequestParams();
				params.put("uid", uid);
				params.put("token", token);

				httpClient.setTimeout(3000);


				if (isChecked) {
					// mod=2;
					// Intent it=new Intent();
					// it.setAction("com.rjx.gogu.PUSHINFO");
					// it.putExtra("mod", mod);
					// sendBroadcast(it);
//					editor.putInt("sys_status", 1);
//					editor.commit();

					httpClient.post(ConstantValue.ACTIVE_ANDROID_MICRO_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											sys_tb.setChecked(false);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									sys_tb.setChecked(false);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				} else {
					// mod=1;
					// Intent it=new Intent();
					// it.setAction("com.rjx.gogu.PUSHINFO");
					// it.putExtra("mod", mod);
					// sendBroadcast(it);
//					editor.putInt("sys_status", 0);
//					editor.commit();

					httpClient.post(ConstantValue.DEACTIVE_ANDROID_MICRO_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											sys_tb.setChecked(true);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									sys_tb.setChecked(true);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				}
			}
		});

		reply_tb = (ToggleButton) findViewById(R.id.set_reply_switch);
		if (reply_status.equals("true")) {
			reply_tb.setChecked(true);
		} else {
			reply_tb.setChecked(false);
		}

		reply_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AsyncHttpClient httpClient = new AsyncHttpClient();

				RequestParams params = new RequestParams();
				params.put("uid", uid);
				params.put("token", token);

				httpClient.setTimeout(3000);

				if (isChecked) {
//					editor.putInt("reply_status", 1);
//					editor.commit();
					httpClient.post(ConstantValue.ACTIVE_ANDROID_REPLY_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											reply_tb.setChecked(false);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									reply_tb.setChecked(false);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				} else {

//					editor.putInt("reply_status", 0);
//					editor.commit();
					httpClient.post(ConstantValue.DEACTIVE_ANDROID_REPLY_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											reply_tb.setChecked(true);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									reply_tb.setChecked(true);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				}
			}
		});

		msg_tb = (ToggleButton) findViewById(R.id.set_msg_switch);
		if (msg_status.equals("true")) {
			msg_tb.setChecked(true);
		} else {
			msg_tb.setChecked(false);
		}

		msg_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				AsyncHttpClient httpClient = new AsyncHttpClient();

				RequestParams params = new RequestParams();
				params.put("uid", uid);
				params.put("token", token);

				httpClient.setTimeout(3000);

				if (isChecked) {
//					editor.putInt("msg_status", 1);
//					editor.commit();
					httpClient.post(ConstantValue.ACTIVE_ANDROID_CHAT_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											msg_tb.setChecked(false);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									msg_tb.setChecked(false);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				} else {

//					editor.putInt("msg_status", 0);
//					editor.commit();
					httpClient.post(ConstantValue.DEACTIVE_ANDROID_CHAT_PUSH, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
													  Header[] headers, JSONObject result) {
									try {
										if (result.getString("result").equals("ok")) {
											showInfo("设置成功~");
										} else {
											showInfo("设置失败，请稍候再试~");
											msg_tb.setChecked(true);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers, result);
								}

								@Override
								public void onFailure(int statusCode,
													  Header[] headers, String responseString,
													  Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									msg_tb.setChecked(true);
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				}
			}
		});

		try {

			TextView version_tv = (TextView) findViewById(R.id.setting_sys_version);
			version_tv.setText(this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
