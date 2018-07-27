package com.ja.sbi.listeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.TrainListAdapter;
import com.ja.sbi.bart.api.TrainDownloader;
import com.ja.sbi.beans.Station;
import com.ja.sbi.beans.Train;

import java.util.List;
import com.ja.dialog.LoadingSpinner;

public class StationListener implements AdapterView.OnItemClickListener {

    private final String LOG_NAME = this.getClass().getName();

    private static LoadingSpinner dialog = null;
    private static List<Station> trainStations;
    private static SimpleBARTInfo sbi;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final TextView stView = (TextView) view.findViewById(com.ja.sbi.R.id.station_short_name);
        final TextView tView = (TextView) view.findViewById(com.ja.sbi.R.id.st_name);

        final String stationName = tView.getText().toString();
        final String stationShortName = stView.getText().toString();

        TrainDownloader.setSelectedStationName(stationName);
        TrainDownloader.setSelectedStationShortName(stationShortName);

        Log.d(LOG_NAME, "Got station name =  " + stationName + " " + stationShortName + " " + id);

        if ( StationListener.sbi == null ) {
            StationListener.sbi = (SimpleBARTInfo) parent.getContext();
        }

        this.setTrainView(sbi);
    }

    public final void setTrainView(SimpleBARTInfo activity) {

        dialog = new LoadingSpinner(activity, "Loading BART Train Data!");

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    StationListener.trainStations = TrainDownloader.getTrains(TrainDownloader.getSelectedStationShortName());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Log.d(LOG_NAME, "Should be seeing something now?");

                final Message msg = updateHandler.obtainMessage();
                updateHandler.sendMessage(msg);
            }
        };
        refresh.start();
    }

    private static final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (StationListener.trainStations == null || StationListener.trainStations.size() < 1) {
                StationListener.dialog.dismiss();
                StationListener.dialog = null;
                return;
            }

            // should only be one train but....
            final List<Train> trains = StationListener.trainStations.get(0).getTrains();
            Log.d("StationListener", "Train count: " + trains);
            if (trains != null) {
                final View view = StationListener.sbi.findViewById(R.id.st_list_rows);
                if (view != null) {
                    final ListView feedList = (ListView) view;
                    feedList.setAdapter(new TrainListAdapter(StationListener.sbi, R.layout.data_row, trains));
                    feedList.setOnItemClickListener(null);
                    final TextView tview = (TextView) StationListener.sbi.findViewById(R.id.simple_bart_info_title);
                    tview.setText("Trains: " + TrainDownloader.getSelectedStationName());
                }
            }
            StationListener.dialog.dismiss();
            StationListener.dialog = null;
        }
    };
}
