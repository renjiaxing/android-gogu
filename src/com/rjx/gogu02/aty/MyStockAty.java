package com.rjx.gogu02.aty;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MicropostsAdapter;
import com.rjx.gogu02.adapter.MyStockAdapter;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.domain.Stock;
import com.rjx.gogu02.utils.ConstantValue;

public class MyStockAty extends Activity {

	private ArrayList<Stock> stockList = new ArrayList<Stock>();
	private MyStockAdapter adapter;
	private ListView lv;
	private String user_id;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_stock_aty);

		SharedPreferences sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");
		
		adapter = new MyStockAdapter(this, stockList,user_id,token,true);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_mystock_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

		ImageView iv_back = (ImageView) findViewById(R.id.mystock_iv_logo_back);

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tv_title=(TextView) findViewById(R.id.mystock_tv_title);
		
		tv_title.setText("我关注的股票");

		lv = (ListView) findViewById(R.id.mystock_listview);

		lv.setAdapter(adapter);

		AsyncHttpClient httpClient = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("uid", user_id);
		params.put("token", token);

		httpClient.setTimeout(3000);

		httpClient.get(ConstantValue.MYSTOCK_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getString("result").equals("ok")) {
								JSONArray result;

								result = new JSONArray(response
										.getString("stocks"));
								stockList.clear();
								for (int i = 0; i < result.length(); i++) {
									JSONObject item = result.getJSONObject(i);
									Stock tmp = new Stock(item.getString("id")
											.toString(), item.getString("code")
											.toString()
											+ ","
											+ item.getString("name").toString()
											+ "," + item.getString("shortname"),"true");
									stockList.add(tmp);
								}
								adapter.notifyDataSetChanged();

							} else {
								showInfo("您没有权限使用该功能~");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						showInfo("网络错误，请稍后再试~");
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
				});

		ImageView iv_add = (ImageView) findViewById(R.id.mystock_iv_add);
		
		iv_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it_add_mystock=new Intent(MyStockAty.this, AddFavStockAty.class);
				startActivity(it_add_mystock);
			}
		});

	}
	
	@Override
	protected void onResume() {

		lv = (ListView) findViewById(R.id.mystock_listview);

		lv.setAdapter(adapter);

		AsyncHttpClient httpClient = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("uid", user_id);
		params.put("token", token);

		httpClient.setTimeout(3000);

		httpClient.get(ConstantValue.MYSTOCK_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getString("result").equals("ok")) {
								JSONArray result;

								result = new JSONArray(response
										.getString("stocks"));
								stockList.clear();
								for (int i = 0; i < result.length(); i++) {
									JSONObject item = result.getJSONObject(i);
									Stock tmp = new Stock(item.getString("id")
											.toString(), item.getString("code")
											.toString()
											+ ","
											+ item.getString("name").toString()
											+ "," + item.getString("shortname"),"true");
									stockList.add(tmp);
								}
								adapter.notifyDataSetChanged();

							} else {
								showInfo("您没有权限使用该功能~");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						showInfo("网络错误，请稍后再试~");
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
				});
		
		super.onResume();

	}

	public void showInfo(String info) {

		Toast.makeText(this.getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}

}
