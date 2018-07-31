package android.backup.br;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.backup.screens.TabScreen;
import android.backup.utils.xml.XMLDocumentBuilder;
import android.backup.utils.xml.XMLDocumentWriter;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactBackupRestore {

    private static final String[] projection = new String[] {
        ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final String DATA_FILE = "contacts.xml";
    
    public static final Object restoreData(Activity acty, boolean restore) {

        Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();
        
        Set<String> columns = new HashSet<String>();
        columns.addAll(Arrays.asList(projection));
        names.put(2, columns);
        
        try {
            Map<Integer, List<Map<String, String>>> results = TabScreen.readData.parseDocument(DATA_FILE, names);
            //Log.v(LOGGER_TAG, "Size of parsed data = " + results.size());
            if ( results == null ) { 
                return new ArrayList<Map<String, String>>();
            }

            List<Map<String, String>> messages = results.get(2);
            //Log.v(LOGGER_TAG, "Size of bookmarks data = " + bookmarks.size());
            if ( messages == null ) { 
                return new ArrayList<Map<String, String>>();
            }
            
            if ( restore ) {
                //acty.getContentResolver().delete(android.provider.Browser.BOOKMARKS_URI, where, null);
    
                // TODO actually do restore
                for ( Map<String, String> data: messages ) { 
                    ContentValues values = new ContentValues();
                    Set<Map.Entry<String, String>> dataParts = data.entrySet();
                    for ( Map.Entry<String, String> entry : dataParts ) {
                        values.put(entry.getKey(), entry.getValue());
                    }
                    //acty.getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, values);
                }
            }
            return (Object)messages;

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

        return new ArrayList<Map<String, String>>();
    }
    
    public static final Object saveData(Activity acty) {
        
        Cursor cur = acty.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        
        Document appXML = null;
        Element parent = null;
        try {
            appXML = XMLDocumentBuilder.createDocument();
            parent = XMLDocumentBuilder.addNode(appXML, null, "contacts", null);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        int i = 0;
        int resultSetCount = cur.getCount();
        boolean result = cur.moveToPosition(0);
       
        // loop through results
        while ( result && i < resultSetCount ) {
            
            Element sectionNode = null;
            if ( appXML != null && parent != null ) {
                sectionNode = XMLDocumentBuilder.addNode(appXML, parent, "contact", null);
            }
            int columnCount = cur.getColumnCount();
            int contactId = 0;
            for ( int j =0; j < columnCount; j+=1 ) {
                
                String name = cur.getColumnName(j);                
                if ( name != null && ! cur.isNull(j) ) {
                    String sColumnData = null;
                    if ( name.equalsIgnoreCase(ContactsContract.Contacts.DISPLAY_NAME) ) {
                        sColumnData = cur.getString(j);
                        XMLDocumentBuilder.addNode(appXML, sectionNode, ContactsContract.Contacts.DISPLAY_NAME, sColumnData);
                    } else if ( name.equalsIgnoreCase(ContactsContract.Contacts._ID) ) {
                        int x = cur.getInt(j);
                        contactId = x;
                    } else if ( name.equalsIgnoreCase(ContactsContract.Contacts.PHOTO_ID) ) {
                        // at this api level we do not have any other photo information
                        int x = cur.getInt(j);
                        sColumnData = Integer.toString(x);
                        XMLDocumentBuilder.addNode(appXML, sectionNode, ContactsContract.Contacts.PHOTO_ID, sColumnData);
                    } else if ( name.equalsIgnoreCase(ContactsContract.Contacts.CUSTOM_RINGTONE) ) {
                        sColumnData = cur.getString(j);
                        XMLDocumentBuilder.addNode(appXML, sectionNode, ContactsContract.Contacts.CUSTOM_RINGTONE, sColumnData);
                    } else if ( name.equalsIgnoreCase(ContactsContract.Contacts.SEND_TO_VOICEMAIL) ) {
                        int x = cur.getInt(j);
                        sColumnData = Integer.toString(x);
                        XMLDocumentBuilder.addNode(appXML, sectionNode, ContactsContract.Contacts.SEND_TO_VOICEMAIL, sColumnData);
                    } else if ( name.equalsIgnoreCase(ContactsContract.Contacts.HAS_PHONE_NUMBER) 
                            && cur.getInt(j) == 1 && contactId != 0 ) 
                    {
                        // phone numbers
                        Cursor phones = acty.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        
                        Element phoneNumbers = XMLDocumentBuilder.addNode(appXML, sectionNode, "phoneNumbers", null);
                        
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            XMLDocumentBuilder.addNode(appXML, phoneNumbers, "phoneNum", phoneNumber);
                        }
                    }
                    
                }
            }    
            i+=1;
            result = cur.moveToPosition(i);
        }
        if ( appXML != null ) { 
            try {
                XMLDocumentWriter.writeDocument(DATA_FILE, appXML);
            } catch (TransformerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}
