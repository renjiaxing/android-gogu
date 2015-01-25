package com.rjx.gogu02.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.NewMessageAty;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.ImageLoadTool;

public class MyChatAdapter extends BaseAdapter {

	private ArrayList<String> messages;
	private Context context;
	private String current_user;
	private ArrayList<String> tousers;
	private ArrayList<String> unRead;
	private ArrayList<String> lastMsg;
	private ArrayList<String> randint;
	private ImageLoadTool imageLoadTool=new ImageLoadTool();
	private String picSer=ConstantValue.SERVER_PIC_URL;

	public MyChatAdapter(ArrayList<String> messages, ArrayList<String> tousers,
			ArrayList<String> unRead,ArrayList<String> lastMsg,ArrayList<String>randint,Context context, String current_user) {
		this.messages = messages;
		this.context = context;
		this.current_user = current_user;
		this.tousers = tousers;
		this.unRead=unRead;
		this.lastMsg=lastMsg;
		this.randint=randint;
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
					R.layout.mychat_item_cell, null);
		}

		TextView user_tv = (TextView) ll.findViewById(R.id.mychat_lastmsg);
		TextView time_tv = (TextView) ll.findViewById(R.id.mychat_time);
		ImageView user_iv=(ImageView) ll.findViewById(R.id.mychat_image);
		ImageView unread_iv=(ImageView) ll.findViewById(R.id.mychat_unread_image);
		TextView firstmsg_tv=(TextView) ll.findViewById(R.id.mychat_firstmsg);

		final String tmpAnonuser = messages.get(position);
		final String tmpTouser = tousers.get(position);
		final String tmpRandint =randint.get(position);
		final Integer pic_id=Integer.parseInt(tmpAnonuser)+Integer.parseInt(tmpRandint);
		
//		String userString;
//
//		if (tmpAnonuser.equals("0")) {
//			userString="我对自己的私信";
//		} else {
//			userString="与匿名用户" + tmpAnonuser + "的私信";
//		}
//		
//		if(!unRead.get(position).equals("0")){
//			userString=userString+"（有新私信）";
//		}
		
		if(unRead.get(position).equals("0")){
			unread_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_white_circle));
		}else{
			unread_iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_red_circle));
		}
		
		imageLoadTool.loadImage(user_iv, picSer+pic_id%100+".png");
		
		user_tv.setText(lastMsg.get(position));
		
		user_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bl = new Bundle();
				bl.putString("uid", tmpTouser);
				bl.putInt("rand_id",Integer.parseInt(tmpRandint));
				Intent it = new Intent(context, NewMessageAty.class);
				it.putExtras(bl);
				context.startActivity(it);

			}
		});
		
		firstmsg_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bl = new Bundle();
				bl.putString("uid", tmpTouser);
				bl.putInt("rand_id",Integer.parseInt(tmpRandint));
				Intent it = new Intent(context, NewMessageAty.class);
				it.putExtras(bl);
				context.startActivity(it);

			}
		});

		time_tv.setText("");
		return ll;
	}

}
