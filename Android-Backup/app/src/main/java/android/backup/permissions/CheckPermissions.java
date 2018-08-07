package android.backup.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class CheckPermissions {


    public int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    public void foo(Context context, Activity activity) {
        boolean weGotPermissions = false;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            // no permisiions so show message asking about them
            if (!weGotPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user
                    //BaseDialog.alert(context,
                    //        "Permission request",
                    //       "Sorry, this needs permissions to access internal storage to export the data.");
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            }

            weGotPermissions = true;
        }    }

}
