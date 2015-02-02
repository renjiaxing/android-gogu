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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jauker.widget.BadgeView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MicropostsAdapter;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.service.NotificationService;
import com.rjx.gogu02.update.UpdateManager;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.view.AddPopDiag;

public class MainActivity extends ActionBarActivity {

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
	private ArrayAdapter<String> adapter;
	private ArrayList<String> mAllList = new ArrayList<String>();
	private String serUrl = ConstantValue.SERVER_URL;
	private ImageView mychat_iv, reply_iv, other_reply_iv;
	private ArrayList<String> unreadList = new ArrayList<String>();
	private BadgeView badge, badge2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageLoader imageLoader = ImageLoader.getInstance();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.

		imageLoader.init(config);

		// CrashReport.testJavaCrash ();
		Intent serIntent = new Intent(this, NotificationService.class);
		startService(serIntent);

		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkUpdateInfo();

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");

		if (user_id.equals("")) {
			Intent back_it = new Intent(MainActivity.this, LoginAty.class);
			startActivity(back_it);
			finish();
		}

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_main_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

		final ImageView iv_search = (ImageView) findViewById(R.id.mn_search);

		reply_iv = (ImageView) findViewById(R.id.mn_my_microposts);

		reply_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, MyMicropostAty.class);
				startActivity(it);
				finish();
			}
		});

		other_reply_iv = (ImageView) findViewById(R.id.mn_reply_microposts);

		other_reply_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, MyReplyAty.class);
				startActivity(it);
				finish();
			}
		});

		iv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it3 = new Intent(MainActivity.this, SearchAty.class);
				startActivity(it3);
			}
		});

		final ImageView iv_new = (ImageView) findViewById(R.id.mn_new);
		iv_new.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, NewMicropostAty.class);
				startActivity(it);
			}
		});

		final ImageView iv_more = (ImageView) findViewById(R.id.mn_more);

		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddPopDiag addPopDiag = new AddPopDiag(MainActivity.this);
				addPopDiag.showPopupWindow(iv_more);
			}
		});

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Micropost>();
		mListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mAdapter = new MicropostsAdapter(this, mListItems, unreadList, token,
				user_id, handler);
		mListView.setAdapter(mAdapter);

		mychat_iv = (ImageView) findViewById(R.id.mn_mychat);
		mychat_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, MyChatAty.class);
				startActivity(it);
				finish();
			}
		});

		readNet(ConstantValue.MICROPOSTS_URL+"?uid=" + user_id + "&token=" + token,
				0);


		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			// ����Pulling Down
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				readNet(ConstantValue.UP_MICROPOSTS_URL+"?up=" + max + "&&uid="
						+ user_id + "&&token=" + token, 1);
			}

			// ����Pulling Up
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {

				readNet(ConstantValue.DOWN_MICROPOSTS_URL+"?down=" + min + "&&uid="
						+ user_id + "&&token=" + token, 2);
			}

		});


	}

	//
	// @Override
	protected void onResume() {

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Micropost>();
		mListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mAdapter = new MicropostsAdapter(this, mListItems, unreadList, token,
				user_id, handler);
		mListView.setAdapter(mAdapter);

		readNet(ConstantValue.MICROPOSTS_URL+"?uid=" + user_id + "&&token=" + token,
				0);
		super.onResume();

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

					JSONObject result = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(
							result.getString("microposts"));
					String msgunread = result.getString("unreadnum");
					String randint = result.getString("randint");

					Editor et = sp.edit();
					et.putString("randint", randint);
					et.commit();

					if (msgunread.equals("0")) {
						// badge.setVisibility(View.GONE);
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat));
					} else {
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat_unread));

					}

					String microunread = result.getString("unreadmicro");

					if (microunread.equals("0")) {
						// badge2.setVisibility(View.GONE);
						reply_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_card_conversation_grey));
					} else {
						reply_iv.setImageDrawable(MainActivity.this
								.getResources()
								.getDrawable(
										R.drawable.ic_card_conversation_grey_new));

					}

					String unreplymicro = result.getString("unreplymicro");

					if (unreplymicro.equals("0")) {
						other_reply_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_info_single_chat));
					} else {
						other_reply_iv
								.setImageDrawable(MainActivity.this
										.getResources()
										.getDrawable(
												R.drawable.ic_action_info_single_chat_un));
					}

					max = arr.getJSONObject(0).getString("id");
					min = arr.getJSONObject(arr.length() - 1).getString("id");

					mListItems.clear();

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
								obj.getString("unread"), obj.getString("image"),
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
					JSONObject result = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(
							result.getString("microposts"));
					String msgunread = result.getString("unreadnum");

					if (msgunread.equals("0")) {
						// badge.setVisibility(View.GONE);
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat));
					} else {
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat_unread));

					}

					String microunread = result.getString("unreadmicro");

					if (microunread.equals("0")) {
						// badge2.setVisibility(View.GONE);
						reply_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_card_conversation_grey));
					} else {
						reply_iv.setImageDrawable(MainActivity.this
								.getResources()
								.getDrawable(
										R.drawable.ic_card_conversation_grey_new));

					}

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
								obj.getString("unread"), obj.getString("image"),
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
					JSONObject result = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(
							result.getString("microposts"));
					String msgunread = result.getString("unreadnum");

					if (msgunread.equals("0")) {
						// badge.setVisibility(View.GONE);
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat));
					} else {
						mychat_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_action_chat_unread));

					}

					String microunread = result.getString("unreadmicro");

					if (microunread.equals("0")) {
						reply_iv.setImageDrawable(MainActivity.this
								.getResources().getDrawable(
										R.drawable.ic_card_conversation_grey));
					} else {
						reply_iv.setImageDrawable(MainActivity.this
								.getResources()
								.getDrawable(
										R.drawable.ic_card_conversation_grey_new));

					}

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
								obj.getString("unread"), obj.getString("image"),
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
						Intent it = new Intent(MainActivity.this,
								MainActivity.class);
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
