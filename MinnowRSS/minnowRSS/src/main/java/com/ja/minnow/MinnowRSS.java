package com.ja.minnow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ja.activity.ActivityHandler;
import com.ja.activity.BaseActivity;
import com.ja.database.DatabaseAdapter;
import com.ja.database.DatabaseAdapterException;
import com.ja.database.Table;
import com.ja.database.TableManager;
import com.ja.dialog.BaseDialog;
import com.ja.minnow.listeners.EditFeedListener;
import com.ja.minnow.listeners.FeedDataWebViewListener;
import com.ja.minnow.tables.FeedDataManager;
import com.ja.minnow.tables.FeedDataTableData;
import com.ja.minnow.tables.FeedsManager;
import com.ja.minnow.tables.FeedsTableData;
import com.ja.minnow.tables.SettingsManager;
import com.ja.minnow.thread.RefreshThread;

import java.util.ArrayList;
import java.util.List;

public class MinnowRSS extends BaseActivity {
	
	public static final String DATABASE_NAME = "MinnowRSS";
	public static final int DATABASE_VERSION = 1;
	
	private final Class<?> _self = getClass();
	private final String TAG = _self.getName();
	
	private List<Table> results;
	
	private FeedDataWebViewListener webView;
	public ProgressDialog progressDialog = null;
	private boolean dismissDialog = false;
	
	private Thread localThread = null;
	private RefreshThread refreshThread = null;
	
    public void initializeActivity() {
        
    	final List<TableManager> composite = new ArrayList<TableManager>(3);
        composite.add(new FeedsManager().getManager());
        composite.add(new FeedDataManager().getManager());
        composite.add(new SettingsManager().getManager());
        
        // create the database thing and it will do the rest
        Log.d(TAG, "Initializing Database Adapter.");   
        dbAdapter = new DatabaseAdapter(this, composite, MinnowRSS.DATABASE_NAME, MinnowRSS.DATABASE_VERSION);
        if ( dbAdapter == null ) {
        	throw new DatabaseAdapterException("Adapter is null, cannot continue!");
        }
        dbAdapter.open();
        
        Log.d(TAG, "Database Created.");
        
        Log.d(TAG, "Attaching event listener.");
        final EditFeedListener bev = new EditFeedListener((BaseActivity)this);
        bev.attachClickEventForView();
  
        this.webView = new FeedDataWebViewListener(this);
		
        Constants.getFeedsservice().setFeedsListPosition(0);
        
        Log.d(TAG, "Events attached.");
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Log.d(TAG, "Initializing!");
        super.onCreate(savedInstanceState);

        if ( super.getCurrentContentView() == -1 ) {
        	super.setContentView(R.layout.main);
        } else { 
        	setContentView( super.getCurrentContentView() );
        }
     
        initializeActivity();   
    }
    
