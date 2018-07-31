package com.ja.minnow.listeners;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ja.database.DatabaseAdapter;
import com.ja.database.Table;
import com.ja.dialog.BaseDialog;
import com.ja.dialog.LoadingSpinner;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.services.FeedsService;
import com.ja.minnow.tables.FeedsTableData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportExportListener implements Runnable {

    private LoadingSpinner dialog;
    private static MinnowRSS context;
    private static List<Table> feedsList;

    private final Class<?> _self = getClass();
    private final String TAG = _self.getName();

    private static final String backupFilename = "MinnowRSS.csv";
    private static final String EXPORT_IMPORT_DIR = "MinnowRSS";

    // default this to export
    private static boolean isExport = true;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 2;

    public void exportFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;
        ImportExportListener.isExport = true;

        boolean weGotPermissions = false;
        if (ContextCompat.checkSelfPermission(ImportExportListener.context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            weGotPermissions = true;
        }

        // no permisiions so show message asking about them
        if (!weGotPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImportExportListener.context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user 
                BaseDialog.alert(ImportExportListener.context,
                        "Permission request",
                        "Sorry, this needs permissions to access internal storage to export the data.");
            } else {
                ActivityCompat.requestPermissions(ImportExportListener.context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }

        // only call this if we have permissions
        if (weGotPermissions) {
            execute(ImportExportListener.isExport);
        }
    }

    public void execute(boolean isExportSelected) {

        ImportExportListener.isExport = isExportSelected;

        dialog = new LoadingSpinner((Context) ImportExportListener.context, "Collecting feeds, please wait.");

        new Thread(this).start();
    }

    public void importFeeds(MinnowRSS activity) {

        ImportExportListener.context = activity;
        ImportExportListener.isExport = false;

        boolean weGotPermissions = false;
        if (ContextCompat.checkSelfPermission(ImportExportListener.context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            weGotPermissions = true;
        }

        // no permisiions so show message asking about them
        if (!weGotPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImportExportListener.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user
                BaseDialog.alert(ImportExportListener.context,
                        "Permission request",
                        "Sorry, this needs permissions to access internal storage to export the data.");
            } else {
                ActivityCompat.requestPermissions(ImportExportListener.context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }

        // only call this if we have permissions
        if (weGotPermissions) {
            execute(ImportExportListener.isExport);
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File makeExternalDirs(boolean createFile) throws IOException {

        if (!isExternalStorageWritable()) {
            Log.d(TAG, "Booo we cannot write!");
            return null;
        }

        File sd = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIR);
        if (!sd.exists() && !sd.mkdirs()) {
            Log.d(TAG, "We tried to write the dirs, but what the fudge write?");
            return null;
        }

        File minnowRSSData = new File(sd, backupFilename);

        if (createFile && !minnowRSSData.exists() && !minnowRSSData.createNewFile()) {
            Log.d(TAG, "We tried to write the file, but no love!");
            return null;
        }
        return minnowRSSData;
    }

    public void run() {
        try {

            if (ImportExportListener.isExport) {
                feedsList = Constants.getFeedsservice().listFeeds(ImportExportListener.context);
                Log.d(TAG, "Got some data " + Integer.valueOf(feedsList.size()).toString());

                // need to convert the data into something useful
                StringBuilder results = new StringBuilder();
                for (Table tbl : feedsList) {
                    final String feedName = tbl.getColumnValue(FeedsTableData.NAME_COL).toString();
                    final String feedUrl = tbl.getColumnValue(FeedsTableData.URL_COL).toString();
                    // write data in csv format
                    final String feedLine = feedName + "," + feedUrl + System.getProperty("line.separator");
                    results.append(feedLine);
                }

                // actually write this out somewhere
                final File datafile = makeExternalDirs(true);
                if (datafile != null) {
                    Log.d(TAG, "We got a file name: " + datafile.getAbsolutePath());
                    FileWriter ostream = new FileWriter(datafile);
                    BufferedWriter bostream = new BufferedWriter(ostream);
                    bostream.write(results.toString());
                    bostream.flush();
                    bostream.close();
                    Log.d(TAG, "We should have written data: " + results.toString());
                }

            } else {
                feedsList = new ArrayList<Table>();

                DatabaseAdapter adapter = ImportExportListener.context.getDbAdapter();
                final FeedsService importService = new FeedsService();

                // delete any feeds
                List<Table> feeds = Constants.getFeedsservice().listFeeds(ImportExportListener.context);
                for ( Table tbl: feeds) {
                    Constants.getFeeddataservice().deleteAllFeedData(adapter, tbl.getId());
                    adapter.deleteById(FeedsTableData.FEEDS_TABLE, tbl.getId());
                }

                // get the filename
                final File datafile = makeExternalDirs(false);
                if (datafile != null) {
                    Log.d(TAG, "We got a file name: " + datafile.getAbsolutePath());
                    FileReader oread = new FileReader(datafile);
                    BufferedReader booread = new BufferedReader(oread);
                    String data = booread.readLine();
                    while ( data != null ) {
                        final String line = data.trim();
                        Log.d(TAG, "We got a line of data: " + line);
                        if ( line.length() > 0 ) {
                            final String parts[] = line.split(",");
                            if ( parts.length > 1) {
                                Map<String, String> row = new HashMap<String, String>();
                                row.put(FeedsTableData.NAME_COL, parts[0]);
                                row.put(FeedsTableData.URL_COL, parts[1].replace("\"", ""));
                                importService.importRow(adapter, row);
                            }
                        }
                        data = booread.readLine();
                    }
                    booread.close();
                }

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
