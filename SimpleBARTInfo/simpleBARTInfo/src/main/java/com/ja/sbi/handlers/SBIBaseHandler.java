package com.ja.sbi.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.Station;

import java.util.ArrayList;
import java.util.List;

public abstract class SBIBaseHandler {

    private final String LOG_NAME = this.getClass().getName();
    protected static List<Station> stations = new ArrayList<Station>();

    private boolean viewStations = true;
    protected String selectedStationName = null;
    protected String selectedStationShortName = null;
    private static ProgressDialog dialog = null;
    protected static SBIBaseHandler self = null;

    public void initializeActivity(Context context) {

        self = this;

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

                    SBIBaseHandler.stations = StationDownloader.getStationList();

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
        return (SBIBaseHandler.stations != null && SBIBaseHandler.stations.size() > 0);
    }

    public abstract void setupView(SimpleBARTInfo context);

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {
            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            self.setupView(sbiThread);
            SBIBaseHandler.dialog.dismiss();
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
