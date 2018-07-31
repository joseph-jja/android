package android.backup.screens;

import java.util.List;
import java.util.Map;

import android.backup.listeners.ApplicationListViewOnClickListener;
import android.backup.screens.base.BaseListActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ApplicationScreen extends BaseListActivity {

    public void renderResultView(Object data) { 

        String[] from = new String[] {
                "appName", "versionCode", "pkgName"
        };
        int to[] = new int[3];
        to[0] = R.id.applicationName;
        to[1] = R.id.applicationVersion;
        to[2] = R.id.applicationID;

        List<Map<String, String>> installedApps = (List<Map<String, String>>)data;
        setListAdapter(new SimpleAdapter(this, installedApps, R.layout.applications, from, to));

        ListView lv = getListView();
        if ( lv != null ) { 
            lv.setOnItemClickListener(new ApplicationListViewOnClickListener());
        }
    }
}

