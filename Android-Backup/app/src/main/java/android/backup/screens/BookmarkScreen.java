package android.backup.screens;

import java.util.List;
import java.util.Map;

import android.backup.screens.base.BaseListActivity;
import android.provider.Browser;
import android.widget.SimpleAdapter;

public class BookmarkScreen extends BaseListActivity {

    
    private static final String[] displayFields = new String[] {
            Browser.BookmarkColumns.TITLE,
            Browser.BookmarkColumns.URL
    };
    private static final int[] displayViews = new int[] { R.id.bmark_title, R.id.bmark_url };
    
    public void renderResultView(Object data) { 
        
        List<Map<String, String>> bookmarks = (List<Map<String, String>>)data;
        
        setListAdapter(new SimpleAdapter(this, bookmarks, 
                R.layout.bookmark,  
                displayFields, displayViews));
    }
}

