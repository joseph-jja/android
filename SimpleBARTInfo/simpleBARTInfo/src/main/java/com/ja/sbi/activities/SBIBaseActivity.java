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
import android.view.View;
import android.widget.CheckBox;

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

import android.content.Context;

public abstract class SBIBaseActivity {

    private final String LOG_NAME = this.getClass().getName();
    protected static List<Station> stations = new ArrayList<Station>();

    private boolean viewStations = true;
    protected String selectedStationName = null;
    protected String selectedStationShortName = null;
    private static ProgressDialog dialog = null;
    protected static SBIBaseActivity self = null;

    public void initializeActivity(Context context) {

        self = this;

        downloadBARTFeed(context);

        final List<TableManager> composite = new ArrayList<TableManager>(1);
        composite.add(new DataManager().getManager());

        // create the database thing and it will do the rest
        Log.d(LOG_NAME, "Initializing Database Adapter.");
        //dbAdapter = new DatabaseAdapter(this, composite, SimpleBARTInfo.DATABASE_NAME, SimpleBARTInfo.DATABASE_VERSION);
        //if ( dbAdapter == null ) {
        //	throw new DatabaseAdapterException("Adapter is null, cannot continue!");
        //}
        //dbAdapter.open();
        Log.d(LOG_NAME, "Database Created.");
    }

    //public void onDestroy() {
    //if ( this.dbAdapter.isDbIsOpen() ) {
    //	this.dbAdapter.close();
    //}
    //super.onDestroy();
    //}

    /**
     * tracking the back key
     */
    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
		
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
	}*/

    public void showLoadingSpinner(Context context) {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Loading BART Train Data!");
        // change to progress bar
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    public void downloadBARTFeed(Context context) {

        showLoadingSpinner(context);

        Log.d(LOG_NAME, "Launched spinner!");
        final SimpleBARTInfo sbiThread = (SimpleBARTInfo) context;
        final Thread refresh = new Thread() {
            public void run() {
                try {

                    SBIBaseActivity.stations = StationDownloader.getStationList();

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
        return (SBIBaseActivity.stations != null && SBIBaseActivity.stations.size() > 0);
    }

    public abstract void setupView(SimpleBARTInfo context);

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {
            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            //Log.d(LOG_NAME, "Where are we? " + sbiThread.isViewStations());
            //if ( ! self.isViewStations() ) {
            //	StationListener.setTrainView(sbiThread);
            //} else {
            self.setupView(sbiThread);
            //}
            SBIBaseActivity.dialog.dismiss();
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
}
