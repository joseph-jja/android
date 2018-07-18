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

public class StationListener implements AdapterView.OnItemClickListener {

    private final String LOG_NAME = this.getClass().getName();

    private static ProgressDialog dialog = null;
    private static List<Station> trainStations;

    public static void showLoadingSpinner(Context context) {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Loading BART Train Data!");
        // change to progress bar
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView stView = (TextView) view.findViewById(com.ja.sbi.R.id.station_short_name);
        TextView tView = (TextView) view.findViewById(com.ja.sbi.R.id.st_name);

        String stationName = tView.getText().toString();
        String stationShortName = stView.getText().toString();
        TrainDownloader.setSelectedStationName(stationName);
        TrainDownloader.setSelectedStationShortName(stationShortName);

        Log.d(LOG_NAME, "Got station name =  " + stationName + " " + stationShortName + " " + id);

        SimpleBARTInfo sbi = (SimpleBARTInfo) parent.getContext();

        this.setTrainView(sbi);
    }

    public final void setTrainView(SimpleBARTInfo activity) {

        showLoadingSpinner(activity);

        final SimpleBARTInfo sbiThread = activity;

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    StationListener.trainStations = TrainDownloader.getTrains(TrainDownloader.getSelectedStationShortName());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Log.d(LOG_NAME, "Should be seeing something now?");

                Message msg = updateHandler.obtainMessage();
                msg.obj = sbiThread;
                updateHandler.sendMessage(msg);
            }
        };
        refresh.start();
    }

    private static final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {

            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            if (StationListener.trainStations == null || StationListener.trainStations.size() < 1) {
                StationListener.dialog.dismiss();
                return;
            }

            // should only be one train but....
            List<Train> trains = StationListener.trainStations.get(0).getTrains();
            Log.d("StationListener", "Train count: " + trains);
            if (trains != null) {
                final View view = sbiThread.findViewById(R.id.st_list_rows);
                if (view != null) {
                    final ListView feedList = (ListView) view;
                    feedList.setAdapter(new TrainListAdapter(sbiThread, R.layout.data_row, trains));
                    feedList.setOnItemClickListener(null);
                    TextView tview = (TextView) sbiThread.findViewById(R.id.simple_bart_info_title);
                    tview.setText("Trains: " + TrainDownloader.getSelectedStationName());
                    //activity.setViewStations(false);
                }
            }
            StationListener.dialog.dismiss();
        }
    };
}
