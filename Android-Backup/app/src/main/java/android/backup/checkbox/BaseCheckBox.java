package android.backup.checkbox;

import android.backup.checkbox.iface.BaseCheckBoxIface;
import android.widget.CheckBox;

public abstract class BaseCheckBox implements BaseCheckBoxIface {

    private CheckBox checkBox;
    
    public CheckBox getCheckBox() { 
        return this.checkBox;
    }

    public void setCheckBox(CheckBox cb) {
        this.checkBox = cb;
    }
}
