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
package android.backup.utils.xml.parsers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.backup.utils.xml.XMLDocumentWriter;
import android.util.Log;

public class XMLToMap extends DefaultHandler {

    private Map<Integer, List<Map<String, String>>> results = new HashMap<Integer, List<Map<String, String>>>();
    private Map<Integer, List<Map<String, String>>> attrResults = new HashMap<Integer, List<Map<String, String>>>();

    private Map<Integer, Set<String>> xmlDocumentNames = new HashMap<Integer, Set<String>>();

    private StringBuilder xmlData = new StringBuilder();  
    private String currentElementName = null;
    private int level = 0;

    private String LOGGER_TAG = getClass().getName();

    public void startElement(String uri, String name, String qName, Attributes atts) {

        // each time we hit a start element we go down up level bookmarks/bookmark/title should be 3
        Set<String> levelElements = this.xmlDocumentNames.get(this.level);
        Log.v(LOGGER_TAG, "Got level and elements " + this.level + " " + levelElements);
        if ( levelElements != null && levelElements.contains(name) ) {
            // create the list, so this list will contain a map of each line in the xml file
            List<Map<String, String>> levelData = results.get(this.level);
            if ( levelData == null ) { 
                levelData = new ArrayList<Map<String, String>>();
                Map<String, String> init = new HashMap<String, String>();
                levelData.add(init);
                results.put(this.level, levelData);
            }
        }
        if ( atts != null ) {
            List<Map<String, String>> attrLevelData = attrResults.get(this.level);
            Map<String, String> attrInit = new HashMap<String, String>();
            if ( attrLevelData == null || attrLevelData.size() == 0 ) { 
                Log.v(LOGGER_TAG, "Attributes count for this element: " + atts.getLength());
                attrLevelData = new ArrayList<Map<String, String>>();
            }
            for ( int j = 0; j < atts.getLength(); j+=1 ) { 
                String attrName = atts.getQName(j); 
                String attrValue = atts.getValue(attrName); 
                //Log.v(LOGGER_TAG, "Name and value = " + attrName + " _ " + attrValue);
                attrInit.put(attrName, attrValue);
            }
            attrLevelData.add(attrInit);
            attrResults.put(this.level, attrLevelData);
            Log.v(LOGGER_TAG, "Attribute sizes " + attrResults.size() + " " + attrLevelData.size());
        }
        this.currentElementName = name;
        this.level +=1;
    }


    public void endElement(String uri, String name, String qName) throws SAXException {

        this.level -= 1;

        Set<String> levelElements = this.xmlDocumentNames.get(this.level);
        Log.v(LOGGER_TAG, "XML data for this element " + levelElements + " " + this.currentElementName + " " + xmlData);
        if ( levelElements != null && levelElements.contains(this.currentElementName) ) {
            List<Map<String, String>> levelData = results.get(this.level);
            Map<String, String> rowData = levelData.get(levelData.size() - 1);
            if ( ! rowData.containsKey(this.currentElementName) ) {
                rowData.put(this.currentElementName, this.xmlData.toString().trim());
            } else {
                levelData.add(new HashMap<String, String>());
                rowData = levelData.get(levelData.size() - 1);
                rowData.put(this.currentElementName, this.xmlData.toString().trim());
            }

            //Log.v(LOGGER_TAG, "So far " + this.currentElementName + " " + xmlData + " " + levelData.size());
            //Log.v(LOGGER_TAG, "and " + rowData + " " + xmlData + " " + levelData.size());

            results.put(this.level, levelData);            
        }

        xmlData = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {

        xmlData.append(String.valueOf(ch).substring(start, start+length));

    }

    private XMLReader parseInit(Map<Integer, Set<String>> names) 
            throws IOException, SAXException, ParserConfigurationException {

        this.xmlDocumentNames = names;
        
        // reset all variables
        this.currentElementName = null;
        this.results.clear();
        this.attrResults.clear();
        this.level = 0;
        this.xmlData = new StringBuilder();

        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);

        return reader;
    }

    public Map<Integer, List<Map<String, String>>> parseString(String xml, Map<Integer, Set<String>> names)
            throws IOException, SAXException, ParserConfigurationException {

        final XMLReader reader = parseInit(names);
        reader.parse(new InputSource(new StringReader( xml )));

        return this.results;
    }

    public Map<Integer, List<Map<String, String>>> parseDocument(String xmlFilename, Map<Integer, Set<String>> names)
            throws IOException, SAXException, ParserConfigurationException {

        String sdcardLocation = "";
        final File sdcard = XMLDocumentWriter.getSDCardLocation();
        if ( sdcard != null ) {
            sdcardLocation = sdcard.getAbsolutePath();
        }
        Log.d(LOGGER_TAG, "sdcard location = " + sdcardLocation);

        final XMLReader reader = parseInit(names);

        final String fname = sdcardLocation + System.getProperty("file.separator") + xmlFilename;
        if ( ! new File(fname).exists() ) {
            Log.e(LOGGER_TAG, "File does not exist! location = " + fname);
            return this.results;
        }

        reader.parse(new InputSource(new FileReader(fname)));

        return this.results;
    }

    /**
     * @return the attrResults
     */
    public Map<Integer, List<Map<String, String>>> getAttrResults() {
        return attrResults;
    }
}
