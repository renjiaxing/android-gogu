package com.rjx.gogu02.service;

import java.net.URISyntaxException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.MainActivity;
import com.rjx.gogu02.utils.ConstantValue;

public class NotificationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		final Socket socket;
		try {
			socket = IO.socket(ConstantValue.SERVER_URL.substring(0,ConstantValue.SERVER_URL.length()-1)+":8080/");
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
//					 System.out.println("connected");
				}

			}).on("chat message", new Emitter.Listener() {

				@Override
				public void call(Object... arg0) {

					if (!isTopAty()) {

						try {
							JSONObject obj = new JSONObject(arg0[0].toString());
							NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(
									getApplication());
							nbuilder.setContentTitle(obj.getString("title"));
							nbuilder.setSmallIcon(R.drawable.logo28);
//							nbuilder.setLargeIcon(BitmapFactory.decodeResource(
//									getResources(), R.drawable.logo28));
							nbuilder.setContentText(obj.getString("content"));
							nbuilder.setDefaults(Notification.DEFAULT_ALL);
							nbuilder.setTicker(obj.getString("topshow"));

							Intent mainAtyIt = new Intent(getApplication(),
									MainActivity.class);

							PendingIntent pi = PendingIntent.getActivity(
									getApplication(), 0, mainAtyIt,
									PendingIntent.FLAG_UPDATE_CURRENT);

							nbuilder.setContentIntent(pi);

							nbuilder.setAutoCancel(true);

							Notification n = nbuilder.build();

							NotificationManager nm = (NotificationManager) getApplication()
									.getSystemService(
											Context.NOTIFICATION_SERVICE);

							nm.notify(Integer.parseInt(obj.getString("msgtype")), n);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			});
			socket.connect();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean isTopAty() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			
			// 应用程序位于堆栈的顶层
			if (ConstantValue.PACKAGENAME.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}
