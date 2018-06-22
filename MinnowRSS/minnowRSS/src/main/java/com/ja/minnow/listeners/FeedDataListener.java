package com.ja.minnow.listeners;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ja.activity.BaseActivity;
import com.ja.database.Table;
import com.ja.dialog.BaseDialog;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;

public class FeedDataListener implements AdapterView.OnItemClickListener, Runnable {
	
	private final Class<?> _self = getClass();
	private final String LV_TAG = _self.getName();
	
	private MinnowRSS activity; 
	private ProgressDialog dialog;
	
	public FeedDataListener(MinnowRSS activity) {
		this.activity = activity;
	}

	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		if ( this.activity.getRefreshThread() != null && this.activity.getLocalThread() != null & this.activity.getLocalThread().isAlive() ) {
			this.activity.getRefreshThread().setSleepThread(true);
		}
		Constants.getFeedsservice().setFeedsListPosition(position);
		Constants.getFeeddataservice().setFeedID(-1);
		
		final String feedName = ((TextView)view.findViewById(R.id.feeds_row_name)).getText().toString();
		final String feedID = ((TextView)view.findViewById(R.id.feeds_row_id)).getText().toString();
		final String feedCount = ((TextView)view.findViewById(R.id.feeds_row_count)).getText().toString();
		
		dialog = BaseDialog.spinnerDialog((BaseActivity)this.activity, "Loading feed " + feedName + " , please wait.");
				
		// setup name count and serial number
		Constants.getFeeddataservice().setFeedID(Integer.parseInt(feedID));
		Constants.getFeeddataservice().setFeedCount(feedCount);
		Constants.getFeeddataservice().setFeedName(feedName);
		
		Log.d(LV_TAG, "Got feed id and name " + feedID + " " + feedName);
		
		if ( this.activity.getRefreshThread() != null && this.activity.getLocalThread() != null & this.activity.getLocalThread().isAlive() ) {
			this.activity.getRefreshThread().setSleepThread(false);
		}
		
		// tell the other thread to sleep again :)
		if ( this.activity.getRefreshThread() != null && this.activity.getLocalThread() != null & this.activity.getLocalThread().isAlive() ) {
			this.activity.getRefreshThread().setSleepThread(true);
		}
		Log.v(LV_TAG, "Position is everything " + position); 
		final Thread loadFeedData = new Thread(this);
		loadFeedData.start();		
	}

	@Override
	public void run() {
			
		try { 
			Log.d(LV_TAG, "Getting data from database for list view.");			
			final List<Table> results = Constants.getFeeddataservice().getAllFeedData(activity.getDbAdapter(), 
					Constants.getFeeddataservice().getFeedID());
			Log.d(LV_TAG, "Got data from database for list view, count = " + results.size());			

		} catch (Exception ex) { 
			ex.printStackTrace();
		} finally {
			progressHandler.sendMessage(progressHandler.obtainMessage());
		}
	}

	// handler for the background updating
    private final Handler progressHandler = new Handler() {
        
    	public void handleMessage(Message msg) {
        	
        	if ( Constants.getFeeddataservice().getFeedID() > 0 ) {
        		Constants.getFeeddataservice().setListPosition(0);
    			Constants.getFeeddatalistadapter().processData(activity);
        	}
        	dialog.dismiss();
        	Log.d(LV_TAG, "Got message " + msg);
        	if ( activity.getRefreshThread() != null && activity.getLocalThread() != null & activity.getLocalThread().isAlive() ) {
    			activity.getRefreshThread().setSleepThread(false);
    		}
        }
    };
}
