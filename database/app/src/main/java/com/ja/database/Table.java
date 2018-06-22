package com.ja.database;

import java.util.HashMap;
import java.util.Map;

/**
 * this is a basic table object and can be used everywhere a table is needed
 *
 * @author Joseph Acosta
 */
public class Table {

    protected Map<String, Object> columnValues;
    protected String tableName;
    public static final String ID_COL = "_id";
    
    public static final String INTEGER_TYPE = "Integer";
    public static final String STRING_TYPE = "String";
    public static final String BLOB_TYPE = "Blob";

    public Table() {
    	this.columnValues = new HashMap<String, Object>();
    }
    
    public Map<String, Object> getInternalData() { 
    	return new HashMap<String, Object>(this.columnValues);
    }
    
    public Integer getId() { 
    	Object cid = this.columnValues.get(ID_COL);
    	if ( cid != null ) {
    		return Integer.parseInt(cid.toString());
    	}
    	return null; 
    }
    
    public void setId(int id) { 
    	this.columnValues.put(ID_COL, id); 
    }

    /**
     *
     * @param columnName
     * @param value
     */
    public void setColumnValue(String columnName, Object value) {
        if ( value != null ) {
            columnValues.put(columnName, value);
        }
    }

    /**
     *
     * @param columName
     * @return
     */
    public Object getColumnValue(String columName) {
        if ( columnValues.containsKey(columName) ) {
            return columnValues.get(columName);
        }
        return null;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     *
     * @param name
     */
    public void setTableName(String name) {
        this.tableName = name;
    }
}
