package com.rjx.gogu02.adapter;

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

import com.jauker.widget.BadgeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rjx.gogu02.R;
import com.rjx.gogu02.R.drawable;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.aty.ChangeMicropostAty;
import com.rjx.gogu02.aty.DetailsMicropostAty;
import com.rjx.gogu02.aty.LoginAty;
import com.rjx.gogu02.aty.MainActivity;
import com.rjx.gogu02.aty.NewMessageAty;
import com.rjx.gogu02.aty.PicDetailAty;
import com.rjx.gogu02.aty.StockMicropostListAty;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.GoguUtils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Global;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MicropostsAdapter extends BaseAdapter {

	private Context context = null;
	private ArrayList<Micropost> arrayList = new ArrayList<Micropost>();
	private String value = "";
	private String currentUser = "";
	private HttpClient client;
	private String token = "";
	private Integer count = 0;
	private String serUrl = ConstantValue.SERVER_URL;
	private Handler handler;
	private ArrayList<String> unreadList;
	private BadgeView badgeView;
	
	ImageLoader imageLoader = ImageLoader.getInstance();

	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
			// .showImageOnLoading(R.drawable.ic_default_user)
			// .showImageForEmptyUri(R.drawable.ic_default_user)
			// .showImageOnFail(R.drawable.ic_default_user)
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.build();

	public void loadImage(ImageView imageView, String url) {
		imageLoader.displayImage(url, imageView, options);
	}

	// private ImageView iv_good;

	public MicropostsAdapter(Context context, ArrayList<Micropost> arrayList,
			ArrayList<String> unreadString, String token, String currentUser,
			Handler handler) {
		this.context = context;
		this.arrayList = arrayList;
		this.token = token;
		this.currentUser = currentUser;
		this.handler = handler;
		this.unreadList = unreadList;
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
		if (arrayList.size() > 6) {
			if (convertView != null) {
				ll = (LinearLayout) convertView;
			} else {
				ll = (LinearLayout) LayoutInflater.from(context).inflate(
						R.layout.item_cell, null);
			}
		} else {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.item_cell, null);
		}

		LinearLayout llp = (LinearLayout) ll.findViewById(R.id.ic_tv_ll);
		final TextView tv_content = (TextView) ll
				.findViewById(R.id.ic_tv_content);
		// TextView idtv = (TextView) ll.findViewById(R.id.id_text);
		final TextView tv_stock = (TextView) ll.findViewById(R.id.ic_tv_stock);
		final ImageView iv_good = (ImageView) ll.findViewById(R.id.ic_iv_good);

		final TextView tv_good_number = (TextView) ll
				.findViewById(R.id.ic_tv_good);

		TextView tv_comment_number = (TextView) ll
				.findViewById(R.id.ic_tv_reply);
		ImageView iv_comment = (ImageView) ll.findViewById(R.id.ic_iv_reply);

		TextView time_ago_tv=(TextView) ll.findViewById(R.id.micropost_item_time);
		
		client = new DefaultHttpClient();

		final Micropost tmp = (Micropost) getItem(position);
		// idtv.setText(tmp.getId());
		CharSequence contentSequence = Html.fromHtml(tmp.getContent());
		tv_content.setText(contentSequence);
		tv_stock.setText(tmp.getStock_name());
		tv_good_number.setText(tmp.getGood_number());
		tv_comment_number.setText(tmp.getComment_size());
		ImageView iv_del = (ImageView) ll.findViewById(R.id.ic_iv_del);
		ImageView iv_change = (ImageView) ll.findViewById(R.id.ic_iv_change);
		ImageView iv_msg = (ImageView) ll.findViewById(R.id.ic_iv_msg);
		final ImageView iv_image=(ImageView) ll.findViewById(R.id.ic_iv_image);
		
		time_ago_tv.setText(GoguUtils.TimeAgoFormat(tmp.getCreate_time()));

		if (currentUser.equals(tmp.getUser_id())) {
			iv_del.setVisibility(ViewGroup.VISIBLE);
			iv_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goodNet(serUrl + "micropost_del_json?uid=" + currentUser
							+ "&&mid=" + tmp.getId() + "&&token=" + token, 11);
				}
			});
			iv_change.setVisibility(ViewGroup.VISIBLE);
			iv_change.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bl = new Bundle();
					bl.putString("content", tmp.getContent());
					bl.putString("mid", tmp.getId());
					bl.putString("stock_full_name", tmp.getStock_full_name());
					bl.putString("image", tmp.getImage());
					Intent it = new Intent(context, ChangeMicropostAty.class);
					it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					it.putExtras(bl);
					context.startActivity(it);
					// ((Activity) context).finish();

				}
			});
		} else {
			iv_del.setVisibility(ViewGroup.INVISIBLE);
			iv_change.setVisibility(ViewGroup.INVISIBLE);
		}

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
				String stock_id = "";
				stock_id = tmp.getStock_id();
				Bundle bd2 = new Bundle();
				bd2.putString("stock_id", stock_id);
				Intent it3 = new Intent(context, StockMicropostListAty.class);
				it3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				it3.putExtras(bd2);
				context.startActivity(it3);
			}
		});

		iv_good.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tmp.getGood().equals("true")) {
					goodNet(serUrl + "micropost_nogood_json?uid=" + currentUser
							+ "&&mid=" + tmp.getId() + "&&token=" + token, 9);
					iv_good.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.ic_card_like_grey));
					tmp.setGood("false");
					tmp.setGood_number((Integer.parseInt(tmp.getGood_number()) - 1)
							+ "");
					tv_good_number.setText(tmp.getGood_number());

				} else {
					goodNet(serUrl + "micropost_good_json?uid=" + currentUser
							+ "&&mid=" + tmp.getId() + "&&token=" + token, 10);
					iv_good.setImageDrawable(context.getResources()
							.getDrawable(R.drawable.ic_card_liked));
					tmp.setGood("true");
					tmp.setGood_number((Integer.parseInt(tmp.getGood_number()) + 1)
							+ "");
					tv_good_number.setText(tmp.getGood_number());

				}
			}
		});

		tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id_st = tmp.getId();
				String id_content = tmp.getContent();
				Bundle data = new Bundle();
				data.putString("mid", id_st);
				data.putString("content", id_content);
				Intent it2 = new Intent(context, DetailsMicropostAty.class);
				it2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				it2.putExtras(data);
				context.startActivity(it2);
			}
		});

		iv_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id_st = tmp.getId();
				String id_content = tmp.getContent();
				Bundle data = new Bundle();
				data.putString("content", id_content);
				data.putString("mid", id_st);
				Intent it2 = new Intent(context, DetailsMicropostAty.class);
				it2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				it2.putExtras(data);
				context.startActivity(it2);
			}
		});

		iv_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bl = new Bundle();
				bl.putString("uid", tmp.getUser_id());
				Intent it2 = new Intent(context, NewMessageAty.class);
				it2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				it2.putExtras(bl);
				context.startActivity(it2);
			}
		});
		
		iv_image.setVisibility(View.GONE);
		
		if(!(tmp.getImage().equals(""))){
			iv_image.setVisibility(View.VISIBLE);
			ImageSize targetSize = new ImageSize(300, 350);
			final String url=ConstantValue.SERVER_URL+tmp.getImage().substring(1);
			imageLoader.loadImage(url, targetSize, options, new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					iv_image.setImageBitmap(loadedImage);
//					super.onLoadingComplete(imageUri, view, loadedImage);
				}
			});
			
			iv_image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle bd=new Bundle();
					bd.putString("url", url);
					Intent it=new Intent(context, PicDetailAty.class);
					it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					it.putExtra("pic", bd);
					context.startActivity(it);
				}
			});
		}
		
		

		if (tmp.getUnread().equals("0")) {
			// badge2.setVisibility(View.GONE);
			iv_comment.setImageDrawable(context.getResources().getDrawable(
					R.drawable.reply30));
		} else {
			iv_comment.setImageDrawable(context.getResources().getDrawable(
					R.drawable.reply30un));
			// badge2.setTargetView(reply_iv);
			// badge2.setVisibility(View.VISIBLE);
			// badge2.setBadgeCount(Integer.parseInt(microunread));

		}

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
				Bundle bl2 = new Bundle();

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());
					bl2.putString("value", value);
					msg.setData(bl2);
					handler.sendMessage(msg);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				return mod;

			}

		}.execute(url, mod);

	}

	// Handler handler = new Handler() {
	//
	// public void handleMessage(Message msg) {
	// try {
	// JSONObject obj = new JSONObject(value);
	// switch (msg.what) {
	// case 11:
	// try {
	// JSONObject con = new JSONObject(value.toString());
	// if (con.getString("result").equals("ok")) {
	// Intent it2 = new Intent(context,
	// MainActivity.class);
	// context.getApplicationContext().startActivity(it2);
	// context.finish();
	// } else {
	// showInfo("无法登陆");
	// et1.setText("");
	// et2.setText("");
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// break;
	//
	// default:
	// break;
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// };

}
