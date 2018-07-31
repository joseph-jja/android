package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.BookmarkBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.BookmarkScreen;
import android.backup.screens.R;

public class BookmarkCheckBox extends BaseCheckBox implements BaseCheckBoxIface{

    @Override
    public int getCheckBoxID() {
        return R.id.CheckBox05;
    }
    
    @Override
    public String getCheckBoxName() {
        return "bookmarks";
    }

    @Override
    public String getTabTitle() {
        return "Bookmarks";
    }
    
    public Class getScreenClass() { 
        return BookmarkScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return BookmarkBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return BookmarkBackupRestore.saveData(acty);
    }
}
