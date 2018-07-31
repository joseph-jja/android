package android.backup.br;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.backup.screens.TabScreen;
import android.backup.utils.xml.CursorToXMLWriter;
import android.backup.utils.xml.beans.CursorToXML;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;

public class SystemSettingsBackupRestore {

    private static final String SETTINGS_FILE = "settings.xml"; 

    public static class ObjectData { 
        public Cursor systemSettings; 
        //        public Cursor secureSettings;
    };

    public static final Object restoreData(Activity acty, boolean restore) {

        Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();

        Set<String> columns = new HashSet<String>();
        columns.add(Settings.System.NAME);
        columns.add(Settings.System.VALUE);
        names.put(2, columns);

        try {
            // system settings
            Map<Integer, List<Map<String, String>>> results = TabScreen.readData.parseDocument(SETTINGS_FILE, names);

            List<Map<String, String>> settings = results.get(2);
            
            if ( restore ) {
                processResults(acty, settings, Settings.System.CONTENT_URI);
            }
            // secure settings
            //Map<Integer, List<Map<String, String>>> secureResults = TabScreen.readData.parseDocument(SECURE_SETTINGS_FILE, names);

            //List<Map<String, String>> secure = secureResults.get(2);

            //processResults(secure, Settings.Secure.CONTENT_URI);

            return settings;
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

    public static final Object saveData(Activity acty) {

        // settings
        Cursor cur = acty.getContentResolver().query(Settings.System.CONTENT_URI, null, null, null, null);

        Map<String, String> columns = new HashMap<String, String>();

        final CursorToXML ctoXML = new CursorToXML();
        ctoXML.setCur(cur);
        ctoXML.setFilename(SETTINGS_FILE);
        ctoXML.setTopLevelNode("settings");
        ctoXML.setSectionName("setting");
        ctoXML.setColumnNames(columns);
        ctoXML.setUseAttributes(false);


        CursorToXMLWriter.writeXML(ctoXML);

        //        // secure settings
        //        Cursor scur = getContentResolver().query(Settings.Secure.CONTENT_URI, null, null, null, null);
        //
        //        final CursorToXML secureXML = new CursorToXML();
        //        secureXML.setCur(scur);
        //        secureXML.setFilename(SECURE_SETTINGS_FILE);
        //        secureXML.setTopLevelNode("settings");
        //        secureXML.setSectionName("setting");
        //        secureXML.setColumnNames(columns);
        //        secureXML.setUseAttributes(false);
        //
        //        CursorToXMLWriter.writeXML(secureXML);

        ObjectData od = new ObjectData();
        od.systemSettings = cur;
        //        od.secureSettings = scur;

        return (Object)od;
    }

    private static final void processResults(Activity acty, List<Map<String, String>> results, Uri uri) { 

        for ( Map<String, String> data: results ) { 

            final String name = data.get(Settings.System.NAME);
            final String where = Settings.System.NAME + " = '" + name + "'";

            //Log.d(LOGGER_TAG, "Settings name = " + name);

            Cursor result = acty.getContentResolver().query(uri, null, where, null, null);

            if ( result.getCount() > 0 ) {
                ContentValues values = new ContentValues();

                values.put(Settings.System.NAME, name);
                values.put(Settings.System.VALUE, data.get(Settings.System.VALUE));

                acty.getContentResolver().update(uri, values, where, null);
            }
        }
    }
}
