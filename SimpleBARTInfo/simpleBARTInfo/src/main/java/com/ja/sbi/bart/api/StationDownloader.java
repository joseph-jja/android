package com.ja.sbi.bart.api;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.StationsParser;

/**
 * download and parse the list of stations
 * 
 * @author joea
 *
 */
public class StationDownloader extends BaseDownloader {

	public static StationsParser stations = new StationsParser();
	public static String allStationsXML = "";
	
	private static String getAllStationsXML() throws IOException {
    	if ( StationDownloader.allStationsXML.equals("") ) { 
    		//Log.v("StationDownloader", "Calling downloader");
           	// get all the stations once and then let the user figure out how they want to display them
           	StationDownloader.allStationsXML = BaseDownloader.retriever.downloadURL(APIConstants.GET_STATION_LIST_API, 0);
    	}
		return StationDownloader.allStationsXML;
	}
	
	public static List<Station> getStationList() throws Exception {
		
		// get data
		Log.v("StationDownloader", "Getting XML data!");
		String bartXMLData = StationDownloader.getAllStationsXML();
		Log.v("StationDownloader", "Got XML data = " + bartXMLData);
		
		// parse data
		Log.v("StationDownloader", "Parsing XML data!");
		List<Station> stations = StationDownloader.stations.parseDocument(StationDownloader.getAllStationsXML());
		
		// sort list here
		Collections.sort(stations, new StationNameSorter());

		Log.d("StationDownloader", "Data size = " + stations.size());
		
		return stations;
		
	}

	private static class StationNameSorter implements Comparator<Station> {

		@Override
		public int compare(Station s1, Station s2) {

			final String name1 = s1.getStationName();
			final String name2 = s2.getStationName();

			return name1.compareTo(name2);
		}    	
    }
}
