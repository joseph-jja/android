package com.ja.minnow.tables;

import com.ja.database.TableManager;

public class FeedsManager {

	private final TableManager manager = new TableManager(FeedsTableData.FEEDS_TABLE);
	
	public FeedsManager() {
		FeedsTableData feeds = new FeedsTableData();
		manager.setColumns(feeds.columns);
		manager.setRequiredFields(feeds.requiredFields);
	};

	/**
	 * @return the manager
	 */
	public TableManager getManager() {
		return manager;
	}
}
