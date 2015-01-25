package com.rjx.gogu02.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.domain.Msg;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.GoguUtils;
import com.rjx.gogu02.utils.ImageLoadTool;

public class MessageAdapter extends BaseAdapter {
	
	private ArrayList<Msg> messages;
	private Context context;
	private String current_user;
	private ImageLoadTool imageLoadTool = new ImageLoadTool();
	private String picUrl = ConstantValue.SERVER_PIC_URL;
	private Integer rand_id;
	
	public MessageAdapter(ArrayList<Msg> messages,Context context,String current_user,Integer rand_id) {
		this.messages=messages;
		this.context=context;
		this.current_user=current_user;
		this.rand_id=rand_id;
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

		RelativeLayout ll = null;
				
		Msg msg_tmp=messages.get(position);
		
		if (convertView != null) {
			ll = (RelativeLayout) convertView;
			if(msg_tmp.getFrom_id().equals(current_user)){
				ll = (RelativeLayout) LayoutInflater.from(context).inflate(
						R.layout.rightitem, null);
			}else {
				ll = (RelativeLayout) LayoutInflater.from(context).inflate(
						R.layout.leftitem, null);
			}
		} else {
			if(msg_tmp.getFrom_id().equals(current_user)){
				ll = (RelativeLayout) LayoutInflater.from(context).inflate(
						R.layout.rightitem, null);
			}else {
				ll = (RelativeLayout) LayoutInflater.from(context).inflate(
						R.layout.leftitem, null);
			}
		}
		ImageView iv_user=(ImageView) ll.findViewById(R.id.nmsg_user_iv);
		TextView tv_time= (TextView) ll.findViewById(R.id.nmsg_time_tv);
		TextView tv_content = (TextView) ll.findViewById(R.id.nmsg_content_tv);
		
//		if( msg_tmp.getFrom_id().equals(msg_tmp.getTo_id())){
//			tv_user.setText("我对自己说：");
//		}else if (msg_tmp.getFrom_id().equals(current_user)) {
//			tv_user.setText("我对匿名用户"+msg_tmp.getAnonnum()+":");
//		}else if (msg_tmp.getTo_id().equals(current_user)){
//			tv_user.setText("匿名用户"+msg_tmp.getAnontonum()+"对我说：");
//		}
		
		tv_time.setText(GoguUtils.TimeAgoFormat(msg_tmp.getCreated_at()));
		
		tv_content.setText(msg_tmp.getMsg());	
		
		if(msg_tmp.getFrom_id().equals(current_user)){
			imageLoadTool.loadImage(iv_user, picUrl + rand_id+".png");
		}else {
			imageLoadTool.loadImage(iv_user, picUrl + (Integer.parseInt(msg_tmp.getAnonnum())+rand_id)%100
					+ ".png");
		}

		
		return ll;
	}

}
