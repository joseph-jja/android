package android.backup.screens;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.utils.modals.BackupRestoreProgressDialog;
import android.backup.utils.xml.parsers.XMLToMap;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TabHost;

public class TabScreen extends TabActivity implements Runnable {

    private final TabActivity self = this;
    public static final XMLToMap readData = new XMLToMap();
    private static final BackupRestoreProgressDialog progressBar = new BackupRestoreProgressDialog();
    private static final Map<String, Object> resultData = new HashMap<String, Object>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        
        resultData.clear();
        
        progressBar.renderProgressBar(this);
        
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        Message msg = new Message();
        
        Bundle bundle = self.getIntent().getExtras();
        boolean restore = false;
        if (bundle.getBoolean(HomeScreen.restoreKey) == true) {
            restore = true;
        }
        
        for ( BaseCheckBoxIface item: HomeScreen.backupItems ) {
            if ( bundle.getBoolean(item.getCheckBoxName()) ) {
                // save data first and then we can use the restore data in the view
                Object restoreResult = null;
                if ( restore ) {
                    restoreResult = item.restoreData(self, true);
                } else {
                    item.saveData(self);          
                    restoreResult = item.restoreData(self, false);
                }
                resultData.put(item.getScreenClass().getName(), restoreResult);
            }
        }
        
        msg.obj = self;
        msg.setTarget(spinner);
        msg.sendToTarget();
    }
    
    private void decideWhatToShow(TabActivity self, TabHost tabHost) {
        Bundle bundle = self.getIntent().getExtras();
        
        Intent intent;
        for ( BaseCheckBoxIface item: HomeScreen.backupItems ) {
            if ( bundle.getBoolean(item.getCheckBoxName()) ) {
                if ( item.getScreenClass() != null ) {
                    intent = new Intent().setClass(self, item.getScreenClass());
                    createTab(tabHost, intent, item.getTabTitle());
                }
            }
        }
    }

    private void createTab(TabHost tabHost, Intent intent, String tabName) {

        TabHost.TabSpec spec;
        Bundle bundle = getIntent().getExtras();

        // Initialize a TabSpec for each tab and add it to the TabHost
        if (bundle.getBoolean(HomeScreen.restoreKey) == true) {
            intent.putExtra(HomeScreen.restoreKey,true);
        }
        spec = tabHost.newTabSpec(tabName).setIndicator(tabName).setContent(
                intent);
        tabHost.addTab(spec);
    }
    
    /**
     * @return the resultData
     */
    public static final Map<String, Object> getResultData() {
        return resultData;
    }

    public static final Handler spinner = new Handler() {

        public void handleMessage(Message msg) { 
            ProgressDialog dlg = TabScreen.progressBar.getProgressBar();
            if ( dlg != null && dlg.isShowing() ) { 
                dlg.dismiss();
            }
            if ( msg.obj != null ) {
                TabScreen ts = (TabScreen)msg.obj;
                ts.decideWhatToShow(ts, ts.getTabHost());
            }
        }
    };
}
