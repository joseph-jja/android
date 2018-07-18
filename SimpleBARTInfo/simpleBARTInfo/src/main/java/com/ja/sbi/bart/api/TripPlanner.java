package com.ja.sbi.bart.api;

import android.util.Log;

import java.util.List;

import com.ja.sbi.beans.Trip;

/**
 * plan a trip
 *
 * @author joea
 */
public class TripPlanner extends BaseDownloader {

    //private static final TrainsAtStationParser parser = new TrainsAtStationParser();

    private static String selectedStationShortName = null;
    private static String selectedStationName = null;

    private static final List<Trip> callTripAPI(String tripAPI) {

        List<Trip> trips = null;

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
    public static final List<Trip> getDepartTrips(String origin, String dest, String departTime) {

        // basically what we want is 3 trips
        final String tripAPI = APIConstants.SCHEDULE_DEPART + origin + "b=1&a=2"
                + APIConstants.SCHEDULE_DEST + dest
                + APIConstants.SCHEDULE_DATE + departTime + APIConstants.KEY_STRING_API;

        return callTripAPI(tripAPI);
    }

    // TODO create Trip bean
    public static final List<Trip> getArrivalTrips(String origin, String dest, String arriveTime) {

        // basically what we want is 3 trips
        final String tripAPI = APIConstants.SCHEDULE_ARRIVE + origin + "b=2&a=1"
                + APIConstants.SCHEDULE_DEST + dest
                + APIConstants.SCHEDULE_DATE + arriveTime + APIConstants.KEY_STRING_API;

        return callTripAPI(tripAPI);
    }
}
