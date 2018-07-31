package com.ja.minnow.tables;

import com.ja.database.Table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeedsTableData {

  public static final String FEEDS_TABLE = "feeds";
  public static final String FEEDCOUNT_COL = "feedCount";
  public static final String NAME_COL = "name";
  public static final String IMAGE_COL = "image";
  public static final String URL_COL = "url";
  public static final String LASTUPDATEDATE_COL = "lastUpdateDate";

  public final Map<String, String> columns = new HashMap<String, String>();
  {
	  columns.put(FeedsTableData.FEEDCOUNT_COL, "Integer");
	  columns.put(FeedsTableData.NAME_COL, "String");
	  columns.put(FeedsTableData.IMAGE_COL, "Blob");
	  columns.put(FeedsTableData.URL_COL, "String");
	  columns.put(FeedsTableData.LASTUPDATEDATE_COL, "String");
  };

  /* id column is only required on update */
  public final Set<String> requiredFields = new HashSet<String>();
  {
	  requiredFields.add(FeedsTableData.NAME_COL);
	  requiredFields.add(FeedsTableData.URL_COL);
  };
}
