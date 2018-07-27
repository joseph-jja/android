package com.ja.sbi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.beans.StationData;
import com.ja.sbi.beans.TripLeg;

import java.util.List;

public class TripLegAdapter extends ArrayAdapter<TripLeg> {

    private int viewId;
    private static List<TripLeg> tripsLocal;
    private LayoutInflater inflator;
    private List<StationData> stationData;
    private Context thisContext;

    public TripLegAdapter(Context context, int resource, List<TripLeg> trips) {
        super(context, resource);
        this.viewId = resource;
        thisContext = context;
        TripLegAdapter.tripsLocal = trips;
    }

    public int getCount() {
        return TripLegAdapter.tripsLocal.size();
    }

    public void setStationData(List<StationData> stationData) {
        this.stationData = stationData;
    }

    private LayoutInflater getInflator() {

        if (this.inflator == null) {
            this.inflator = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return this.inflator;
    }

    private String findStationLongName(String stationShortName) {
        String result = stationShortName;
        if (this.stationData == null) {
            return result;
        }

        for (StationData item : this.stationData) {
            if (item.getStationCode().equals(stationShortName)) {
                result = item.getStationName();
                break;
            }
        }
        return result;
    }

    public View getView(int position, View currentView, ViewGroup parent) {
        final View rView = (currentView != null) ? currentView : getInflator().inflate(this.viewId, null);

        final TripLeg currentTripLeg = TripLegAdapter.tripsLocal.get(position);
        if (currentTripLeg != null && rView != null) {

            final String origin = currentTripLeg.getOrigin();
            final String destination = currentTripLeg.getDestination();

            final String tripLegStartTime = currentTripLeg.getOriginTime() + " " + currentTripLeg.getOriginDate();
            final String tripLegEndTime = currentTripLeg.getDestinationTime() + " " + currentTripLeg.getDestinationDate();

            final String trainHeadStationName = currentTripLeg.getTrainHeadStation();

            TripLegViewHolder holder = null;
            if (rView.getTag() == null) {
                holder = new TripLegViewHolder();
                holder.tripLegOriginName = (TextView) rView.findViewById(R.id.trip_leg_origin_name);
                holder.tripLegDestinationName = (TextView) rView.findViewById(R.id.trip_leg_destination_name);
                holder.tripLegOriginDateTime = (TextView) rView.findViewById(R.id.trip_leg_origin_time);
                holder.tripLegDestinationDateTime = (TextView) rView.findViewById(R.id.trip_leg_destination_time);
                holder.trainEndStationName = (TextView) rView.findViewById(R.id.trip_leg_train_head_station_name);
            } else {
                holder = (TripLegViewHolder) rView.getTag();
            }

            holder.tripLegOriginName.setText("Stop Name: " + findStationLongName(origin));
            holder.tripLegDestinationName.setText("Stop Name: " + findStationLongName(destination));

            holder.tripLegOriginDateTime.setText("Est Leave Time: " + tripLegStartTime);
            holder.tripLegDestinationDateTime.setText("Est Arrive Time: " + tripLegEndTime);

            holder.trainEndStationName.setText("Train Name: " + findStationLongName(trainHeadStationName));
        }
        return rView;
    }


    private static class TripLegViewHolder {

        public TextView tripLegOriginName;
        public TextView tripLegDestinationName;

        public TextView tripLegOriginDateTime;
        public TextView tripLegDestinationDateTime;

        public TextView trainEndStationName;
    }
}
