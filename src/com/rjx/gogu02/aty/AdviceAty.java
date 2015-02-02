package com.rjx.gogu02.aty;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

public class AdviceAty extends Activity {

	private SharedPreferences sp;
	private String user_id = "";
	private String token = "";
	private EditText title_et, content_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advice_aty);

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

		title_et = (EditText) findViewById(R.id.advice_title_et);
		content_et = (EditText) findViewById(R.id.advice_content_et);

		ImageView back_iv = (ImageView) findViewById(R.id.common_logo_back);
		Button send_bt = (Button) findViewById(R.id.advice_send);
		Button back_bt = (Button) findViewById(R.id.advice_back);

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

		send_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (title_et.getText().toString().equals("")) {
					showInfo("标题不能为空");
				} else if (content_et.getText().toString().equals("")) {
					showInfo("内容不能为空");
				} else {
					RequestParams params = new RequestParams();
					params.put("uid", user_id);
					params.put("token", token);
					params.put("title", title_et.getText().toString());
					params.put("content", content_et.getText().toString());
					AsyncHttpClient client = new AsyncHttpClient();
					client.setTimeout(1000);
					client.post(ConstantValue.NEW_ADVICE_URL, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									showInfo("发送失败，请稍后再试~");
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers, Throwable throwable,
										JSONArray errorResponse) {
									showInfo("发送失败，请稍后再试~");
									super.onFailure(statusCode, headers,
											throwable, errorResponse);
								}

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getString("result")
												.equals("ok")) {
											finish();
										} else {
											showInfo("发送失败，请稍后再试~");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
				}
			}
		});
	}

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
