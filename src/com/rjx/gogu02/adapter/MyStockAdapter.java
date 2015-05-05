package com.rjx.gogu02.adapter;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.domain.Stock;
import com.rjx.gogu02.utils.ConstantValue;

public class MyStockAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<Stock> stockList = new ArrayList<Stock>();
	private String user_id;
	private String token;
	private Boolean checked;

	public MyStockAdapter(Context context, ArrayList<Stock> stockList,
			String user_id, String token, Boolean checked) {
		this.context = context;
		this.stockList = stockList;
		this.user_id = user_id;
		this.token = token;
		this.checked = checked;
	}

	@Override
	public int getCount() {
		return stockList.size();
	}

	@Override
	public Object getItem(int position) {
		return stockList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.mystock_item_cell, null);
		}
		
		TextView stock_name = (TextView) convertView
				.findViewById(R.id.my_stock_name);
		final Stock tmp = (Stock) getItem(position);
		stock_name.setText(tmp.getStock_name());

		final CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.my_stock_checkbox);
		if (tmp.getFollow().equals("true")) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}

		
		checkBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				AsyncHttpClient httpClient = new AsyncHttpClient();

				RequestParams params = new RequestParams();
				params.put("uid", user_id);
				params.put("token", token);
				params.put("sid", tmp.getStock_id());
//				System.out.println(tmp.getStock_name());
//				System.out.println(tmp.getFollow());
//				System.out.println(isChecked);

				httpClient.setTimeout(3000);

				if (((CheckBox)v).isChecked()) {
					httpClient.post(ConstantValue.ADDSTOCK_URL, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getString("result")
												.equals("ok")) {
//											((CheckBox)v).setChecked(true);
										} else {
											showInfo("关注失败，请稍后再试~");
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers,
											response);
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});

				} else {
					httpClient.post(ConstantValue.DELSTOCK_URL, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode,
										Header[] headers, JSONObject response) {
									try {
										if (response.getString("result")
												.equals("ok")) {
//											((CheckBox)v).setChecked(false);
										} else {
											showInfo("取消关注失败，请稍后再试~");
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									super.onSuccess(statusCode, headers,
											response);
								}

								@Override
								public void onFailure(int statusCode,
										Header[] headers,
										String responseString,
										Throwable throwable) {
									showInfo("网络错误，请稍后再试~");
									super.onFailure(statusCode, headers,
											responseString, throwable);
								}
							});
				}
			}
		});

		return convertView;
	}

	public void showInfo(String info) {

		Toast.makeText(context.getApplicationContext(), info,
				Toast.LENGTH_SHORT).show();
	}
}
