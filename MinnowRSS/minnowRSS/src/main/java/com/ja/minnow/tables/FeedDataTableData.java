package com.ja.minnow.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeedDataTableData {

    public static final String FEED_DATA_TABLE = "feed_data";
    public static final String SUMMARY_COL = "summary";
    public static final String ITEM_IS_READ = "is_read";
    public static final String TITLE_COL = "title";
    public static final String IMAGE_COL = "image";
    public static final String FEED_ID_COL = "feed_id";
    public static final String URL_COL = "url";
    public static final String LASTUPDATEDATE_COL = "lastUpdateDate";

    public final Map<String, String> columns = new HashMap<String, String>();

    {
        columns.put(FeedDataTableData.SUMMARY_COL, "String");
        columns.put(FeedDataTableData.TITLE_COL, "String");
        columns.put(FeedDataTableData.ITEM_IS_READ, "Integer");
        columns.put(FeedDataTableData.IMAGE_COL, "Blob");
        columns.put(FeedDataTableData.FEED_ID_COL, "Integer");
        columns.put(FeedDataTableData.URL_COL, "String");
        columns.put(FeedDataTableData.LASTUPDATEDATE_COL, "String");
    }

    ;

    /* id column is only required on update */
    public final Set<String> requiredFields = new HashSet<String>();

    {
        requiredFields.add(FeedDataTableData.SUMMARY_COL);
        requiredFields.add(FeedDataTableData.ITEM_IS_READ);
        requiredFields.add(FeedDataTableData.TITLE_COL);
        requiredFields.add(FeedDataTableData.FEED_ID_COL);
        requiredFields.add(FeedDataTableData.URL_COL);
    }

    ;
}
