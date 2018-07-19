package com.ja.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * this class is a template for all database table engines
 *
 * @author Joseph Acosta
 */
public class TableManager {

    protected ContentValues ctxValues;
    protected Table table;

    // columns key should be name and value is type
    private Map<String, String> columns = new HashMap<String, String>();
    ;
    // an entry in requiredFields determines if the field is required or not
    private Set<String> requiredFields = new HashSet<String>();
    ;
    // upgradeColumns contains the integer version to alter and the columns to
    // add
    private Map<Integer, Map<String, String>> updateColumns;

    private String tableName;

    private final Class<?> _self = getClass();
    private final String TABLE_TAG = _self.getName();

    /**
     * constructor so that implementation can do what they need to do takes the
     * table name so that it is there for delete
     */
    public TableManager(String tname) {
        this.ctxValues = new ContentValues();
        this.tableName = tname;
    }

    /**
     * needed for delete this is the table name
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * this is used by the delete method of crud no need to implement the delete
     * as it just returns the id this is then used with the tablename to delete
     * by id
     */
    public void setId(int id) {
        this.table.setId(id);
    }

    public int getId() {
        return this.table.getId();
    }

    // method needed to get the actual implementing table
    public Table getTable() {
        return this.table;
    }

    public String dropIfExists() {
        Log.v(TABLE_TAG, "Entering drop table, for table" + this.tableName);

        StringBuilder dropTable = new StringBuilder();
        dropTable.append("DROP TABLE IF EXISTS ");
        dropTable.append(this.tableName);

        return dropTable.toString();
    }
    /* delete method is not needed as there is only delete by id */
    /* findById method is not needed as it just needs the id */
    /* find method is not needed as it just needs the column and value */

    /* db creation operations */
    public String createTable() {
        Log.v(TABLE_TAG, "Entering create table, for table "
                + this.tableName + " with column count = " + columns.size());
        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TABLE ");
        createTable.append(this.tableName);
        createTable.append("(_id integer primary key autoincrement");
        if (columns.size() > 0) {
            createTable.append(", ");
        }
        Set<Map.Entry<String, String>> fields = columns.entrySet();
        final int count = fields.size();
        int index = 0;
        for (Map.Entry<String, String> column : fields) {
            createTable.append(" " + column.getKey() + " " + column.getValue().toLowerCase() + " ");
            if (requiredFields.contains(column.getKey())) {
                createTable.append(" not null ");
            }
            if (index < count - 1) {
                createTable.append(", ");
            }
            index += 1;
        }
        createTable.append(");");
        Log.v(TABLE_TAG, "Exiting create table: " + createTable.toString());
        return createTable.toString();
    }

    public List<String> upgradeTable(int oldVer, int newVer) {
        Log.v(TABLE_TAG, "Entering update table. Old version = " + oldVer + ". New Version = " + newVer);
        List<String> updates = new ArrayList<String>();

        Set<Map.Entry<Integer, Map<String, String>>> keys = updateColumns
                .entrySet();
        Map<String, String> newColumns = new HashMap<String, String>();
        for (Map.Entry<Integer, Map<String, String>> version : keys) {
            if (version.getKey().intValue() > oldVer
                    && version.getKey().intValue() <= newVer) {
                newColumns.putAll(version.getValue());
            }
        }
        Set<Map.Entry<String, String>> cols = newColumns.entrySet();
        for (Map.Entry<String, String> version : cols) {
            StringBuilder alterTables = new StringBuilder();
            alterTables.append("ALTER TABLE \"" + this.tableName + "\" ");
            alterTables.append("ADD COLUMN " + version.getKey() + " ");
            alterTables.append(version.getValue());
            if (requiredFields.contains(version.getKey())) {
                alterTables.append(" not null ");
            }
            alterTables.append(" ; ");
            updates.add(alterTables.toString());
        }
        Log.v(TABLE_TAG, "Exiting update table: " + updates.size());
        return updates;
    }

    private void addToContext(Map.Entry<String, Object> entry, String columnName, String columnType) {

        Log.v(TABLE_TAG, "Column name is (" + columnName + ") and type is (" + columnType + ")");
        final Object value = entry.getValue();
        if (value == null) {
            return;
        }
        if (columnType.equalsIgnoreCase(Table.INTEGER_TYPE)) {
            Log.v(TABLE_TAG, "Integer type data = " + Integer.valueOf(entry.getValue().toString()));
            ctxValues.put(columnName, Integer.valueOf(entry.getValue().toString()));
            Log.v(TABLE_TAG, "Is it in there? " + ctxValues.get(columnName));
        } else if (columnType.equalsIgnoreCase(Table.STRING_TYPE)) {
            ctxValues.put(columnName, entry.getValue().toString());
        } else if (columnType.equalsIgnoreCase(Table.BLOB_TYPE)) {
            ctxValues.put(columnName, (byte[]) entry.getValue());
        }
    }

    public ContentValues insert(Map<String, Object> values) {
        this.ctxValues = new ContentValues();

        // TODO add in required fields check?
        Set<Map.Entry<String, Object>> entries = values.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            final String columnName = entry.getKey();
            final String columnType = this.columns.get(columnName);
            if (!columnName.equalsIgnoreCase(Table.ID_COL)) {
                this.addToContext(entry, columnName, columnType);
            }
        }
        return this.ctxValues;
    }

    public ContentValues update(Map<String, Object> values) {
        this.ctxValues = new ContentValues();

        // TODO add in required fields check?
        Set<Map.Entry<String, Object>> entries = values.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            final String columnName = entry.getKey();
            final String columnType = (columnName.equalsIgnoreCase(Table.ID_COL)) ? Table.INTEGER_TYPE : this.columns.get(columnName);
            this.addToContext(entry, columnName, columnType);
        }
        return this.ctxValues;
    }

    /* used in the find method to map a table row */
    public Table mapTableRow(Cursor csr) {

        Table table = new Table();
        final int cnlen = csr.getColumnCount(); // column count
        for (int i = 0; i < cnlen; i += 1) {
            final String colName = csr.getColumnName(i);
            final String columnType = (colName.equalsIgnoreCase(Table.ID_COL)) ? Table.INTEGER_TYPE : this.columns.get(colName);
            Log.v(TABLE_TAG, "Column Type is: " + columnType);
            if (columnType != null) {
                if (columnType.equalsIgnoreCase(Table.INTEGER_TYPE)) {
                    table.setColumnValue(colName, (Object) Integer.valueOf(csr.getInt(i)));
                } else if (columnType.equalsIgnoreCase(Table.STRING_TYPE)) {
                    table.setColumnValue(colName, (Object) csr.getString(i));
                } else if (columnType.equalsIgnoreCase(Table.BLOB_TYPE)) {
                    table.setColumnValue(colName, (Object) csr.getBlob(i));
                }
            }
        }
        return table;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    /**
     * @return the columns
     */
    public Map<String, String> getColumns() {
        return columns;
    }

    /**
     * @param requiredFields the requiredFields to set
     */
    public void setRequiredFields(Set<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    /**
     * @return the requiredFields
     */
    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    /**
     * @param updateColumns the updateColumns to set
     */
    public void setUpdateColumns(Map<Integer, Map<String, String>> updateColumns) {
        this.updateColumns = updateColumns;
    }

    /**
     * @return the updateColumns
     */
    public Map<Integer, Map<String, String>> getUpdateColumns() {
        return updateColumns;
    }
}
