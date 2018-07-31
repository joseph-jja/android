package android.backup.screens;

import java.util.List;
import java.util.Map;

import android.backup.constants.SmsConstants;
import android.backup.screens.base.BaseListActivity;
import android.widget.SimpleAdapter;

public class SmsScreen extends BaseListActivity {

    
    public void renderResultView(Object data) {

        List<Map<String, String>> cur = (List<Map<String, String>>)data;
        
        final String[] displayFields = new String[] {
                SmsConstants.ADDRESS, SmsConstants.BODY
        };
        final int[] displayViews = new int[] { R.id.text1, R.id.text2 };

        setListAdapter(new SimpleAdapter(this, cur,
                R.layout.contactrow,  
                displayFields, displayViews));
    }
    
    
    
//    private String getSMSBackupCompatibleFilename() {
//        
//        StringBuilder fname = new StringBuilder();
//        
//        Calendar cal = Calendar.getInstance();
//        
//        fname.append(cal.get(Calendar.YEAR));
//        fname.append( formatTwoCharacter( Integer.toString(cal.get(Calendar.MONTH) + 1) ) );
//        fname.append( formatTwoCharacter( Integer.toString(cal.get(Calendar.DATE)) ) );
//        
//    long hour = cal.get(Calendar.HOUR);
//    fname.append( ((hour == 0)?12:hour) );
    // fname.append( formatTwoCharacter(cal.get(Calendar.MINUTE)) );
//    fname.append( formatTwoCharacter(cal.get(Calendar.SECOND)) );
////        
//        return fname.toString();
//    }
}
