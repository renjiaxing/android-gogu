package com.rjx.gogu02.aty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.widget.Toast;

import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

public class AdviceAty extends Activity {

	private SharedPreferences sp;
	private String user_id = "";
	private String token = "";
	private String value = "";
	private HttpClient client;
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

		client = new DefaultHttpClient();

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

				String title, content;
				try {
					title = URLEncoder.encode(title_et.getText().toString(),
							"UTF-8");
					content = URLEncoder.encode(
							content_et.getText().toString(), "UTF-8");
					AdviceNet(ConstantValue.SERVER_URL + "advice_new_json?uid=" + user_id
							+ "&token=" + token + "&title=" + title
							+ "&content=" + content, 17);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	public void AdviceNet(String url, Integer mod) {
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
			case 17:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						showInfo("发送成功");
						finish();
					} else {
						showInfo("发送未成功，请重试~");
						title_et.setText("");
						content_et.setText("");
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
