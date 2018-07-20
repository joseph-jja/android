package com.ja.minnow.listeners;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.ja.activity.BaseActivity;
import com.ja.database.Table;
import com.ja.dialog.BaseDialog;
import com.ja.events.ButtonEventListener;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;

public class EditFeedListener extends ButtonEventListener implements View.OnClickListener, Runnable { 

	private ProgressDialog dialog;
	private boolean success = false; 
	private List<Table> results; 
	private boolean saveFeed = false;
	
	public EditFeedListener(BaseActivity activity) { 
		super(activity);
	}
	
	/** 
     * process the on click event for the buttons
     */
    public void onClick(View v) {
    	final int vid = v.getId();
    	switch ( vid ) { 
        	case R.id.save_feed_button: 
        		dialog = BaseDialog.spinnerDialog((BaseActivity)this.activity, "Downloading, verifying, and saving feed, please wait.");
        		saveFeed = true;
        		new Thread(this).start();
        		break;
        	case R.id.main_button:		
        	case R.id.cancel_feed_button:
        		dialog = BaseDialog.spinnerDialog((BaseActivity)this.activity, "Gathering feeds_list, please wait.");
        		saveFeed = false;
        		success = true;
        		new Thread(this).start();
        	default:            	
            	break;
    	}
    }
	   
    public List<Integer> getButtonList() { 

    	buttons.add( R.id.main_button );

        buttons.add( R.id.save_feed_button );
        buttons.add( R.id.cancel_feed_button );
        
        return buttons;
    }
    
    public void run() {
    	try { 
    		if ( saveFeed ) { 
    			success = Constants.getFeedsservice().saveFeed((MinnowRSS)activity);
    		}
    		this.results = Constants.getFeedsservice().listFeeds((MinnowRSS)this.activity);
    	} catch (Exception ex) { 
    		ex.printStackTrace();
    	} finally {
    		loadingHandler.sendMessage(loadingHandler.obtainMessage());
        }
    }
	
    private final Handler loadingHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			if ( success ) { 
				activity.setContentView(R.layout.feeds_list);
				Constants.getFeeddataservice().setFeedID(-1);
                Constants.getFeedlistadapter().updateFeedList((MinnowRSS)activity, results);
				attachClickEventForView();
			} else {
				BaseDialog.alert((BaseActivity)activity, "Error", "Could not find feed at specified URL.");
			}
			dialog.dismiss();
		}
	};
}
