package com.ja.sbi.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.dialog.LoadingSpinner;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.StationListAdapter;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.beans.Station;
import com.ja.sbi.listeners.StationListener;

import java.util.ArrayList;
import java.util.List;

public class StationsHandler {

    private final String LOG_NAME = this.getClass().getName();
    protected static List<Station> stations = new ArrayList<Station>();

    private boolean viewStations = true;
    protected String selectedStationName = null;
    protected String selectedStationShortName = null;
    private static LoadingSpinner dialog = null;
    protected static StationsHandler self = null;

    public void initializeActivity(Context context, boolean useStale) {

        // only set this if it is not set
        if (StationsHandler.self == null) {
            StationsHandler.self = this;
        }

        if (useStale && StationsHandler.stations.size() > 0) {
            this.setupView((SimpleBARTInfo) context);
            return;
        }
        downloadBARTFeed(context);
    }

    public void downloadBARTFeed(Context context) {

        dialog = new LoadingSpinner(context, "Loading BART Station Data!");

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
        if (view == null) {
            Log.d(LOG_NAME, "Guess we did not find the view?");
            return;
        }

        final TextView tview = (TextView) view;
        tview.setText("Stations");

        final ListView feedList = (ListView) context.findViewById(R.id.st_list_rows);
        Log.d(LOG_NAME, "Do we have any stations? " + stations);

        final StationListAdapter adapter = new StationListAdapter(context, R.layout.stations_data_row, stations);
        feedList.setAdapter(adapter);
        feedList.setOnItemClickListener(new StationListener());

        this.setViewStations(true);
        selectedStationName = null;
        selectedStationShortName = null;
    }

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {
            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            self.setupView(sbiThread);
            StationsHandler.dialog.dismiss();
            StationsHandler.dialog = null;
        }
    };

    /**
     * @param viewStations the viewStations to set
     */
    public void setViewStations(boolean viewStations) {

        this.viewStations = viewStations;
    }

    public boolean getViewStations() {
        return this.viewStations;
    }

    /**
     * @return the viewStations
     */
    public boolean isViewStations() {
        return viewStations;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stationsList) {
        if (stations.size() > 0) {
            return;
        }
        stations.addAll(stationsList);
    }
}
