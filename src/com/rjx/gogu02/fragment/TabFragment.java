package com.rjx.gogu02.fragment;

import java.io.IOException;
import java.util.ArrayList;

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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jauker.widget.BadgeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MicropostsAdapter;
import com.rjx.gogu02.adapter.TabAdapter;
import com.rjx.gogu02.aty.MainActivityold;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.utils.ConstantValue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TabFragment extends Fragment {
	private int pos;

	private ArrayList<Micropost> mListItems;
	private PullToRefreshListView mListView;
	private MicropostsAdapter mAdapter;
	private HttpClient client;
	private String value = "";
	private String min = "0";
	private String max = "0";
	private String user_id = "";
	private String token = "";
	private ArrayAdapter<String> adapter;
	private ArrayList<String> mAllList = new ArrayList<String>();
	private String serUrl = ConstantValue.SERVER_URL;
	private ImageView mychat_iv, reply_iv, other_reply_iv;
	private ArrayList<String> unreadList = new ArrayList<String>();
	private BadgeView badge, badge2;
	private String msgunread = "";
	private String microunread = "";
	private String unreplymicro = "";
	private SharedPreferences sp;
//	private AsyncHttpClient httpClient;

//	public TabFragment(int pos) {
//		this.pos = pos;
//	}

	public TabFragment(){}

//	public TabFragment() {};

	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		token = TabAdapter.token;
		user_id = TabAdapter.user_id;

		sp = getActivity().getSharedPreferences("login1",
				getActivity().getApplicationContext().MODE_PRIVATE);

		if( getArguments() != null) {
			int pos = getArguments().getInt("pos");
			this.pos = pos;
		}

		View view = inflater.inflate(R.layout.frag, container, false);
		mListView = (PullToRefreshListView) view.findViewById(R.id.list_view);

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Micropost>();

		mAdapter = new MicropostsAdapter(getActivity(),
				mListItems, unreadList, TabAdapter.token, TabAdapter.user_id,
				handler);
		mListView.setAdapter(mAdapter);

		RequestParams params = new RequestParams();
		params.put("uid", user_id);
		params.put("token", token);
		if(pos==1){
			params.put("my_stocks", user_id);
		}

		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.setTimeout(3000);
		httpClient.get(ConstantValue.MICROPOSTS_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						JSONArray arr;
						try {
							arr = new JSONArray(response
									.getString("microposts"));

							msgunread = response.getString("unreadnum");
							String randint = response.getString("randint");

							Editor et = sp.edit();
							et.putString("randint", randint);
							et.commit();

							microunread = response.getString("unreadmicro");

							unreplymicro = response.getString("unreplymicro");
							max = arr.getJSONObject(0).getString("id");
							min = arr.getJSONObject(arr.length() - 1)
									.getString("id");

							mListItems.clear();

							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = arr.getJSONObject(i);
								Micropost tmp = new Micropost(obj
										.getString("id"), obj
										.getString("content"), obj
										.getString("user_id"), obj
										.getString("stock_id"), obj
										.getString("stock_name"), obj
										.getString("comment_number"), obj
										.getString("good"), obj
										.getString("good_number"), obj
										.getString("created_at"), obj
										.getString("unread"), obj
										.getString("image"), obj
										.getString("stock_full_name"));
								mListItems.add(tmp);
							}

							mAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						mListView.onRefreshComplete();
						showInfo("网络错误，请稍后再试~");
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						showInfo("网络错误，请稍后再试~");
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
				});

		// readNet(ConstantValue.MICROPOSTS_URL + "?uid=" + user_id + "&token="
		// + token, 0);

		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			// ����Pulling Down
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {

				RequestParams params = new RequestParams();
				params.put("uid", user_id);
				params.put("token", token);
				params.put("up", max);
				if(pos==1){
					params.put("my_stocks", user_id);
				}

				AsyncHttpClient httpClient1 = new AsyncHttpClient();
				httpClient1.setTimeout(3000);
				httpClient1.get(ConstantValue.UP_MICROPOSTS_URL, params,
						new JsonHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								mListView.onRefreshComplete();
								JSONArray arr;
								try {
									arr = new JSONArray(response
											.getString("microposts"));

									msgunread = response.getString("unreadnum");

									microunread = response
											.getString("unreadmicro");

									unreplymicro = response
											.getString("unreplymicro");
									if(arr.length()>0){
										max = arr.getJSONObject(0).getString("id");
									}

									for (int i = 0; i < arr.length(); i++) {
										JSONObject obj = arr.getJSONObject(i);
										Micropost tmp = new Micropost(
												obj.getString("id"),
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
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								super.onSuccess(statusCode, headers, response);
							}

							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								mListView.onRefreshComplete();
								showInfo("网络错误，请稍后再试~");
								super.onFailure(statusCode, headers, throwable, errorResponse);
							}
							
							@Override
							public void onFailure(int statusCode,
									Header[] headers, String responseString,
									Throwable throwable) {
								mListView.onRefreshComplete();
								showInfo("网络错误，请稍后再试~");
								super.onFailure(statusCode, headers,
										responseString, throwable);
							}
						});

				// readNet(ConstantValue.UP_MICROPOSTS_URL + "?up=" + max
				// + "&&uid=" + user_id + "&&token=" + token, 1);
			}

			// ����Pulling Up
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				RequestParams params = new RequestParams();
				params.put("uid", user_id);
				params.put("token", token);
				params.put("down", min);
				if(pos==1){
					params.put("my_stocks", user_id);
				}

				AsyncHttpClient httpClient2 = new AsyncHttpClient();
				httpClient2.setTimeout(3000);
				httpClient2.get(ConstantValue.DOWN_MICROPOSTS_URL, params,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								mListView.onRefreshComplete();
								JSONArray arr;
								try {
									arr = new JSONArray(response
											.getString("microposts"));

									msgunread = response.getString("unreadnum");
									microunread = response
											.getString("unreadmicro");
									unreplymicro = response
											.getString("unreplymicro");
									if(arr.length()>0){
										min = arr.getJSONObject(arr.length() - 1)
											.getString("id");
									}

									for (int i = 0; i < arr.length(); i++) {
										JSONObject obj = arr.getJSONObject(i);
										Micropost tmp = new Micropost(
												obj.getString("id"),
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
								super.onSuccess(statusCode, headers, response);
							}
							
							@Override
							public void onFailure(int statusCode, Header[] headers,
									Throwable throwable, JSONObject errorResponse) {
								mListView.onRefreshComplete();
								showInfo("网络错误，请稍后再试~");
								super.onFailure(statusCode, headers, throwable, errorResponse);
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, String responseString,
									Throwable throwable) {
								mListView.onRefreshComplete();
								showInfo("网络错误，请稍后再试~");
								super.onFailure(statusCode, headers,
										responseString, throwable);
							}
						});

				// readNet(ConstantValue.DOWN_MICROPOSTS_URL + "?down=" + min
				// + "&&uid=" + user_id + "&&token=" + token, 2);
			}

		});

		// tv.setText(TabAdapter.TITLES[pos]);
		return view;
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
					msgunread = result.getString("unreadnum");
					String randint = result.getString("randint");

					Editor et = sp.edit();
					et.putString("randint", randint);
					et.commit();

					// if (msgunread.equals("0")) {
					// // badge.setVisibility(View.GONE);
					// mychat_iv.setImageDrawable(MainActivity.this
					// .getResources().getDrawable(
					// R.drawable.ic_action_chat));
					// } else {
					// mychat_iv.setImageDrawable(MainActivity.this
					// .getResources().getDrawable(
					// R.drawable.ic_action_chat_unread));
					//
					// }

					microunread = result.getString("unreadmicro");

					// if (microunread.equals("0")) {
					// // badge2.setVisibility(View.GONE);
					// reply_iv.setImageDrawable(MainActivity.this
					// .getResources().getDrawable(
					// R.drawable.ic_card_conversation_grey));
					// } else {
					// reply_iv.setImageDrawable(MainActivity.this
					// .getResources()
					// .getDrawable(
					// R.drawable.ic_card_conversation_grey_new));
					//
					// }

					unreplymicro = result.getString("unreplymicro");

					// getWindow().invalidatePanelMenu(
					// Window.FEATURE_OPTIONS_PANEL);

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
					JSONObject result = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(
							result.getString("microposts"));
					String msgunread = result.getString("unreadnum");

					String microunread = result.getString("unreadmicro");

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
					JSONObject result = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(
							result.getString("microposts"));
					String msgunread = result.getString("unreadnum");

					String microunread = result.getString("unreadmicro");

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
						mAdapter.notifyDataSetChanged();
					} else {
						showInfo("删除失败，请稍后再试");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			}
		};
	};

	public void showInfo(String info) {

		Toast.makeText(getActivity().getApplicationContext(), info,
				Toast.LENGTH_SHORT).show();
	}
}
