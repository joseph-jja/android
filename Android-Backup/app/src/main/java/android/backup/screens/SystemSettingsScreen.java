package android.backup.screens;

import java.util.List;
import java.util.Map;

import android.backup.screens.base.BaseListActivity;
import android.provider.Settings;
import android.widget.SimpleAdapter;

public class SystemSettingsScreen extends BaseListActivity {

//    private static final String SECURE_SETTINGS_FILE = "secure_settings.xml";
    
    private static final String[] displayFields = new String[] {
        Settings.System.NAME,
        Settings.System.VALUE
    };
        
    private static final int[] displayViews = new int[] { R.id.bmark_title, R.id.bmark_url };
    
    @Override
    public void renderResultView(Object data) {
       
        if ( data != null ) { 
            List<Map<String, String>> bookmarks = (List<Map<String, String>>)data;
            
            setListAdapter(new SimpleAdapter(this, bookmarks, 
                    R.layout.bookmark,  
                    displayFields, displayViews));
        }
    }
}
