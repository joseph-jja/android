package com.ja.sbi.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ja.sbi.trains.beans.Station;

public class FavoritesActivity extends SBIBaseActivity {

	private final String LOG_NAME = this.getClass().getName();

    public boolean onContextItemSelected(MenuItem item) {
    	
    	Log.d(LOG_NAME, "Context menu item selected.");
    	final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	View view = info.targetView;
    	final LinearLayout layout = ((LinearLayout)view);
    	View dbView = layout.findViewById(R.id.station_short_name);
    	
    	switch (item.getItemId()) {
    		case R.id.context_remove:
    			if ( dbView != null ) { 
    				TextView tv = (TextView)dbView;
        			this.dbAdapter.beginTransaction();
        			final String whereClause = DataManager.NAME_COL + " = \'" + DataManager.FAVORITES_SETTING + "\' and "
        						+ DataManager.VALUE_COL + " = \'" + tv.getText().toString() + "\'";
        			try { 
	        			this.dbAdapter.deleteWhere(DataManager.SETTINGS_TABLE, whereClause, null);
	        			this.dbAdapter.setTransactionSuccessful();
        			} catch (Exception ex) { 
        				ex.printStackTrace();
        			}
        			this.dbAdapter.endTransaction();
    			}
    			break;
    	}
    	
    	setupView();
    	return super.onContextItemSelected(item);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		Log.d(LOG_NAME, "In create context menu + " + v.toString());
		// only create this menu if the view is the feeds screen
		final MenuInflater inflater = getMenuInflater();
		
		if ( this.isViewStations() ) {
			Log.d(LOG_NAME, "Got correct view: favorites");
		    inflater.inflate(R.menu.remove_favorite, menu);
			super.onCreateContextMenu(menu, v, menuInfo);
		} 
	}

	public void setupView() {
		
		setContentView(R.layout.stations); 
		
		final View view = this.findViewById(R.id.simple_bart_info_title);
		if ( view == null ) { 
			return;
		}
    	
    	TextView tview = (TextView)view;
    	tview.setText("Favorite Stations");
    	
    	final ListView feedList = (ListView)this.findViewById(R.id.st_list_rows);
    	
    	Map<String, Station> stationmap = new HashMap<String, Station>();
    	for ( Station stat : this.stations ) { 
    		stationmap.put(stat.getShortName(), stat);
    	}
    	
    	List<Table> favorites = this.dbAdapter.find(DataManager.SETTINGS_TABLE, DataManager.NAME_COL, "\'" + DataManager.FAVORITES_SETTING + "\'");
    	Log.d(LOG_NAME, "Favorites size = " + ( ( favorites != null ) ? favorites.size() : 0 ) );
    	if ( favorites != null ) { 
	    	List<Station> favstations = new ArrayList<Station>();
	    	for ( Table tbl : favorites ) { 
	    		favstations.add(stationmap.get((String)tbl.getColumnValue(DataManager.VALUE_COL)));
	    	}
			
	    	feedList.setAdapter( new StationListAdapter(this, R.layout.data_row, favstations) );	
	
			feedList.setOnItemClickListener( new StationListener() );
			this.registerForContextMenu(feedList);
    	}
		this.setViewStations(true);
		selectedStationName = null;
		selectedStationShortName = null;
		
		this.getIntent().putExtra(SimpleBARTInfo.DATA_KEY, SimpleBARTInfo.FAVORITES);
	}
}
