package com.ja.sbi.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.adapters.StationListAdapter;
import com.ja.sbi.listeners.StationListener;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class StationsHandler {

    private final String LOG_NAME = this.getClass().getName();
    protected static List<Station> stations = new ArrayList<Station>();

    private boolean viewStations = true;
    protected String selectedStationName = null;
    protected String selectedStationShortName = null;
    private static ProgressDialog dialog = null;
    protected static StationsHandler self = null;

    public void initializeActivity(Context context, boolean useStale) {

        self = this;
        if (useStale && StationsHandler.stations.size() > 0) {
            self.setupView((SimpleBARTInfo)context);
            return;
        }
        downloadBARTFeed(context);

    }

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

                    StationsHandler.stations = StationDownloader.getStationList();

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
        return (StationsHandler.stations != null && StationsHandler.stations.size() > 0);
    }

    public void setupView(SimpleBARTInfo context) {
		
		final View view = context.findViewById(R.id.simple_bart_info_title);
		if ( view == null ) { 
			Log.d(LOG_NAME, "Guess we did not find the view?");
			return;
		}
    	
    	TextView tview = (TextView)view;
    	tview.setText("Stations");
    	
    	final ListView feedList = (ListView)context.findViewById(R.id.st_list_rows);
    	Log.d(LOG_NAME, "Do we have any stations? " + stations);
		feedList.setAdapter( new StationListAdapter(context, R.layout.data_row, stations) );
		feedList.setOnItemClickListener( new StationListener() );

		this.setViewStations(true);
		selectedStationName = null;
		selectedStationShortName = null;
	}
    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {
            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            self.setupView(sbiThread);
            StationsHandler.dialog.dismiss();
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
    
    public List<Station> getStations() {
        return this.stations;   
    }

    public void setStations(List<Station> stationsList) {
        if ( this.stations.size() > 0 ) {
            return;
        }
        this.stations.addAll(stationsList);
    }
}
