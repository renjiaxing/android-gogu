package com.rjx.gogu02;

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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Global;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MicropostsAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<Micropost> arrayList = new ArrayList<Micropost>();
	private String value = "";
	private HttpClient client;
	private String token = "";
	private Integer count=0;
//	private ImageView iv_good;

	public MicropostsAdapter(Context context, ArrayList<Micropost> arrayList,
			String token) {
		this.context = context;
		this.arrayList = arrayList;
		this.token = token;
	}

	public Context getContext() {
		return context;
	}

	public ArrayList<Micropost> getArrayList() {
		return arrayList;
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll = null;
		if (convertView != null) {
			ll = (LinearLayout) convertView;
		} else {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.item_cell, null);
		}

		final TextView tv_content = (TextView) ll
				.findViewById(R.id.ic_tv_content);
		TextView idtv = (TextView) ll.findViewById(R.id.id_text);
		final TextView tv_stock = (TextView) ll.findViewById(R.id.ic_tv_stock);
	    final ImageView iv_good = (ImageView) ll.findViewById(R.id.ic_iv_good);
		final TextView tv_good_number = (TextView) ll.findViewById(R.id.ic_tv_good);
		
		TextView tv_comment_number = (TextView) ll
				.findViewById(R.id.ic_tv_reply);
		ImageView iv_comment = (ImageView) ll.findViewById(R.id.ic_iv_reply);

		client = new DefaultHttpClient();

		final Micropost tmp = (Micropost) getItem(position);
		idtv.setText(tmp.getId());
		tv_content.setText(tmp.getContent());
		tv_stock.setText(tmp.getStock_name());
		tv_good_number.setText(tmp.getGood_number());
		tv_comment_number.setText(tmp.getComment_size());

		if (tmp.getGood().equals("true")) {
			iv_good.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_card_liked));
		} else {
			iv_good.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_card_like_grey));
		}
		
		tv_stock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String stock_id="";
				stock_id=tmp.getStock_id();
				Bundle bd2=new Bundle();
				bd2.putString("stock_id", stock_id);
				Intent it3=new Intent(context,StockMicropostList.class);
				it3.putExtras(bd2);
				context.startActivity(it3);				
			}
		});

		iv_good.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tmp.getGood().equals("true")) {
					goodNet("http://121.41.25.221/micropost_nogood_json?uid="
							+ tmp.getUser_id() + "&&mid=" + tmp.getId()
							+ "&&token=" + token, 9);
					iv_good.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_card_like_grey));
					tmp.setGood("false");
					tmp.setGood_number((Integer.parseInt(tmp.getGood_number())-1)+"");
					tv_good_number.setText(tmp.getGood_number());

				} else {
					goodNet("http://121.41.25.221/micropost_good_json?uid="
							+ tmp.getUser_id() + "&&mid=" + tmp.getId()
							+ "&&token=" + token, 10);
					iv_good.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_card_liked));
					tmp.setGood("true");
					tmp.setGood_number((Integer.parseInt(tmp.getGood_number())+1)+"");
					tv_good_number.setText(tmp.getGood_number());
					
				}
			}
		});

		tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id_st = tmp.getId();
				Bundle data = new Bundle();
				data.putString("mid", id_st);
				Intent it2 = new Intent(context, DetailsMicropostAty.class);
				it2.putExtras(data);
				context.startActivity(it2);
			}
		});

		iv_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id_st = tmp.getId();
				Bundle data = new Bundle();
				data.putString("mid", id_st);
				Intent it2 = new Intent(context, DetailsMicropostAty.class);
				it2.putExtras(data);
				context.startActivity(it2);
			}
		});

		return ll;
	}

	public void goodNet(String url, Integer mod) {
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

//					handler.sendMessage(msg);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return mod;

			}

		}.execute(url, mod);

	}

//	Handler handler = new Handler() {
//
//		public void handleMessage(Message msg) {
//			try {
//				JSONObject obj = new JSONObject(value);
//				switch (msg.what) {
//				case 9:
//					if (obj.getString("result").equals("ok")) {
//						iv_good.setImageDrawable(context.getResources()
//								.getDrawable(R.drawable.ic_card_like_grey));
//					}
//					break;
//				case 10:
//					if (obj.getString("result").equals("ok")) {
//						iv_good.setImageDrawable(context.getResources()
//								.getDrawable(R.drawable.ic_card_liked));
//					}
//					break;
//
//				default:
//					break;
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//	};

}
