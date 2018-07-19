package com.ja.dialog;

import android.app.ProgressDialog;
import android.content.Context;

// simplify ProgressDialog
public class LoadingSpinner {

    private static ProgressDialog dialog = null;

    public LoadingSpinner(Context context, String message) {

        // defaults for our loading spinner
        // dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(message);

        // change to progress bar
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
