package com.rjx.gogu02.receiver;

import com.rjx.gogu02.utils.ConstantValue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().substring(8);

		}

		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString().substring(8);
			if (ConstantValue.PACKAGENAME.equals(packageName)) {
				Intent newIntent = new Intent();
				newIntent.setClassName(packageName, packageName
						+ ".aty.LogoPicAty");
				newIntent.setAction("android.intent.action.MAIN");
				newIntent.addCategory("android.intent.category.LAUNCHER");
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}
		}
	}
}
