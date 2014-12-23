package com.rjx.gogu02.utils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NetworkResources {
	
	
	static public void NetResource(String url, Integer mod,String value,HttpClient client,Handler handler) {
			
		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {

				String urlString = (String) params[0];
				Integer mod = (Integer) params[1];
				String value=(String) params[2];
				HttpClient client=(HttpClient) params[3];
				Handler handler=(Handler)params[4];

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
			

		}.execute(url, mod,value,client,handler);

	}
	
	static public void showInfo(String info,Context context) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT)
				.show();
	}
	
	

}
