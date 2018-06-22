package com.ja.minnow.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsTableData {

  public static final String SETTINGS_TABLE = "settings";
  public static final String NAME_COL = "name";
  public static final String VALUE_COL = "value";

  public final Map<String, String> columns = new HashMap<String, String>(); 
  {
	  columns.put(SettingsTableData.NAME_COL, "String");
	  columns.put(SettingsTableData.VALUE_COL, "String");
  };
  
  /* id column is only required on update */
  public final Set<String> requiredFields = new HashSet<String>();
  {
	  requiredFields.add(SettingsTableData.NAME_COL);
	  requiredFields.add(SettingsTableData.VALUE_COL);
  };  
}
