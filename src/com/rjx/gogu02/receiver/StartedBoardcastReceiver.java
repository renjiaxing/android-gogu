package com.rjx.gogu02.receiver;

import com.rjx.gogu02.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartedBoardcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startIntent=new Intent(context, NotificationService.class);
		context.startService(startIntent);
	}

}
