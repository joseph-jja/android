package android.backup.utils.sort;

import java.util.Comparator;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageInfoSort implements Comparator<PackageInfo> {

    PackageManager pm = null;
    
    public PackageInfoSort(PackageManager pm ) { 
        this.pm = pm;
    }
    
    @Override
    public int compare(PackageInfo pi1, PackageInfo pi2) {
        
        if ( pi1 == null && pi2 == null ) { 
            return 0;
        }
        if ( pi1 != null && pi2 == null ) { 
            return 1;
        }
        if ( pi1 == null && pi2 != null ) {
            return -1;
        }
        final ApplicationInfo ai1 = pi1.applicationInfo;
        final ApplicationInfo ai2 = pi2.applicationInfo;
        
        if ( ai1 == null && ai2 == null ) { 
            return 0;
        }
        if ( ai1 != null && ai2 == null ) { 
            return 1;
        }
        if ( ai1 == null && ai2 != null ) { 
            return -1;
        }
        final String an1 = ai1.loadLabel(this.pm).toString();
        final String an2 = ai2.loadLabel(this.pm).toString();
        
        if ( an1 == null && an2 == null ) { 
            return 0;
        }
        return an1.compareTo(an2);
    }

}
