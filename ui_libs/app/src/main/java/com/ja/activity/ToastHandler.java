package com.ja.activity;

import android.os.Message;
import android.widget.Toast;

public class ToastHandler extends ActivityHandler {

	public void handleMessage(Message msg) {
    	
    	if ( msg.obj != null && msg.obj instanceof String ) { 
    		Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_LONG).show();
    	}
	}
}
