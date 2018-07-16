package com.ja.sbi.xml;

import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Trip;
import com.ja.sbi.trains.beans.TripLeg;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class TripParser {

    private List<Trip> trips = new ArrayList<Trip>();
	
	private boolean inTrip = false;

    public void startElement(String uri, String name, String qName, Attributes atts) {

	    if (name.trim().equals("trip")) {
		Trip tripItem = new Trip();
		tripItem.setFareDetails( new Fare() );    
		tripItem.setLegs( new ArrayList<TripLeg>() );
		inTrip = true;   
		if ( atts != null ) {
		    tripItem.setOrigin( atts.getValue("origin") );	
		    tripItem.setDestination( atts.getValue("destination") );	
		    tripItem.setOrigin( atts.setOriginDate("origTimeMin") );	
		    tripItem.setOrigin( atts.setOriginDate("origTimeDate") );	
		    tripItem.setOrigin( atts.setDestinationTime("destTimeMin") );	
		    tripItem.setOrigin( atts.setDestinationDate("destTimeDate") );	
		}
		trips.add(tripItem);
	    } else if ( inTrip && name.trim().equals("fare")) {
		Trip tripItem = trips.get(trips.size() - 1);
		Fare = fareItem = tripItem.getFareDetails();
		    
		String amount = atts.getValue("amount");
            	String fareClass = atts.getValue("class");
            	if (fareClass.equals("cash")) {
                	fareItem.setFare(amount);
            	} else if (fareClass.equals("clipper")) {
                	fareItem.setClipperDiscount(amount);
            	} else if (fareClass.equals("rtcclipper")) {
                	fareItem.setSeniorDisabledClipper(amount);
            	} else if (fareClass.equals("student")) {
                	fareItem.setYouthClipper(amount);
            	}
		tripItem.setFareDetails(fareItem);
		trips.remove(0);
            	trips.add(tripItem);
	    }
    }

    public void endElement(String uri, String name, String qName) throws SAXException {

	   if (name.trim().equals("trip")) {
		inTrip = false;    
	   }
    }
    
    public void characters(char ch[], int start, int length) {
	
	final String xmlData = String.valueOf(ch).substring(start, start+length);
		
    }

    public List<Trip> parseDocument(String urlContent)
      
      this.trips = new ArrayList<Trip>();
      
	if (!isValidRSS(urlContent)) {
            throw new IOException("Not valid XML data!");
        }

	final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(urlContent)));
	
      return this.trips;
    }
    
    private boolean isValidRSS(String xmlData) {
        if (xmlData == null || xmlData.trim().indexOf("<?xml") != 0 || xmlData.indexOf("<trip") == -1) {
            return false;
        }
        return true;
    }
}
