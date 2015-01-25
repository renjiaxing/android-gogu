package com.rjx.gogu02.service;

import java.net.URISyntaxException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.LogoPicAty;
import com.rjx.gogu02.aty.MainActivity;
import com.rjx.gogu02.aty.MyChatAty;
import com.rjx.gogu02.aty.MyMicropostAty;
import com.rjx.gogu02.aty.MyReplyAty;
import com.rjx.gogu02.utils.ConstantValue;

public class NotificationService extends Service {

	private SharedPreferences sp;
	private String uid, token, last_uid = "";
	// private CommandReceiver receiver=new CommandReceiver();
	private int test1 = 0;
	private Integer sys_push = 1;
	private Integer reply_push = 1;
	private Integer msg_push = 1;
	private Integer my_reply_push = 1;
	private Integer room_connect = 0;
	private Integer room=0;
	private Socket socket = null;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		try {
			// IO.Options opts = new IO.Options();
			// opts.forceNew = true;
			// opts.reconnection = false;
			socket = IO.socket(ConstantValue.SERVER_URL.substring(0,
					ConstantValue.SERVER_URL.length() - 1) + ":8080/");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				// System.out.println("connected");
				// socket.emit("subscribe", {"room":uid});
				// if (!uid.equals("")) {
				// JSONObject obj = new JSONObject();
				// try {
				// obj.put("room", uid);
				// socket.emit("subscribe", obj);
				// System.out.println("connected room " + uid);
				// room_connect = 1;
				// last_uid=uid;
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

				sp = getSharedPreferences("login1", MODE_PRIVATE);
				uid = sp.getString("user_id", "");
				token = sp.getString("token", "");

				// if (uid.equals("") && (room_connect == 1) &&
				// (!last_uid.equals(""))) {
				// JSONObject obj = new JSONObject();
				// try {
				// obj.put("room", last_uid);
				// socket.emit("unsubscribe", obj);
				// last_uid = "";
				// room_connect = 0;
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

				// if ((!uid.equals("")) && (room_connect == 0)) {
				if (!uid.equals("")) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("room", uid);
						socket.emit("subscribe", obj);
						System.out.println("connected room " + uid);
						room_connect = 1;
						last_uid = uid;
						System.out.println(room_connect);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// }

			}

		}).on("chat message", new Emitter.Listener() {

			@Override
			public void call(Object... arg0) {

				sp = getSharedPreferences("login1", MODE_PRIVATE);
				uid = sp.getString("user_id", "");
				token = sp.getString("token", "");

				sys_push = sp.getInt("sys_status", 1);
				reply_push = sp.getInt("reply_status", 1);
				msg_push = sp.getInt("msg_status", 1);

				System.out.println(uid + " " + sys_push + " " + reply_push
						+ " " + msg_push + " " + room_connect);

				if (!isTopAty()) {

					try {
						JSONObject obj = new JSONObject(arg0[0].toString());
						System.out.println(obj);

						// if (("1".equals(obj.getString("msgtype")))
						// && (sys_push == 1)) {
						// pushNotification(obj);
						// }
						//
						// if ((("3".equals(obj.getString("msgtype")))
						// && (uid.equals(obj.getString("user_id"))) &&
						// (msg_push == 1))
						// || (("2".equals(obj.getString("msgtype")))
						// && (uid.equals(obj
						// .getString("user_id"))) && (reply_push == 1))
						// || (("1".equals(obj.getString("msgtype"))) &&
						// (sys_push == 1))) {
						if ((!"".equals(uid))
								|| ("1".equals(obj.getString("msgtype")))) {
							if (sys_push == 1 || reply_push == 1
									|| msg_push == 1 || my_reply_push == 1) {
								pushNotification(obj);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				// JSONObject obj = new JSONObject();
				// try {
				// if (!uid.equals("")) {
				// obj.put("room", uid);
				// socket.emit("unsubscribe", obj);
				// last_uid = "";
				// room_connect = 0;
				// }
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}

		}).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... arg0) {
				// JSONObject obj = new JSONObject();
				// if ((!uid.equals(""))) {
				// try {
				// obj.put("room", uid);
				// socket.emit("subscribe", obj);
				// room_connect = 1;
				// last_uid = uid;
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

			}
		});
		socket.connect();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// System.out.println(uid+token);

		// IntentFilter itfFilter=new IntentFilter();
		// itfFilter.addAction("com.rjx.gogu02.PUSHINFO");
		// registerReceiver(receiver, itfFilter);
		sp = getSharedPreferences("login1", MODE_PRIVATE);
		uid = sp.getString("user_id", "");
		token = sp.getString("token", "");
		
		if(uid.equals("")&&(!last_uid.equals(""))){
			JSONObject obj = new JSONObject();
			try {
				obj.put("room", last_uid);
				socket.emit("unsubscribe", obj);
				System.out.println("connected room " + last_uid);
				last_uid = uid;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!uid.equals(last_uid)){
			JSONObject obj = new JSONObject();
			try {
				obj.put("room", uid);
				socket.emit("subscribe", obj);
				System.out.println("connected room " + uid);
				last_uid = uid;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		return super.onStartCommand(intent, flags, startId);
	}

	// private class CommandReceiver extends BroadcastReceiver{
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// test1=intent.getIntExtra("mod", -1);
	// }
	//
	//
	// }

	@Override
	public void onDestroy() {
		socket.close();
		super.onDestroy();
	}

	private void pushNotification(JSONObject obj) throws JSONException {
		NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(
				getApplication());
		nbuilder.setContentTitle(obj.getString("title"));
		nbuilder.setSmallIcon(R.drawable.logo28);
		// nbuilder.setLargeIcon(BitmapFactory.decodeResource(
		// getResources(), R.drawable.logo28));
		nbuilder.setContentText(obj.getString("content"));
		nbuilder.setDefaults(Notification.DEFAULT_ALL);
		nbuilder.setTicker(obj.getString("topshow"));
		Intent mainAtyIt;

		if (uid.equals("")) {
			mainAtyIt = new Intent(getApplication(), LogoPicAty.class);
		} else if (obj.getString("msgtype").equals("1")) {
			mainAtyIt = new Intent(getApplication(), MainActivity.class);
		} else if (obj.getString("msgtype").equals("2")) {
			mainAtyIt = new Intent(getApplication(), MyMicropostAty.class);
		} else if (obj.getString("msgtype").equals("3")) {
			mainAtyIt = new Intent(getApplication(), MyChatAty.class);
		} else {
			mainAtyIt = new Intent(getApplication(), MyReplyAty.class);
		}

		PendingIntent pi = PendingIntent.getActivity(getApplication(), 0,
				mainAtyIt, PendingIntent.FLAG_UPDATE_CURRENT);

		nbuilder.setContentIntent(pi);

		nbuilder.setAutoCancel(true);

		Notification n = nbuilder.build();

		NotificationManager nm = (NotificationManager) getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		nm.notify(Integer.parseInt(obj.getString("msgtype")), n);
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
