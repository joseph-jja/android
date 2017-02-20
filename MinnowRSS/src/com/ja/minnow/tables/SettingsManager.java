package com.ja.minnow.tables;

import com.ja.database.TableManager;

public class SettingsManager {

	private final TableManager manager = new TableManager(SettingsTableData.SETTINGS_TABLE);
	
	public SettingsManager () {
		SettingsTableData settings = new SettingsTableData();
		manager.setColumns(settings.columns);
		manager.setRequiredFields(settings.requiredFields);
	};

	/**
	 * @return the manager
	 */
	public TableManager getManager() {
		return manager;
	}
}
