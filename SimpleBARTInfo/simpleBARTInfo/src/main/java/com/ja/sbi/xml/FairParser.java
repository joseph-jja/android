package com.ja.sbi.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.trains.beans.Fare;


/**
 * simple rss feed parser that puts data into a table
 * 
 * @author Joseph Acosta
 *
 */
public class FairParser extends DefaultHandler {
	
	public static final String TRAIN_LIST_API = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=" + APIConstants.BART_API_KEY;
	
	// Used to define what elements we are currently in
	private boolean inTrip = true;
	private boolean inFare = false;
	private boolean inClipperDiscount = false;

	private String xmlData;  // feed string data
	
	// the storage of the feed in a map for the database insert
	private List<Fare> tripFairs = new ArrayList<Fare>();
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		
		if (name.trim().equals("trip")) {
            inTrip = true;
            Fare xStation = new Fare();
            tripFairs.add(xStation);
        } else if ( name.trim().endsWith("fare") ) {
        	inFare = true;
		} else if ( name.trim().equals("clipper") ) { 
			inClipperDiscount = true;
		}
	}
	
	public void endElement(String uri, String name, String qName) throws SAXException {
	
		if (name.trim().equals("trip")) {
            inTrip = false;
        } else if (name.trim().equals("fare")) {
        	inFare = false;
        } else if (name.trim().equals("clipper")) {
        	inClipperDiscount = false;
        }
	}
	 
	public void characters(char ch[], int start, int length) {
	
		xmlData = String.valueOf(ch).substring(start, start+length);
		
		if ( this.inTrip && xmlData != null ) {
			// get the station we are working on
			final int size = tripFairs.size();
			Fare xStation = tripFairs.get(size - 1);
			if ( this.inFare ) {
				xStation.setFare( append(xStation.getFare(), xmlData) );
			} else if ( this.inClipperDiscount ) {
				xStation.setClipperDiscount( append(xStation.getClipperDiscount(), xmlData) );
			}
			if ( tripFairs.remove(size - 1) != null ) { 
				tripFairs.add(xStation);
			}
		}
	}	
	
	private String append(String initialString, String xmlData) { 
		
		if ( initialString != null && initialString.length() > 0 ) {
			return initialString + xmlData;
		}
		return xmlData;		
	}
	 
	public List<Fare> parseDocument(String urlContent) 
		throws IOException, SAXException, ParserConfigurationException {

		// initialize 
		this.xmlData = urlContent;
		
		
		if ( ! isValidRSS() ) { 
			throw new IOException("Not valid XML data!");
		}
		
		this.inTrip = false;
		this.inFare = false;
		this.inClipperDiscount = false;
		
		this.tripFairs = new ArrayList<Fare>();
		
		final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		final SAXParser saxParser = saxFactory.newSAXParser();
		final XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(this);
		reader.parse(new InputSource(new StringReader(this.xmlData)));
		
		return this.tripFairs;
	}
	
	private boolean isValidRSS() { 
		if ( this.xmlData == null || this.xmlData.trim().indexOf("<?xml") != 0 || this.xmlData.indexOf("<trip") == -1 ) {
			return false;
		}
		return true;
	}

}
