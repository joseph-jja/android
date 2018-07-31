package android.backup.checkbox.iface;

import android.app.Activity;
import android.widget.CheckBox;

/**
 * simple class to make updates to the project easier 
 * 
 * @author Joe
 *
 */
public interface BaseCheckBoxIface {
        
    public int getCheckBoxID();
    
    public void setCheckBox(CheckBox cb);

    public CheckBox getCheckBox();
    
    public String getCheckBoxName();
    
    public String getTabTitle();
    
    public Class getScreenClass();

    public Object restoreData(Activity acty, boolean restore);

    public Object saveData(Activity acty);
}
