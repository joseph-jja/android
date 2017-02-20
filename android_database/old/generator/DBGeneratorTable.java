package com.ja.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * class that represents a DBGenerator Table object
 *
 * @author Joseph Acosta
 */
public class DBGeneratorTable {

    private String tableName;
    private String pkgName;
    private Map<String, DBGeneratorColumn> columns;

    public void addColumn(DBGeneratorColumn column) {
        if ( columns == null ) {
           columns = new HashMap<String, DBGeneratorColumn>();
        }
        columns.put(column.getName(), column);
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public DBGeneratorColumn getColumn(String name) {
        return this.columns.get(name);
    }

    public Map<String, DBGeneratorColumn> getColumns() {
        return this.columns;
    }
}
