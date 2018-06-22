package com.ja.sbi.listeners;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.activities.SBIBaseActivity;
import com.ja.sbi.adapters.TrainListAdapter;
import com.ja.sbi.bart.api.TrainDownloader;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.trains.beans.Train;

public class StationListener implements AdapterView.OnItemClickListener {

	private final String LOG_NAME = this.getClass().getName();
		
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		TextView stView = (TextView)view.findViewById(com.ja.sbi.R.id.station_short_name);
		TextView tView = (TextView)view.findViewById(com.ja.sbi.R.id.st_name);
		
		String stationName = tView.getText().toString();
		String stationShortName = stView.getText().toString();
		TrainDownloader.setSelectedStationName(stationName);
		TrainDownloader.setSelectedStationShortName(stationShortName);
		
		Log.d(LOG_NAME, "Got station name =  " + stationName + " " + stationShortName + " " + id);
		
		SBIBaseActivity sbi = (SBIBaseActivity)parent.getContext();
		
		StationListener.setTrainView(sbi);
	}
	
	public static final void setTrainView(SBIBaseActivity activity) {
		
		List<Station> trainStations = TrainDownloader.getTrains(TrainDownloader.getSelectedStationShortName());
		
		if ( trainStations == null || trainStations.size() < 1 ) {
			return;			
		}
		
		// should only be one train but....
		List<Train> trains = trainStations.get(0).getTrains();
		Log.d("StationListener", "Train count: " + trains);
		if ( trains != null ) { 
			final View view = activity.findViewById(R.id.st_list_rows);
			if ( view != null ) { 
				final ListView feedList = (ListView)view;
				feedList.setAdapter( new TrainListAdapter(activity, R.layout.data_row, trains) );
				feedList.setOnItemClickListener( null );
				TextView tview = (TextView)activity.findViewById(R.id.simple_bart_info_title);
		    	tview.setText("Trains: " + TrainDownloader.getSelectedStationName());
				activity.setViewStations(false);
			}
		}		
	}
}
