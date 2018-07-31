package android.backup.listeners;

import android.backup.br.ApplicationBackupRestore;
import android.backup.screens.R;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class ApplicationListViewOnClickListener implements AdapterView.OnItemClickListener {

    private String LOGGER_TAG = getClass().getName();
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        View appID = view.findViewById(R.id.applicationID);
        
        TextView tv = null;
        if ( appID != null ) { 
            tv = (TextView)appID;
        
            String name = tv.getText().toString();
            name = name.replace(ApplicationBackupRestore.INSTALLED_TEXT, "").trim();
            
            Log.d(LOGGER_TAG, "URL = " + ApplicationBackupRestore.PLAY_STORE_URL + name);
            
            Intent gStore = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationBackupRestore.PLAY_STORE_URL + name));
            parent.getContext().startActivity(gStore);
        }

    }

}
