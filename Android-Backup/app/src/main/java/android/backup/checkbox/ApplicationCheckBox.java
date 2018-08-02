package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.ApplicationBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.ApplicationScreen;
import android.backup.screens.R;

import java.util.List;
import java.util.Map;

public class ApplicationCheckBox extends BaseCheckBox implements BaseCheckBoxIface {

    @Override
    public int getCheckBoxID() {
        return R.id.applicationCheckbox;
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
    
    public List<Map<String, String>> restoreData(Activity acty, boolean restore) {
        return ApplicationBackupRestore.restoreData(acty, restore);
    }

    public List<Map<String, String>> saveData(Activity acty) {
        return ApplicationBackupRestore.saveData(acty);
    }
}
