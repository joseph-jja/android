package com.ja.sbi.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.ja.dialog.LoadingSpinner;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.TripPlannerAdapter;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.listeners.TripDetailsListener;
import com.ja.sbi.beans.StationData;
import com.ja.sbi.beans.Trip;
import com.ja.sbi.beans.Station;
import com.ja.sbi.xml.TripParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TripPlannerHandler implements StationListSpinnerIface {

    private final String LOG_NAME = this.getClass().getName();

    private static final List<StationData> trainStops = new ArrayList<StationData>();
    private static List<Trip> trips;

    private static final String SELECT_STATION_TEXT = "Please Select Station";

    private static String sourceStation = null;
    private static String destinationStation = null;

    private static LoadingSpinner dialog = null;

    private static final TripParser tripParser = new TripParser();

    private static final List<String> arrivingOrDeparting = Arrays.asList("Departing", "Arriving");
    private static final List<String> ampm = Arrays.asList("AM", "PM");

    final TripPlannerHandler self = this;

    private static StationListSpinnerHandler sourceStop;
    private static StationListSpinnerHandler destinationStop;

    public TripPlannerHandler(Context context, List<Station> stations) {

        final SimpleBARTInfo bartInfoActivity = (SimpleBARTInfo) context;
        final List<Station> localStationCopy = stations;

        dialog = new LoadingSpinner(context, "Loading BART Fares...");

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    final List<Station> stationList = ((localStationCopy != null && localStationCopy.size() > 0) ? localStationCopy : StationDownloader.getStationList());
                    trainStops.clear();
                    List<StationData> sortedTrainStops = StationListSpinnerHandler.convertStationsToStationData(stationList);
                    trainStops.addAll(sortedTrainStops);
                } catch (Exception e) {
                    Log.d(LOG_NAME, e.getMessage());
                }

                Message msg = initializeHandler.obtainMessage();
                msg.obj = bartInfoActivity;
                initializeHandler.sendMessage(msg);
            }
        };
        refresh.start();
    }

    public static List<Trip> getTrips() {
        return TripPlannerHandler.trips;
    }

    private String paddNumber(int num) {
        if (num > 9) {
            return Integer.valueOf(num).toString();
        }
        return "0" + Integer.valueOf(num).toString();
    }

    private final Handler initializeHandler = new Handler() {

        public void handleMessage(Message msg) {

            final SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            TripPlannerHandler.dialog.dismiss();

            if (trainStops != null && trainStops.size() > 0) {

                TripPlannerHandler.sourceStop = new StationListSpinnerHandler(sbiThread, R.id.tpStationOriginList);
                TripPlannerHandler.sourceStop.initializeSpinnerLists(trainStops, self);

                TripPlannerHandler.destinationStop = new StationListSpinnerHandler(sbiThread, R.id.tpStationDestList);
                TripPlannerHandler.destinationStop.initializeSpinnerLists(trainStops, self);

                // TODO implement all the times here to populate

                Calendar cal = Calendar.getInstance();
                int currentMonth = cal.get(Calendar.MONTH) + 1;
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                int currentYear = cal.get(Calendar.YEAR);
                int currentHour = cal.get(Calendar.HOUR);
                int currentMinute = cal.get(Calendar.MINUTE);

                //cal.add(Calendar.DATE, 1);

                Spinner month = (Spinner) sbiThread.findViewById(R.id.tripMonth);
                Spinner day = (Spinner) sbiThread.findViewById(R.id.tripDay);
                Spinner year = (Spinner) sbiThread.findViewById(R.id.tripFullYear);

                List<String> months = new ArrayList<String>();
                months.add(paddNumber(currentMonth));
                month.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, months));

                List<String> days = new ArrayList<String>();
                days.add(paddNumber(currentDay));
                day.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, days));

                List<String> years = new ArrayList<String>();
                years.add(Integer.valueOf(currentYear).toString());
                years.add(Integer.valueOf(currentYear + 1).toString());
                year.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, years));

                Spinner hour = (Spinner) sbiThread.findViewById(R.id.tpTripHours);
                Spinner minute = (Spinner) sbiThread.findViewById(R.id.tpTripMinutes);

                List<String> hours = new ArrayList<String>();
                for (int i = 1; i <= 12; i++) {
                    hours.add(paddNumber(i));
                }
                hour.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, hours));

                List<String> minutes = new ArrayList<String>();
                for (int i = 0; i < 60; i++) {
                    minutes.add(paddNumber(i));
                }
                minute.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, minutes));


                Spinner tpTripAMPM = (Spinner) sbiThread.findViewById(R.id.tpTripAMPM);
                tpTripAMPM.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, ampm));

                Spinner departArrive = (Spinner) sbiThread.findViewById(R.id.tpDepartingOrArriving);
                departArrive.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, arrivingOrDeparting));

            }
        }
    };

    public void processSpinnerListData(SimpleBARTInfo sbi) {

        final SimpleBARTInfo bartInfoActivity = sbi;

        TripPlannerHandler.sourceStation = TripPlannerHandler.sourceStop.getSelectStationText();
        TripPlannerHandler.destinationStation = TripPlannerHandler.destinationStop.getSelectStationText();

        Log.d(LOG_NAME, "Codes: " + TripPlannerHandler.sourceStation + " = " + TripPlannerHandler.destinationStation);

        if (TripPlannerHandler.sourceStation == null || TripPlannerHandler.destinationStation == null) {
            return;
        }
        if (TripPlannerHandler.sourceStation.equals(SELECT_STATION_TEXT) || TripPlannerHandler.destinationStation.equals(SELECT_STATION_TEXT)) {
            return;
        }

        dialog = new LoadingSpinner(sbi, "Loading BART Fares...");

        final Thread refresh = new Thread() {

            public void run() {
                try {

                    final String departURL = APIConstants.SCHEDULE_DEPART + TripPlannerHandler.sourceStation
                            + APIConstants.SCHEDULE_DEST + TripPlannerHandler.destinationStation
                            + APIConstants.SCHEDULE_TIME + "now"
                            + APIConstants.SCHEDULE_DATE + "now"
                            + APIConstants.KEY_STRING_API;

                    final String arriveURL = APIConstants.SCHEDULE_ARRIVE + TripPlannerHandler.sourceStation
                            + APIConstants.SCHEDULE_DEST + TripPlannerHandler.destinationStation
                            + APIConstants.SCHEDULE_TIME + "now"
                            + APIConstants.SCHEDULE_DATE + "now"
                            + APIConstants.KEY_STRING_API;

                    // call api here
                    final String fairData = BaseDownloader.retriever.downloadURL(departURL, 0);

                    TripPlannerHandler.trips = tripParser.parseDocument(fairData);

                    Log.d(LOG_NAME, fairData);

                } catch (Exception e) {
                    Log.d(LOG_NAME, e.getMessage());
                }

                Message msg = updateHandler.obtainMessage();
                msg.obj = bartInfoActivity;
                updateHandler.sendMessage(msg);
            }
        };
        refresh.start();
    }

    public static void LoadInitialScreen(SimpleBARTInfo sbiThread) {

        ListView results = (ListView) sbiThread.findViewById(R.id.trip_planner_results);
        TripPlannerAdapter adapter = new TripPlannerAdapter(sbiThread, R.layout.trip_data, TripPlannerHandler.trips);
        adapter.setStationData(TripPlannerHandler.trainStops);
        results.setAdapter(adapter);

        TripDetailsListener listener = new TripDetailsListener();
        listener.setTripList(TripPlannerHandler.trips);
        listener.setStationData(TripPlannerHandler.trainStops);
        results.setOnItemClickListener(listener);
    }

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {

            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            LoadInitialScreen(sbiThread);

            Log.d(LOG_NAME, "Got something.");
            TripPlannerHandler.dialog.dismiss();
        }
    };
}
