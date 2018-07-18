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

import com.ja.dialog.LoadingSpinner;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.beans.Fare;
import com.ja.sbi.beans.Station;
import com.ja.sbi.beans.StationData;
import com.ja.sbi.xml.FairParser;

import java.util.ArrayList;
import java.util.List;

public class FareCalculatorHandler implements StationListSpinnerIface {

    private final String LOG_NAME = this.getClass().getName();
    private static final List<StationData> trainStops = new ArrayList<StationData>();
    private static final FairParser parser = new FairParser();
    private static String sourceStation = null;
    private static String destinationStation = null;

    private static LoadingSpinner dialog = null;

    private static final String SELECT_STATION_TEXT = "Please Select Station";

    private static Fare currentFare;
    private final FareCalculatorHandler self = this;

    private static StationListSpinnerHandler sourceStop;
    private static StationListSpinnerHandler destinationStop;

    public FareCalculatorHandler(Context context, List<Station> stations) {

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

    private final Handler initializeHandler = new Handler() {

        public void handleMessage(Message msg) {

            final SimpleBARTInfo sbiThread = (SimpleBARTInfo) msg.obj;

            FareCalculatorHandler.dialog.dismiss();

            if (trainStops != null && trainStops.size() > 0) {

                FareCalculatorHandler.sourceStop = new StationListSpinnerHandler(sbiThread, R.id.stationInList);
                FareCalculatorHandler.sourceStop.initializeSpinnerLists(trainStops, self);

                FareCalculatorHandler.destinationStop = new StationListSpinnerHandler(sbiThread, R.id.stationsAvailable);
                FareCalculatorHandler.destinationStop.initializeSpinnerLists(trainStops, self);

            }
        }
    };

    public void processSpinnerListData(SimpleBARTInfo sbi) {

        final SimpleBARTInfo bartInfoActivity = sbi;

        FareCalculatorHandler.sourceStation = FareCalculatorHandler.sourceStop.getSelectStationText();
        FareCalculatorHandler.destinationStation =FareCalculatorHandler.destinationStop.getSelectStationText();

        Log.d(LOG_NAME, "Codes: " + FareCalculatorHandler.sourceStation + " = " + FareCalculatorHandler.destinationStation);

        if (FareCalculatorHandler.sourceStation == null || FareCalculatorHandler.destinationStation == null) {
            return;
        }
        if (FareCalculatorHandler.sourceStation.equals(SELECT_STATION_TEXT) || FareCalculatorHandler.destinationStation.equals(SELECT_STATION_TEXT)) {
            return;
        }

        dialog = new LoadingSpinner(sbi, "Loading BART Fares...");

        final Thread refresh = new Thread() {

            public void run() {
                try {
                    // call fare api here
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
}
