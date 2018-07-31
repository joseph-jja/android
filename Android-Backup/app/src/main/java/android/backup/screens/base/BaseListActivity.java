package android.backup.screens.base;

import android.app.ListActivity;
import android.backup.screens.TabScreen;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseListActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   

        final String name = this.getClass().getName();
        Log.d("BASE_LIST_ACTIVITY", "Class name is " + name);
        
        Object results = TabScreen.getResultData().get(name);
        if ( results != null ) {
            this.renderResultView(results);
        }
    }   
    
    public abstract void renderResultView(Object data);
}
