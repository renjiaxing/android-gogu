package com.rjx.gogu02.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.domain.Msg;
import com.rjx.gogu02.utils.GoguUtils;

public class MessageAdapter extends BaseAdapter {
	
	private ArrayList<Msg> messages;
	private Context context;
	private String current_user;
	
	public MessageAdapter(ArrayList<Msg> messages,Context context,String current_user) {
		this.messages=messages;
		this.context=context;
		this.current_user=current_user;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
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
					R.layout.message_item_cell, null);
		}
		TextView tv_user=(TextView) ll.findViewById(R.id.messagei_user);
		TextView tv_time= (TextView) ll.findViewById(R.id.messagei_time);
		TextView tv_content = (TextView) ll.findViewById(R.id.messagei_content);
		
		Msg msg_tmp=messages.get(position);
		
		if( msg_tmp.getFrom_id().equals(msg_tmp.getTo_id())){
			tv_user.setText("我对自己说：");
		}else if (msg_tmp.getFrom_id().equals(current_user)) {
			tv_user.setText("我对匿名用户"+msg_tmp.getAnonnum()+":");
		}else if (msg_tmp.getTo_id().equals(current_user)){
			tv_user.setText("匿名用户"+msg_tmp.getAnontonum()+"对我说：");
		}
		
		tv_time.setText(GoguUtils.TimeAgoFormat(msg_tmp.getCreated_at()));
		
		tv_content.setText(msg_tmp.getMsg());
		
		
		return ll;
	}

}
