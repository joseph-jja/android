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
 */
public class FairParser extends DefaultHandler {

    public static final String TRAIN_LIST_API = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=" + APIConstants.BART_API_KEY;

    // the storage of the feed in a map for the database insert
    private List<Fare> tripFairs = new ArrayList<Fare>();

    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (tripFairs == null || tripFairs.size() <= 0) {
            Fare xStation = new Fare();
            tripFairs.add(xStation);
        }


        if (tripFairs != null && tripFairs.size() > 0 && atts != null && atts.getLength() >= 3) {
            Fare xStation = tripFairs.get(0);

            String amount = atts.getValue("amount");
            String fareClass = atts.getValue("class");
            if (fareClass.equals("cash")) {
                xStation.setFare(amount);
            } else if (fareClass.equals("clipper")) {
                xStation.setClipperDiscount(amount);
            } else if (fareClass.equals("rtcclipper")) {
                xStation.setSeniorDisabledClipper(amount);
            } else if (fareClass.equals("student")) {
                xStation.setYouthClipper(amount);
            }
            tripFairs.remove(0);
            tripFairs.add(xStation);
        }
    }

    public List<Fare> parseDocument(String urlContent)
            throws IOException, SAXException, ParserConfigurationException {

        if (!isValidRSS(urlContent)) {
            throw new IOException("Not valid XML data!");
        }

        this.tripFairs = new ArrayList<Fare>();

        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(urlContent)));

        return this.tripFairs;
    }

    private boolean isValidRSS(String xmlData) {
        if (xmlData == null || xmlData.trim().indexOf("<?xml") != 0 || xmlData.indexOf("<trip") == -1) {
            return false;
        }
        return true;
    }

}
