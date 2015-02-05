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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.adapter.MicropostsAdapter;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.utils.ConstantValue;

public class StockMicropostListAty extends Activity {

	private ArrayList<Micropost> mListItems;
	private PullToRefreshListView mListView;
	private MicropostsAdapter mAdapter;
	private HttpClient client;
	private String value = "";
	private String min = "";
	private String max = "";
	private SharedPreferences sp;
	private String user_id = "";
	private String token = "";
	private String stock_id = "";
	private ArrayAdapter<String> adapter;
	private ArrayList<String> mAllList = new ArrayList<String>();
	private String serUrl = ConstantValue.SERVER_URL;
	private ArrayList<String> unreadList=new ArrayList<String>();
	private TextView title_tv;
	private String stock_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Bundle bd = getIntent().getExtras();
		stock_id = bd.getString("stock_id");
		stock_name = bd.getString("stock_name", "");

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
		title_tv.setText(stock_name.split(",")[1]);

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Micropost>();
		mListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mAdapter = new MicropostsAdapter(this, mListItems,unreadList, token, user_id,
				handler);
		mListView.setAdapter(mAdapter);

		readNet(ConstantValue.MICROPOSTS_URL+"?uid=" + user_id
				+ "&&token=" + token + "&&stock_id=" + stock_id, 0);

		ImageView back = (ImageView) findViewById(R.id.common_logo_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// ����ListView

		// ����PullToRefresh
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			// ����Pulling Down
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				readNet(ConstantValue.UP_MICROPOSTS_URL+"?up=" + max
						+ "&&uid=" + user_id + "&&token=" + token
						+ "&&stock_id=" + stock_id, 1);
			}

			// ����Pulling Up
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {

				readNet(ConstantValue.DOWN_MICROPOSTS_URL+"?down="
						+ min + "&&uid=" + user_id + "&&token=" + token
						+ "&&stock_id=" + stock_id, 2);
			}

		});

	}

	@Override
	protected void onResume() {

		super.onResume();
		client = new DefaultHttpClient();
		mListItems = new ArrayList<Micropost>();
		mListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mAdapter = new MicropostsAdapter(this, mListItems,unreadList, token, user_id,
				handler);
		mListView.setAdapter(mAdapter);

		readNet(ConstantValue.MICROPOSTS_URL+"?uid=" + user_id
				+ "&&token=" + token + "&&stock_id=" + stock_id, 0);
	}

	public void readNet(String url, Integer mod) {
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

			protected void onPostExecute(Integer mod) {
				if (mod == 1 || mod == 2) {
					mListView.onRefreshComplete();
				}
			};
		}.execute(url, mod);

	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					JSONObject result=new JSONObject(value.toString());
					JSONArray arr =new JSONArray(result.getString("microposts"));
					
					mListItems.clear();

					max = arr.getJSONObject(0).getString("id");
					min = arr.getJSONObject(arr.length() - 1).getString("id");

					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						Micropost tmp = new Micropost(obj.getString("id"),
								obj.getString("content"),
								obj.getString("user_id"),
								obj.getString("stock_id"),
								obj.getString("stock_name"),
								obj.getString("comment_number"),
								obj.getString("good"),
								obj.getString("good_number"),
								obj.getString("created_at"),
								obj.getString("unread"),
								obj.getString("image"),
								obj.getString("stock_full_name"));
						mListItems.add(tmp);
					}
					mAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					JSONObject result=new JSONObject(value.toString());
					JSONArray arr =new JSONArray(result.getString("microposts"));

					max = arr.getJSONObject(0).getString("id");

					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						Micropost tmp = new Micropost(obj.getString("id"),
								obj.getString("content"),
								obj.getString("user_id"),
								obj.getString("stock_id"),
								obj.getString("stock_name"),
								obj.getString("comment_number"),
								obj.getString("good"),
								obj.getString("good_number"),
								obj.getString("created_at"),
								obj.getString("unread"),
								obj.getString("image"),
								obj.getString("stock_full_name"));
						mListItems.add(0, tmp);
					}
					mAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					JSONObject result=new JSONObject(value.toString());
					JSONArray arr =new JSONArray(result.getString("microposts"));

					min = arr.getJSONObject(arr.length() - 1).getString("id");

					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						Micropost tmp = new Micropost(obj.getString("id"),
								obj.getString("content"),
								obj.getString("user_id"),
								obj.getString("stock_id"),
								obj.getString("stock_name"),
								obj.getString("comment_number"),
								obj.getString("good"),
								obj.getString("good_number"),
								obj.getString("created_at"),
								obj.getString("unread"),
								obj.getString("image"),
								obj.getString("stock_full_name"));
						mListItems.add(tmp);
					}
					mAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 11:
				try {
					JSONObject obj = new JSONObject(msg.getData()
							.getString("value").toString());
					if (obj.getString("result").equals("ok")) {
						Intent it = new Intent(StockMicropostListAty.this,
								StockMicropostListAty.class);
						startActivity(it);
						finish();
					} else {
						showInfo("删除失败，请稍后再试");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
