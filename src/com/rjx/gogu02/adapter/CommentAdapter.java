package com.rjx.gogu02.adapter;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.DetailsMicropostAty;
import com.rjx.gogu02.aty.NewMessageAty;
import com.rjx.gogu02.domain.Comments;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.GoguUtils;
import com.rjx.gogu02.utils.ImageLoadTool;

public class CommentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Comments> comments;
	private String currentUser;
	private String token;
	private String user_randint;
	private Handler handler;
	private HttpClient client;
	private String value = "";
	private String serUrl = ConstantValue.SERVER_URL;
	private String picUrl = ConstantValue.SERVER_PIC_URL;
	private ArrayList<Comments> mListItems=new ArrayList<Comments>();

	ImageLoadTool imageLoadTool = new ImageLoadTool();

	public CommentAdapter(Context context, ArrayList<Comments> comments,
			String currentUser, String token, String user_randint, Handler handler) {
		this.context = context;
		this.comments = comments;
		this.currentUser = currentUser;
		this.token = token;
		this.user_randint = user_randint;
		this.handler = handler;
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
		// TextView anonname= (TextView) ll.findViewById(R.id.ci_anonname);
		TextView comment_del = (TextView) ll.findViewById(R.id.ci_del);
		TextView comment_time = (TextView) ll.findViewById(R.id.ci_time);
		TextView comment_content = (TextView) ll.findViewById(R.id.ci_content);
		ImageView iv_user_image = (ImageView) ll
				.findViewById(R.id.ci_user_image);

		client = new DefaultHttpClient();

		final Comments tmp = (Comments) getItem(position);

		imageLoadTool.loadImage(
				iv_user_image,
				picUrl
						+ (Integer.parseInt(tmp.getAnon_id()) + Integer
								.parseInt(tmp.getRandint())) % 100 + ".png");

		// anonname.setText("匿名用户"+tmp.getAnon_id());
		comment_del.setText("");

		if (currentUser.equals(tmp.getUser_id())) {
			comment_del.setText("删除");

			comment_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					DelCommentNet(ConstantValue.DEL_COMMENT_URL + "?uid="
							+ currentUser + "&&cid=" + tmp.getId() + "&&token="
							+ token, 12);
				}
			});
		}

		comment_time.setText(GoguUtils.TimeAgoFormat(tmp.getCreate_time()));
		comment_content.setText(tmp.getMsg());
		
		comment_content.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(context, NewMessageAty.class);
				Bundle bl=new Bundle();
				bl.putString("uid", tmp.getUser_id());
				bl.putInt("rand_id", Integer.parseInt(user_randint));
				it.putExtras(bl);
				context.startActivity(it);
				
			}
		});
		
		iv_user_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent(context, NewMessageAty.class);
				Bundle bl=new Bundle();
				bl.putString("uid", tmp.getUser_id());
				bl.putInt("rand_id", Integer.parseInt(user_randint));
				it.putExtras(bl);
				context.startActivity(it);
				
			}
		});

		return ll;
	}

	public void DelCommentNet(String url, Integer mod) {
		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];
				Integer mod = (Integer) params[1];

				HttpGet get = new HttpGet(urlString);

				Bundle bl = new Bundle();

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
