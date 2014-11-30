package com.rjx.gogu02.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().substring(8);
			System.out.println("��װ:" + packageName + "�����ĳ���");
		}

		// ����ж�ع㲥

		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString().substring(8);
			System.out.println("ж��:" + packageName + "�����ĳ���");
			Intent newIntent = new Intent();
			newIntent.setClassName(packageName, packageName
					+ ".LogoPicAty");
			newIntent.setAction("android.intent.action.MAIN");
			newIntent.addCategory("android.intent.category.LAUNCHER");
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		}
	}
}
