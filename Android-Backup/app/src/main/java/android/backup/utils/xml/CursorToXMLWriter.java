/**
 * Copyright 2012 Joseph Acosta
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package android.backup.utils.xml;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.backup.utils.xml.beans.CursorToXML;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

public class CursorToXMLWriter {

    public static final String STRING_TYPE = "String";
    public static final String NUMBER_TYPE = "Number";
    public static final String BLOB_TYPE = "Blob";
    public static final String UNKNOWN_TYPE = "unknown";
    public static final String IMAGE_TYPE = "Image";

    public static final String LOGGER_TAG = "CursorToXMLWriter";

    public static final Document buildXML(CursorToXML ctoXML) throws ParserConfigurationException {

        final Cursor cur = ctoXML.getCur(); 
        final String topLevelNode = ctoXML.getTopLevelNode();
        final String sectionName = ctoXML.getSectionName();
        final Map<String, String> columnNames = ctoXML.getColumnNames();
        final boolean useAttributes = ctoXML.isUseAttributes();

        Document doc = XMLDocumentBuilder.createDocument();

        Element root = XMLDocumentBuilder.addNode(doc, null, topLevelNode, null);
        int i = 0;
        int resultSetCount = cur.getCount();
        boolean result = cur.moveToPosition(0);

        // loop through results
        while ( result && i < resultSetCount ) {

            Element sectionNode = XMLDocumentBuilder.addNode(doc, root, sectionName, null);

            // loop through columns
            int columnCount = cur.getColumnCount();
            for ( int j =0; j < columnCount; j+=1 ) {
                String name = cur.getColumnName(j);
                if ( name != null && ! cur.isNull(j) ) {
                    if ( columnNames.containsKey(name) ) {
                        String type = columnNames.get(name);
                        if ( type.equals(STRING_TYPE) ) {
                            String sColumnData = cur.getString(j);
                            if ( useAttributes ) {
                                XMLDocumentBuilder.addAttribute(doc, sectionNode, name.toLowerCase(), sColumnData);
                            } else {
                                XMLDocumentBuilder.addNode(doc, sectionNode, name.toLowerCase(), sColumnData);
                            }
                        } else if ( type.equals(NUMBER_TYPE) ) {
                            int iColumnData = cur.getInt(j);
                            if ( useAttributes ) {
                                XMLDocumentBuilder.addAttribute(doc, sectionNode, name.toLowerCase(), Integer.toString(iColumnData));
                            } else {
                                XMLDocumentBuilder.addNode(doc, sectionNode, name.toLowerCase(), Integer.toString(iColumnData));
                            }
                        } else if ( type.equals(BLOB_TYPE) || type.equals(IMAGE_TYPE) ) {
                            byte bColumnData[] = cur.getBlob(j);
                            String bsColumnData = "";
                            if ( type.equals(IMAGE_TYPE) ) {
                                bsColumnData = Base64.encodeToString(bColumnData, Base64.NO_WRAP);
                            } else {
                                bsColumnData = new String(bColumnData);
                            }
                            if ( useAttributes ) {
                                XMLDocumentBuilder.addAttribute(doc, sectionNode, name.toLowerCase(), bsColumnData);
                            } else {
                                XMLDocumentBuilder.addNode(doc, sectionNode, name.toLowerCase(), bsColumnData);
                            }
                        } else if ( type.equals(UNKNOWN_TYPE) ) {
                            // if there are more columns and no type or unknown type we can try to write as string or blob
                            try {
                                String sColumnData = cur.getString(j);
                                if ( useAttributes ) {
                                    XMLDocumentBuilder.addAttribute(doc, sectionNode, name.toLowerCase(), sColumnData);
                                } else {
                                    XMLDocumentBuilder.addNode(doc, sectionNode, name.toLowerCase(), sColumnData);
                                }
                            } catch (Exception ex) {
                                // skip column
                                Log.e(LOGGER_TAG, "Exception: " + ex.getLocalizedMessage());
                            }
                        }
                    } else if ( columnNames.isEmpty() ) {
                        try {
                            String sColumnData = cur.getString(j);
                            if ( useAttributes ) {
                                XMLDocumentBuilder.addAttribute(doc, sectionNode, name.toLowerCase(), sColumnData);
                            } else {
                                XMLDocumentBuilder.addNode(doc, sectionNode, name.toLowerCase(), sColumnData);
                            }
                        } catch (Exception ex) {
                            // skip column
                            Log.e(LOGGER_TAG, "Exception: " + ex.getLocalizedMessage());
                        }
                    }
                } 
            }

            i+=1;
            result = cur.moveToPosition(i);
        }
        return doc;
    }

    public static final void writeXML(CursorToXML ctoXML) {

        final String filename = ctoXML.getFilename();

        try {
            Document doc = buildXML(ctoXML);

            // now we have the document what can we do at api level 7?
            // easy switch to API level 8 and require android 2.2

            if ( doc != null ) {
                XMLDocumentWriter.writeDocument(filename, doc);
            }
        } catch (ParserConfigurationException e) {

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
