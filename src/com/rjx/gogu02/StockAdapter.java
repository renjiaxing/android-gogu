package com.rjx.gogu02;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StockAdapter extends BaseAdapter {
	
	private Context context=null;
	private ArrayList<Stock> stockList=new ArrayList<Stock>();
	
	public StockAdapter(Context context,ArrayList<Stock> stockList) {
		this.context=context;
		this.stockList=stockList;
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
		LinearLayout ll = null;
		if (convertView != null) {
			ll = (LinearLayout) convertView;
		} else {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.stock_item_cell, null);
		}
		TextView stock_id= (TextView) ll.findViewById(R.id.sk_stock_id);
		TextView stock_name=(TextView) ll.findViewById(R.id.sk_stock_name);
		final Stock tmp=(Stock) getItem(position);
		stock_id.setText(tmp.getStock_id());
		stock_name.setText(tmp.getStock_name());
		
		stock_name.setOnClickListener(new OnClickListener() {
			
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
		
		return ll;
	}

}
