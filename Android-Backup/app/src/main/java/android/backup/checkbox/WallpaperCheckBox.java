package android.backup.checkbox;

import android.app.Activity;
import android.backup.br.WallpaperBackupRestore;
import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.backup.screens.R;
import android.backup.screens.WallpaperScreen;

public class WallpaperCheckBox extends BaseCheckBox implements BaseCheckBoxIface{

    @Override
    public int getCheckBoxID() {
        return R.id.CheckBox07;
    }

    @Override
    public String getCheckBoxName() {
        return "wallpaper";
    }

    @Override
    public String getTabTitle() {
        return "Wallpaper";
    }
    
    public Class getScreenClass() { 
        return WallpaperScreen.class;
    }
    
    public Object restoreData(Activity acty, boolean restore) {
        return WallpaperBackupRestore.restoreData(acty, restore);
    }

    public Object saveData(Activity acty) {
        return WallpaperBackupRestore.saveData(acty);
    }
}
