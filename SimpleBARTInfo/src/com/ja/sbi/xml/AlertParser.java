package com.ja.sbi.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * simple rss feed parser that puts data into a table
 * 
 * @author Joseph Acosta
 *
 */
public class AlertParser extends DefaultHandler {
	
	private final Class<?> _self = getClass();
	//private final String RSS_TAG = _self.getName();
	
	private boolean inBSATag = false;
	private boolean inStationTag = false; 
	private boolean inDescriptionTag = false; 
	
	private StringBuilder xmlData = new StringBuilder();  // feed string data
	
	// the storage of the feed in a map for the database insert
	private Map<String, String> storage = new HashMap<String, String>();
	private List<Map<String, String>> results = new ArrayList<Map<String, String>>();
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		
	    if (name.trim().equals("bsa")) {
                inBSATag = true;
		this.storage.clear();	
		xmlData = xmlData = new StringBuilder();
	    }
	}
	
	public void endElement(String uri, String name, String qName) throws SAXException {
	
		if ( inBSATag ) { 
			if (name.trim().equals("station") ) {
				storage.put("station", xmlData.toString());
			} else if ( name.trim().equals("description")) {
				storage.put("description", xmlData.toString());
			} else if ( name.trim().equals("bsa") ) {
				results.add(storage);
				inBSATag = false;
			}
			xmlData = new StringBuilder();
		}		
	}
	 
	public void characters(char ch[], int start, int length) {
	
		this.xmlData.append(String.valueOf(ch).substring(start, start+length));		
	}	
	 
	public List<Map<String, String>> parseDocument(String urlContent) 
		throws IOException, SAXException, ParserConfigurationException {

		this.xmlData = new StringBuilder();

		this.inBSATag = false;

		final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		final SAXParser saxParser = saxFactory.newSAXParser();
		final XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(this);
		reader.parse(new InputSource(new StringReader(urlContent)));
		
		return results;
	}
	
	public static final boolean isValidRSS(String feedURL) { 
		if ( feedURL == null || feedURL.trim().indexOf("<?xml") != 0 || feedURL.indexOf("<bsa") == -1 ) {
			return false;
		}
		return true;
	}
}
