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

import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.adapter.StockAdapter;
import com.rjx.gogu02.domain.Stock;
import com.rjx.gogu02.utils.ConstantValue;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class SearchAty extends Activity {

	private HttpClient client;
	private String value="";
	private ArrayList<Stock> stockList=new ArrayList<Stock>();
	private StockAdapter adapter;
	private ListView lv;
	private String serUrl=ConstantValue.SERVER_URL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seach_stock_aty);
		
		client=new DefaultHttpClient();
		adapter = new StockAdapter(this, stockList);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_search_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
		
		ImageView iv=(ImageView) findViewById(R.id.search_back_button);
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		lv=(ListView) findViewById(R.id.search_listview);
		
//		lv.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent it2=new Intent(getApplicationContext(), StockAdapter.class);
//				Bundle bd=new Bundle();
//				bd.putString("stock",((TextView)view.findViewById(android.R.id.text1)).getText().toString());
//				it2.putExtras(bd);
//				startActivity(it2);
//			}
//		});
		
		SearchView sv = (SearchView) findViewById(R.id.search_view);
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				checkstock(serUrl+"stock_json?code="
						+ newText + "&&maxRows=10");
				return false;
			}
		});

	}

	public void checkstock(String url) {

		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];

				HttpGet get = new HttpGet(urlString);

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());

					JSONArray result = new JSONArray(value);
					stockList.clear();
					for (int i = 0; i < result.length(); i++) {
						JSONObject item = result.getJSONObject(i);
						Stock tmp=new Stock(item.getString("id").toString(), item.getString("code").toString() + ","
								+ item.getString("name").toString());
						stockList.add(tmp);
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;

			}

			protected void onPostExecute(Integer result) {
				
				lv.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			};

		}.execute(url);

	}
}
