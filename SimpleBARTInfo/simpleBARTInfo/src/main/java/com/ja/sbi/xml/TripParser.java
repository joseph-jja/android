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

import com.ja.sbi.trains.beans.Fare;

public class TripParser extends DefaultHandler {

    private List<Trip> trips = new ArrayList<Trip>();

    private boolean inTrip = false;

    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (name.trim().equals("trip")) {

            Trip tripItem = new Trip();
            tripItem.setFareDetails(new Fare());
            tripItem.setLegs(new ArrayList<TripLeg>());

            inTrip = true;
            if (atts != null) {
                tripItem.setOrigin(atts.getValue("origin"));
                tripItem.setDestination(atts.getValue("destination"));
                tripItem.setOriginTime(atts.getValue("origTimeMin"));
                tripItem.setOriginDate(atts.getValue("origTimeDate"));
                tripItem.setDestinationTime(atts.getValue("destTimeMin"));
                tripItem.setDestinationDate(atts.getValue("destTimeDate"));
            }
            trips.add(tripItem);
        } else if (inTrip && name.trim().equals("fare")) {
            Trip tripItem = trips.get(trips.size() - 1);
            Fare fareItem = tripItem.getFareDetails();

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
            trips.remove(trips.size() - 1);
            trips.add(tripItem);
        } else if (inTrip && name.trim().equals("leg")) {
            Trip tripItem = trips.get(trips.size() - 1);
            List<TripLeg> tripLegs = tripItem.getLegs();
            if ( tripLegs == null ) {
                tripLegs = new ArrayList<TripLeg>();
            }
            TripLeg currentLeg = new TripLeg();

            currentLeg.setOrder(atts.getValue("order"));
            currentLeg.setTransferCode(atts.getValue("transferCode"));
            currentLeg.setRouteLine(atts.getValue("routeLine"));
            currentLeg.setTrainHeadStation(atts.getValue("trainHeadStation"));
            currentLeg.setOrigin(atts.getValue("origin"));
            currentLeg.setDestination(atts.getValue("destination"));
            currentLeg.setOriginDate(atts.getValue("origTimeMin"));
            currentLeg.setOriginDate(atts.getValue("origTimeDate"));
            currentLeg.setDestinationTime(atts.getValue("destTimeMin"));
            currentLeg.setDestinationDate(atts.getValue("destTimeDate"));

            tripLegs.add(currentLeg);
            tripItem.setLegs(tripLegs);
            trips.remove(trips.size() - 1);
            trips.add(tripItem);
        }
    }

    public void endElement(String uri, String name, String qName) {

        if (name.trim().equals("trip")) {
            inTrip = false;
        }
    }

    public void characters(char ch[], int start, int length) {

        final String xmlData = String.valueOf(ch).substring(start, start + length);

    }

    public List<Trip> parseDocument(String urlContent)
            throws IOException, SAXException, ParserConfigurationException {

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
        return xmlData != null && xmlData.trim().indexOf("<?xml") == 0 && xmlData.indexOf("<trip") != -1;
    }
}
