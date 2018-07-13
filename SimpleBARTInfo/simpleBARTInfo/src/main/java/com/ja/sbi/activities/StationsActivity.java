package com.ja.sbi.activities;

import java.util.List;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.database.Table;
import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.adapters.StationListAdapter;
import com.ja.sbi.listeners.StationListener;
import com.ja.sbi.table.DataManager;

public class StationsActivity extends SBIBaseActivity {

	private final String LOG_NAME = this.getClass().getName();

    public void setupView(SimpleBARTInfo context) {
		
		final View view = context.findViewById(R.id.simple_bart_info_title);
		if ( view == null ) { 
			Log.d(LOG_NAME, "Guess we did not find the view?");
			return;
		}
    	
    	TextView tview = (TextView)view;
    	tview.setText("Stations");
    	
    	final ListView feedList = (ListView)context.findViewById(R.id.st_list_rows);
    	Log.d(LOG_NAME, "Do we have any stations? " + this.stations);
		feedList.setAdapter( new StationListAdapter(context, R.layout.data_row, this.stations) );
		feedList.setOnItemClickListener( new StationListener() );

		this.setViewStations(true);
		selectedStationName = null;
		selectedStationShortName = null;
		
		//this.getIntent().putExtra(SimpleBARTInfo.DATA_KEY, SimpleBARTInfo.ALL_STATIONS);
	}	
}
