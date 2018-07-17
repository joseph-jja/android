package com.ja.sbi.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.FairParser;
import com.ja.sbi.trains.beans.StationData;
import com.ja.dialog.LoadingSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FareCalculatorHandler {

    private final String LOG_NAME = this.getClass().getName();
    private static final List<StationData> trainStops = new ArrayList<StationData>();
    private static final FairParser parser = new FairParser();
    private static String sourceStation = null;
    private static String destinationStation = null;

    private static LoadingSpinner dialog = null;

    private static final String SELECT_DEPARTURE_TEXT = "Please Select Departue Station";
    private static final String SELECT_ARRIVAL_TEXT = "Please Select Arrival Station";

    private static Fare currentFare;

    public FareCalculatorHandler(Context context, List<Station> stations) {

        final SimpleBARTInfo bartInfoActivity = (SimpleBARTInfo) context;
        final List<Station> localStationCopy = stations;

        dialog = new LoadingSpinner(context, "Loading BART Fares...");

        final Thread refresh = new Thread() {
            public void run() {
                try {
                    final List<Station> stationList = ((localStationCopy != null && localStationCopy.size() > 0) ? localStationCopy: StationDownloader.getStationList());
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

            FareCalculatorHandler.dialog.dismiss();

            if (trainStops != null && trainStops.size() > 0) {
                Spinner sourceStop = (Spinner) sbiThread.findViewById(R.id.stationInList);
                Spinner destinationStop = (Spinner) sbiThread.findViewById(R.id.stationsAvailable);

                final List<String> stationData = new ArrayList<String>();
                final List<String> stationCodes = new ArrayList<String>();
                stationData.add(SELECT_DEPARTURE_TEXT);
                stationCodes.add(SELECT_ARRIVAL_TEXT);

                int i = 0;
                for (StationData data : trainStops) {
                    stationData.add(data.getStationName());
                    stationCodes.add(data.getStationCode());
                    i += 1;
                }
                sourceStop.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, stationData));
                destinationStop.setAdapter(new ArrayAdapter<String>(sbiThread, android.R.layout.simple_spinner_item, stationData));

                sourceStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        Log.d(LOG_NAME, "Position is everything: " + position + " data = "
                                + stationData.get(position) + " key = " + stationCodes.get(position));

                        FareCalculatorHandler.sourceStation = stationCodes.get(position);

                        getFare(sbiThread);
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

                        FareCalculatorHandler.destinationStation = stationCodes.get(position);

                        getFare(sbiThread);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }
    };

    private void getFare(SimpleBARTInfo sbi) {

        final SimpleBARTInfo bartInfoActivity = sbi;

        Log.d(LOG_NAME, "Codes: " + FareCalculatorHandler.sourceStation + " = " + FareCalculatorHandler.destinationStation);

        if (FareCalculatorHandler.sourceStation == null || FareCalculatorHandler.destinationStation == null) {
            return;
        }
        if (FareCalculatorHandler.sourceStation.equals(SELECT_DEPARTURE_TEXT) || FareCalculatorHandler.destinationStation.equals(SELECT_ARRIVAL_TEXT)) {
            return;
        }

        dialog = new LoadingSpinner(sbi, "Loading BART Fares...");

        final Thread refresh = new Thread() {

            public void run() {
                try {
                    // call api here
                    final String fairData = BaseDownloader.retriever.downloadURL(APIConstants.FAIR_API + FareCalculatorHandler.sourceStation + APIConstants.FAIR_DEST + FareCalculatorHandler.destinationStation + APIConstants.KEY_STRING_API, 0);

                    List<Fare> fares = parser.parseDocument(fairData);

                    currentFare = fares.get(0);


                    Log.d(LOG_NAME, currentFare.getFare());
                    Log.d(LOG_NAME, currentFare.getClipperDiscount());

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

    private final Handler updateHandler = new Handler() {

        public void handleMessage(Message msg) {

            SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            ((TextView) sbiThread.findViewById(R.id.fareValue)).setText("Fare: " + currentFare.getFare());
            ((TextView) sbiThread.findViewById(R.id.clipperFare)).setText("Clipper Card Fare: " + currentFare.getClipperDiscount());
            ((TextView) sbiThread.findViewById(R.id.seniorDisabledClipper)).setText("Senior/Disabled Clipper Fare: " + currentFare.getSeniorDisabledClipper());

            Log.d(LOG_NAME, "Got something.");
            FareCalculatorHandler.dialog.dismiss();
        }
    };

    public static final class StationDataSorter implements Comparator<StationData> {

        public int compare(StationData stationOne, StationData stationTwo) {

            if (stationOne == null) {
                return -1;
            }
            if (stationTwo == null) {
                return 1;
            }
            return stationOne.getStationName().compareTo(stationTwo.getStationName());
        }

    }
}
