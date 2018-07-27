package com.ja.sbi.bart.api;

import android.util.Log;

import com.ja.sbi.beans.Station;
import com.ja.sbi.xml.TrainsAtStationParser;

import java.util.List;

/**
 * given a station it will download and parse the list of trains
 *
 * @author joea
 */
public class TrainDownloader extends BaseDownloader {

    private static final TrainsAtStationParser parser = new TrainsAtStationParser();

    private static String selectedStationShortName = null;
    private static String selectedStationName = null;

    public static final List<Station> getTrains(String stationName) {

        final String trainAPI = APIConstants.GET_TRAIN_LIST_API + stationName + APIConstants.KEY_STRING_API;
        List<Station> trainStations;

        selectedStationShortName = stationName;
        try {
            String trainData = BaseDownloader.retriever.downloadURL(trainAPI, 0);
            Log.d("TrainDownloader", "Got trains: " + trainData);

            trainStations = parser.parseDocument(trainData);

            Log.d("TrainDownloader", "Stations size: " + trainStations.size());

        } catch (Exception ex) {
            Log.d("TrainDownloader", ex.getMessage());
            return null;
        }
        return trainStations;
    }

    public static void setSelectedStationName(String stationSelected) {
        TrainDownloader.selectedStationName = stationSelected;
    }

    public static String getSelectedStationName() {
        return TrainDownloader.selectedStationName;
    }

    public static String getSelectedStationShortName() {
        return TrainDownloader.selectedStationShortName;
    }

    public static void setSelectedStationShortName(String selectedStationShortName) {
        TrainDownloader.selectedStationShortName = selectedStationShortName;
    }

}
