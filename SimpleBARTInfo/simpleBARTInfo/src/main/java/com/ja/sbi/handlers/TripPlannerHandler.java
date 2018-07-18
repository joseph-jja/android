package com.ja.sbi.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ja.dialog.LoadingSpinner;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.StationData;
import com.ja.sbi.trains.beans.Trip;
import com.ja.sbi.trains.beans.TripLeg;
import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.TripParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ja.sbi.handlers.StationDataSorter;

public class TripPlannerHandler {

    private final String LOG_NAME = this.getClass().getName();

    private static final List<StationData> trainStops = new ArrayList<StationData>();

    private static final String SELECT_STATION_TEXT = "Please Select Station";

    private static String sourceStation = null;
    private static String destinationStation = null;

    private static LoadingSpinner dialog = null;


    public TripPlannerHandler(Context context, List<Station> stations) {


        final SimpleBARTInfo bartInfoActivity = (SimpleBARTInfo) context;
        final List<Station> localStationCopy = stations;

        dialog = new LoadingSpinner(context, "Loading BART Fares...");

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    final List<Station> stationList = ((localStationCopy != null && localStationCopy.size() > 0) ? localStationCopy : StationDownloader.getStationList());
                    trainStops.clear();
                    for (Station s : stationList) {
                        StationData sd = new StationData();
                        Log.d(LOG_NAME, s.getStationName());
                        sd.setStationName(s.getStationName());
                        sd.setStationCode(s.getShortName());
                        trainStops.add(sd);
                    }
                    // sort
                    Collections.sort(trainStops, new StationDataSorter());
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

    private final Handler initializeHandler = new Handler() {

        public void handleMessage(Message msg) {

            final SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            TripPlannerHandler.dialog.dismiss();

            if (trainStops != null && trainStops.size() > 0) {
                Spinner sourceStop = (Spinner) sbiThread.findViewById(R.id.tpStationOriginList);
                Spinner destinationStop = (Spinner) sbiThread.findViewById(R.id.tpStationDestList);

                final List<String> stationData = new ArrayList<String>();
                final List<String> stationCodes = new ArrayList<String>();
                stationData.add(SELECT_STATION_TEXT);
                stationCodes.add(SELECT_STATION_TEXT);

                int i = 0;
                for (StationData data : trainStops) {
                    stationData.add(data.getStationName());
                    stationCodes.add(data.getStationCode());
                    i += 1;
                }

                ArrayAdapter sourceAdapter = new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, stationData);
                sourceAdapter.setDropDownViewResource(R.layout.spinner_item);
                sourceStop.setAdapter(sourceAdapter);

                ArrayAdapter destinationAdapter = new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, stationData);
                destinationAdapter.setDropDownViewResource(R.layout.spinner_item);
                destinationStop.setAdapter(destinationAdapter);

                sourceStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        Log.d(LOG_NAME, "Position is everything: " + position + " data = "
                                + stationData.get(position) + " key = " + stationCodes.get(position));

                        TripPlannerHandler.sourceStation = stationCodes.get(position);

                        //getFare(sbiThread);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                destinationStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        Log.d(LOG_NAME, "Second position: " + position + " data = "
                                + stationData.get(position) + " key = " + stationCodes.get(position));

                        TripPlannerHandler.destinationStation = stationCodes.get(position);

                        //getFare(sbiThread);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
            Spinner month = (Spinner) sbiThread.findViewById(R.id.tripMonth);
            Spinner day = (Spinner) sbiThread.findViewById(R.id.tripDay);
            Spinner year = (Spinner) sbiThread.findViewById(R.id.tripFullYear);

            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentDay = cal.get(Calendar.DAY_OF_MONTH);
            int currentYear = cal.get(Calendar.YEAR);

            List<String> months = new ArrayList<String>();
            months.add(Integer.valueOf(currentMonth).toString());
            month.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, months));

            List<String> days = new ArrayList<String>();
            days.add(Integer.valueOf(currentDay).toString());
            day.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, days));

            List<String> years = new ArrayList<String>();
            years.add(Integer.valueOf(currentYear).toString());
            year.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, years));

            Spinner hour = (Spinner) sbiThread.findViewById(R.id.tpTripHours);
            Spinner minute = (Spinner) sbiThread.findViewById(R.id.tpTripMinutes);
            int currentHour = cal.get(Calendar.HOUR);
            int currentMinute = cal.get(Calendar.MINUTE);

            List<String> hours = new ArrayList<String>();
            for (int i = 0; i < 24; i++) {
                hours.add(Integer.valueOf(i).toString());
            }
            hour.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, hours));

            List<String> minutes = new ArrayList<String>();
            for (int i = 0; i < 60; i++) {
                minutes.add(Integer.valueOf(i).toString());
            }
            minute.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, minutes));
        }

    };
}
