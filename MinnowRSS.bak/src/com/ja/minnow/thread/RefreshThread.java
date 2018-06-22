package com.ja.minnow.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ja.database.DatabaseAdapter;
import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;
import com.ja.minnow.tables.FeedsTableData;

/**
 * sleepy thread, this thread can be asked to sleep 
 * the advantage of this is that anther thread can ask this thread to sleep 
 * then that thread can do some work
 * this thread will sleep for THREAD_SLEEP_TIME
 * 
 * @author joea
 *
 */
public class RefreshThread implements Runnable {

	private final Class<?> _self = getClass();
	private final String RS_TAG = _self.getName();
	
	private Calendar currentDate;
	private MinnowRSS activity; 
	
	private int currentRefreshFeedID = -1;
	
	private boolean sleepThread = false;
	
	private static final int THREAD_SLEEP_TIME = 2000;	
	private static final int REFRESG_THREAD_ID = 1;
	private static final CharSequence contentTitle = "Refresh Completed!";
	private static final String tickerText = "Feeds refresh completed. ";

	private static final String notificationService = Context.NOTIFICATION_SERVICE;
	private NotificationManager notifManager = null;
	
	public RefreshThread(MinnowRSS activity) {
		this.activity = activity;
		this.notifManager = (NotificationManager)this.activity.getSystemService(notificationService);
	}
	
	public void run() {
		
		// own copy of the db adapter so that it can open db itself?
		final DatabaseAdapter db = this.activity.getDbAdapter();
		
		// default refresh is one hour
		currentDate = Calendar.getInstance();
		currentDate.add(Calendar.HOUR, Constants.getSettingsservice().getUpdateDateTime(activity.getDbAdapter()));
		final long outOfDate = currentDate.getTime().getTime();	
		Log.d(RS_TAG, "Out of date time is " + new Date(outOfDate));
		
		final List<Table> feeds = Constants.getFeedsservice().listFeeds(activity);
		Log.d(RS_TAG, "Feed count for refresh = " + feeds.size());
		final StringBuilder notUpdated = new StringBuilder();
		for (Table tbl : feeds) {
			int viewFeedID = Constants.getFeeddataservice().getFeedID();
			this.currentRefreshFeedID = tbl.getId();
			try {
				if ( this.currentRefreshFeedID != viewFeedID ) { 
					if ( isSleepThread() ) { Thread.sleep(THREAD_SLEEP_TIME); }
					final boolean update = Constants.getFeeddataservice().isFeedDataOutOfDate(db, this.currentRefreshFeedID, outOfDate);
					Log.d(RS_TAG, "Do we need to do an update? " + update);
					if ( isSleepThread() ) { Thread.sleep(THREAD_SLEEP_TIME); }
					if ( update ) { 
						final boolean gotLock = Constants.getFeeddataservice().refreshFeedData(activity, this.currentRefreshFeedID);
						if ( ! gotLock ) {
							final Message msg = notificationHandler.obtainMessage();
							msg.obj = "Refresh of " + tbl.getColumnValue(FeedsTableData.NAME_COL) + " failed.";
							notificationHandler.sendMessage(msg);
						}
						if ( isSleepThread() ) { Thread.sleep(THREAD_SLEEP_TIME); }
					} else {
						final Message msg = notificationHandler.obtainMessage();
						msg.obj = "Feed " + (String)tbl.getColumnValue(FeedsTableData.NAME_COL) + " was not refreshed because it was not out of date.";
						notificationHandler.sendMessage(msg);
					}
				} else {
					final Message msg = notificationHandler.obtainMessage();
					msg.obj = "Feed " + (String)tbl.getColumnValue(FeedsTableData.NAME_COL) + " was not refreshed as it was in use.";
					notificationHandler.sendMessage(msg);
				}
				if ( isSleepThread() ) { Thread.sleep(THREAD_SLEEP_TIME); }
			} catch (Exception e) {
				Log.e(RS_TAG, e.getMessage());
				e.printStackTrace();
				final Message msg = notificationHandler.obtainMessage();
				msg.obj = "Refresh service could not refresh " + (String)tbl.getColumnValue(FeedsTableData.NAME_COL) + ". " + e.getMessage();
				notificationHandler.sendMessage(msg);
			}
		}	
		if ( notUpdated.length() > 0 ) {
			notUpdated.append(" was/were not refreshed because it/they was/were not out of date.");
		}
		Message msg = notificationHandler.obtainMessage();
		msg.obj = notUpdated.toString(); 
		notificationHandler.sendMessage(msg);
	}
	
	public synchronized int getCurrentRefreshFeedID() { 
		return this.currentRefreshFeedID;
	}

	/**
	 * @param sleepThread the sleepThread to set
	 */
	public synchronized void setSleepThread(boolean sleepThread) {
		this.sleepThread = sleepThread;
	}

	/**
	 * @return the sleepThread
	 */
	public synchronized boolean isSleepThread() {
		return sleepThread;
	}
	
	private final Handler notificationHandler = new Handler() { 
		
		// TODO resize notification message window to fit all text?
		public void handleMessage(Message msg) {

			final String msgs = ( msg.obj != null ) ? tickerText + (String)msg.obj: tickerText;
			CharSequence cseq = msgs.subSequence(0, msgs.length());
			//Log.v("STUFF", "Got stuff " + msgs);
			
			final int icon = R.drawable.icon;
			final long when = System.currentTimeMillis();
			final Notification notification = new Notification(icon, cseq, when);
			
			final Intent notificationIntent = new Intent(activity, MinnowRSS.class);
			final PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, 0);

			notification.setLatestEventInfo(activity, contentTitle, cseq, contentIntent);
			
			notifManager.notify(RefreshThread.REFRESG_THREAD_ID, notification);
		}
	};
}
