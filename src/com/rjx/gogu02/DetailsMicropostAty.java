package com.rjx.gogu02;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjx.gogu02.ResizeLayout.OnResizeListener;

public class DetailsMicropostAty extends ActionBarActivity {

	private ArrayList<Comments> mListItems;
	private ListView mListView;
	private TextView tv;
	private ArrayAdapter<Comments> mAdapter;
	private HttpClient client;
	private String value = "";
	private String mid;
	private SharedPreferences sp;
	private String uid;
	private String token;
	private EditText et;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private int max = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deatil_microposts_aty);

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

		ImageView back = (ImageView) findViewById(R.id.common_logo_back);
		ResizeLayout rl = (ResizeLayout) findViewById(R.id.dm_layout);

		rl.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = SMALLER;
				if (max < h) {
					max = h;
				}
				Message msg = new Message();
				if (h == max) {
					change = BIGGER;
				}

				msg.what = change;

				handler.sendMessage(msg);

			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button btn2 = (Button) findViewById(R.id.dm_btn2);
		tv = (TextView) findViewById(R.id.dm_showText1);
		mListView = (ListView) findViewById(R.id.dm_listView1);

		Bundle data = getIntent().getExtras();
		mid = data.getString("mid");

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		uid = sp.getString("user_id", "");
		token = sp.getString("token", "");

		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et = (EditText) findViewById(R.id.dm_editText1);
				// String comment =
				// et.getText().toString().replaceAll("(\r\n|\r|\n|\n\r)",
				// "&lt;br&gt;");
				String comment = "";
				try {
					comment = URLEncoder.encode(et.getText().toString(),
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DetailsMicropostNet(
						"http://121.41.25.221/new_comment_json?mid=" + mid
								+ "&&msg=" + comment + "&&uid=" + uid
								+ "&&token=" + token, 4);
				 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				 imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Comments>();
		mAdapter = new ArrayAdapter<Comments>(this,
				android.R.layout.simple_list_item_1, mListItems);
		mListView.setAdapter(mAdapter);

		DetailsMicropostNet("http://121.41.25.221/detail_micropost_json?mid="
				+ mid + "&&uid=" + uid + "&&token=" + token, 3);

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// MenuInflater inflater=getMenuInflater();
	// inflater.inflate(R.menu.details_micropost, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// finish();
	// break;
	//
	// default:
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	public void DetailsMicropostNet(String url, Integer mod) {
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
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) tv
					.getLayoutParams();
			switch (msg.what) {
			case BIGGER:
				linearParams.height = (int) (getApplicationContext()
						.getResources().getDisplayMetrics().density * 250 + 0.5f);// 当控件的高强制设成50象素
				tv.setLayoutParams(linearParams);
				break;

			case SMALLER:
				linearParams.height = (int) (getApplicationContext()
						.getResources().getDisplayMetrics().density * 220 + 0.5f);// 当控件的高强制设成50象素
				tv.setLayoutParams(linearParams);
				break;

			case 3:
				try {
					JSONObject con = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(con.getString("comments"));
					tv.setText(con.getString("content"));
					if (arr.length() >= 1) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							Comments tmp = new Comments(obj.getString("id"),
									obj.getString("msg"),
									obj.getString("user_id"));
							mListItems.add(tmp);
						}
						mAdapter.notifyDataSetChanged();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 4:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						JSONArray arr = new JSONArray(con.getString("comments"));

						if (arr.length() >= 1) {
							mListItems.clear();
							et.setText("");
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = arr.getJSONObject(i);
								Comments tmp = new Comments(
										obj.getString("id"),
										obj.getString("msg"),
										obj.getString("user_id"));
								mListItems.add(tmp);
							}
							mAdapter.notifyDataSetChanged();
						}
					} else {
						showInfo("无法建立评论");
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

	private void setOverflownoShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
