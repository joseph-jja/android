package com.ja.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.ja.activity.BaseActivity;

public class BaseDialog {

	public static final void alert(Activity activity, String title , String message) {
		
		AlertDialog dialog = new AlertDialog.Builder(activity).create();
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setButton( DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) { 
            	 dialog.dismiss();
            	 return;
             }
        }); 
		dialog.show();
	}
	
	public static final ProgressDialog spinnerDialog(Activity activity, String message ) { 

		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(false);
		dialog.setMessage(message);
		// change to progress bar
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
		return dialog;
	}
}
