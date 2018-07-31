package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.ContactBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.ContactScreen;
import android.backup.screens.R;

public class ContactCheckBox extends BaseCheckBox implements BaseCheckBoxIface{

    @Override
    public int getCheckBoxID() {
        return R.id.CheckBox01;
    }
    
    @Override
    public String getCheckBoxName() {
        return "contacts";
    }

    @Override
    public String getTabTitle() {
        return "Contacts";
    }
    
    public Class getScreenClass() { 
        return ContactScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return ContactBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return ContactBackupRestore.saveData(acty);
    }
}
