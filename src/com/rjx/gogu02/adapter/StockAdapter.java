package com.rjx.gogu02.adapter;

import java.util.ArrayList;

import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.aty.StockMicropostListAty;
import com.rjx.gogu02.domain.Stock;

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
		TextView stock_name=(TextView) ll.findViewById(R.id.sk_stock_name);
		final Stock tmp=(Stock) getItem(position);
		stock_name.setText(tmp.getStock_name());
		
		stock_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String stock_id="";
				stock_id=tmp.getStock_id();
				Bundle bd2=new Bundle();
				bd2.putString("stock_id", stock_id);
				bd2.putString("stock_name", tmp.getStock_name());
				Intent it3=new Intent(context,StockMicropostListAty.class);
				it3.putExtras(bd2);
				context.startActivity(it3);	
			}
		});
		
		return ll;
	}

}
