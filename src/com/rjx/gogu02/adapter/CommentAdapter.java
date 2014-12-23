package com.rjx.gogu02.adapter;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.domain.Comments;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.GoguUtils;

public class CommentAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Comments> comments;
	private String currentUser;
	private String token;
	private Handler handler;
	private HttpClient client;
	private String value="";
	private String serUrl = ConstantValue.SERVER_URL;
	
	public CommentAdapter(Context context,ArrayList<Comments> comments,String currentUser,String token,Handler handler) {
		this.context=context;
		this.comments=comments;
		this.currentUser=currentUser;
		this.token=token;
		this.handler=handler;
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
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
					R.layout.comments_item_cell, null);
		}
		TextView anonname= (TextView) ll.findViewById(R.id.ci_anonname);
		TextView comment_del=(TextView) ll.findViewById(R.id.ci_del);
		TextView comment_time=(TextView) ll.findViewById(R.id.ci_time);
		TextView comment_content=(TextView) ll.findViewById(R.id.ci_content);
		
		client = new DefaultHttpClient();
		
		final Comments tmp=(Comments) getItem(position);
		anonname.setText("匿名用户"+tmp.getAnon_id());
		comment_del.setText("删除");
		
		comment_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DelCommentNet(serUrl + "del_comment_json?uid=" + currentUser
						+ "&&cid=" + tmp.getId() + "&&token=" + token, 12);
			}
		});
		
		comment_time.setText(GoguUtils.TimeAgoFormat(tmp.getCreate_time()));
		comment_content.setText(tmp.getMsg());
		
		return ll;
	}
	
	public void DelCommentNet(String url, Integer mod) {
		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];
				Integer mod = (Integer) params[1];

				HttpGet get = new HttpGet(urlString);
				
				Bundle bl=new Bundle();
				

				Message msg = new Message();
				msg.what = mod;

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());
					bl.putString("value", value);
					msg.setData(bl);
					handler.sendMessage(msg);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;

			}

		}.execute(url, mod);

	}

}
