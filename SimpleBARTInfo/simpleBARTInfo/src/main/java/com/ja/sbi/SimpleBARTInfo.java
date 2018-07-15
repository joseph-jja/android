package com.ja.sbi;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.List;
import java.util.ArrayList;
import com.ja.activity.BaseActivity;
import com.ja.dialog.BaseDialog;
import com.ja.sbi.handlers.AlertHandler;
import com.ja.sbi.handlers.FareCalculatorHandler;
import com.ja.sbi.handlers.StationsHandler;
import com.ja.sbi.map.BartMapManager;

public class SimpleBARTInfo extends BaseActivity {
    public static final String DATABASE_NAME = "SimpleBARTInfo";
    public static final int DATABASE_VERSION = 1;

    public static final String DATA_KEY = "intent_key";
    public static final String FAVORITES = "favorites";
    public static final String ALL_STATIONS = "stations";

    private StationsHandler stations = new StationsHandler();
    private List<Station> stationsData;

    private final String LOG_NAME = this.getClass().getName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stations);
        stations.initializeActivity(this, true);
        stationsData = stations.getStations();

        Log.d(LOG_NAME, "Super called!");
    }

    @Override
    public void initializeActivity() {
    }


    /**
     * tracking the back key
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(LOG_NAME, "In the key press event method " + keyCode);
        if (keyCode == 4) {
            setContentView(R.layout.stations);
            stations.initializeActivity(this, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        Log.d(LOG_NAME, "Created options menu");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.stations:
                setContentView(R.layout.stations);
                if (stationsData != null) {
                    stations.setStations(stationsData);
                } 
                stations.initializeActivity(this, true);
                break;
            case R.id.check_fares:
                setContentView(R.layout.fares);
                FareCalculatorHandler fares = new FareCalculatorHandler();
                fares.initializeActivity(this, stationsData);
                break;
            case R.id.check_alerts:
                final int currentView = getCurrentContentView();
                if (currentView != R.layout.alerts) {
                    setContentView(R.layout.alerts);
                }
                AlertHandler handler = new AlertHandler();
                handler.showLoadingSpinner(this);
                handler.downloadAlerts(this);
                break;
            case R.id.about:
                BaseDialog.alert(this, "About", getResources().getString(R.string.about_app));
                break;
            case R.id.refresh:
                setContentView(R.layout.stations);
                stationsData = null;
                stations.setStations(new ArrayList<Station>());
                stations.initializeActivity(this, false);
            case R.id.view_map:
                setContentView(R.layout.bart_map);
                BartMapManager map = new BartMapManager();
                map.initializeActivity(this);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
