package android.backup.screens;

import android.backup.screens.base.BaseListActivity;
import android.provider.ContactsContract;

public class ContactScreen extends BaseListActivity {
    
    @Override
    public void renderResultView(Object data) {

        String[] displayFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
        };
        int[] displayViews = new int[] { R.id.text1 };

        //Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

/*        setListAdapter(new SimpleCursorAdapter(this, 
                R.layout.contactrow, cur, 
                displayFields, displayViews));
*/
        
    }
}


