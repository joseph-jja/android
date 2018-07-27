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
import com.ja.sbi.adapters.AlertAdapter;
import com.ja.sbi.bart.api.AlertDownloader;
import com.ja.sbi.beans.Alerts;

import java.util.ArrayList;
import java.util.List;

public class AlertHandler {

    private final String LOG_NAME = this.getClass().getName();
    private static LoadingSpinner dialog = null;
    private static List<Alerts> alertItems = new ArrayList<Alerts>();

    public void showLoadingSpinner(Context context) {

        dialog = new LoadingSpinner(context, "Loading BART Alerts...");

    }

    public void downloadAlerts(Context context) {

        final Context contextCopy = context;

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
                msg.obj = contextCopy;
                updateHandler.sendMessage(msg);

                Log.d(LOG_NAME, "Should be seeing something now?");
            }
        };
        refresh.start();

    }

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {

            final SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            final View view = sbiThread.findViewById(R.id.bart_alert_title);
            if ( view == null ) {
                Log.d(LOG_NAME, "Guess we did not find the view?");
                return;
            }

            final TextView tview = (TextView)view;
            tview.setText("Alerts");

            // render view
            final ListView lv = (ListView) sbiThread.findViewById(R.id.alerts_list_rows);

            lv.setAdapter(new AlertAdapter(sbiThread, R.layout.alert_row, AlertHandler.alertItems));

            Log.d(LOG_NAME, "Got something.");
            AlertHandler.dialog.dismiss();
            AlertHandler.dialog = null;
        }
    };
}
