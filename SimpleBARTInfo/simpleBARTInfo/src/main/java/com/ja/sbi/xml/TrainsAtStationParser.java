package com.ja.sbi.xml;

import android.annotation.TargetApi;
import android.os.Build;

import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.trains.beans.Train;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.ja.sbi.xml.XMLUtils;

/**
 * simple rss feed parser that puts data into a table
 *
 * @author Joseph Acosta
 */
public class TrainsAtStationParser extends DefaultHandler {

    // Used to define what elements we are currently in
    private boolean inStationTag = false;
    private boolean inNameTag = false;

    private boolean inETATag = false;
    private boolean inDestinationTrainTag = false;

    private boolean inEstimateTag = false;
    private boolean inMinutes = false;
    private boolean inPlatform = false;
    private boolean inLength = false;
    private boolean inDirection = false;

    // the storage of the feed in a map for the database insert
    private List<Station> stations = new ArrayList<Station>();

    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (name.trim().equals("station")) {
            inStationTag = true;
            Station xStation = new Station();
            xStation.setTrains(new ArrayList<Train>());
            stations.add(xStation);
        } else if (name.trim().equals("name")) {
            inNameTag = true;
        } else if (name.trim().equals("etd")) {
            inETATag = true;
            // get stations
            final int size = stations.size();
            Station xStation = stations.get(size - 1);

            // get trains
            List<Train> trains = xStation.getTrains();

            // create new train
            Train xTrain = new Train();
            trains.add(xTrain);

            xStation.setTrains(trains);
        } else if (name.trim().equals("destination")) {
            inDestinationTrainTag = true;
        } else if (name.trim().equals("estimate")) {
            inEstimateTag = true;
        } else if (name.trim().equals("minutes")) {
            inMinutes = true;
        } else if (name.trim().equals("platform")) {
            inPlatform = true;
        } else if (name.trim().equals("length")) {
            inLength = true;
        } else if (name.trim().equals("direction")) {
            inDirection = true;
        }
    }

    public void endElement(String uri, String name, String qName) throws SAXException {

        if (name.trim().equals("station")) {
            inStationTag = false;
        } else if (name.trim().equals("name")) {
            inNameTag = false;
        } else if (name.trim().equals("etd")) {
            inETATag = false;
        } else if (name.trim().equals("destination")) {
            inDestinationTrainTag = false;
        } else if (name.trim().equals("estimate")) {
            inEstimateTag = false;
        } else if (name.trim().equals("minutes")) {
            inMinutes = false;
        } else if (name.trim().equals("platform")) {
            inPlatform = false;
        } else if (name.trim().equals("length")) {
            inLength = false;
        } else if (name.trim().equals("direction")) {
            inDirection = false;
        }
    }

    private String appendData(String initial, String addTo) {
        if ( initial != null ) {
            return XMLUtils.append(initial + ", ", addTo);
        }
        return XMLUtils.append(initial, addTo);
    }

    public void characters(char ch[], int start, int length) {

        String xmlData = String.valueOf(ch).substring(start, start + length);

        if (this.inStationTag && xmlData != null) {
            // get the station we are working on
            final int size = stations.size();
            Station xStation = stations.get(size - 1);
            if (this.inNameTag) {
                xStation.setStationName(XMLUtils.append(xStation.getStationName(), xmlData));
            } else if (this.inETATag) {
                List<Train> trains = xStation.getTrains();
                final int tSize = trains.size();
                Train xTrain = trains.get(tSize - 1);
                if (this.inDestinationTrainTag) {
                    xTrain.setTrainName(XMLUtils.append(xTrain.getTrainName(), xmlData));
                } else if (this.inEstimateTag) {
                    if (this.inMinutes) {
                        xTrain.setTrainTime(appendData(xTrain.getTrainTime(), xmlData));
                    } else if (this.inPlatform) {
                        xTrain.setPlatform(appendData(xTrain.getPlatform(), xmlData));
                    } else if (this.inLength) {
                        xTrain.setLength(appendData(xTrain.getLength(), xmlData));
                    } else if (this.inDirection) {
                        xTrain.setDirection(appendData(xTrain.getDirection(), xmlData));
                    }
                }
                if (trains.remove(tSize - 1) != null) {
                    trains.add(xTrain);
                }
            }
            if (stations.remove(size - 1) != null) {
                stations.add(xStation);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public List<Station> parseDocument(String urlContent)
            throws IOException, SAXException, ParserConfigurationException {


        if (!isValidRSS(urlContent)) {
            throw new IOException("Not valid XML data!");
        }

        this.inStationTag = false;
        this.inNameTag = false;
        this.inETATag = false;
        this.inDestinationTrainTag = false;
        this.inEstimateTag = false;
        this.inMinutes = false;
        this.inLength = false;
        this.inDirection = false;

        this.stations = new ArrayList<Station>();

        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(urlContent)));

        List<Station> resultStations = new ArrayList<Station>();
        for (Station currentStation : this.stations) {
            List<Train> resultTrains = new ArrayList<Train>();
            for (Train currentTrain : currentStation.getTrains()) {
                final String direction = currentTrain.getDirection();
                final String[] oneDirection = direction.split(",");
                currentTrain.setDirection(oneDirection[0].trim());
                resultTrains.add(currentTrain);
            }
            Collections.sort(resultTrains, new Train());
            currentStation.setTrains(resultTrains);
            resultStations.add(currentStation);
        }

        return resultStations;
    }

    private boolean isValidRSS(String xmlData) {
        if (xmlData == null || xmlData.trim().indexOf("<?xml") != 0 || xmlData.indexOf("<destination") == -1) {
            return false;
        }
        return true;
    }

}
