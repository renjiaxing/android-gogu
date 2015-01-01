package com.rjx.gogu02.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rjx.gogu02.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinMethod {
	
	private IWXAPI api;	
	final public static String APP_ID="wx93895a32017d8532";
	private Context context;

	
	public WeixinMethod(Context context) {
		this.context=context;
	}
	
	public boolean RegWxApi(){
	   api=WXAPIFactory.createWXAPI(context, APP_ID);
	   return api.registerApp(APP_ID);
	}
	
	public void UnregWxApi(){
		api.unregisterApp();
	}
	
	private void wechatShare(int flag){  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = "http://www.baidu.com";  
	    WXMediaMessage msg = new WXMediaMessage(webpage);  
	    msg.title = "百度";  
	    msg.description = "啦啦啦";  
	    //这里替换一张自己工程里的图片资源  
	    Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_green);  
	    msg.setThumbImage(thumb);  
	      
	    SendMessageToWX.Req req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	    api.sendReq(req);  
	} 

}
