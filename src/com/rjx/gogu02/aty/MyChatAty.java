package com.rjx.gogu02.aty;

import java.io.IOException;
import java.util.ArrayList;

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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MyChatAdapter;
import com.rjx.gogu02.utils.ConstantValue;

public class MyChatAty extends Activity {

	private String value = "";
	private ArrayList<String> chatUsers = new ArrayList<String>();
	private ArrayList<String> toUsers = new ArrayList<String>();
	private ArrayList<String> unRead = new ArrayList<String>();
	private HttpClient client;
	private SharedPreferences sp;
	private String user_id;
	private String token;
	private MyChatAdapter mAdapter;
	private String serUrl = ConstantValue.SERVER_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_chat_aty);

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
		
		ListView users_lv = (ListView) findViewById(R.id.my_chat_lv);
		chatUsers = new ArrayList<String>();

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");

		client = new DefaultHttpClient();
		
		ImageView back_iv=(ImageView) findViewById(R.id.common_logo_back);
		
		back_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
//		chatUsers.add("1");
//		chatUsers.add("2");
//		chatUsers.add("3");
//		
//		toUsers.add("1");
//		toUsers.add("2");
//		toUsers.add("3");

		ListView u_lv = (ListView) findViewById(R.id.my_chat_lv);
		mAdapter = new MyChatAdapter(chatUsers, toUsers,unRead, this, user_id);
		u_lv.setAdapter(mAdapter);

		MyChatNet(serUrl + "message_user_json?uid=" + user_id + "&token="
				+ token, 15);

	}
	
	@Override
	protected void onResume() {
		MyChatNet(serUrl + "message_user_json?uid=" + user_id + "&token="
				+ token, 15);
		super.onResume();
	}

	public void MyChatNet(String url, Integer mod) {
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

				return mod;

			}

		}.execute(url, mod);

	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 15:
				try {
					JSONArray userArray = new JSONArray(value.toString());

					chatUsers.clear();
					toUsers.clear();
					unRead.clear();
					if (userArray.length() > 0) {
						for (int i = 0; i < userArray.length(); i++) {
							JSONObject obj = userArray.getJSONObject(i);
							chatUsers.add(obj.getString("anon"));
							toUsers.add(obj.getString("touser"));
							unRead.add(obj.getString("unreadnum"));
						}
						mAdapter.notifyDataSetChanged();
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
