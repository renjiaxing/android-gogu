package com.rjx.gogu02;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class testaty extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testaty);
		
		ListView lv=(ListView) findViewById(R.id.test_listView1);
		ArrayAdapter<String> aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		aa.add("aaa");
		aa.add("bbb");
		lv.setAdapter(aa);
	}
}
