package com.ja.sbi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Trip;
import com.ja.sbi.trains.beans.TripLeg;

import java.util.List;

public class TripPlannerAdapter extends ArrayAdapter<Trip> {

    private int viewId;
    private static List<Trip> tripsLocal;
    private LayoutInflater inflator;

    public TripPlannerAdapter(Context context, int resource, List<Trip> trips) {
        super(context, resource);
        this.viewId = resource;

        TripPlannerAdapter.tripsLocal = trips;
    }

    public int getCount() {
        return TripPlannerAdapter.tripsLocal.size();
    }

    private LayoutInflater getInflator() {

        if (this.inflator == null) {
            this.inflator = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return this.inflator;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        final View rView = (currentView != null) ? currentView : getInflator().inflate(this.viewId, null);

        final Trip currentTrip = TripPlannerAdapter.tripsLocal.get(position);
        if (currentTrip != null && rView != null) {

            final String destination = currentTrip.getDestination();
            final String origin = currentTrip.getOrigin();

            final String originTime = currentTrip.getOriginTime();
            final String originDate = currentTrip.getOriginDate();

            final String destinationTime = currentTrip.getDestinationTime();
            final String destinationDate = currentTrip.getDestinationDate();

            final Fare fareDetails = currentTrip.getFareDetails();
            final List<TripLeg> tripLegs = currentTrip.getLegs();

            TripPlannerViewHolder tripHolder = null;
            if (rView.getTag() == null) {
                tripHolder = new TripPlannerViewHolder();
                tripHolder.origin = (TextView) rView.findViewById(R.id.trip_origin_name);
                tripHolder.destination = (TextView) rView.findViewById(R.id.trip_destination_name);

                tripHolder.originDateTime = (TextView) rView.findViewById(R.id.trip_estimated_leave_time);
                tripHolder.destinationDateTime = (TextView) rView.findViewById(R.id.trip_estimate_arrival_time);

                tripHolder.fare = (TextView) rView.findViewById(R.id.trip_destination_name);
                tripHolder.clipperDiscount = (TextView) rView.findViewById(R.id.trip_destination_name);
                tripHolder.seniorDisabledClipper = (TextView) rView.findViewById(R.id.trip_destination_name);
                //tripHolder.youthClipper = (TextView) rView.findViewById(R.id.trip_destination_name);

            } else {
                tripHolder = (TripPlannerViewHolder) rView.getTag();
            }

            tripHolder.origin.setText(origin);
            tripHolder.destination.setText(destination);

            tripHolder.originDateTime.setText(originTime + " " + originDate);
            tripHolder.destinationDateTime.setText(destinationTime + " " + destinationDate);

            tripHolder.fare.setText(fareDetails.getFare());
            tripHolder.clipperDiscount.setText(fareDetails.getClipperDiscount());
            tripHolder.seniorDisabledClipper.setText(fareDetails.getSeniorDisabledClipper());
            //tripHolder.youthClipper.setText(fareDetails.getYouthClipper());
        }

        return rView;
    }

    private static class TripPlannerViewHolder {

        public TextView origin;
        public TextView destination;

        public TextView originDateTime;
        public TextView destinationDateTime;

        public TextView fare;
        public TextView clipperDiscount;
        public TextView seniorDisabledClipper;
        //public TextView youthClipper;

    }
}