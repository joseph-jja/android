package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.ApplicationBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.ApplicationScreen;
import android.backup.screens.R;

public class ApplicationCheckBox extends BaseCheckBox implements BaseCheckBoxIface {

    @Override
    public int getCheckBoxID() {
        return R.id.CheckBox06;
    }

    @Override
    public String getCheckBoxName() {
        return "applications";
    }

    @Override
    public String getTabTitle() {
        return "Applications";
    }
    
    public Class getScreenClass() { 
        return ApplicationScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return ApplicationBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return ApplicationBackupRestore.saveData(acty);
    }
}
