package com.rjx.gogu02;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rjx.gogu02.ResizeLayout.OnResizeListener;

public class NewMicropostAty extends ActionBarActivity {

	private String value = "";
	private HttpClient client;
	private String resp = "";
	private EditText et1;
	private String uid = "";
	private String token = "";
	private SharedPreferences sp;
	private AutoCompleteTextView actx;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> stockList = new ArrayList<String>();
	private ArrayList<String> sidList = new ArrayList<String>();
	private ArrayList<String> tmp_stockList = new ArrayList<String>();
	private ArrayList<String> tmp_sidList = new ArrayList<String>();
	private ImageView iv;
	private Integer count=0;
	private ImageView iv_back;
	private ImageView iv_send;
    private static final int BIGGER = 1; 
    private static final int SMALLER = 2; 
    private int max = 0; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_micropost_aty);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(R.layout.custom_newmicropost_actoin_bar,
				null);
		getActionBar().setCustomView(
				mTitleView,
				new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

		client = new DefaultHttpClient();
		sp = getSharedPreferences("login1", MODE_PRIVATE);
		uid = sp.getString("user_id", "");
		token = sp.getString("token", "");
		
		et1 = (EditText) findViewById(R.id.nm_editText1);
		actx = (AutoCompleteTextView) findViewById(R.id.nm_auto);		
		ResizeLayout rl=(ResizeLayout) findViewById(R.id.nm_relative);
		
//		actx.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) et1.getLayoutParams(); // 取控件mGrid当前的布局参数   
//				linearParams.height = (int)(getApplicationContext().getResources().getDisplayMetrics().density*200+0.5f);// 当控件的高强制设成50象素   
//				et1.setLayoutParams(linearParams);
//			}
//		});
		
		rl.setOnResizeListener(new OnResizeListener() {
			
			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change=SMALLER;
				if(max<h){max=h;}
				Message msg=new Message();
				if(h==max){
					change=BIGGER;
				}
				
//				System.out.println(h);
//				System.out.println(oldh);
				
				msg.what=change;
//				if (change==BIGGER){
//				    showInfo("aaa");
//				    
//				}else{
//					showInfo("bbb");
//				}
				handler.sendMessage(msg);
				
			}
		});
		
		iv_back=(ImageView) findViewById(R.id.nm_iv_logo_back);
		
		iv_send=(ImageView) findViewById(R.id.nm_iv_send);
		
//        et1.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) et1.getLayoutParams(); // 取控件mGrid当前的布局参数   
//				linearParams.height = (int)(getApplicationContext().getResources().getDisplayMetrics().density*200+0.5f);// 当控件的高强制设成50象素   
//				et1.setLayoutParams(linearParams);
//			}
//		});
//        

		
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		iv_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String stock = actx.getText().toString();
				if(!stock.equals("")){
					value = et1.getText().toString();
					if(!value.equals("")){
					String content="";
					try {
						content = URLEncoder.encode(et1.getText().toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					createMicropostNet("http://192.168.110.128/new_micropost_json?uid="
							+ uid + "&&content=" + content + "&&stock=" + stock+"&&token="
							+token);}
					else {
						showInfo("匿名信息为必填~");
					}
				}else{
					showInfo("股票代码为必填~");
				}
				
			}
		});

		actx.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				checkstock("http://192.168.110.128/stock_json?code="
						+ s.toString() + "&maxRows=10");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}
	
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) et1.getLayoutParams();
			switch (msg.what) {
			case BIGGER:
				linearParams.height = (int)(getApplicationContext().getResources().getDisplayMetrics().density*300+0.5f);// 当控件的高强制设成50象素   
				et1.setLayoutParams(linearParams);
				break;

			case SMALLER:
				linearParams.height = (int)(getApplicationContext().getResources().getDisplayMetrics().density*220+0.5f);// 当控件的高强制设成50象素   
				et1.setLayoutParams(linearParams);
				break;
			}
		}
	};

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.new_micropost, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			break;
//
//		case R.id.nm_send:
//			value = et1.getText().toString();
//			String content = et1.getText().toString();
//			String stock = actx.getText().toString();
//			// stock_id=sidList.get(stockList.indexOf(ac));
//			createMicropostNet("http://192.168.110.128/new_micropost_json?uid="
//					+ uid + "&&content=" + content + "&&stock=" + stock+"&&token="
//					+token);
//
//			break;
//		default:
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private void setOverflownoShowingAlways() {
//		try {
//			ViewConfiguration config = ViewConfiguration.get(this);
//			Field menuKeyField = ViewConfiguration.class
//					.getDeclaredField("sHasPermanentMenuKey");
//			menuKeyField.setAccessible(true);
//			menuKeyField.setBoolean(config, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void createMicropostNet(String url) {

		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];

				HttpGet get = new HttpGet(urlString);

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());

					JSONObject result = new JSONObject(value);
					if (result.getString("result").equals("ok")) {
						Intent it = new Intent(NewMicropostAty.this,
								MainActivity.class);
						startActivity(it);
					} else {
						System.out.println("false");
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;

			}

		}.execute(url);

	}

	public void checkstock(String url) {

		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];

				HttpGet get = new HttpGet(urlString);

				try {
					HttpResponse response = client.execute(get);
					value = EntityUtils.toString(response.getEntity());

					JSONArray result = new JSONArray(value);
					stockList.clear();
					sidList.clear();
					for (int i = 0; i < result.length(); i++) {
						JSONObject item = result.getJSONObject(i);
						stockList.add(item.getString("code").toString() + ","
								+ item.getString("name").toString());
						sidList.add(item.getString("id").toString());
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;

			}

			protected void onPostExecute(Integer result) {
				adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.stock_list_item, stockList);
				actx.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			};

		}.execute(url);

	}
	
	public void showInfo(String info) {
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}

}
