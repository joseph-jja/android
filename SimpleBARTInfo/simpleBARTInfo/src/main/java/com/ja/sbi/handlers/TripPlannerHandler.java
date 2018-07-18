package com.ja.sbi.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.ja.dialog.LoadingSpinner;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.TripPlannerAdapter;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.beans.Station;
import com.ja.sbi.beans.StationData;
import com.ja.sbi.beans.Trip;
import com.ja.sbi.listeners.TripDetailsListener;
import com.ja.sbi.utils.SimpleSpinner;
import com.ja.sbi.utils.StationListSpinner;
import com.ja.sbi.utils.StationListSpinnerIface;
import com.ja.sbi.xml.TripParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    private static final String DEPARTING_TEXT = "Departing";
    private static final String AM_TEXT = "AM";
    private static final String PM_TEXT = "PM";

    private static final List<String> arrivingOrDeparting = Arrays.asList(TripPlannerHandler.DEPARTING_TEXT, "Arriving");
    private static final List<String> ampm = Arrays.asList(TripPlannerHandler.AM_TEXT, TripPlannerHandler.PM_TEXT);
    private static final List<String> hours = new ArrayList<String>();
    private static final List<String> minutes = new ArrayList<String>();

    final TripPlannerHandler self = this;

    private static StationListSpinner sourceStop;
    private static StationListSpinner destinationStop;
    private static SimpleSpinner departArrive;
    private static SimpleSpinner monthSpinner;
    private static SimpleSpinner daySpinner;
    private static SimpleSpinner yearSpinner;
    private static SimpleSpinner hourSpinner;
    private static SimpleSpinner minuteSpinner;
    private static SimpleSpinner ampmSpinner;

    public TripPlannerHandler(Context context, List<Station> stations) {

        // initialize hours
        for (int i = 1; i <= 12; i++) {
            TripPlannerHandler.hours.add(paddNumber(i));
        }

        // initialize minutes
        for (int i = 0; i < 60; i++) {
            TripPlannerHandler.minutes.add(paddNumber(i));
        }

        final SimpleBARTInfo bartInfoActivity = (SimpleBARTInfo) context;
        final List<Station> localStationCopy = stations;

        dialog = new LoadingSpinner(context, "Loading BART Fares...");

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    final List<Station> stationList = ((localStationCopy != null && localStationCopy.size() > 0) ? localStationCopy : StationDownloader.getStationList());
                    TripPlannerHandler.trainStops.clear();
                    List<StationData> sortedTrainStops = StationListSpinner.convertStationsToStationData(stationList);
                    TripPlannerHandler.trainStops.addAll(sortedTrainStops);
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

            if (TripPlannerHandler.trainStops != null && TripPlannerHandler.trainStops.size() > 0) {

                TripPlannerHandler.sourceStop = new StationListSpinner(sbiThread, R.id.tpStationOriginList);
                TripPlannerHandler.sourceStop.initializeSpinnerLists(TripPlannerHandler.trainStops, self);

                TripPlannerHandler.destinationStop = new StationListSpinner(sbiThread, R.id.tpStationDestList);
                TripPlannerHandler.destinationStop.initializeSpinnerLists(TripPlannerHandler.trainStops, self);

                // TODO implement all the times here to populate

                Calendar cal = Calendar.getInstance();
                int currentMonth = cal.get(Calendar.MONTH) + 1;
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                int currentYear = cal.get(Calendar.YEAR);
                int currentHour = cal.get(Calendar.HOUR);
                int currentMinute = cal.get(Calendar.MINUTE);
                String useAMPM = (cal.get(Calendar.AM_PM) == 0 ? TripPlannerHandler.AM_TEXT : TripPlannerHandler.PM_TEXT);

                List<String> days = new ArrayList<String>();
                days.add(paddNumber(currentDay));

                cal.add(Calendar.DATE, 1);
                int nextDay = cal.get(Calendar.DAY_OF_MONTH);
                days.add(paddNumber(nextDay));

                List<String> months = new ArrayList<String>();
                months.add(paddNumber(currentMonth));

                if ((cal.get(Calendar.MONTH) + 1) > currentMonth) {
                    months.add(paddNumber(cal.get(Calendar.MONTH) + 1));
                }

                List<String> years = new ArrayList<String>();
                years.add(Integer.valueOf(currentYear).toString());
                if ((cal.get(Calendar.YEAR)) > currentYear) {
                    years.add(Integer.valueOf(cal.get(Calendar.YEAR)).toString());
                }

                // mm/dd/yyyy
                TripPlannerHandler.monthSpinner = new SimpleSpinner(sbiThread, R.id.tripMonth,
                        months,
                        paddNumber(currentMonth), self);
                TripPlannerHandler.daySpinner = new SimpleSpinner(sbiThread, R.id.tripDay,
                        days,
                        paddNumber(currentDay), self);
                TripPlannerHandler.yearSpinner = new SimpleSpinner(sbiThread, R.id.tripFullYear,
                        years,
                        Integer.valueOf(currentYear).toString(), self);

                // time
                TripPlannerHandler.hourSpinner = new SimpleSpinner(sbiThread, R.id.tpTripHours,
                        TripPlannerHandler.hours,
                        paddNumber(currentHour), self);
                TripPlannerHandler.minuteSpinner = new SimpleSpinner(sbiThread, R.id.tpTripMinutes,
                        TripPlannerHandler.minutes,
                        paddNumber(currentMinute), self);

                // am pm
                TripPlannerHandler.ampmSpinner = new SimpleSpinner(sbiThread, R.id.tpTripAMPM,
                        TripPlannerHandler.ampm,
                        useAMPM, self);

                TripPlannerHandler.departArrive = new SimpleSpinner(sbiThread, R.id.tpDepartingOrArriving,
                        TripPlannerHandler.arrivingOrDeparting,
                        TripPlannerHandler.DEPARTING_TEXT, self);
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

        TripPlannerHandler.dialog = new LoadingSpinner(sbi, "Loading BART Fares...");

        final Thread refresh = new Thread() {

            public void run() {
                try {

                    final String departureOrArrivalTime = TripPlannerHandler.departArrive.getSelectText();

                    final String ampmTime = TripPlannerHandler.ampmSpinner.getSelectText();

                    final String selectedDate = TripPlannerHandler.monthSpinner.getSelectText()
                            + "/" + TripPlannerHandler.daySpinner.getSelectText()
                            + "/" + TripPlannerHandler.yearSpinner.getSelectText();

                    final String selectedTime = TripPlannerHandler.hourSpinner.getSelectText()
                            + ":" + TripPlannerHandler.minuteSpinner.getSelectText()
                            + " " + TripPlannerHandler.ampmSpinner.getSelectText();

                    final String baseURL = (departureOrArrivalTime.equals(TripPlannerHandler.DEPARTING_TEXT)) ? APIConstants.SCHEDULE_DEPART : APIConstants.SCHEDULE_ARRIVE;

                    final String requestUrl = baseURL + TripPlannerHandler.sourceStation
                            + APIConstants.SCHEDULE_DEST + TripPlannerHandler.destinationStation
                            + APIConstants.SCHEDULE_TIME + selectedTime
                            + APIConstants.SCHEDULE_DATE + selectedDate
                            + APIConstants.KEY_STRING_API;

                    // call api here
                    final String fairData = BaseDownloader.retriever.downloadURL(requestUrl, 0);

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
