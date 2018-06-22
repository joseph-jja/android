package com.ja.sbi.table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ja.database.TableManager;

/**
 * simple manager to manage settings for the application like favorites
 * 
 * @author joea
 */
public class DataManager {

	public static final String SETTINGS_TABLE = "sbi_settings";
	public static final String NAME_COL = "name";
	public static final String VALUE_COL = "value";
	
	public static final String FAVORITES_SETTING = "favorites";	
	  
	private final TableManager manager = new TableManager(DataManager.SETTINGS_TABLE);
	
	public final Map<String, String> columns = new HashMap<String, String>(); 
	{
		columns.put(DataManager.NAME_COL, "String");
		columns.put(DataManager.VALUE_COL, "String");
	};

	public final Set<String> requiredFields = new HashSet<String>();
	{
		columns.put(DataManager.NAME_COL, "String");
		columns.put(DataManager.VALUE_COL, "String");
	};

	public DataManager() { 
		manager.setColumns(this.columns);
		manager.setRequiredFields(this.requiredFields);
	}
	
	/**
	 * @return the manager
	 */
	public TableManager getManager() {
		return manager;
	}
}
