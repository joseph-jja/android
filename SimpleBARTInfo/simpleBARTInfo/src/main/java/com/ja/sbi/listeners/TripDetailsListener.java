package com.ja.sbi.listeners;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.TripLegAdapter;
import com.ja.sbi.trains.beans.StationData;
import com.ja.sbi.trains.beans.Trip;

import java.util.List;

public class TripDetailsListener implements AdapterView.OnItemClickListener  {

    private List<Trip> trips;
    private List<StationData> stationData;

    public void setTripList(List<Trip> tripList) {
        this.trips = tripList;
    }

    public void setStationData(List<StationData> data) {
        this.stationData = data;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Trip currentTrip = this.trips.get(position);
        SimpleBARTInfo sbi = (SimpleBARTInfo) parent.getContext();

        sbi.setContentView(R.layout.trip_details);

        TripLegAdapter tripLeg = new TripLegAdapter(sbi, R.layout.trip_details, currentTrip.getLegs());
        tripLeg.setStationData(this.stationData);

        ListView list = (ListView)sbi.findViewById(R.id.trip_planner_leg_rows);
        //list.setAdapter(tripLeg);
    }

}
