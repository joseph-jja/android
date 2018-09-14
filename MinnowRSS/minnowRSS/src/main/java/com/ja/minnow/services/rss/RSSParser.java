package com.ja.minnow.services.rss;

import android.util.Log;

import com.ja.database.DatabaseAdapter;
import com.ja.minnow.tables.FeedDataTableData;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * simple rss feed parser that puts data into a table
 *
 * @author Joseph Acosta
 */
public class RSSParser extends DefaultHandler {

    private final Class<?> _self = getClass();
    private final String RSS_TAG = _self.getName();

    // Used to define what elements we are currently in
    private boolean inItemTag = false;
    private boolean inTitleTag = false;
    private boolean inLinkTag = false;
    private boolean inDescriptionTag = false;
    private boolean isImageTag = false;
    private boolean isURL = false;

    private DatabaseAdapter adapter;  // database handle
    private int feedId; // id of the feed we are working on
    private String xmlData;  // feed string data
    private String imageURL;

    private String now;

    // the storage of the feed in a map for the database insert
    private Map<String, Object> storage = new HashMap<String, Object>();

    public void startElement(String uri, String name, String qName, Attributes atts) {

        if (name.trim().equals("title")) {
            inTitleTag = true;
        } else if (name.trim().equals("item")) {
            inItemTag = true;
            storage.clear();
        } else if (name.trim().equals("link")) {
            inLinkTag = true;
        } else if (name.trim().equals("description")) {
            inDescriptionTag = true;
        } else if (name.trim().equals("image")) {
            isImageTag = true;
        } else if (name.trim().equals("url")) {
            isURL = true;
        }
    }

    public void endElement(String uri, String name, String qName) throws SAXException {

        if (name.trim().equals("title")) {
            inTitleTag = false;
        } else if (name.trim().equals("item")) {
            inItemTag = false;
            if (storage.size() >= 3) {
                storage.put(FeedDataTableData.FEED_ID_COL, this.feedId);
                storage.put(FeedDataTableData.LASTUPDATEDATE_COL, this.now);
                final String data = (String) storage.get(FeedDataTableData.SUMMARY_COL);
                storage.put(FeedDataTableData.SUMMARY_COL, data.trim());
                storage.put(FeedDataTableData.ITEM_IS_READ, new Integer(0));
                this.adapter.beginTransaction();
                try {
                    this.adapter.insert(FeedDataTableData.FEED_DATA_TABLE, storage);
                    this.adapter.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.d(RSS_TAG, ex.getMessage());
                } finally {
                    this.adapter.endTransaction();
                }
            }
        } else if (name.trim().equals("link")) {
            inLinkTag = false;
        } else if (name.trim().equals("description")) {
            inDescriptionTag = false;
        } else if (name.trim().equals("image")) {
            isImageTag = false;
        } else if (name.trim().equals("url")) {
            isURL = false;
        }
    }

    public void characters(char ch[], int start, int length) {

        xmlData = String.valueOf(ch).substring(start, start + length);

        if (this.inItemTag && xmlData != null) {
            if (this.inTitleTag && storage != null) {
                if (storage.containsKey(FeedDataTableData.TITLE_COL)) {
                    storage.put(FeedDataTableData.TITLE_COL, (String) storage.get(FeedDataTableData.TITLE_COL) + xmlData);
                } else {
                    storage.put(FeedDataTableData.TITLE_COL, xmlData);
                }
            } else if (this.inLinkTag && storage != null) {
                if (storage.containsKey(FeedDataTableData.URL_COL)) {
                    storage.put(FeedDataTableData.URL_COL, (String) storage.get(FeedDataTableData.URL_COL) + xmlData);
                } else {
                    storage.put(FeedDataTableData.URL_COL, xmlData);
                }
            } else if (this.inDescriptionTag && storage != null) {
                if (storage.containsKey(FeedDataTableData.SUMMARY_COL)) {
                    storage.put(FeedDataTableData.SUMMARY_COL, (String) storage.get(FeedDataTableData.SUMMARY_COL) + xmlData);
                } else {
                    storage.put(FeedDataTableData.SUMMARY_COL, xmlData);
                }
            }
        } else if (!this.inItemTag && xmlData != null && this.isImageTag) {
            if (this.isURL) {
                this.imageURL = xmlData;
            }
        }
    }

    public void parseDocument(DatabaseAdapter adapter, int dbFeedId, String urlContent)
            throws IOException, SAXException, ParserConfigurationException {

        Log.d(RSS_TAG, "Feed id is (" + dbFeedId + ")");

        // initialize
        this.adapter = adapter;
        this.feedId = dbFeedId;
        this.imageURL = null;
        this.xmlData = null;
        this.now = Long.toString(new Date().getTime());

        this.inItemTag = false;
        this.inTitleTag = false;
        this.inLinkTag = false;
        this.inDescriptionTag = false;
        this.isImageTag = false;
        this.isURL = false;

        // open adapter if it is closed
        if (!this.adapter.isDbIsOpen()) {
            this.adapter.open();
        }
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(this);
        reader.parse(new InputSource(new StringReader(urlContent)));
    }

    public boolean isValidRSS(String feedURL) {
        if (feedURL == null || feedURL.trim().indexOf("<?xml") != 0 || feedURL.indexOf("<channel") == -1) {
            return false;
        }
        return true;
    }

    /**
     * @return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }
}
