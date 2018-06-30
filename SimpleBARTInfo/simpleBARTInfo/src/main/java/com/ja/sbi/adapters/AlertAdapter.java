package com.ja.sbi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.trains.beans.Alerts;

import java.util.List;

public class AlertAdapter extends ArrayAdapter<Alerts> {

    private final String LOG_NAME = this.getClass().getName();

    private LayoutInflater inflator;
    private int viewId;
    private static List<Alerts> alertItems;
    private static AlertAdapter self;

    public AlertAdapter(Context context, int resource, List<Alerts> alerts) {
        super(context, resource);
        this.viewId = resource;
        this.alertItems = alerts;
        Log.d(LOG_NAME, "Constructor called of " + LOG_NAME);
    }

    public int getCount() {
        return (this.alertItems != null ? this.alertItems.size() : 0);
    }

    public static List<Alerts> getAlerts() {
        return self.alertItems;
    }

    private LayoutInflater getInflator() {

        if (this.inflator == null) {
            this.inflator = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return this.inflator;
    }

    public View getView(int position, View currentView, ViewGroup parent) {

        final View rView = (currentView != null) ? currentView : getInflator().inflate(this.viewId, null);

        final Alerts alerts = alertItems.get(position);
        Log.d(LOG_NAME, "Got an alert!");
        if (alerts != null && rView != null) {
            final String station = (alerts.getStation() != null ? alerts.getStation() : "");
            final String description = (alerts.getDescription() != null ? alerts.getDescription() : "");
            final String alertType = (alerts.getAlertType() != null ? alerts.getAlertType() : "");
            //final String expires = (alerts.getExpires() != null ? alerts.getExpires() : "");
            final String posted = (alerts.getPosted() != null ? alerts.getPosted() : "");
            //final String smsText = (alerts.getSmsText() != null ? alerts.getSmsText() : "");
            Log.d(LOG_NAME, "Station name = " + station + " description " + description);

            AlertViewHolder holder = null;
            if (rView.getTag() == null) {
                holder = new AlertViewHolder();
                holder.station = ((TextView) rView.findViewById(R.id.stationName));
                holder.alertType = ((TextView) rView.findViewById(R.id.alertType));
                holder.description = ((TextView) rView.findViewById(R.id.description));
                holder.posted = ((TextView) rView.findViewById(R.id.posted));
                rView.setTag(holder);
            } else {
                holder = (AlertViewHolder) rView.getTag();
            }

            Log.d(LOG_NAME, "Got station " + station);
            Log.d(LOG_NAME, "Got description " + description);
            //Log.d(LOG_NAME, "Got holder name field " + holder.station);

            holder.station.setText("Station: " + station);
            holder.alertType.setText("Type: " + alertType);
            holder.description.setText("Description: " + description);
            holder.posted.setText("Posted: " + posted);
        }
        return rView;
    }

    private static class AlertViewHolder {

        public TextView station;
        public TextView description;
        public TextView alertType;
        public TextView posted;
    }
}
