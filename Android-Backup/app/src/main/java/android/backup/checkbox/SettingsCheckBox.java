package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.SystemSettingsBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.R;
import android.backup.screens.SystemSettingsScreen;

public class SettingsCheckBox extends BaseCheckBox implements BaseCheckBoxIface{

    @Override
    public int getCheckBoxID() {
        return R.id.CheckBox09;
    }
    
    @Override
    public String getCheckBoxName() {
        return "settings";
    }

    @Override
    public String getTabTitle() {
        return "Settings";
    }
    
    public Class getScreenClass() { 
        return SystemSettingsScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return SystemSettingsBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return SystemSettingsBackupRestore.saveData(acty);
    }
}
