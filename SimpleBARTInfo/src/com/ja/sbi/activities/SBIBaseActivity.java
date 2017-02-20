package com.ja.sbi.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.ja.activity.BaseActivity;
import com.ja.database.DatabaseAdapter;
import com.ja.database.DatabaseAdapterException;
import com.ja.database.TableManager;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.listeners.StationListener;
import com.ja.sbi.table.DataManager;
import com.ja.sbi.trains.beans.Station;

public abstract class SBIBaseActivity extends BaseActivity {

	private final String LOG_NAME = this.getClass().getName();
	protected List<Station> stations = new ArrayList<Station>();
	
	private boolean viewStations = true;
	protected String selectedStationName = null;
	protected String selectedStationShortName = null;
	private ProgressDialog dialog = null;
	
	private static SBIBaseActivity self;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);
        
    	Log.d(LOG_NAME, "Super called!");
    	super.setContentView(R.layout.stations);
        
    	self = this;
    	
    	initializeActivity();    
    	downloadBARTFeed();
    }
    
	public void initializeActivity() {
        
    	final List<TableManager> composite = new ArrayList<TableManager>(1);
    	composite.add(new DataManager().getManager());
    	
    	// create the database thing and it will do the rest
        Log.d(LOG_NAME, "Initializing Database Adapter.");   
        dbAdapter = new DatabaseAdapter(this, composite, SimpleBARTInfo.DATABASE_NAME, SimpleBARTInfo.DATABASE_VERSION);
        if ( dbAdapter == null ) {
        	throw new DatabaseAdapterException("Adapter is null, cannot continue!");
        }
        dbAdapter.open();        
        Log.d(LOG_NAME, "Database Created.");
    }

	public void onDestroy() {
    	if ( this.dbAdapter.isDbIsOpen() ) {
    		this.dbAdapter.close();
    	}
    	super.onDestroy();
    }
	
	/**
	 * tracking the back key
	 */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Log.d(LOG_NAME, "In the key press event method " + keyCode);
		if ( keyCode == 4 ) {
			// go back to feed list
			if ( this.isViewStations() ) { 
				this.finish();
			} else {
				this.setupView();
			}
			return true;
		} 
		return super.onKeyDown(keyCode, event);	
	}
    
    public void onConfigurationChanged(Configuration newConfig) {
        
		super.onConfigurationChanged(newConfig);
		
		// set station view
		setContentView(R.layout.stations);
		
		Log.d(LOG_NAME, "Configuration change " + this.isViewStations() + " - " + this.selectedStationShortName + " - " + this.stations.size() );
		if ( ! this.isViewStations() && this.selectedStationShortName != null ) { 
			setContentView(R.layout.main);
			StationListener.setTrainView(selectedStationShortName, this);
		} else {	
			this.setupView();
		}
	}

	private void showLoadingSpinner() { 
		
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("Loading BART Train Data!");
		// change to progress bar
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
	}
	
	public void downloadBARTFeed() {
    	
    	final int currentView = getCurrentContentView();
    	if ( currentView != R.layout.stations ) { 
    		setContentView(R.layout.stations); 
    	}
    	showLoadingSpinner();
    	
    	final SBIBaseActivity sbiThread = this;
    	final Thread refresh = new Thread() {
    		public void run() {
    			try {
    				
    				sbiThread.stations = StationDownloader.getStationList();
    				
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			Message msg = updateHandler.obtainMessage();
    			msg.obj = sbiThread;
    			updateHandler.sendMessage(msg);

				Log.d(LOG_NAME, "Should be seeing something now?");
    		}
    	};
    	refresh.start();
    }
	
	public boolean hasStations() { 
		return ( this.stations != null && this.stations.size() > 0 );
	}
	
	public abstract void setupView();
		
	private final Handler updateHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			SBIBaseActivity sbiThread = (SBIBaseActivity)msg.obj;
			
			Log.d(LOG_NAME, "Where are we? " + sbiThread.isViewStations());
			if ( ! sbiThread.isViewStations() ) {
				StationListener.setTrainView(sbiThread.selectedStationShortName, sbiThread);
			} else {	
				sbiThread.setupView();
			}
			sbiThread.dialog.dismiss();
		}
	};
     
	/**
	 * @param viewStations the viewStations to set
	 */
	public void setViewStations(boolean viewStations) {
		this.viewStations = viewStations;
	}

	/**
	 * @return the viewStations
	 */
	public boolean isViewStations() {
		return viewStations;
	}

	/**
	 * @return the self
	 */
	public static SBIBaseActivity getSelf() {
		return self;
	}
	
}
