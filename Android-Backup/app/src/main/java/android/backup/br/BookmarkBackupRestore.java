package android.backup.br;

import java.io.IOException;
import java.util.Arrays;
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
import android.provider.Browser;
import android.util.Base64;

public class BookmarkBackupRestore {

    private static final String[] projection = new String[] {
        Browser.BookmarkColumns.CREATED,
        Browser.BookmarkColumns.TITLE,
        Browser.BookmarkColumns.URL,
        Browser.BookmarkColumns.FAVICON
    };
    private static final String where = Browser.BookmarkColumns.BOOKMARK + " = 1 "; 

    private static final String datafile = "bookmarks.xml";

    public static final Object restoreData(Activity acty, boolean restore) {

        Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();

        Set<String> columns = new HashSet<String>();
        columns.addAll(Arrays.asList(projection));
        names.put(2, columns);

        try {
            Map<Integer, List<Map<String, String>>> results = TabScreen.readData.parseDocument(datafile, names);
            //Log.v(LOGGER_TAG, "Size of parsed data = " + results.size());

            List<Map<String, String>> bookmarks = results.get(2);
            //Log.v(LOGGER_TAG, "Size of bookmarks data = " + bookmarks.size());

            if ( restore ) {
                acty.getContentResolver().delete(android.provider.Browser.BOOKMARKS_URI, where, null);
            
                // TODO fix the restore of the favicon
                for ( Map<String, String> data: bookmarks ) { 
                    ContentValues values = new ContentValues();
                    Set<Map.Entry<String, String>> dataParts = data.entrySet();
                    for ( Map.Entry<String, String> entry : dataParts ) {
                        if ( entry.getKey().equals(Browser.BookmarkColumns.FAVICON) ) {
                            byte favIcon[] = Base64.decode(entry.getValue() + "\r", Base64.NO_WRAP);
                            values.put(entry.getKey(), favIcon);
                            //Log.d(LOGGER_TAG, "Trying to restore a favicon!");
                        } else {
                            values.put(entry.getKey(), entry.getValue());
                        }
                    }
                    values.put(Browser.BookmarkColumns.BOOKMARK, 1);
                    values.put(Browser.BookmarkColumns.VISITS, 0);
                    acty.getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, values);
                }
            }
            return (Object)bookmarks;

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

        Cursor cur = acty.getContentResolver().query(android.provider.Browser.BOOKMARKS_URI, null, where, null, null);

        Map<String, String> columns = new HashMap<String, String>();
        columns.put( Browser.BookmarkColumns.URL, CursorToXMLWriter.STRING_TYPE );
        columns.put( Browser.BookmarkColumns.TITLE, CursorToXMLWriter.STRING_TYPE );
        columns.put( Browser.BookmarkColumns.CREATED, CursorToXMLWriter.NUMBER_TYPE );
        columns.put( Browser.BookmarkColumns.DATE, CursorToXMLWriter.NUMBER_TYPE );
        columns.put( Browser.BookmarkColumns.FAVICON, CursorToXMLWriter.IMAGE_TYPE );

        final CursorToXML ctoXML = new CursorToXML();
        ctoXML.setCur(cur);
        ctoXML.setFilename(datafile);
        ctoXML.setTopLevelNode("bookmarks");
        ctoXML.setSectionName("bookmark");
        ctoXML.setColumnNames(columns);
        ctoXML.setUseAttributes(false);

        CursorToXMLWriter.writeXML(ctoXML);

        return (Object)cur;
    }
}
