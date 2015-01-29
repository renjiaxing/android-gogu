package com.rjx.gogu02.aty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
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

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rjx.gogu02.R;
import com.rjx.gogu02.R.id;
import com.rjx.gogu02.R.layout;
import com.rjx.gogu02.adapter.CommentAdapter;
import com.rjx.gogu02.domain.Comments;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.ImageLoadTool;
import com.rjx.gogu02.utils.NetworkResources;
import com.rjx.gogu02.view.ResizeLayout;
import com.rjx.gogu02.view.ResizeLayout.OnResizeListener;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class DetailsMicropostAty extends ActionBarActivity {

	private ArrayList<Comments> mListItems;
	private ListView mListView;
	private TextView tv;
	private CommentAdapter mAdapter;
	private HttpClient client;
	private String value = "";
	private String mid;
	private SharedPreferences sp;
	private String uid;
	private String token;
	private EditText et;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private int max = 0;
	private String serUrl = ConstantValue.SERVER_URL;
	private IWXAPI wxapi;
	final public static String APP_ID = "wx184f80b52d0ec155";
	private String from_content;
	private ImageView iv_image;
	private ScrollView sc;
	private String randint;
	private String user_randint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deatil_microposts_aty);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_back_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

		ImageView back = (ImageView) findViewById(R.id.common_logo_back);
		ResizeLayout rl = (ResizeLayout) findViewById(R.id.dm_layout);
		iv_image = (ImageView) findViewById(R.id.dm_iv_image);
		sc = (ScrollView) findViewById(R.id.dm_sc);

		rl.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = SMALLER;
				if (max < h) {
					max = h;
				}
				Message msg = new Message();
				if (h == max) {
					change = BIGGER;
				}

				msg.what = change;

				handler.sendMessage(msg);

			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		wxapi = WXAPIFactory.createWXAPI(this, APP_ID);
		wxapi.registerApp(APP_ID);

		ImageView weixin_iv = (ImageView) findViewById(R.id.dm_weixin);

		Button btn2 = (Button) findViewById(R.id.dm_btn2);
		tv = (TextView) findViewById(R.id.dm_showText1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		mListView = (ListView) findViewById(R.id.dm_listView1);

		Bundle data = getIntent().getExtras();
		mid = data.getString("mid");
		from_content = data.getString("content");

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		uid = sp.getString("user_id", "");
		token = sp.getString("token", "");
		user_randint = sp.getString("randint", "");

		weixin_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = "" + System.currentTimeMillis();
				WXWebpageObject webpage = new WXWebpageObject();
				webpage.webpageUrl = serUrl + "microposts/" + mid + "/details";
				req.scene = SendMessageToWX.Req.WXSceneSession;
				req.message = new WXMediaMessage(webpage);
				req.message.description = from_content;
				req.message.title = "股刺网";

				wxapi.sendReq(req);
			}
		});

		ImageView friend_iv = (ImageView) findViewById(R.id.dm_friends);

		friend_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = "" + System.currentTimeMillis();
				WXWebpageObject webpage = new WXWebpageObject();
				webpage.webpageUrl = serUrl + "microposts/" + mid + "/details";
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				req.message = new WXMediaMessage(webpage);
				req.message.description = from_content;
				req.message.title = "股刺网：" + from_content;

				wxapi.sendReq(req);
			}
		});

		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et = (EditText) findViewById(R.id.dm_editText1);
				// String comment =
				// et.getText().toString().replaceAll("(\r\n|\r|\n|\n\r)",
				// "&lt;br&gt;");
				String comment = "";
				if (et.getText().toString().equals("")) {
					showInfo("评论不能为空~");
				} else {
					try {
						comment = URLEncoder.encode(et.getText().toString(),
								"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DetailsMicropostNet(serUrl + "new_comment_json?mid=" + mid
							+ "&&msg=" + comment + "&&uid=" + uid + "&&token="
							+ token, 4);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		client = new DefaultHttpClient();
		mListItems = new ArrayList<Comments>();
		// mAdapter = new ArrayAdapter<Comments>(this,
		// android.R.layout.simple_list_item_1, mListItems);
		mAdapter = new CommentAdapter(this, mListItems, uid, token,
				user_randint, handler);
		mListView.setAdapter(mAdapter);

		DetailsMicropostNet(serUrl + "detail_micropost_json?mid=" + mid
				+ "&&uid=" + uid + "&&token=" + token, 3);

	}

	@Override
	protected void onDestroy() {
		wxapi.unregisterApp();
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// MenuInflater inflater=getMenuInflater();
	// inflater.inflate(R.menu.details_micropost, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// finish();
	// break;
	//
	// default:
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	public void DetailsMicropostNet(String url, Integer mod) {
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

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			// LinearLayout.LayoutParams linearParams =
			// (LinearLayout.LayoutParams) tv
			// .getLayoutParams();
			android.view.ViewGroup.LayoutParams lp = sc.getLayoutParams();
			switch (msg.what) {
			case BIGGER:
				lp.height = (int) (getApplicationContext().getResources()
						.getDisplayMetrics().density * 230 + 0.5f);// 当控件的高强制设成50象素
				sc.setLayoutParams(lp);
				break;

			case SMALLER:
				lp.height = (int) (getApplicationContext().getResources()
						.getDisplayMetrics().density * 190 + 0.5f);// 当控件的高强制设成50象素
				sc.setLayoutParams(lp);
				break;

			case 3:
				try {
					JSONObject con = new JSONObject(value.toString());
					JSONArray arr = new JSONArray(con.getString("comments"));
					randint = con.getString("randint");
					tv.setText(con.getString("content"));
					JSONObject imageObject = new JSONObject(
							con.getString("image"));

					if (!(imageObject.getString("url").equals("null"))) {
						String url = ConstantValue.SERVER_URL
								+ imageObject.getString("url").substring(1);
						ImageLoadTool imageLoadTool = new ImageLoadTool();
						imageLoadTool.loadImage(iv_image, url);
					}

					if (arr.length() >= 1) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							Comments tmp = new Comments(obj.getString("id"),
									obj.getString("msg"),
									obj.getString("user_id"),
									obj.getString("anonid"),
									obj.getString("created_at"), randint);
							mListItems.add(tmp);
						}
						mAdapter.notifyDataSetChanged();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 4:
				try {
					JSONObject con = new JSONObject(value.toString());
					if (con.getString("result").equals("ok")) {
						JSONArray arr = new JSONArray(con.getString("comments"));

						if (arr.length() >= 1) {
							mListItems.clear();
							et.setText("");
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = arr.getJSONObject(i);
								Comments tmp = new Comments(
										obj.getString("id"),
										obj.getString("msg"),
										obj.getString("user_id"),
										obj.getString("anonid"),
										obj.getString("created_at"), randint);
								mListItems.add(tmp);
							}
							mAdapter.notifyDataSetChanged();
						}
					} else {
						showInfo("无法建立评论");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 12:
				try {
					JSONObject con = new JSONObject(msg.getData()
							.getString("value").toString());
					if (con.getString("result").equals("ok")) {
						JSONArray arr = new JSONArray(con.getString("comments"));

						if (arr.length() >= 1) {
							mListItems.clear();
							// et.setText("");
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = arr.getJSONObject(i);
								Comments tmp = new Comments(
										obj.getString("id"),
										obj.getString("msg"),
										obj.getString("user_id"),
										obj.getString("anonid"),
										obj.getString("created_at"), randint);
								mListItems.add(tmp);
							}
							mAdapter.notifyDataSetChanged();
						}
					} else {
						showInfo("无法建立评论");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		};
	};

	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}

	private void setOverflownoShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
