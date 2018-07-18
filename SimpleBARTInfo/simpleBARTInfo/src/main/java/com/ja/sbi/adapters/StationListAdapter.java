package com.ja.sbi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.beans.Station;

import java.util.List;

public class StationListAdapter extends ArrayAdapter<Station> {

	private final String LOG_NAME = this.getClass().getName();
	
	private LayoutInflater inflator;
	private int viewId;
	private static List<Station> stations;
	private static StationListAdapter self;
	
	public StationListAdapter(Context context, int resource, List<Station> stations) {
		super(context, resource);
		this.viewId = resource;
		StationListAdapter.stations = stations;
		Log.d(LOG_NAME, "Constructor called of " + LOG_NAME);
		self = this;
	}
	
	public int getCount() { 
		return ( stations != null ) ? stations.size() : 0;
	}
	
	public static List<Station> getStationNames() { 
		return stations;
	}
	
	private LayoutInflater getInflator() { 
		
		if ( this.inflator == null ) { 
			this.inflator = (LayoutInflater)super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return this.inflator;
	}
	
	@Override
    public View getView(int position, View currentView, ViewGroup parent) {
		
		final View rView = ( currentView != null ) ? currentView : getInflator().inflate(this.viewId, null);    
		final Station xStation = stations.get(position);
		Log.d(LOG_NAME, "Got a station!");
		if ( xStation != null && rView != null ) {
			final String name = xStation.getStationName();
			final String time = xStation.getStationTime();
			final String shortName = xStation.getShortName();
			Log.d(LOG_NAME, "Station name = " + name + " - abbr = " + shortName);
			
			StationViewHolder holder = null;
			if ( rView.getTag() == null ) {
				holder = new StationViewHolder(); 
				holder.name = ((TextView)rView.findViewById(R.id.st_name));
				holder.time = ((TextView)rView.findViewById(R.id.st_time));
				holder.shortName = ((TextView)rView.findViewById(R.id.station_short_name));
				rView.setTag(holder);
			} else {
				holder = (StationViewHolder)rView.getTag();
			}
			
			Log.d(LOG_NAME, "Got holder " + holder);
			//Log.d(LOG_NAME, "Got holder name field " + holder.name);
			
			holder.name.setText(name);
			holder.time.setText(time);
			holder.shortName.setText(shortName);
		}
		return rView;
    }
	
	private static class StationViewHolder {
		
		public TextView name;
		public TextView time;
		public TextView shortName;
	}
}
