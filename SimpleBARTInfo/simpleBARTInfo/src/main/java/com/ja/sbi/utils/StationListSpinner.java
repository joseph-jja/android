package com.ja.sbi.utils;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.beans.Station;
import com.ja.sbi.beans.StationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationListSpinner {

    private final String LOG_NAME = this.getClass().getName();
    private static final String SELECT_STATION_TEXT = "Please Select Station";

    private Spinner dropdown;
    final List<String> stationData = new ArrayList<String>();
    final List<String> stationCodes = new ArrayList<String>();

    private SimpleBARTInfo context;
    private String selectedStation;

    public StationListSpinner(SimpleBARTInfo sbiContext, int spinnerID) {

        dropdown = (Spinner) sbiContext.findViewById(spinnerID);

        stationData.add(SELECT_STATION_TEXT);
        stationCodes.add(SELECT_STATION_TEXT);

        context = sbiContext;
    }

    public void initializeSpinnerLists(List<StationData> trainStops, final StationListSpinnerIface methodImpl) {

        for (StationData data : trainStops) {
            stationData.add(data.getStationName());
            stationCodes.add(data.getStationCode());
        }
        ArrayAdapter sourceAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stationData);
        sourceAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.dropdown.setAdapter(sourceAdapter);

        this.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                Log.d(LOG_NAME, "Position is everything: " + position + " data = "
                        + stationData.get(position) + " key = " + stationCodes.get(position));

                selectedStation = stationCodes.get(position);

                methodImpl.processSpinnerListData(context);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String getSelectStationText() {
        return selectedStation;
    }

    public static List<StationData> convertStationsToStationData(List<Station> stationList) {

        List<StationData> trainStops = new ArrayList<StationData>();

        for (Station s : stationList) {
            StationData sd = new StationData();
            sd.setStationName(s.getStationName());
            sd.setStationCode(s.getShortName());
            trainStops.add(sd);
        }
        // sort
        Collections.sort(trainStops, new StationDataSorter());

        return trainStops;
    }
}
