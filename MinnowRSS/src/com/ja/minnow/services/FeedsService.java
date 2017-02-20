package com.ja.minnow.services;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.EditText;

import com.ja.database.DatabaseAdapter;
import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;
import com.ja.minnow.tables.FeedDataTableData;
import com.ja.minnow.tables.FeedsTableData;
import com.ja.screenhandler.ScreenToDBMap;

/**
 * class for dealing with the RSS feed info itself
 * this does not include the downloaded feed data
 * just the actual feed itself
 * 
 * @author Joseph Acosta
 *
 */
public class FeedsService {

	private final Class<?> _self = getClass();
	private final String FE_TAG = _self.getName();
	
	public static final int MAX_IMAGE_WIDTH = 25;
	public static final int MAX_IMAGE_HEIGHT = 25;	
	
	private int feedsListPosition;

	private static final String GET_FEED_COUNT_SQL = "select " + FeedDataTableData.FEED_ID_COL 
				+ ", case when count (" + FeedDataTableData.FEED_ID_COL + ") is not null "
				+ " then count (" + FeedDataTableData.FEED_ID_COL + ") "
				+ " else  0 end count, "
				+ " case when min (" + FeedDataTableData.LASTUPDATEDATE_COL + ") is not null " 
				+ " then min (" + FeedDataTableData.LASTUPDATEDATE_COL + ") " 
				+ " else 1276372738939 end " + FeedDataTableData.LASTUPDATEDATE_COL
				+ " from " + FeedDataTableData.FEED_DATA_TABLE
				+ " where " + FeedDataTableData.FEED_ID_COL + " = ";

	private static final String GET_FEED_COUNT_GROUP_BY = " group by " + FeedDataTableData.FEED_ID_COL ;
	
	private static final ScreenToDBMap feeds = new ScreenToDBMap(FeedsTableData.FEEDS_TABLE);
	{
		feeds.addScreenInfo(R.id.add_id, "integer", Table.ID_COL);
		feeds.addScreenInfo(R.id.add_name, "string", FeedsTableData.NAME_COL);
		feeds.addScreenInfo(R.id.add_url, "url", FeedsTableData.URL_COL);
	}

	/**
	 * read one feed for editing
	 * 
	 * @param activity
	 * @param dbid
	 */
	public void editFeed(MinnowRSS activity, int dbid) {
		if ( ! activity.getDbAdapter().isDbIsOpen() ) {
			activity.getDbAdapter().open();
		}
		feeds.mapTableToScreen(activity, dbid);
	}

	/** 
	 * create and update a feed 
	 * 
	 * @param activity
	 * @return
	 */
	public boolean saveFeed(MinnowRSS activity) {
		
		boolean success = false;
		if ( ! activity.getDbAdapter().isDbIsOpen() ) {
			activity.getDbAdapter().open();
		}
		activity.getDbAdapter().beginTransaction();
		try {
			// get URL
			final EditText urlText = (EditText)activity.findViewById(R.id.add_url);
			final String rssFeed = Constants.getRetriever().downloadURL(urlText.getText().toString());

			// verify it's xml
			if ( ! Constants.getParser().isValidRSS(rssFeed) ) { return false; }
			final long feedId = feeds.doDBUpdate( activity, activity.getDbAdapter());
			try {
     	   		Constants.getFeeddataservice().deleteAllFeedData(activity.getDbAdapter(), Long.valueOf(feedId).intValue());
     	   	} catch(Exception ex) {
     	   		Log.e(FE_TAG, "Exception: " + ex.getMessage());
     	   		ex.printStackTrace();
     	   	}
     	   	try { 
				Constants.getParser().parseDocument(activity.getDbAdapter(), Long.valueOf(feedId).intValue(), rssFeed);
				Log.e(FE_TAG , "Image URL for " + feedId + " is " + Constants.getParser().getImageURL());

				final Table feed = Constants.getFeedsservice().getFeed(activity.getDbAdapter(), Long.valueOf(feedId).intValue());
				Log.d(FE_TAG, "Got the feed from the database: " + feed.getId());
				
				retrieveImageToTable(feed);
				
				Constants.getFeedsservice().updateFeedFromFeedData(activity.getDbAdapter(), feed);
				Constants.getFeeddataservice().setFeedID(-1);
				success = true;
				activity.getDbAdapter().setTransactionSuccessful();
			} catch (Exception ex) { 
				ex.printStackTrace();
				Log.e(FE_TAG, ex.getMessage());
			}
		} catch (Exception e ) {
			e.printStackTrace();
			Log.e(FE_TAG, e.getMessage());
		} finally {
			activity.getDbAdapter().endTransaction();
		}
		return success;
	}
	
