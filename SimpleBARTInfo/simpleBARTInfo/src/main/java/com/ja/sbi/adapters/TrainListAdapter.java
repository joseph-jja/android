package com.ja.sbi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.beans.Train;

import java.util.List;

public class TrainListAdapter  extends ArrayAdapter<Train> {

	private final String LOG_NAME = this.getClass().getName();

	private LayoutInflater inflator;
	private int viewId;
	private List<Train> trains;

	public TrainListAdapter(Context context, int resource, List<Train> trains) {
		super(context, resource);
		this.viewId = resource;
		this.trains = trains;
		Log.d(LOG_NAME, "Constructor called of " + LOG_NAME);
	}

	public int getCount() { 
		return ( this.trains != null ) ? this.trains.size() : 0;
	}

	public List<Train> getStationNames() { 
		return this.trains;
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
		final Train xTrains = trains.get(position);
		Log.d(LOG_NAME, "Got a station!");
		if (xTrains != null) {
			final String name = xTrains.getTrainName();
			final String time = xTrains.getTrainTime();
			final String length = xTrains.getLength();
			final String platform = xTrains.getPlatform();
			final String direction = xTrains.getDirection();
			Log.d(LOG_NAME, "Station name = " + name);

			TrainViewHolder holder = null;
			if ( rView.getTag() == null ) {
				holder = new TrainViewHolder(); 
				holder.name = ((TextView)rView.findViewById(R.id.st_name));
				holder.time = ((TextView)rView.findViewById(R.id.st_time));
				holder.length = ((TextView)rView.findViewById(R.id.train_length));
				holder.platform = ((TextView)rView.findViewById(R.id.station_platform));				
				holder.direction = ((TextView)rView.findViewById(R.id.train_direction));				
				rView.setTag(holder);
			} else {
				holder = (TrainViewHolder)rView.getTag();
			}
			ViewGroup.LayoutParams layoutParams = holder.name.getLayoutParams();
			ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)layoutParams;
			marginLayoutParams.bottomMargin = 0;
			marginLayoutParams.topMargin = 0;
			holder.time.setLayoutParams(layoutParams);
			holder.length.setLayoutParams(layoutParams);
			holder.platform.setLayoutParams(layoutParams);
			holder.direction.setLayoutParams(layoutParams);

			Log.v(LOG_NAME, "Got holder " + holder);
			Log.v(LOG_NAME, "Got holder name field " + holder.name);
			
			holder.name.setText(name);
			holder.time.setText("Times: " + time);
			holder.length.setText("Train Lengths: " + length);
			holder.platform.setText("Platforms: " + platform);
			holder.direction.setText("Direction: " + direction);
		}
		return rView;
	}

	private static class TrainViewHolder {

		public TextView name;
		public TextView time;
		public TextView length; 
		public TextView platform;
		public TextView direction;
	}

}
