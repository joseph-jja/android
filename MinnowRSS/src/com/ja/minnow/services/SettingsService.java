package com.ja.minnow.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ja.database.DatabaseAdapter;
import com.ja.database.Table;
import com.ja.minnow.tables.SettingsTableData;

public class SettingsService {

	// how often to update the feed - default one hour
	public static final String updateInterval = Integer.toString(60*60*100);

	public int getUpdateDateTime(DatabaseAdapter dbAdapter) {
		
		int hour = -1;
		if ( ! dbAdapter.isDbIsOpen() ) {
			dbAdapter.open();
		}
		dbAdapter.beginTransaction();
		try {		
			// do delete first just in case then we don't have to figure out if in update mode
			final List<Table> results = dbAdapter.find(SettingsTableData.SETTINGS_TABLE, SettingsTableData.NAME_COL, 
					" 'lastUpdateDate' ");
			if ( results != null && results.get(0).getColumnValue(SettingsTableData.VALUE_COL) != null ) {
				String value = (String)results.get(0).getColumnValue(SettingsTableData.VALUE_COL);
				hour = - Integer.parseInt(value);
			}
			dbAdapter.setTransactionSuccessful();
		} finally { 
			dbAdapter.endTransaction();
		}
		return hour;
	}
	
	public void setUpdateDateTime(DatabaseAdapter dbAdapter, int updateTime) {
		
		if ( ! dbAdapter.isDbIsOpen() ) {
			dbAdapter.open();
		}
		dbAdapter.beginTransaction();
		try {
			final Map<String, Object> settings = new HashMap<String, Object>(2);
			
			// do delete first just in case then we don't have to figure out if in update mode
			dbAdapter.deleteWhere(SettingsTableData.SETTINGS_TABLE,
					SettingsTableData.NAME_COL + " = 'lastUpdateDate' ", null);
			
			// insert
			settings.put(SettingsTableData.NAME_COL, "lastUpdateDate");
			settings.put(SettingsTableData.VALUE_COL, Integer.toString(updateTime));
			dbAdapter.insert(SettingsTableData.SETTINGS_TABLE, settings);
			
			dbAdapter.setTransactionSuccessful();
		} finally { 
			dbAdapter.endTransaction();
		}
	}
}
