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

import java.io.File;
import java.util.List;

public class ImportExportListener implements Runnable {

    private LoadingSpinner dialog;
    private static MinnowRSS context;
    private static List<Table> feedsList;

    private final Class<?> _self = getClass();
    private final String TAG = _self.getName();

    private static final String backupFilename = "/MinnowRSS/feedlist.txt";

    public void exportFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;

        dialog = new LoadingSpinner((Context)activity, "Collecting feeds, please wait.");

        new Thread(this).start();
    }

    public void run() {
        try {
            feedsList = Constants.getFeedsservice().listFeeds(ImportExportListener.context);
            Log.d(TAG, "Got some data " + Integer.valueOf(feedsList.size()).toString());
            File exd = ImportExportListener.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (sd != null && sd.canWrite()) {
                Log.d(TAG, "Can write " + sd);
            }
            if (exd != null && exd.canWrite()) {
                Log.d(TAG, "Or write write " + exd);
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
