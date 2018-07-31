package android.backup.br;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.backup.constants.SmsConstants;
import android.backup.screens.TabScreen;
import android.backup.utils.xml.CursorToXMLWriter;
import android.backup.utils.xml.XMLDocumentBuilder;
import android.backup.utils.xml.XMLDocumentWriter;
import android.backup.utils.xml.beans.CursorToXML;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SmsBackupRestore {

    private static final String months[] = new String[] {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };  
    
    private static final String[] columnNames = new String[] {
        "sms"
    };
    
    private static final String DATA_FILE = "sms.xml";

    public static final Object restoreData(Activity acty, boolean restore) {

        final String LOGGER_TAG = "SmsBackupRestore";
        
        Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();
        
        Set<String> columns = new HashSet<String>();
        columns.addAll(Arrays.asList(columnNames));
        names.put(1, columns);
        
        try {
            Map<Integer, List<Map<String, String>>> results = TabScreen.readData.parseDocument(DATA_FILE, names);
            
            // we don't acre about the results we need the attributes
            results = TabScreen.readData.getAttrResults();

            Log.v(LOGGER_TAG, "Size of parsed data = " + results.size());
            if ( results == null || results.size() == 0 ) { 
                return new ArrayList<Map<String, String>>();
            }

            List<Map<String, String>> messages = results.get(1);
            Log.v(LOGGER_TAG, "Size of sms data = " + messages.size());
            if ( messages == null || messages.size() == 0 ) { 
                return new ArrayList<Map<String, String>>();
            }
            //if ( restore ) {
            if ( false ) {
                Uri mSmsQueryUri = Uri.parse("content://sms/");
                Cursor info = acty.getContentResolver().query(mSmsQueryUri, null, null, null, null);
                final int count = info.getColumnCount();
                Set<String> columnNames = new HashSet<String>();
                for ( int i =0; i < count; i+=1 ) {
                    columnNames.add(info.getColumnName(i));
                }
                
                acty.getContentResolver().delete(mSmsQueryUri, null, null);
    
                // actually do restore
                for ( Map<String, String> data: messages ) { 
                    ContentValues values = new ContentValues();
                    Set<Map.Entry<String, String>> dataParts = data.entrySet();
                    for ( Map.Entry<String, String> entry : dataParts ) {
                        if ( columnNames.contains(entry.getKey()) && ! entry.getKey().equalsIgnoreCase("_id") ) {
                            values.put(entry.getKey(), entry.getValue());
                        }
                    }
                    acty.getContentResolver().insert(mSmsQueryUri, values);
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

        final String LOGGER_TAG = "SmsBackupRestore";
        
        String sortOrder = SmsConstants.DATE;        

        Uri boxUri = Uri.parse("content://sms/");
        Cursor cur = acty.getContentResolver().query(boxUri, null, null, null, sortOrder);

        Map<String, String> columns = new HashMap<String, String>();

        final CursorToXML ctoXML = new CursorToXML();
        ctoXML.setCur(cur);
        ctoXML.setFilename(DATA_FILE);
        ctoXML.setTopLevelNode("smses");
        ctoXML.setSectionName("sms");
        ctoXML.setColumnNames(columns);
        ctoXML.setUseAttributes(true);

        try {
            Document doc = CursorToXMLWriter.buildXML(ctoXML);

            NodeList smsMessages = doc.getElementsByTagName("sms");
            final int msgCount = smsMessages.getLength();
            Log.d(LOGGER_TAG, "Length = " + msgCount);
            for ( int i = 0; i < msgCount; i+=1 ) { 
                Node msg = smsMessages.item(i);
                NamedNodeMap attrs = msg.getAttributes();
                Node attribute = attrs.getNamedItem("date");
                if ( attribute != null ) {
                    // readable_date
                    String readableDate = formatReadableDate(Long.parseLong(attribute.getNodeValue()));
                    XMLDocumentBuilder.addAttribute(doc, (Element)msg, "readable_date", readableDate);
                }
            }

            XMLDocumentWriter.writeDocument(ctoXML.getFilename(), doc);

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (Object)cur;
    }

    private static final String formatReadableDate(long date) { 

        StringBuilder fname = new StringBuilder();

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(date);

        fname.append( months[cal.get(Calendar.MONTH)] + " " );

        fname.append( formatTwoCharacter(cal.get(Calendar.DATE)) + ", " + cal.get(Calendar.YEAR));

        long hour = cal.get(Calendar.HOUR);
        fname.append(" " + ((hour == 0)?12:hour) + ":" + formatTwoCharacter(cal.get(Calendar.MINUTE)) );
        fname.append(":" + formatTwoCharacter(cal.get(Calendar.SECOND)) );

        if ( cal.get(Calendar.AM_PM) == Calendar.AM ) { 
            fname.append(" AM");
        } else {
            fname.append(" PM");
        }


        return fname.toString();
    }

    private static final String formatTwoCharacter(long in) { 

        if ( in < 10 ) { 
            return "0" + Long.toString(in);
        }
        return Long.toString(in);
    }
}
