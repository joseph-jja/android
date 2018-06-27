package com.ja.sbi.bart.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.ja.sbi.xml.AlertParser;

public class AlertDownloader extends BaseDownloader {

	private static final AlertParser parser = new AlertParser();

	public static String getAlertData() throws Exception {

		// get data
		Log.v("AlertDownloader", "Getting XML data!");
		String bartXMLData = BaseDownloader.retriever.downloadURL(APIConstants.ALERTS_API);
		Log.v("AlertDownloader", "Got XML data = " + bartXMLData);

		return bartXMLData;
	}

	public static List<Map<String, String>> parseAlerts(String bartXMLData) throws Exception {
		
		if ( ! AlertParser.isValidRSS(bartXMLData) ) {
			Log.v("AlertDownloader", "Invalid XML data!");
		}

		// parse data
		Log.v("AlertDownloader", "Parsing XML data!");
		final List<Map<String, String>> data = parser.parseDocument(bartXMLData);
		Log.v("AlertDownloader", "XML result count: " + data.size());

		// DEBUGGING
		final Map<String, String> originalXML = new HashMap<String, String>();
		originalXML.put("Original Response", bartXMLData);

		data.add(originalXML);

		return data;		
	}
}
