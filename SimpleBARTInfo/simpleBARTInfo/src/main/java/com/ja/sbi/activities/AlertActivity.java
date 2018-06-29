package com.ja.sbi.activities;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ja.activity.BaseActivity;
import com.ja.sbi.R;
import com.ja.sbi.adapters.AlertAdapter;
import com.ja.sbi.bart.api.AlertDownloader;
import com.ja.sbi.trains.beans.Alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertActivity extends BaseActivity {

    private final String LOG_NAME = this.getClass().getName();
    private ProgressDialog dialog = null;
    private List<Alerts> alertItems = new ArrayList<Alerts>();
    private static AlertActivity self;

    public void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.alerts);

        self = this;

        initializeActivity();
    }

    @Override
    public void initializeActivity() {

        downloadAlerts();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        downloadAlerts();
    }

    private void showLoadingSpinner() {

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading BART Alerts...");
        // change to progress bar
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    public void downloadAlerts() {

        final int currentView = getCurrentContentView();
        if (currentView != R.layout.alerts) {
            setContentView(R.layout.alerts);
        }
        showLoadingSpinner();

        final AlertActivity sbiThread = this;
        final Thread refresh = new Thread() {
            public void run() {
                try {
                    alertItems.clear();
                    final String bartData = AlertDownloader.getAlertData();
                    final List<Alerts> data = AlertDownloader.parseAlerts(bartData);
                    alertItems.addAll(data);
                    Log.v(LOG_NAME, "XML result count: " + alertItems.size());
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

    public static AlertActivity getSelf() {
        return self;
    }

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {
            AlertActivity sbiThread = (AlertActivity) msg.obj;

            // render view
            final ListView lv = (ListView) sbiThread.findViewById(R.id.alerts_list_rows);

            lv.setAdapter(new AlertAdapter(sbiThread, R.layout.alert_row, sbiThread.alertItems));

            Log.d(LOG_NAME, "Got something.");
            sbiThread.dialog.dismiss();
        }
    };
}
