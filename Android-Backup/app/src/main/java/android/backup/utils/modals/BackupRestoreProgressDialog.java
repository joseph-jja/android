package android.backup.utils.modals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.backup.screens.HomeScreen;
import android.os.Bundle;

public class BackupRestoreProgressDialog {

    private static ProgressDialog progressBar = null;

    /**
     * @return the progressBar
     */
    public static ProgressDialog getProgressBar() {
        return progressBar;
    }

    public void renderProgressBar(Activity atvy) { 

        progressBar = new ProgressDialog(atvy);
        progressBar.setCancelable(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Bundle bundle = atvy.getIntent().getExtras();
        if ( bundle != null && bundle.containsKey(HomeScreen.restoreKey) 
                && bundle.getBoolean(HomeScreen.restoreKey) == true ) 
        {
            progressBar.setMessage("Restoring data ...");
        } else {
            progressBar.setMessage("Backing up data ...");
        }
        progressBar.show();

    }
}
