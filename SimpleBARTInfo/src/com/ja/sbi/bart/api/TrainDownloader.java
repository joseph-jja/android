package com.ja.sbi.bart.api;

import java.util.List;

import android.util.Log;

import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.TrainsAtStationParser;

/**
 * given a station it will download and parse the list of trains
 * 
 * @author joea
 *
 */
public class TrainDownloader extends BaseDownloader {

	private static final TrainsAtStationParser parser = new TrainsAtStationParser();

	private static String selectedStationShortName = null;
	private static String selectedStationName = null;
	
	public static final List<Station> getTrains(String stationName) { 
		
		String trainAPI = APIConstants.GET_TRAIN_LIST_API  + stationName + APIConstants.KEY_STRING_API;
		List<Station> trainStations = null;
		
		selectedStationShortName = stationName;
		try { 
			String trainData = BaseDownloader.retriever.downloadURL(trainAPI);
			Log.d("StationListener", "Got trains: " + trainData);
			
			trainStations = parser.parseDocument(trainData);
			Log.d("StationListener", "Stations size: " + trainStations.size());
			
		} catch (Exception ex ) { 
			Log.d("StationListener", ex.getMessage());
			return null;
		}
		return trainStations;
	}

	public static void setSelectedStationName(String stationSelected) { 
		selectedStationName = stationSelected;
	}
	
	public static String getSelectedStationName() { 
		return selectedStationName;
	}

	public static String getSelectedStationShortName() {
		return selectedStationShortName;
	}

	public static void setSelectedStationShortName(String selectedStationShortName) {
		TrainDownloader.selectedStationShortName = selectedStationShortName;
	}

}