	public Table getFeed(DatabaseAdapter db, int feedID) { 
		
		if ( ! db.isDbIsOpen() ) {
			db.open();
		}
		final Table results = db.findById(FeedsTableData.FEEDS_TABLE, feedID);
		return results;
	}
	
	public List<Table> listFeeds(MinnowRSS activity) {
		
		final DatabaseAdapter db = activity.getDbAdapter();
		
		if ( ! db.isDbIsOpen() ) {
			db.open();
		}
		final List<Table> results = db.findAll(FeedsTableData.FEEDS_TABLE);
		if ( results != null && results.get(0) != null ) {
			Log.v("FeedsEditor", "Got " + results.get(0).getColumnValue(FeedsTableData.FEEDCOUNT_COL));
		}
		return results;
	}
	
	/** 
	 * NOTE: this deletes all feed data too 
	 * 
	 * @param activity
	 * @param dbid
	 */
	public void deleteFeed(MinnowRSS activity, int dbid) {
		
		final DatabaseAdapter db = activity.getDbAdapter();
		if ( ! db.isDbIsOpen() ) {
			db.open();
		}
		db.beginTransaction();
		try { 
			final int count = db.deleteWhere(FeedDataTableData.FEED_DATA_TABLE, FeedDataTableData.FEED_ID_COL + " = " + dbid, null);
			Log.v("FeedsEditor", "Deleted " + count);
			db.deleteById(FeedsTableData.FEEDS_TABLE, dbid);
			db.setTransactionSuccessful();
		} finally { 
			db.endTransaction();
		}
	}

	/**
	 * updates the feed table with the last update date and count
	 * 
	 * @param db
	 * @param feedContent
	 */
	public void updateFeedFromFeedData(DatabaseAdapter db, Table feedContent) { 
		
		if ( ! db.isDbIsOpen() ) {
			db.open();
		}
		db.beginTransaction();
		try { 
			final List<Table> tables = db.query(GET_FEED_COUNT_SQL + feedContent.getId() + GET_FEED_COUNT_GROUP_BY);
			if ( tables != null && tables.size() > 0 ) { 
				Log.v("FeedsEditor", " table count = " + tables.size());
				final Map<String, Object> updateData = feedContent.getInternalData();
				final Table match = tables.get(0);
				updateData.put(FeedsTableData.LASTUPDATEDATE_COL, match.getColumnValue(FeedDataTableData.LASTUPDATEDATE_COL));
				updateData.put(FeedsTableData.FEEDCOUNT_COL, match.getColumnValue("count"));
				long results = db.update(FeedsTableData.FEEDS_TABLE, updateData);
				Log.v("FeedsEditor", "Result from update = " + results);
			}
			db.setTransactionSuccessful();
		} finally { 
			db.endTransaction();
		}
	}
	
	public final void retrieveImageToTable(Table table) {
		
		final String imageURL = Constants.getParser().getImageURL();
		table.setColumnValue(FeedsTableData.IMAGE_COL, null);	
		if ( imageURL != null && ! imageURL.trim().equals("") ) {
			Log.d(FE_TAG, "Got image url.");
			try { 
				Bitmap imageData = Constants.getRetriever().downloadImage(imageURL);
				Log.d(FE_TAG, "Got image = " + imageData);
				if ( imageData != null ) { 
					// make sure image is 20x20
					final int width = imageData.getWidth();
					final int height = imageData.getHeight();
					final int newWidth = MAX_IMAGE_WIDTH;
					final int newHeight = MAX_IMAGE_HEIGHT;
					final float scaleWidth = ((float) newWidth) /  width;
					final float scaleHeight = ((float) newHeight) /  height;
					final Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					final Bitmap resizedBitmap = Bitmap.createBitmap(imageData, 0, 0, width, height, matrix, true);
					
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					resizedBitmap.compress(CompressFormat.PNG, 100, baos);
					final byte imageBA[] = baos.toByteArray();
					table.setColumnValue(FeedsTableData.IMAGE_COL, imageBA);
				}				
				Log.d(FE_TAG, "Downloaded image " + imageData);
			} catch (Exception ex) { 
				ex.printStackTrace();
				Log.d(FE_TAG, "Image download failed: " + ex.getMessage());
			}
		}
	}

	/**
	 * @param feedsListPosition the feedsListPosition to set
	 */
	public void setFeedsListPosition(int feedsListPosition) {
		this.feedsListPosition = feedsListPosition;
	}

	/**
	 * @return the feedsListPosition
	 */
	public int getFeedsListPosition() {
		return feedsListPosition;
	}
}
