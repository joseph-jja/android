package com.ja.sbi;

import java.io.IOException;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.ja.dialog.BaseDialog;
import com.ja.sbi.activities.AlertActivity;
import com.ja.sbi.activities.FareCalculatorActivity;
import com.ja.sbi.activities.FavoritesActivity;
import com.ja.sbi.activities.SBIBaseActivity;
import com.ja.sbi.activities.StationsActivity;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.bart.api.TrainDownloader;
import com.ja.sbi.listeners.StationListener;
import com.ja.sbi.map.BartMapActivity;

public class SimpleBARTInfo extends TabActivity
{
	public static final String DATABASE_NAME = "SimpleBARTInfo";
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATA_KEY = "intent_key";
	public static final String FAVORITES = "favorites";
	public static final String ALL_STATIONS = "stations";
	
	private final String LOG_NAME = this.getClass().getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);
        Log.d(LOG_NAME, "Super called!");
    	
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        
        intent = new Intent();
        intent.setClass(this, StationsActivity.class);
        
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("stations").setIndicator("Stations", 
        		res.getDrawable(R.drawable.ic_tab_trains)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent();
        intent.setClass(this, FavoritesActivity.class);
        spec = tabHost.newTabSpec("favorites").setIndicator("Favorites", 
        		res.getDrawable(R.drawable.ic_tab_trains)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent();
        intent.setClass(this, BartMapActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("map").setIndicator("Map",
        		res.getDrawable(R.drawable.ic_tab_map)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent();
        intent.setClass(this, FareCalculatorActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("fares").setIndicator("Fares",
        		res.getDrawable(R.drawable.ic_tab_map)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent();
        intent.setClass(this, AlertActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("alerts").setIndicator("Alerts",
        		res.getDrawable(R.drawable.ic_tab_trains)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
        
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { 
        	public void onTabChanged(String tabId) {
        		Log.d(LOG_NAME, "Tab changed to id = " + tabId);
        		if ( tabId.equals("favorites") ) {
        			SBIBaseActivity.getSelf().setupView();
        		} 
        	}
        });
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    Log.d(LOG_NAME, "Created options menu");
	    return super.onCreateOptionsMenu(menu);
	}
    
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.refresh:
				TabHost tabHost = getTabHost();
				if ( tabHost != null ) { 
					final int currentTabID = tabHost.getCurrentTab();
					Log.d(LOG_NAME, "Active tab is " + currentTabID );
					switch ( currentTabID ) {
						case 0:
							StationListener.setTrainView(StationsActivity.getSelf());
							break;
						case 1:
							StationListener.setTrainView(StationsActivity.getSelf());
							break;
						case 4:
							AlertActivity.getSelf().downloadAlerts();
							break;
						default:
							View v = findViewById(R.id.simple_bart_info_title);
							if ( v == null ) { 
								tabHost.setCurrentTab(0);
							}
							break;
					}
				}
				break;
			case R.id.about: 
				BaseDialog.alert(this, "About", getResources().getString(R.string.about_app));
				break;
			default:
				break;
		}
	
		return super.onOptionsItemSelected(item);
	}
}
