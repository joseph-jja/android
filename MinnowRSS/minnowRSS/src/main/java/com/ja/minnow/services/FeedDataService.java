package com.ja.minnow.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.ja.database.DatabaseAdapter;
import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.tables.FeedDataTableData;

public class FeedDataService {

    private List<Table> results = null;

    private final Class<?> _self = getClass();
    private final String FDBM_TAG = _self.getName();

    private int feedID = -1;
    private int feedDataID = -1;
    private String feedName;
    private String feedCount;
    private int listPosition;
    private static final List<Integer> feedItemIds = new ArrayList<Integer>();
    public static final int MAX_IMAGE_WIDTH = 25;
    public static final int MAX_IMAGE_HEIGHT = 25;

    public String downloadRSSFeedData(DatabaseAdapter db, int dbFeedId) throws IOException {
        if (!db.isDbIsOpen()) {
            db.open();
        }
        final Table feed = db.findById("feeds", dbFeedId);
        final String feedURL = (String) feed.getColumnValue("url");
        return Constants.getRetriever().downloadURL(feedURL, 0);
    }

    public void deleteAllFeedData(DatabaseAdapter adapter, int dbFeedId) {
        if (!adapter.isDbIsOpen()) {
            adapter.open();
        }
        adapter.beginTransaction();
        try {
            final int count = adapter.deleteWhere(FeedDataTableData.FEED_DATA_TABLE,
                    FeedDataTableData.FEED_ID_COL + " = " + dbFeedId, null);
            Log.d(FDBM_TAG, "Number of feeds deleted: " + count);
            adapter.setTransactionSuccessful();
        } finally {
            adapter.endTransaction();
        }
    }

    public Table getFeedData(DatabaseAdapter adapter, int dbFeedDataId) {
        if (!adapter.isDbIsOpen()) {
            adapter.open();
        }
        final Table table = adapter.findById(FeedDataTableData.FEED_DATA_TABLE, dbFeedDataId);
        return table;
    }

    public List<Table> getAllFeedData(DatabaseAdapter adapter, int dbFeedId) {
        if (!adapter.isDbIsOpen()) {
            adapter.open();
        }
        results = adapter.query("select fd.*, f.image image from feed_data fd, feeds f where f._id = fd.feed_id and feed_id = "
                + dbFeedId);
        return results;
    }

    public boolean isFeedDataOutOfDate(DatabaseAdapter db, int dbFeedId, long datePastDue) {

        // look for the settings last update date
        boolean feedDataOutOfDate = false;
        if (!db.isDbIsOpen()) {
            db.open();
        }
        db.beginTransaction();
        try {
            // performance improvement, we try to delete feeds out of date
            // so if count > 0 we need to update
            final int count = db.deleteWhere(FeedDataTableData.FEED_DATA_TABLE,
                    FeedDataTableData.FEED_ID_COL + " = " + dbFeedId + " and "
                            + datePastDue + ">" + FeedDataTableData.LASTUPDATEDATE_COL,
                    null);
            Log.d(FDBM_TAG, "Delete count = " + count + " " + new Date(datePastDue));
            if (count > 0) {
                feedDataOutOfDate = true;
            }
            // also if there are no feeds they need to update
            results = db.query("select count(*) count from "
                    + FeedDataTableData.FEED_DATA_TABLE + " where "
                    + FeedDataTableData.FEED_ID_COL + " = " + dbFeedId);
            if (results == null || results.size() == 0) {
                feedDataOutOfDate = true;
                Log.e(FDBM_TAG, "No results returned!");
            } else {
                final String ct = (String) results.get(0).getColumnValue("count");
                Log.d(FDBM_TAG, "Results in database = " + results.size() + " count = " + ct);
                if (Integer.parseInt(ct) <= 0) {
                    feedDataOutOfDate = true;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return feedDataOutOfDate;
    }

    public boolean refreshFeedData(MinnowRSS activity, int dbFeedID) {

        boolean hasLock = false;
        try {
            // try to get a lock
            if (Constants.getRefreshOneWaiter().tryAcquire(5, TimeUnit.MILLISECONDS)) {
                hasLock = true;
                Log.d(FDBM_TAG, "Queue length = " + Constants.getRefreshOneWaiter().getQueueLength());
                refreshSingleFeed(activity, dbFeedID);
                Constants.getRefreshOneWaiter().release();
                return true;
            }
            Log.d(FDBM_TAG, "Could not get a lock que has = " + Constants.getRefreshOneWaiter().getQueueLength());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(FDBM_TAG, "Exception occured " + e.getMessage());
        } finally {
            if (hasLock) {
                Constants.getRefreshOneWaiter().release();
            }
            Log.d(FDBM_TAG, "Releasing lock.");
        }
        return false;
    }

    private synchronized void refreshSingleFeed(MinnowRSS activity, int dbFeedID) {
        final DatabaseAdapter db = activity.getDbAdapter();
        try {
            Log.v(FDBM_TAG, "About to download the data.");
            final String urldata = this.downloadRSSFeedData(db, dbFeedID);
            Log.v(FDBM_TAG, urldata);

            // delete old feeds
            if (dbFeedID != Constants.getFeeddataservice().getFeedID()) {
                Log.d(FDBM_TAG, "About to delete old feed data.");
                this.deleteAllFeedData(db, dbFeedID);
                Log.d(FDBM_TAG, "Completed delete of old feed data.");
            }

            // we have the feed now, so parse it, stuff in List<Map<String, String>>
            Log.d(FDBM_TAG, "About to parse feed.");
            Constants.getParser().parseDocument(db, dbFeedID, urldata);
            Log.d(FDBM_TAG, "Completed parsing of feed.");

            Log.d(FDBM_TAG, "Get feed for update.");
            final Table feed = Constants.getFeedsservice().getFeed(db, dbFeedID);
            Log.d(FDBM_TAG, "Got feed.");

            //if ( feed.getColumnValue(FeedsTable.IMAGE_COL) == null ) {
            Constants.getFeedsservice().retrieveImageToTable(feed);
            //}

            Log.d(FDBM_TAG, "Update parent feed.");
            Constants.getFeedsservice().updateFeedFromFeedData(db, feed);
            Log.d(FDBM_TAG, "Completed update of feed.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FDBM_TAG, "Exception occured " + e.getMessage());
        }
    }

    /**
     * @param feedName the feedName to set
     */
    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    /**
     * @return the feedName
     */
    public String getFeedName() {
        return feedName;
    }

    /**
     * @param listPosition the listPosition to set
     */
    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    /**
     * @return the listPosition
     */
    public int getListPosition() {
        return listPosition;
    }

    /**
     * @return the feeditemids
     */
    public static List<Integer> getFeeditemids() {
        return feedItemIds;
    }

    /**
     * @param feedCount the feedCount to set
     */
    public void setFeedCount(String feedCount) {
        this.feedCount = feedCount;
    }

    /**
     * @return the feedCount
     */
    public String getFeedCount() {
        return feedCount;
    }

    /**
     * @param feedID the feedID to set
     */
    public void setFeedID(int feedID) {
        this.feedID = feedID;
    }

    /**
     * @return the feedID
     */
    public synchronized int getFeedID() {
        return feedID;
    }

    /**
     * @param feedDataID the feedDataID to set
     */
    public void setFeedDataID(int feedDataID) {
        this.feedDataID = feedDataID;
    }

    /**
     * @return the feedDataID
     */
    public int getFeedDataID() {
        return feedDataID;
    }
}
