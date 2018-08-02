package android.backup.br;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.backup.screens.TabScreen;
import android.backup.utils.sort.PackageInfoSort;
import android.backup.utils.xml.XMLDocumentBuilder;
import android.backup.utils.xml.XMLDocumentWriter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ApplicationBackupRestore {

    public  static final String APPLICATION_BACKUP_FILENAME = "applications.xml";

    public static final String INSTALLED_TEXT = " (Installed)";

    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";  

    public static final List<Map<String, String>>  restoreData(Activity acty, boolean restore) {

        Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();

        Set<String> columns = new HashSet<String>();
        columns.add("appName");
        columns.add("pkgName");
        columns.add("versionName");
        columns.add("versionCode");
        names.put(2, columns);

        Set<String> appNames = new HashSet<String>();
        List<Map<String, String>> applications = getUserInstalledApplicationList(acty);
        for ( Map<String, String> app : applications ) { 
            appNames.add(app.get("appName"));
        }
        //Log.d(LOGGER_TAG, "Installed applications count = " + applications.size());

        try {
            Map<Integer, List<Map<String, String>>> results = TabScreen.readData.parseDocument(APPLICATION_BACKUP_FILENAME, names);

            List<Map<String, String>> apps = results.get(2);
            //Log.d(LOGGER_TAG, "Backedup applications count = " + apps.size());

            List<Map<String, String>> installedApps = new ArrayList<Map<String, String>>();
            for ( Map<String, String> data: apps ) { 
                String name = data.get("appName");
                if ( appNames.contains(name) ) {
                    data.put("appName", name + INSTALLED_TEXT );
                } else {
                    data.put("appName", name);
                }
                installedApps.add(data);
            }

            return installedApps;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static final List<Map<String, String>> saveData(Activity acty) {

        try {

            List<Map<String, String>> applications = getUserInstalledApplicationList(acty);

            if ( applications.size() > 0 ) { 
                Document appXML = null;
                Element parent = null;
                try {
                    appXML = XMLDocumentBuilder.createDocument();
                    parent = XMLDocumentBuilder.addNode(appXML, null, "applications", null);
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if ( parent != null ) {
                    for ( Map<String, String> app:  applications ) {
                        Element appXml = XMLDocumentBuilder.addNode(appXML, parent, "application", null);
                        Set<Map.Entry<String, String>> data = app.entrySet();
                        for ( Map.Entry<String, String> part : data ) { 
                            XMLDocumentBuilder.addNode(appXML, appXml, part.getKey(), part.getValue());
                        }
                    }
                }

                XMLDocumentWriter.writeDocument(APPLICATION_BACKUP_FILENAME, appXML);

                return applications;
            }
        } catch (Exception e) { 
            // TODO fix this
            e.printStackTrace();
        }

        return null;
    }

    private static final List<Map<String, String>> getUserInstalledApplicationList(Activity activity) {
        PackageManager pm = activity.getPackageManager();

        List<PackageInfo> installedApplications = pm.getInstalledPackages(PackageManager.GET_META_DATA);

        List<Map<String, String>> applications = new ArrayList<Map<String, String>>(installedApplications.size());

        if ( installedApplications == null || installedApplications.size() <= 0 ) { 
            return applications;
        }
        // sort list
        Collections.sort(installedApplications, new PackageInfoSort(pm));

        // TODO get correct version 
        for (PackageInfo applicaiton: installedApplications ) {
            ApplicationInfo ai = applicaiton.applicationInfo;
            // determine if this is system package or not
            boolean isSystemApp = ( ( ai.flags & ApplicationInfo.FLAG_SYSTEM ) == 1 );
            if ( ! isSystemApp ) {

                Map<String, String> line = new HashMap<String, String>();

                // for displaying the data
                line.put("appName", ai.loadLabel(activity.getPackageManager()).toString() );
                line.put("pkgName", applicaiton.packageName);
                line.put("versionName", applicaiton.versionName);
                line.put("versionCode", Integer.toString(applicaiton.versionCode));
                applications.add(line);
            }
        }
        return applications;
    }
}
