package com.ja.minnow.tables;

import com.ja.database.TableManager;

public class FeedDataManager {

	private final TableManager manager = new TableManager(FeedDataTableData.FEED_DATA_TABLE);

	public FeedDataManager() {
		FeedDataTableData feedData = new FeedDataTableData();
		manager.setColumns(feedData.columns);
		manager.setRequiredFields(feedData.requiredFields);
	}

	/**
	 * @return the manager
	 */
	public TableManager getManager() {
		return manager;
	}
}
