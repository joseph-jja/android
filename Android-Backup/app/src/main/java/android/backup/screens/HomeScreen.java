package android.backup.screens;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.backup.checkbox.ApplicationCheckBox;
import android.backup.checkbox.SmsCheckBox;
import android.backup.checkbox.WallpaperCheckBox;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class HomeScreen extends Activity {
    /**
     * Called when the activity is first created.
     */

    public static final List<BaseCheckBoxIface> backupItems = new ArrayList<BaseCheckBoxIface>();
    
    public static final String restoreKey = "read";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        boolean enabled = true;
        String message = "";
        String state = Environment.getExternalStorageState();
        if ( ! Environment.MEDIA_MOUNTED.equals(state) ) {
            message = getResources().getString(R.string.sdcard_mount_required);
            enabled = false;
        } else if ( Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {
            message = getResources().getString(R.string.sdcard_read_write_mount_required);
            enabled = false;
        } 

        if ( enabled ) { 
            List<BaseCheckBoxIface> initialItems = new ArrayList<BaseCheckBoxIface>();
            initialItems.add(new ApplicationCheckBox());
            initialItems.add(new SmsCheckBox());
            initialItems.add(new WallpaperCheckBox());

            backupItems.clear();

            for ( BaseCheckBoxIface item: initialItems ) {
                View v = findViewById(item.getCheckBoxID());
                if ( v != null ) { 
                    CheckBox cb = (CheckBox)v;
                    item.setCheckBox(cb);
                    backupItems.add(item);
                }
            }
            View sdcardMount = findViewById(R.id.TextView01);
            if ( sdcardMount != null ) {
                ((TextView)sdcardMount).setText(getResources().getString(R.string.backup_restore_info_text));
            }
        } else {
            View sdcardMount = findViewById(R.id.TextView01);
            if ( sdcardMount != null ) {
                ((TextView)sdcardMount).setText(message);
                ((TextView)sdcardMount).setHeight(40);
            }
            View backup = findViewById(R.id.Button01);
            View restore = findViewById(R.id.Restore);
            if ( backup != null ) { 
                ((Button)backup).setEnabled(false);
            }
            if ( restore != null ) { 
                ((Button)restore).setEnabled(false);
            }
        }
    }
    
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    public void SubmitClickHandler(View views) {
        Intent i = new Intent(this, TabScreen.class);
        for ( BaseCheckBoxIface item: backupItems ) {
            i.putExtra(item.getCheckBoxName(), item.getCheckBox().isChecked());
        }
        i.removeExtra(restoreKey);
        startActivity(i);
    }

    public void RestoreClickHandler(View views) {
        Intent i = new Intent(this, TabScreen.class);
        for ( BaseCheckBoxIface item: backupItems ) {
            i.putExtra(item.getCheckBoxName(), item.getCheckBox().isChecked());
        }
        i.putExtra(restoreKey, true);
        startActivity(i);
    }

}

