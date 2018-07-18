package com.ja.sbi.xml;

import com.ja.sbi.beans.Alerts;

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

/**
 * simple rss feed parser that puts data into a table
 *
 * @author Joseph Acosta
 */
public class AlertParser extends DefaultHandler {

    private final Class<?> _self = getClass();
    //private final String RSS_TAG = _self.getName();

    private List<Alerts> alertItems = new ArrayList<Alerts>();

    private boolean inStationTag = false;
    private boolean inDescriptionTag = false;
    private boolean inAlertTypeTag = false;
    private boolean insmsTextTag = false;
    private boolean inPostedTag = false;
    private boolean inExpiresTag = false;
    private boolean inBSATag = false;

    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (name.trim().equals("bsa")) {
            Alerts alertItem = new Alerts();
            alertItems.add(alertItem);
            inBSATag = true;
        } else if (name.trim().equals("station")) {
            inStationTag = true;
        } else if (name.trim().equals("type")) {
            inAlertTypeTag = true;
        } else if (name.trim().equals("description")) {
            inDescriptionTag = true;
        } else if (name.trim().equals("sms_text")) {
            insmsTextTag = true;
        } else if (name.trim().equals("posted")) {
            inPostedTag = true;
        } else if (name.trim().equals("expires")) {
            inExpiresTag = true;
        }
    }

    public void endElement(String uri, String name, String qName) {

        if (name.trim().equals("bsa")) {
            inBSATag = false;
        } else if (name.trim().equals("station")) {
            inStationTag = false;
        } else if (name.trim().equals("type")) {
            inAlertTypeTag = false;
        } else if (name.trim().equals("description")) {
            inDescriptionTag = false;
        } else if (name.trim().equals("sms_text")) {
            insmsTextTag = false;
        } else if (name.trim().equals("posted")) {
            inPostedTag = false;
        } else if (name.trim().equals("expires")) {
            inExpiresTag = false;
        }
    }

    public void characters(char ch[], int start, int length) {

        String xmlData = String.valueOf(ch).substring(start, start + length);

        if (this.inBSATag && xmlData != null) {
            final int size = alertItems.size();
            Alerts currentAlert = alertItems.get(size - 1);
            if (this.inStationTag) {
                currentAlert.setStation(XMLUtils.append(currentAlert.getStation(), xmlData));
            } else if (this.inAlertTypeTag) {
                currentAlert.setAlertType(XMLUtils.append(currentAlert.getAlertType(), xmlData));
            } else if (this.inDescriptionTag) {
                currentAlert.setDescription(XMLUtils.append(currentAlert.getDescription(), xmlData));
            } else if (this.insmsTextTag) {
                currentAlert.setSmsText(XMLUtils.append(currentAlert.getSmsText(), xmlData));
            } else if (this.inPostedTag) {
                currentAlert.setPosted(XMLUtils.append(currentAlert.getPosted(), xmlData));
            } else if (this.inExpiresTag) {
                currentAlert.setExpires(XMLUtils.append(currentAlert.getExpires(), xmlData));
            }
        }
    }

    public List<Alerts> parseDocument(String urlContent)
            throws IOException, SAXException, ParserConfigurationException {

        if (!isValidRSS(urlContent)) {
            throw new IOException("Not valid XML data!");
        }

        alertItems = new ArrayList<Alerts>();

        inStationTag = false;
        inDescriptionTag = false;
        inAlertTypeTag = false;
        insmsTextTag = false;
        inPostedTag = false;
        inExpiresTag = false;
        inBSATag = false;

        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(urlContent)));

        return alertItems;
    }

    private boolean isValidRSS(String feedData) {
        return feedData != null && feedData.trim().indexOf("<?xml") == 0 && feedData.indexOf("<bsa") != -1;
    }
}
