package com.ja.sbi.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ja.activity.BaseActivity;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.FairParser;

public class FareCalculatorActivity extends BaseActivity {

	private final String LOG_NAME = this.getClass().getName();
	private static final List<StationData> trainStops = new ArrayList<StationData>();
	private static final FairParser parser = new FairParser();
	private String sourceStation = null;
	private String destinationStation = null;
	
	private static final String SELECT_STATION_TEXT = "Please Select Station";
	
	private static Fare currentFare;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);
        
		super.setContentView(R.layout.fares);

    	initializeActivity();
    }
    
	public void initializeActivity() {

		loadTrainData();
		
		if ( trainStops != null && trainStops.size() > 0 ) {
			Collections.sort(trainStops, new StationDataSorter());
			Spinner sourceStop = (Spinner)this.findViewById(R.id.stationInList);
			Spinner destinationStop = (Spinner)this.findViewById(R.id.stationsAvailable);
			
			final List<String> stationData = new ArrayList<String>();
			final List<String> stationCodes = new ArrayList<String>();
			stationData.add(SELECT_STATION_TEXT);
			stationCodes.add(SELECT_STATION_TEXT);
			
			int i = 0;
			for ( StationData data : trainStops ) {
				stationData.add(data.getStationName());
				stationCodes.add(data.getStationCode());
				i += 1;
			}
			sourceStop.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stationData));
			destinationStop.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stationData));
			
			sourceStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					// TODO Auto-generated method stub
					Log.d(LOG_NAME, "Position is everything: " + position + " data = " 
							+  stationData.get(position) + " key = " + stationCodes.get(position));
					
					((FareCalculatorActivity)view.getContext()).sourceStation = stationCodes.get(position);
					
					getFare();
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
							+  stationData.get(position) + " key = " + stationCodes.get(position));
					
					((FareCalculatorActivity)view.getContext()).destinationStation = stationCodes.get(position);
					
					getFare();
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {

				}				
			});
		}
	}
	
	private void getFare() { 
		
		Log.d(LOG_NAME, "Codes: " + this.sourceStation + " = " + this.destinationStation);
		
		if ( this.sourceStation == null || this.destinationStation == null ) {
			return;
		}
		if ( this.sourceStation.equals(SELECT_STATION_TEXT) || this.destinationStation.equals(SELECT_STATION_TEXT) ) {
			return;
		}

		try { 
			// TODO call api here
			final String fairData = BaseDownloader.retriever.downloadURL(APIConstants.FAIR_API + this.sourceStation + APIConstants.FAIR_DEST + this.destinationStation + APIConstants.KEY_STRING_API);
		
			List<Fare> fares = parser.parseDocument(fairData);
			
			currentFare = fares.get(0);
			
			((TextView)this.findViewById(R.id.fareValue)).setText("Fare: " + currentFare.getFare());
			((TextView)this.findViewById(R.id.clipperFare)).setText("Clipper Card Fare: " + currentFare.getClipperDiscount());
			((TextView)this.findViewById(R.id.seniorDisabledClipper)).setText("Senior/Disabled Clipper Fare: " + currentFare.getSeniorDisabledClipper());

			Log.d(LOG_NAME, currentFare.getFare());
			Log.d(LOG_NAME, currentFare.getClipperDiscount());
			
		} catch (Exception e) { 
			Log.d(LOG_NAME, e.getMessage());
		}
	}
	
	private void loadTrainData() { 
		
		try {
			final List<Station> stations = StationDownloader.getStationList();
			trainStops.clear();
			for ( Station s: stations ) {
				StationData sd = new StationData();
				Log.d(LOG_NAME, s.getStationName());
				sd.setStationName( s.getStationName() );
				sd.setStationCode( s.getShortName() );
				trainStops.add(sd);
			}
		} catch ( Exception e ) { 
			Log.d(LOG_NAME, e.getMessage());
		}
	}

	public static class StationData {
		
		private String stationName;
		private String stationCode;
		private String fare;
		/**
		 * @param stationName the stationName to set
		 */
		public void setStationName(String stationName) {
			this.stationName = stationName;
		}
		/**
		 * @return the stationName
		 */
		public String getStationName() {
			return stationName;
		}
		/**
		 * @param stationCode the stationCode to set
		 */
		public void setStationCode(String stationCode) {
			this.stationCode = stationCode;
		}
		/**
		 * @return the stationCode
		 */
		public String getStationCode() {
			return stationCode;
		}
		/**
		 * @param fare the fare to set
		 */
		public void setFare(String fare) {
			this.fare = fare;
		}
		/**
		 * @return the fare
		 */
		public String getFare() {
			return fare;
		}
	}
	
	public static final class StationDataSorter implements Comparator<StationData> {

		public int compare(StationData stationOne, StationData stationTwo) {
			
			if ( stationOne == null ) { return -1; }
			if ( stationTwo == null ) { return 1; }
			return stationOne.getStationName().compareTo(stationTwo.getStationName());
		}
		
	}
}
