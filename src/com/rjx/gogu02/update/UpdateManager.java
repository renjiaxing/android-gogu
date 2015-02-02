package com.rjx.gogu02.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.rjx.gogu02.R;
import com.rjx.gogu02.utils.ConstantValue;

public class UpdateManager {
	
	private String serUrl=ConstantValue.SERVER_URL; 
	private Context mContext;

	// 提示语
	private String updateMsg = "有最新的软件包哦，修正了一下bug，请快下载吧~";

	private String apkUrl = ConstantValue.FILE_URL;

	private Dialog noticeDialog;

	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = "/Download/";

	private static final String saveFileName = savePath
			+ "gogu02.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private int progress;

	private Thread downLoadThread;

	private boolean interceptFlag = false;
	
	private NotificationManager mNotifyManager;
	private android.support.v4.app.NotificationCompat.Builder mBuilder;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:

				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo() {
		CheckVersion(ConstantValue.VERSION_URL);
	}
	
	public void CheckVersion(String url) {
		new AsyncTask<Object, Void, Object>() {

			@Override
			protected Object doInBackground(Object... params) {

				String urlString = (String) params[0];

				HttpGet get = new HttpGet(urlString);
				
				HttpClient client = new DefaultHttpClient();
				
				int versionCode=0;
				JSONObject object=new JSONObject();

				try {
					HttpResponse response = client.execute(get);
					String value = EntityUtils.toString(response.getEntity());
					object=new JSONObject(value.toString());
					

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				return object;

			}

			protected void onPostExecute(Object object) {
				int versionCode=0;
				int serverVersion=0;
				JSONObject jsonObject=(JSONObject)object;
				try {
					versionCode = mContext
							.getPackageManager()
							.getPackageInfo(
									mContext.getPackageName(), 0).versionCode;
					serverVersion=Integer.parseInt(jsonObject.getString("version"));
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				if (serverVersion>versionCode) {
					showNoticeDialog();
				}
			};
		}.execute(url);

	}

	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("后台下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mNotifyManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				mBuilder = new NotificationCompat.Builder(mContext);
				mBuilder.setContentTitle("股刺网").setSmallIcon(R.drawable.logo28);
				downloadApk();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			FileOutputStream fos = null;
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

//				File file = new File(savePath);
//				if (!file.exists()) {
//					file.mkdir();
//				}
//				String apkFile = saveFileName;
				File ApkFile = new File(mContext.getCacheDir(),"gogu02.apk");
				fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					updateProgress(progress);
//					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
//						mHandler.sendEmptyMessage(DOWN_OVER);
						mBuilder.setContentText("下载完毕~");
						
						Notification noti = mBuilder.build();
						noti.flags = android.app.Notification.FLAG_AUTO_CANCEL;
						mNotifyManager.notify(0, noti);
						mNotifyManager.cancel(0);
						installApk();
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};
	
	private void updateProgress(int progress) {
		//"正在下载:" + progress + "%"
		mBuilder.setContentText("正在下载").setProgress(100, progress, false);
		//setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
		PendingIntent pendingintent = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pendingintent);
		mNotifyManager.notify(0, mBuilder.build());
	}

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(mContext.getCacheDir(),"gogu02.apk");
		if (!apkfile.exists()) {
			return;
		}
		try {
			String[] command = {"chmod","777",apkfile.toString()};
            Log.i("gogu02", "command = " + command);
            Runtime runtime = Runtime.getRuntime(); 
            Process proc = runtime.exec(command);
           } catch (IOException e) {
            Log.i("gogu02","chmod fail!!!!");
            e.printStackTrace();
           }
		Intent i = new Intent(Intent.ACTION_VIEW);

		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}
}
