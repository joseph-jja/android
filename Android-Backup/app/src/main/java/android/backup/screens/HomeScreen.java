package android.backup.screens;

import android.app.Activity;
import android.backup.br.ApplicationBackupRestore;
import android.backup.checkbox.ApplicationCheckBox;
import android.backup.checkbox.SmsCheckBox;
import android.backup.checkbox.WallpaperCheckBox;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeScreen extends Activity {
    /**
     * Called when the activity is first created.
     */

    private final String LOG_TAG = this.getClass().getName();

    public static final List<BaseCheckBoxIface> backupItems = new ArrayList<BaseCheckBoxIface>();

    public static final String restoreKey = "read";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        boolean enabled = true;
        String message = "";
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            message = getResources().getString(R.string.sdcard_mount_required);
            enabled = false;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            message = getResources().getString(R.string.sdcard_read_write_mount_required);
            enabled = false;
        }

        if (enabled) {
            List<BaseCheckBoxIface> initialItems = new ArrayList<BaseCheckBoxIface>();
            initialItems.add(new ApplicationCheckBox());
            initialItems.add(new SmsCheckBox());
            initialItems.add(new WallpaperCheckBox());

            backupItems.clear();

            for (BaseCheckBoxIface item : initialItems) {
                View v = findViewById(item.getCheckBoxID());
                if (v != null) {
                    CheckBox cb = (CheckBox) v;
                    item.setCheckBox(cb);
                    item.getCheckBox().isChecked();
                    backupItems.add(item);
                }
            }
            View sdcardMount = findViewById(R.id.TextView01);
            if (sdcardMount != null) {
                ((TextView) sdcardMount).setText(getResources().getString(R.string.backup_restore_info_text));
            }
        } else {
            View sdcardMount = findViewById(R.id.TextView01);
            if (sdcardMount != null) {
                ((TextView) sdcardMount).setText(message);
                ((TextView) sdcardMount).setHeight(40);
            }
            View backup = findViewById(R.id.Button01);
            View restore = findViewById(R.id.Restore);
            if (backup != null) {
                ((Button) backup).setEnabled(false);
            }
            if (restore != null) {
                ((Button) restore).setEnabled(false);
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    public void CancelToHomeScreen(View view) {

        setContentView(R.layout.main);
    }

    public void SubmitClickHandler(View views) {

        boolean appChecked = false;
        boolean smsChecked = false;
        boolean wallpaperChecked = false;

        for (BaseCheckBoxIface item : backupItems) {
            CheckBox cb = (CheckBox) findViewById(item.getCheckBoxID());
            if (cb != null) {
                if (cb.isChecked()) {
                    switch (item.getCheckBoxID()) {
                        case R.id.applicationCheckbox:
                            appChecked = true;
                            break;
                        case R.id.smsCheckbox:
                            smsChecked = true;
                            break;
                        case R.id.wallpaperCheckbox:
                            wallpaperChecked = true;
                            break;
                    }
                }
            }
        }

        if (!appChecked && !smsChecked && !wallpaperChecked) {
            // TODO message user?
            return;
        }

        setContentView(R.layout.viewdetails);

        View appCardView = findViewById(R.id.applicationCardview);
        if (appCardView != null) {
            appCardView.setVisibility(View.INVISIBLE);
        }
        if (appChecked) {
            List<Map<String, String>> results = ApplicationBackupRestore.saveData(this);
            if (appCardView == null) {
                final ViewStub appStub = (ViewStub) findViewById(R.id.applicationViewStub);
                appStub.inflate();
                appCardView = findViewById(R.id.applicationCardview);
            }
            if (appCardView != null) {
                appCardView.setVisibility(View.VISIBLE);
                EditText apPText = (EditText) findViewById(R.id.applicationBackUpDetails);
                apPText.setText(Integer.toString(results.size()) + " apps were backed up.");
            }
        }

        View smsCardView = findViewById(R.id.smsCardview);
        if (smsCardView != null) {
            smsCardView.setVisibility(View.INVISIBLE);
        }
        if (smsChecked) {
            if (smsCardView != null) {
                smsCardView.setVisibility(View.VISIBLE);
            } else {
                final ViewStub smsStub = (ViewStub) findViewById(R.id.smsViewStub);
                smsStub.inflate();
                smsCardView = findViewById(R.id.smsCardview);
                if (smsCardView != null) {
                    smsCardView.setVisibility(View.VISIBLE);
                }
            }
        }

        View wallpaperCardView = findViewById(R.id.wallpaperCardview);
        if (wallpaperCardView != null) {
            wallpaperCardView.setVisibility(View.INVISIBLE);
        }
        if (wallpaperChecked) {
            if (wallpaperCardView != null) {
                wallpaperCardView.setVisibility(View.VISIBLE);
            } else {
                final ViewStub wallpaperStub = (ViewStub) findViewById(R.id.wallpaperViewStub);
                wallpaperStub.inflate();
                wallpaperCardView = findViewById(R.id.wallpaperCardview);
                if (wallpaperCardView != null) {
                    wallpaperCardView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void RestoreClickHandler(View views) {
        setContentView(R.layout.viewdetails);
        for (BaseCheckBoxIface item : backupItems) {
            if (item.getCheckBox().isChecked()) {

                switch (item.getCheckBoxID()) {
                    case R.id.applicationCheckbox:
                        ViewStub appStub = (ViewStub) findViewById(R.id.applicationViewStub);
                        appStub.inflate();
                        break;
                    case R.id.smsCheckbox:
                        ViewStub smsStub = (ViewStub) findViewById(R.id.smsViewStub);
                        smsStub.inflate();
                        break;
                    case R.id.wallpaperCheckbox:
                        ViewStub wallpaperStub = (ViewStub) findViewById(R.id.wallpaperViewStub);
                        wallpaperStub.inflate();
                        break;
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            View res = this.findViewById(R.id.applicationCheckbox);
            if (res == null) {
                setContentView(R.layout.main);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

