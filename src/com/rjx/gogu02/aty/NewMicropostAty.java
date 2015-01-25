package com.rjx.gogu02.aty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.view.ResizeLayout;
import com.rjx.gogu02.view.ResizeLayout.OnResizeListener;

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
	private Integer count = 0;
	private ImageView iv_back;
	private ImageView iv_send;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int NOT_RIGHT_STOCK_FORMAT = 3;
	private int max = 0;
	private String serUrl = ConstantValue.SERVER_URL;
	private ImageView iv_add_pic;
	private Boolean hasPic = false;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_micropost_aty);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mTitleView = mInflater.inflate(
				R.layout.custom_newmicropost_actoin_bar, null);
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
		iv_add_pic = (ImageView) findViewById(R.id.nm_iv_addpic);
		ResizeLayout rl = (ResizeLayout) findViewById(R.id.nm_relative);

		iv_add_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasPic == false) {
					Intent intent = new Intent();
					// intent.setType("image/*");
					// intent.setAction(Intent.ACTION_GET_CONTENT);
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(NewMicropostAty.this,
							DelPicAty.class);
					Bundle bd = new Bundle();
					bd.putString("url", url);
					intent.putExtra("pic", bd);
					startActivityForResult(intent, 2);
				}
			}
		});

		// actx.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)
		// et1.getLayoutParams(); // ȡ�ؼ�mGrid��ǰ�Ĳ��ֲ���
		// linearParams.height =
		// (int)(getApplicationContext().getResources().getDisplayMetrics().density*200+0.5f);//
		// ���ؼ��ĸ�ǿ�����50����
		// et1.setLayoutParams(linearParams);
		// }
		// });

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

				// System.out.println(h);
				// System.out.println(oldh);

				msg.what = change;
				// if (change==BIGGER){
				// showInfo("aaa");
				//
				// }else{
				// showInfo("bbb");
				// }
				handler.sendMessage(msg);

			}
		});

		iv_back = (ImageView) findViewById(R.id.nm_iv_logo_back);

		iv_send = (ImageView) findViewById(R.id.nm_iv_send);

		// et1.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)
		// et1.getLayoutParams(); // ȡ�ؼ�mGrid��ǰ�Ĳ��ֲ���
		// linearParams.height =
		// (int)(getApplicationContext().getResources().getDisplayMetrics().density*200+0.5f);//
		// ���ؼ��ĸ�ǿ�����50����
		// et1.setLayoutParams(linearParams);
		// }
		// });
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
				if (!stock.equals("")) {
					value = et1.getText().toString();
					if (!value.equals("")) {
						String content = "";
						try {
							content = URLEncoder.encode(et1.getText()
									.toString(), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// createMicropostNet(serUrl + "new_micropost_json?uid="
						// + uid + "&&content=" + content + "&&stock="
						// + stock + "&&token=" + token);
						try {
							RequestParams params = new RequestParams();
							params.put("micropost[content]", content);
							params.put("micropost[user_id]", uid);
							params.put("micropost[stock_id]",
									stock.split(",")[0]);
							if (!(url.equals(""))) {
								params.put("micropost[image]", new File(url));
							}
							AsyncHttpClient client = new AsyncHttpClient();
							client.setTimeout(1000);
							client.post(ConstantValue.SERVER_URL
									+ "add_micropost_test_api/", params,
									new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode,
												Header[] headers,
												JSONObject response) {
											// TODO Auto-generated method stub
											System.out.println("ok:"
													+ statusCode + " "
													+ headers.toString() + " "
													+ response.toString());
											super.onSuccess(statusCode,
													headers, response);
											try {
												if (response
														.getString("result")
														.equals("ok")) {
													finish();
												} else {
													showInfo("创建失败");
												}
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}

										@Override
										public void onFailure(int statusCode,
												Header[] headers,
												Throwable throwable,
												JSONObject errorResponse) {
											// TODO Auto-generated method stub
											System.out.println("failed:aaaaa"
													+ statusCode + " "
													+ headers);
											showInfo("创建失败");
											// super.onFailure(statusCode,
											// headers, throwable,
											// errorResponse);
										}
									});
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						showInfo("匿名信息为必填~");
					}
				} else {
					showInfo("股票代码为必填~");
				}

			}
		});

		actx.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					checkstock(serUrl + "stock_json?code="
							+ URLEncoder.encode(s.toString(), "UTF-8")
							+ "&maxRows=10");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				if (uri != null && "content".equals(uri.getScheme())) {
                    Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();   
                    url = cursor.getString(0);
                    File f=new File(url);
                    cursor.close();
                }
                else {
                	url = uri.getPath();
                }
				ImageSize targetSize = new ImageSize(200, 250);
				Bitmap bm = imageLoader.loadImageSync(data.getData().toString(), targetSize);
				iv_add_pic.setImageBitmap(bm);
				hasPic = true;
			}
		} else if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				url = "";
				iv_add_pic.setImageDrawable(getResources().getDrawable(
						R.drawable.pic_add));
				hasPic = false;
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) et1
					.getLayoutParams();
			switch (msg.what) {
			case BIGGER:
				linearParams.height = (int) (getApplicationContext()
						.getResources().getDisplayMetrics().density * 300 + 0.5f);// ���ؼ��ĸ�ǿ�����50����
				et1.setLayoutParams(linearParams);
				break;

			case SMALLER:
				linearParams.height = (int) (getApplicationContext()
						.getResources().getDisplayMetrics().density * 210 + 0.5f);// ���ؼ��ĸ�ǿ�����50����
				et1.setLayoutParams(linearParams);
				break;

			case NOT_RIGHT_STOCK_FORMAT:
				actx.setText("");
				showInfo("请输入股票代码或者股票名称！");
				break;
			}
		}
	};

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.new_micropost, menu);
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
	// case R.id.nm_send:
	// value = et1.getText().toString();
	// String content = et1.getText().toString();
	// String stock = actx.getText().toString();
	// // stock_id=sidList.get(stockList.indexOf(ac));
	// createMicropostNet(serUrl+"new_micropost_json?uid="
	// + uid + "&&content=" + content + "&&stock=" + stock+"&&token="
	// +token);
	//
	// break;
	// default:
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }
	//
	// private void setOverflownoShowingAlways() {
	// try {
	// ViewConfiguration config = ViewConfiguration.get(this);
	// Field menuKeyField = ViewConfiguration.class
	// .getDeclaredField("sHasPermanentMenuKey");
	// menuKeyField.setAccessible(true);
	// menuKeyField.setBoolean(config, true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

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
						finish();
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

					if ("[]".equals(value)) {
						Message msg = new Message();
						msg.what = NOT_RIGHT_STOCK_FORMAT;
						handler.sendMessage(msg);
					} else {
						JSONArray result = new JSONArray(value);
						stockList.clear();
						sidList.clear();
						for (int i = 0; i < result.length(); i++) {
							JSONObject item = result.getJSONObject(i);
							stockList.add(item.getString("code").toString()
									+ "," + item.getString("name").toString());
							sidList.add(item.getString("id").toString());
						}
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
