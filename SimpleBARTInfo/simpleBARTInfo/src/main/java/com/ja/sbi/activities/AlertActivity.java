package com.ja.sbi.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ja.activity.BaseActivity;
import com.ja.sbi.R;
import com.ja.sbi.bart.api.AlertDownloader;

public class AlertActivity extends BaseActivity {

	private final String LOG_NAME = this.getClass().getName();
	private ProgressDialog dialog = null;
	private static final List<Map<String, String>> results = new ArrayList<Map<String, String>>();
	private static AlertActivity self;

	public void onCreate(Bundle savedInstanceState) {

		Log.d(LOG_NAME, "Initializing!");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.alerts);
		
		self = this; 
		
		initializeActivity();
	}
	
	@Override
	public void initializeActivity() {
	
		downloadAlerts();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		downloadAlerts();
	}

	private void showLoadingSpinner() { 

		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("Loading BART Alerts...");
		// change to progress bar
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
	}

	public void downloadAlerts() {

		final int currentView = getCurrentContentView();
		if ( currentView != R.layout.alerts ) { 
			setContentView(R.layout.alerts); 
		}
		showLoadingSpinner();

		final AlertActivity sbiThread = this;
		final Thread refresh = new Thread() {
			public void run() {
				try {
					results.clear();
					final List<Map<String, String>> data = AlertDownloader.getAlerts();
					results.addAll(data);
					Log.v(LOG_NAME, "XML result count: " + results.size());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = updateHandler.obtainMessage();
				msg.obj = sbiThread;
				updateHandler.sendMessage(msg);

				Log.d(LOG_NAME, "Should be seeing something now?");
			}
		};
		refresh.start();

	}

	public static AlertActivity getSelf() { 
		return self; 
	}
	
	private final Handler updateHandler = new Handler() {

		public void handleMessage(Message msg) {
			AlertActivity sbiThread = (AlertActivity)msg.obj;

			// render view
			final ListView lv = (ListView)sbiThread.findViewById(R.id.alert_list_rows);
			
			if ( sbiThread.results.size() > 0 ) {
			
				final String displayFields[] = new String[] {"station", "description"};
				final int displayViews[] = new int[] {R.id.st_name, R.id.station_short_name};

				lv.setAdapter( new SimpleAdapter(sbiThread, sbiThread.results,
						R.layout.data_row,
						displayFields, displayViews) );

				Log.d(LOG_NAME, "Got alerts.");
			} else {
				
				final String displayFields[] = new String[] {"description"};
				final int displayViews[] = new int[] {R.id.st_name};
	
				List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
				Map<String, String> data = new HashMap<String, String>();
				data.put("description", "No alerts at this time.");
				listData.add(data);
				
				lv.setAdapter( new SimpleAdapter(sbiThread, listData,
						R.layout.data_row,
						displayFields, displayViews) );
				
				Log.d(LOG_NAME, "No alerts.");
			}
			sbiThread.dialog.dismiss();
		}
	};
}
