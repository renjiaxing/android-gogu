package com.rjx.gogu02.aty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.MicropostsAdapter;
import com.rjx.gogu02.adapter.TabAdapter;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.domain.Stock;
import com.rjx.gogu02.service.NotificationService;
import com.rjx.gogu02.update.UpdateManager;
import com.rjx.gogu02.utils.ConstantValue;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity  {
	private ViewPager mViewPager;
	private TabPageIndicator mTabPageIndicator;
	private TabAdapter mAdapter ;
	
	private SharedPreferences sp;
	private String user_id = "";
	private String token = "";
	
	private String msgunread = "";
	private String microunread = "";
	private String unreplymicro = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ImageLoader imageLoader = ImageLoader.getInstance();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.

		imageLoader.init(config);

		// CrashReport.testJavaCrash ();
		Intent serIntent = new Intent(this, NotificationService.class);
		startService(serIntent);

		UpdateManager updateManager = new UpdateManager(this);
		updateManager.checkUpdateInfo();

		sp = getSharedPreferences("login1", MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		token = sp.getString("token", "");		

		if (user_id.equals("")) {
			Intent back_it = new Intent(MainActivity.this, LoginAty.class);
			startActivity(back_it);
			finish();
		}

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		
		initView();
	}

	private void initView()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mTabPageIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
		mAdapter = new TabAdapter(getSupportFragmentManager(),user_id,token);
		mViewPager.setAdapter(mAdapter);
		
		mTabPageIndicator.setViewPager(mViewPager, 0);
		
		AsyncHttpClient httpClient = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("uid", user_id);
		params.put("token", token);

		httpClient.setTimeout(3000);

		httpClient.get(ConstantValue.MAIN_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode,
							Header[] headers, JSONObject result) {
						try {
							msgunread = result.getString("unreadnum");
							microunread = result.getString("unreadmicro");
							unreplymicro = result.getString("unreplymicro");
							String randint = result.getString("randint");
							
							getWindow().invalidatePanelMenu(
									Window.FEATURE_OPTIONS_PANEL);
							
							Editor et = sp.edit();
							et.putString("randint", randint);
							et.commit();
						} catch (JSONException e) {
							e.printStackTrace();
						}

						super.onSuccess(statusCode, headers, result);
					}

					@Override
					public void onFailure(int statusCode,
							Header[] headers, String responseString,
							Throwable throwable) {
						showInfo("网络错误，请稍后再试~");
						super.onFailure(statusCode, headers,
								responseString, throwable);
					}
				});
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item_msgunread = menu.findItem(R.id.main_menu_mychat);
		MenuItem item_microunread = menu.findItem(R.id.main_menu_my_micropost);
		MenuItem item_unreplymicro = menu.findItem(R.id.main_menu_my_reply);
		MenuItem item_more = menu.findItem(R.id.main_menu_more);

		item_more.setIcon(R.drawable.ic_action_more);
		
		if ((msgunread.equals("0") && microunread.equals("0")
				&& unreplymicro.equals("0"))||(msgunread.equals("") && microunread.equals("")
						&& unreplymicro.equals(""))) {
			item_more.setIcon(R.drawable.ic_action_more);
		} else {
			item_more.setIcon(R.drawable.ic_action_more_notice);
		}

		if (msgunread.equals("0")) {
			item_msgunread.setIcon(R.drawable.ic_action_chat);
		} else {
			item_msgunread.setIcon(R.drawable.ic_action_chat_unread);
		}

		if (microunread.equals("0")) {
			item_microunread.setIcon(R.drawable.ic_card_conversation_grey);
		} else {
			item_microunread.setIcon(R.drawable.ic_card_conversation_grey_new);
		}

		if (unreplymicro.equals("0")) {
			item_unreplymicro.setIcon(R.drawable.ic_action_info_single_chat);
		} else {
			item_unreplymicro.setIcon(R.drawable.ic_action_info_single_chat_un);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item_more = menu.findItem(R.id.main_menu_more);
		item_more.setIcon(R.drawable.ic_action_more);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_mystock:
			Intent it_mystock = new Intent(MainActivity.this, MyStockAty.class);
			startActivity(it_mystock);
			break;
		case R.id.main_menu_search:
			Intent it_search = new Intent(MainActivity.this, SearchAty.class);
			startActivity(it_search);
			break;
		case R.id.main_menu_new_micropost:
			Intent it_new_micropost = new Intent(MainActivity.this,
					NewMicropostAty.class);
			startActivity(it_new_micropost);
			break;
		case R.id.main_menu_my_micropost:
			Intent it_my_micropost = new Intent(MainActivity.this,
					MyMicropostAty.class);
			startActivity(it_my_micropost);
			finish();
			break;
		case R.id.main_menu_my_reply:
			Intent it_my_reply = new Intent(MainActivity.this, MyReplyAty.class);
			startActivity(it_my_reply);
			finish();
			break;
		case R.id.main_menu_mychat:
			Intent it_my_chat = new Intent(MainActivity.this, MyChatAty.class);
			startActivity(it_my_chat);
			finish();
			break;
		case R.id.main_menu_setting:
			Intent it_setting = new Intent(MainActivity.this, SettingsAty.class);
			startActivity(it_setting);
			break;
		case R.id.main_menu_logout:
			Editor ed = sp.edit();
			ed.clear();
			ed.commit();
			Intent it_logout = new Intent(MainActivity.this, LoginAty.class);
			startActivity(it_logout);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {

		initView();
		super.onResume();

	}
	
	public void showInfo(String info) {

		Toast.makeText(this.getApplicationContext(), info, Toast.LENGTH_SHORT)
				.show();
	}
}
	

