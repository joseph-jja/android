package com.ja.minnow;

import java.util.concurrent.Semaphore;

import com.ja.minnow.services.FeedDataService;
import com.ja.minnow.services.FeedsService;
import com.ja.minnow.services.SettingsService;
import com.ja.minnow.services.rss.RSSParser;
import com.ja.minnow.ui.FeedDataListAdapter;
import com.ja.minnow.ui.FeedListAdapter;
import com.ja.net.ContentRetriever;

public final class Constants {

	// references to our application parts
	private static final ContentRetriever retriever = new ContentRetriever();
	private static final FeedsService feedsService = new FeedsService();	
	private static final FeedDataService feedDataService = new FeedDataService();
	private static final RSSParser parser = new RSSParser();
	private static final SettingsService settingsService = new SettingsService();
	private static final FeedListAdapter feedListAdapter = new FeedListAdapter();
	private static final FeedDataListAdapter feedDataListAdapter = new FeedDataListAdapter();
	
	private static final Semaphore refreshOneWaiter = new Semaphore(1, false);
	
	/**
	 * @return the retriever
	 */
	public static ContentRetriever getRetriever() {
		return retriever;
	}

	/**
	 * @return the feedsservice
	 */
	public static FeedsService getFeedsservice() {
		return feedsService;
	}

	/**
	 * @return the feeddataservice
	 */
	public static FeedDataService getFeeddataservice() {
		return feedDataService;
	}

	/**
	 * @return the parser
	 */
	public static RSSParser getParser() {
		return parser;
	}

	/**
	 * @return the settingsservice
	 */
	public static SettingsService getSettingsservice() {
		return settingsService;
	}

	/**
	 * @return the refreshOneWaiter
	 */
	public static Semaphore getRefreshOneWaiter() {
		return refreshOneWaiter;
	}

	/**
	 * @return the feedlistadapter
	 */
	public static FeedListAdapter getFeedlistadapter() {
		return feedListAdapter;
	}

	/**
	 * @return the feeddatalistadapter
	 */
	public static FeedDataListAdapter getFeeddatalistadapter() {
		return feedDataListAdapter;
	}

	
}
