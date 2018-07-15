package com.ja.sbi.bart.api;

import android.util.Log;

import java.util.List;

/**
 * plan a trip
 *
 * @author joea
 */
public class TripPlanner extends BaseDownloader {

    //private static final TrainsAtStationParser parser = new TrainsAtStationParser();

    private static String selectedStationShortName = null;
    private static String selectedStationName = null;
    
    private static final List<String> callTripAPI(String tripAPI) {

      List<String> trips = null;
      try {
            String tripData = BaseDownloader.retriever.downloadURL(tripAPI, 0);
            Log.d("TripPlanner", "Got trips: " + tripData);

            // TODO implement parser
            //trips = parser.parseDocument(trainData);

            Log.d("TripPlanner", "Number of trips: " + trips.size());

        } catch (Exception ex) {
            Log.d("TripPlanner", ex.getMessage());
            return null;
        }
        return trips;
    }
    
    // TODO create Trip bean
    public static final List<String> getDepartTrips(String origin, String dest, String departTime) {

        final String tripAPI = APIConstants.SCHEDULE_DEPART + orig 
          + SCHEDULE_DEST + dest + SCHEDULE_DATE = departTime + APIConstants.KEY_STRING_API;

        return callTripAPI(tripAPI);
    }
    
    // TODO create Trip bean
    public static final List<String> getArrivalTrips(String origin, String dest, String arriveTime) {

        final String tripAPI = APIConstants.SCHEDULE_ARRIVE + orig 
          + SCHEDULE_DEST + dest + SCHEDULE_DATE = arriveTime + APIConstants.KEY_STRING_API;

        return callTripAPI(tripAPI);
    }
}