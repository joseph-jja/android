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

    public void startElement(String uri, String name, String qName, Attributes atts) {

    }

    public void endElement(String uri, String name, String qName) throws SAXException {

    }
    
    public void characters(char ch[], int start, int length) {
	
		final String xmlData = String.valueOf(ch).substring(start, start+length);
		
    }

    public List<Trip> parseDocument(String urlContent)
      
      this.trips = new ArrayList<Trip>();
      

      return this.trips;
    }
    
    private boolean isValidRSS(String xmlData) {
        if (xmlData == null || xmlData.trim().indexOf("<?xml") != 0 || xmlData.indexOf("<trip") == -1) {
            return false;
        }
        return true;
    }
}
