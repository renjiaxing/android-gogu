package com.rjx.gogu02.aty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MessageAdapter;
import com.rjx.gogu02.domain.Msg;
import com.rjx.gogu02.utils.ConstantValue;

public class NewMessageAty extends Activity {

	private MessageAdapter mAdapter;
	private ArrayList<Msg> messageList;
	private SharedPreferences sp;
	private String user_id;
	private String token;
	private String to_uid;
	private HttpClient client;
	private String value = "";
	private String serUrl = ConstantValue.SERVER_URL;
	private EditText msg_et;
	private Integer pic_id;
	private Integer rand_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_message_aty);

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");

		Bundle bl = getIntent().getExtras();
		to_uid = bl.getString("uid", "");
		rand_id=bl.getInt("rand_id",0);

		client = new DefaultHttpClient();

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

		ListView msg_lv = (ListView) findViewById(R.id.nmessage_lv);
		ImageView common_back = (ImageView) findViewById(R.id.common_logo_back);
		msg_et = (EditText) findViewById(R.id.nmessage_content);
		Button send_btn = (Button) findViewById(R.id.nmessage_send);

		messageList = new ArrayList<Msg>();
		mAdapter = new MessageAdapter(messageList, this, user_id,rand_id);
		msg_lv.setAdapter(mAdapter);

		NewMessageNet(serUrl + "messages_json?uid=" + user_id + "&token="
				+ token + "&from_id=" + user_id + "&to_id=" + to_uid, 13);

		common_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					NewMessageNet(
							serUrl
									+ "new_message_json?uid="
									+ user_id
									+ "&token="
									+ token
									+ "&from_id="
									+ user_id
									+ "&to_id="
									+ to_uid
									+ "&msg="
									+ URLEncoder.encode(msg_et.getText()
											.toString(), "UTF-8"), 14);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void NewMessageNet(String url, Integer mod) {
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
			case 13:
				try {
					JSONObject con = new JSONObject(value.toString());

					if (con.getString("result").equals("ok")) {
						JSONArray msgArray = new JSONArray(con.getString(
								"msgArray").toString());
						if (msgArray.length() > 0) {
							for (int i = 0; i < msgArray.length(); i++) {
								JSONObject obj = msgArray.getJSONObject(i);
								Msg tmp = new Msg(obj.getString("fromuser_id"),
										obj.getString("touser_id"),
										obj.getString("msg"),
										obj.getString("created_at"),
										obj.getString("anonnum"),
										obj.getString("anontonum"));
								messageList.add(tmp);
							}
							mAdapter.notifyDataSetChanged();
						}
					} else {
						showInfo("无法获取私信信息:(");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 14:
				try {
					JSONObject con = new JSONObject(value.toString());

					if (con.getString("result").equals("ok")) {
						JSONObject obj = new JSONObject(con.getString("msg")
								.toString());

						Msg tmp = new Msg(obj.getString("fromuser_id"),
								obj.getString("touser_id"),
								obj.getString("msg"),
								obj.getString("created_at"),
								obj.getString("anonnum"),
								obj.getString("anontonum"));
						messageList.add(tmp);

						mAdapter.notifyDataSetChanged();

					} else {
						showInfo("无法获取私信信息:(");
					}
					msg_et.setText("");
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