    public void onDestroy() {
    	if ( this.dbAdapter.isDbIsOpen() ) {
    		this.dbAdapter.close();
    	}
    	super.onDestroy();
    }
        
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		Log.d(TAG, "In create context menu + " + v.toString());
		// only create this menu if the view is the feeds screen
		final MenuInflater inflater = getMenuInflater();
		if ( v.getId() == R.id.feeds_list_rows ) {
			Log.d(TAG, "Got correct view.");
		    inflater.inflate(R.menu.context, menu);
			super.onCreateContextMenu(menu, v, menuInfo);
		} else if ( v.getId() == R.id.feed_data_list_rows ) {
			Log.d(TAG, "Got correct view.");
		    inflater.inflate(R.menu.textviewer, menu);
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	/**
	 * this is the menu that is shown when a user long clicks
	 * on a list
	 */
	public boolean onContextItemSelected(MenuItem item) {

		final MinnowRSS atvy = this;
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 // index in the list is here
		//Log.d(TAG, "Item id, info id " + item.getItemId() + ", " + info.id + " " + info.position);
		View view = info.targetView;
		
		// let it blow up if this is wrong
		final LinearLayout layout = ((LinearLayout)view);
		final String vdbid = ( layout.findViewById(R.id.feeds_row_id) != null ) ? 
				((TextView)layout.findViewById(R.id.feeds_row_id)).getText().toString() : 
					( layout.findViewById(R.id.feed_data_list_row_id) != null ) ? 
							((TextView)layout.findViewById(R.id.feed_data_list_row_id)).getText().toString() : 
								null;	
		if ( vdbid == null ) { super.onContextItemSelected(item); } 
		
		final Integer dbid = Integer.parseInt(vdbid);
		if ( view instanceof LinearLayout ) {
			Log.d(TAG, "Got Feed ID is " + vdbid);
		}
		Log.d(TAG, "Feed ID is " + dbid);
		
		switch (item.getItemId()) {
	    	case R.id.context_delete:
	    		if ( localThread != null && localThread.isAlive() ) { 
					BaseDialog.alert(this, "Refresh in Progress", "Deleting feeds is not permitted, while refreshing database.");
	    			break; 
	    		}
	    		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    		final Table table = Constants.getFeedsservice().getFeed(super.getDbAdapter(), dbid);
	    		final String feedName = (String)table.getColumnValue(FeedsTableData.NAME_COL);
	    		//BaseDialog.okCancel((BaseActivity)this, "Delete?", "Are you sure you want to delete " + feedName + "?", "Delete")
	    		alertDialog.setTitle("Delete?");
	    		alertDialog.setMessage("Are you sure you want to delete " + feedName + "?");
	    		alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {   
	    				Log.d(TAG, "ID of item to be deleted " + dbid);
	    				// delete data associated with that feed
	    				Constants.getFeedsservice().deleteFeed(atvy, dbid);
	    				final List<Table> results = Constants.getFeedsservice().listFeeds(atvy);
	    				Constants.getFeedlistadapter().updateFeedList(atvy, results);
	    				return;
	    			}
	    		}); 
	    		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				return;
	    			}
	    		}); 		 
	    		alertDialog.show();
	    		break;
	    	case R.id.context_refresh:
	    		if ( localThread != null && localThread.isAlive() ) { 
	    			BaseDialog.alert(this, "Refesh in Progress", "Could not refresh feed because a full refresh is in progress.");
	    			break; 
	    		}
	    		final Table feedtable = Constants.getFeedsservice().getFeed(super.getDbAdapter(), dbid);
	    		final String updateFeedName = (String)feedtable.getColumnValue(FeedsTableData.NAME_COL);
	    		progressDialog = BaseDialog.spinnerDialog((BaseActivity)this, "Updating feed (" + updateFeedName + "), please wait!");
	    		final Thread refresh = new Thread() {  
	    			public void run() {
	    				updateHandler.setActivity(atvy);
	    				try {
	    					// deal with locking
	    					final boolean gotLock = Constants.getFeeddataservice().refreshFeedData(atvy, dbid);
	    					if ( ! gotLock ) {
	    						// we don't care here as we are blocking the UI
	    					}
	    				} catch (Exception e) {
	    					final Message msg = updateHandler.obtainMessage();
							msg.obj = e.getMessage();
							updateHandler.sendMessage(msg);
							e.printStackTrace();
	    				}		
	    				results = Constants.getFeedsservice().listFeeds((MinnowRSS)atvy);
	    				atvy.dismissDialog = true;
	    				updateHandler.sendMessage(updateHandler.obtainMessage());
	    			}
	    		};
	    		refresh.start();
				break;
	    	case R.id.context_edit:
	    		if ( localThread != null && localThread.isAlive() ) { 
					BaseDialog.alert(this, "Refresh in Progress", "Editing feeds is not permitted, while refreshing database.");
	    			break; 
	    		}
	    		this.setContentView(R.layout.add_feed);
	    		Constants.getFeeddataservice().setFeedID(dbid);
	    		Constants.getFeedsservice().editFeed(this, dbid);
				final EditFeedListener bev = new EditFeedListener(this);
				bev.attachClickEventForView();
				break;
	    	case R.id.textview_view:
	    		if ( this.getRefreshThread() != null && this.getLocalThread() != null & this.getLocalThread().isAlive() ) {
	    			this.getRefreshThread().setSleepThread(true);
	    		}
	    		// FIXME need to set correct list position
	    		Constants.getFeeddataservice().setListPosition(info.position);
	    		final Table feedItem = Constants.getFeeddataservice().getFeedData(super.getDbAdapter(), dbid);
	    		Constants.getFeeddataservice().setFeedDataID(dbid);
	    		this.webView.updateWebView(feedItem, true);
	    		if ( this.getRefreshThread() != null && this.getLocalThread() != null & this.getLocalThread().isAlive() ) {
	    			this.getRefreshThread().setSleepThread(false);
	    		}
	    		break;
			default: 
				break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * tracking the back key
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Log.d(TAG, "In the key press event method " + keyCode);
		if ( keyCode == 4 ) {
			// go back to feed list
			if ( this.getCurrentContentView() == R.layout.feed_web_view 
					|| this.getCurrentContentView() == R.layout.feed_text_view ) { 
				
				Constants.getFeeddatalistadapter().processData(this);
				Constants.getFeeddataservice().setFeedDataID(-1);
				return true;
			} else if ( this.getCurrentContentView() == R.layout.feed_data_list 
					|| this.getCurrentContentView() == R.layout.add_feed ) {
				this.setContentView(R.layout.feeds);
				final EditFeedListener bev = new EditFeedListener(this);
		        bev.attachClickEventForView();
		        final List<Table> results = Constants.getFeedsservice().listFeeds((MinnowRSS)this);
                Constants.getFeedlistadapter().updateFeedList((MinnowRSS)this, results);
                Constants.getFeeddataservice().setFeedID(-1);
				return true;
			} else { 
				this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	/**
	 * option menu from the menu button
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {
		
		final int dres = Math.abs(Constants.getSettingsservice().getUpdateDateTime(super.getDbAdapter()));
		if ( menu == null ) {
			return super.onMenuOpened(featureId, menu);
		}
	    final MenuItem item = menu.getItem(2);
	    SubMenu subMenu = item.getSubMenu();
	    final int slen = subMenu.size();
	    MenuItem mitem = null;
	    for ( int i = 0 ; i < slen; i+=1 ) {
	    	mitem = subMenu.getItem(i);
	    	final String title = mitem.getTitle().toString().replace(" ***", "");
	    	mitem.setTitle(title);
	    }
	    mitem = null;
	    switch ( dres ) { 
	    	case 6:
	    		mitem = subMenu.getItem(3);
    			break;
	    	case 12:
	    		mitem = subMenu.getItem(4);
    			break;
	    	case 24: 
	    		mitem = subMenu.getItem(5);
    			break;
    		default:
	    		mitem = subMenu.getItem(dres - 1);
    			break;
	    }
	    if ( mitem != null ) { 
	    	mitem.setTitle(mitem.getTitle() + " ***");
	    }
		
		return super.onMenuOpened(featureId, menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final MinnowRSS activity = this;
		
		Log.d("Selected menu ", " selected " + item.getItemId());
		
		switch (item.getItemId()) {
			case R.id.settings_add_feed:
				this.setContentView(R.layout.add_feed);
				final EditFeedListener bev = new EditFeedListener(this);
				bev.attachClickEventForView();
				break;
			case R.id.settings_refresh:
				if ( localThread != null && localThread.isAlive() ) { 
					BaseDialog.alert(this, "Refresh in Progress", "Could not start refresh as a refresh is already in progress.");
	    			break; 
	    		}
				refreshThread = new RefreshThread(this);
				localThread = new Thread(refreshThread);
	    		localThread.start();
				break;
			case R.id.setting_set_refresh_age_1:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 1);
				break;
			case R.id.setting_set_refresh_age_2:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 2);
				break;
			case R.id.setting_set_refresh_age_3:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 3);
				break;
			case R.id.setting_set_refresh_age_6:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 6);
				break;
			case R.id.setting_set_refresh_age_12:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 12);
				break;
			case R.id.setting_set_refresh_age_24:
				Constants.getSettingsservice().setUpdateDateTime(super.getDbAdapter(), 24);
				break;
			//case R.id.setting_export:
			/*	List<Table> feeds = FeedsEditor.getAllFeeds(this);
				final XMLExporter exporter = new XMLExporter();
				Document xmlout = exporter.getXMLDocument();
				if ( xmlout != null ) { 
					Element root = xmlout.createElement("minnow_feeds");
					xmlout.appendChild( root ); 
					for ( Table feed: feeds ) {
						final FeedsTable feedTable = new FeedsTable(feed);
						Element feedElement = xmlout.createElement("feed");
						feedElement.setAttribute("name", feedTable.getName());
						feedElement.setAttribute("url", feedTable.getUrl());
						root.appendChild(feedElement);
					}
					final String xmlDoc  = exporter.getDocumetAsString(xmlout);

					Log.e(TAG, xmlDoc );
				}*/
				// TODO write the data out
			//case R.id.setting_import:
				// TODO read data in
				
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		if ( this.refreshThread != null && this.localThread != null & this.localThread.isAlive() ) {
			this.refreshThread.setSleepThread(true);
		}
        super.onConfigurationChanged(newConfig);
        setContentView(this.currentContentView);
        Log.d(TAG, "Configuration changed and restarting app.");
        
		final EditFeedListener bev = new EditFeedListener(this);
        switch ( this.currentContentView ) {
        	case R.layout.add_feed:
        		final int feedID = Constants.getFeeddataservice().getFeedID();
        		if ( feedID != -1 && feedID > 0 ) 
          	   	{
        			Constants.getFeedsservice().editFeed(this, feedID);
          	   	}
                bev.attachClickEventForView();
                break;
        	case R.layout.feed_text_view:
        	case R.layout.feed_web_view:
        		final int wFeedID = Constants.getFeeddataservice().getFeedDataID();
        		final Table table = Constants.getFeeddataservice().getFeedData(this.dbAdapter, wFeedID);
        		Log.d(TAG, "Table = " + table.getColumnValue(FeedDataTableData.SUMMARY_COL));
        		this.webView.updateWebView(table, false);
        		break;
			case R.layout.feed_data_list:
				Constants.getFeeddatalistadapter().processData(this);
				break;
    		case R.layout.feeds:
    			final List<Table> results = Constants.getFeedsservice().listFeeds((MinnowRSS)this);
    			Constants.getFeedlistadapter().updateFeedList((MinnowRSS)this, results);
    			bev.attachClickEventForView();
        		break;
            case R.layout.main:
        	default:
        		initializeActivity();
        		break;
        }        
        if ( this.refreshThread != null && this.localThread != null & this.localThread.isAlive() ) {
			this.refreshThread.setSleepThread(false);
		}
	}
	
	/**
	 * @return the webView
	 */
	public FeedDataWebViewListener getWebView() {
		return webView;
	}

	/**
	 * @return the refreshThread
	 */
	public RefreshThread getRefreshThread() {
		return refreshThread;
	}

	/**
	 * @return the localThread
	 */
	public Thread getLocalThread() {
		return localThread;
	}

	private final ActivityHandler updateHandler = new ActivityHandler() {
		
		public void handleMessage(Message msg) {

			if ( msg.obj != null && msg.obj instanceof String ) { 
        		Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_LONG).show();
        	}
			try { 
				// find view by id throws exception if it cannot find the view
				// so this will just blow up if it cannot find the views
				View view = ((MinnowRSS)activity).getCurrentFocus();
				//view.findViewById(R.layout.feeds);
				Constants.getFeedlistadapter().updateFeedList((MinnowRSS)activity, results);
			//	Toast.makeText(this.activity, "Feed update completed.", Toast.LENGTH_LONG).show();
			} catch (Exception ex) { 
				Toast.makeText(activity, "Feed update completed with errors.", Toast.LENGTH_LONG).show();
				ex.printStackTrace();
			}
			if ( ((MinnowRSS)activity).dismissDialog ) { 
				progressDialog.dismiss();
			}
		}
	};
}
