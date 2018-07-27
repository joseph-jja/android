package com.ja.minnow.listeners;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ja.activity.BaseActivity;
import com.ja.database.Table;
import com.ja.dialog.BaseDialog;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.dialog.LoadingSpinner;
import com.ja.minnow.R;

import java.util.List;

public class ImportExportListener implements Runnable {

    private LoadingSpinner dialog;
    private static MinnowRSS context;
    private static List<Table> feedsList;

    public void exportFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;

        dialog = new LoadingSpinner((Context)activity, "Collecting feeds, please wait.");

    }

    public void run() {
        try {
            feedsList = Constants.getFeedsservice().listFeeds(ImportExportListener.context);
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
