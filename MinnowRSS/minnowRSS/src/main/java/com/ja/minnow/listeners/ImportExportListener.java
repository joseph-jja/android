package com.ja.minnow.listeners;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ja.activity.BaseActivity;
import com.ja.database.Table;
import com.ja.dialog.BaseDialog;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.dialog.LoadingSpinner;
import com.ja.minnow.R;
import com.ja.minnow.tables.FeedsTableData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportExportListener implements Runnable {

    private LoadingSpinner dialog;
    private static MinnowRSS context;
    private static List<Table> feedsList;

    private final Class<?> _self = getClass();
    private final String TAG = _self.getName();

    private static final String backupFilename = "MinnowRSS.csv";

    // default this to export
    private static boolean isExport = true;

    public void exportFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;
        ImportExportListener.isExport = true;

        dialog = new LoadingSpinner((Context) activity, "Collecting feeds, please wait.");

        new Thread(this).start();
    }

    public void importFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;
        ImportExportListener.isExport = false;

        dialog = new LoadingSpinner((Context) activity, "Collecting feeds, please wait.");

        new Thread(this).start();
    }

    public void run() {
        try {
            File exd = ImportExportListener.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String state = Environment.getExternalStorageState();


            File minnowRSSData = new File(sd, backupFilename);
            if (minnowRSSData != null && minnowRSSData.canWrite()) {
                Log.d(TAG, "Can write " + sd);
            }
            Log.d(TAG, "Or something " + Environment.isExternalStorageEmulated());
            if (exd != null && exd.canWrite()) {
                Log.d(TAG, "Or write " + exd);
            }

            if (ImportExportListener.isExport) {
                feedsList = Constants.getFeedsservice().listFeeds(ImportExportListener.context);
                Log.d(TAG, "Got some data " + Integer.valueOf(feedsList.size()).toString());

                // need to convert the data into something useful
                StringBuilder results = new StringBuilder();
                for ( Table tbl : feedsList) {
                    final String feedName = "\"" +  tbl.getColumnValue(FeedsTableData.NAME_COL).toString() + "\"";
                    final String feedUrl = "\"" + tbl.getColumnValue(FeedsTableData.URL_COL).toString() + "\"";
                    // write data in csv format
                    final String feedLine = feedName + "," + feedUrl + System.getProperty("line.separator");
                    results.append(feedLine);
                }
                // TODO actually write this out somewhere
            } else {

                feedsList = new ArrayList<Table>();
                // TODO read the data file in, if it exists and then add the tables in
                // should compare data
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            loadingHandler.sendMessage(loadingHandler.obtainMessage());
        }

    }

    private final Handler loadingHandler = new Handler() {
        public void handleMessage(Message msg) {

            //ImportExportListener.context.setContentView(R.layout.feeds_list);

            dialog.dismiss();
        }
    };
}
