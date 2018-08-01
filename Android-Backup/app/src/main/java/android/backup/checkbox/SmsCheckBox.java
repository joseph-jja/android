package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.SmsBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.R;
import android.backup.screens.SmsScreen;

public class SmsCheckBox extends BaseCheckBox implements BaseCheckBoxIface{

    @Override
    public int getCheckBoxID() {
        return R.id.smsCheckbox;
    }

    
    @Override
    public String getCheckBoxName() {
        return "sms";
    }

    @Override
    public String getTabTitle() {
        return "SMS";
    }
    
    public Class getScreenClass() { 
        return SmsScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return SmsBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return SmsBackupRestore.saveData(acty);
    }
}
