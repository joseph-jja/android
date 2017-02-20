package com.ja.generator;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class DBGeneratorTableManagerWriter extends DBGeneratorFileWriter {

    public DBGeneratorTableManagerWriter(DBGeneratorTable dbtable) {
        super(dbtable);

        super.setFilename(super.getManagerClassName());
    }

    public void write() throws IOException {

        writeLine("package " + table.getPkgName() + ";");
        writeLine("");
        writeLine("import android.database.Cursor;");
        writeLine("import android.content.ContentValues;");
        writeLine("");
        writeLine("import com.ja.database.DBTable;");
        writeLine("import com.ja.database.DBBaseTableManager;");
        writeLine("import com.ja.database.IDBBaseTableManager;");
        writeLine("");
        writeLine("import java.util.Map;");
        writeLine("import java.util.List;");
        writeLine("import java.util.ArrayList;");
        writeLine("");
        writeLine("public class " + super.getManagerClassName() + " extends DBBaseTableManager implements IDBBaseTableManager {");
        writeLine("");
        Map<String, DBGeneratorColumn> columns = super.table.getColumns();
        Set<Map.Entry<String, DBGeneratorColumn>> columnKeys = columns.entrySet();

        StringBuilder createTable = new StringBuilder("\"create table " + super.table.getTableName());
        createTable.append("(_id integer primary key autoincrement");
        // generate the properties of this table object
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
           String col = cols.getKey();
           DBGeneratorColumn column = cols.getValue();
           createTable.append(",");
           createTable.append(" " + column.getName() );
           if ( column.getType().equalsIgnoreCase("String") ) {
               createTable.append(" text ");
           } else if ( column.getType().equalsIgnoreCase("Integer") ) {
               createTable.append(" integer ");
           } else if ( column.getType().equalsIgnoreCase("Blob") ) {
        	    createTable.append(" blob ");
           }
           if ( ! column.isNullable() ) {
               createTable.append(" not null ");
           }
        }
        createTable.append(");\"");
        writeLine("");

        // factory constructor
        writeLine("  public " + super.getManagerClassName() + "() {");
        writeLine("    super(" + super.getTableClassName() + "." + table.getTableName().toUpperCase().trim() + "_TABLE);");
        writeLine("    super.table = new " + super.getTableClassName() + "();");
        writeLine("  }");
        writeLine("");

        // method to get the table
        writeLine("  public " + super.getTableClassName() + " getTable() {");
        writeLine("    return (" + super.getTableClassName() + ")super.table;");
        writeLine("  }");
        writeLine("");

         /* db operations */
        writeLine("  public String createTable() {");
        writeLine("    return " + createTable.toString() + ";");
        writeLine("  }");
        writeLine("");

        writeLine("  public List<String> upgradeTable(int oldVer, int newVer) {");
        String tableName = super.getTableClassName() + "." + super.table.getTableName().toUpperCase().trim() + "_TABLE";
        writeLine("      List<String> columnUpdates = new ArrayList<String>();");
        writeLine("      StringBuilder updateTable = new StringBuilder(\"\");");
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
        	DBGeneratorColumn column = cols.getValue();
        	// version 1 = create
        	if ( column.getVersion() > 1 ) {
        		writeLine("      updateTable = new StringBuilder(\"\");");
               	String columnName = super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL";
	            writeLine("      if (  " + column.getVersion() + " > oldVer  && " + column.getVersion() + " <= newVer ) {");
	        	writeLine("          updateTable.append(\" ALTER TABLE \\\"\" + " + tableName + " + \"\\\"\"); ");
	        	writeLine("          updateTable.append(\" ADD COLUMN \\\"\" + " + columnName + " + \"\\\"\"); ");
        	if ( column.getType().equalsIgnoreCase("String") ) {
        		writeLine("          updateTable.append(\" text  \");");
        	} else if ( column.getType().equalsIgnoreCase("Integer") ) {
        		writeLine("          updateTable.append(\" integer   \");");
        	} else if ( column.getType().equalsIgnoreCase("Blob") ) {
        		writeLine("          updateTable.append(\" blob  \");");
        	}
        	if ( ! column.isNullable() ) {
        		writeLine("          updateTable.append(\" not null  \");");
        	}
    		writeLine("          updateTable.append(\" ; \");");
    		writeLine("          columnUpdates.add(updateTable.toString());");
        	writeLine("      }");
        	}
        }
        writeLine("      return columnUpdates;");
        writeLine("  }");
        writeLine("");

        writeLine("  public ContentValues insert(Map<String, Object> values) {");
        columnKeys = columns.entrySet();
    	writeLine("      super.ctxValues = new ContentValues();");
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
            DBGeneratorColumn column = cols.getValue();
            String methodName = column.getName().substring(0,1).toUpperCase() + column.getName().substring(1);
            String castAs = "(String)";
            if ( column.getType().equalsIgnoreCase("Blob") ) {
            	castAs = "(byte[])";
            } 
            if ( ! column.isNullable() ) {
                writeLine("      super.ctxValues.put(" + super.getTableClassName() + "." + methodName.toUpperCase().trim()
                        + "_COL, " + castAs + "values.get(\"" + column.getName() + "\"));");
            } else {
                writeLine("      if ( values.get(" + super.getTableClassName() + "." + methodName.toUpperCase().trim() + "_COL) != null ) {");
                writeLine("          super.ctxValues.put(" + super.getTableClassName() + "." + methodName.toUpperCase().trim()
                        + "_COL, " + castAs + "values.get(" + super.getTableClassName() + "." + methodName.toUpperCase().trim() + "_COL));");
                writeLine("      }");
            }
        }
        writeLine("      return super.ctxValues;");
        writeLine("  }");
        writeLine("");

        writeLine("  public ContentValues update(Map<String, Object> values) {");
    	writeLine("      super.ctxValues = new ContentValues();");
        writeLine("      super.ctxValues.put(" + super.getTableClassName() + ".ID_COL, (Integer)values.get(" + super.getTableClassName() + ".ID_COL) );");
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
            DBGeneratorColumn column = cols.getValue();
            String methodName = column.getName().substring(0,1).toUpperCase() + column.getName().substring(1);
            String castAs = "(String)";
            if ( column.getType().equalsIgnoreCase("Blob") ) {
            	castAs = "(byte[])";
            }            
            if ( ! column.isNullable() ) {
                writeLine("      super.ctxValues.put(" + super.getTableClassName() + "." + methodName.toUpperCase().trim()
                        + "_COL, " + castAs + "values.get(\"" + column.getName() + "\"));");
            } else {
                writeLine("      if ( values.get(" + super.getTableClassName() + "." + methodName.toUpperCase().trim() + "_COL) != null ) {");
                writeLine("          super.ctxValues.put(" + super.getTableClassName() + "." + methodName.toUpperCase().trim()
                        + "_COL, " + castAs + "values.get(" + super.getTableClassName() + "." + methodName.toUpperCase().trim() + "_COL));");
                writeLine("      }");
            }
        }
        writeLine("      return super.ctxValues;");
        writeLine("  }");
        writeLine("");

        writeLine("  public DBTable mapTableRow(Cursor csr) {");
        writeLine("      super.table = new " + super.getTableClassName() + "();");
        writeLine("      final int cnlen = csr.getColumnCount();      // column count");
        writeLine("      for ( int i = 0; i < cnlen; i+=1 ) {");
        writeLine("           final String colName = csr.getColumnName(i);");
        writeLine("           if ( colName.equals(" + super.getTableClassName() + ".ID_COL) ) {");
        writeLine("               super.table.setId(csr.getInt(i));");
        writeLine("           }");
        columnKeys = columns.entrySet();
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
            DBGeneratorColumn column = cols.getValue();
            writeLine("           if ( colName.equals(" + super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL) ) {");
            String setMethod = column.getName().substring(0,1).toUpperCase() + column.getName().substring(1);
            if ( column.getType().equalsIgnoreCase("String") ) {
                writeLine("               ((" + super.getTableClassName() + ")super.table).set" + setMethod + "(csr.getString(i));");
            } else if ( column.getType().equalsIgnoreCase("Blob") ) {
            	writeLine("               ((" + super.getTableClassName() + ")super.table).set" + setMethod + "(csr.getBlob(i));");
            } else if ( column.getType().equalsIgnoreCase("Integer") ) {
                writeLine("               ((" + super.getTableClassName() + ")super.table).set" + setMethod + "(csr.getInt(i));");
            }
            writeLine("           }");
        }
        writeLine("      }");
        writeLine("      return super.table;");
        writeLine("  }");

        writeLine("");
        writeLine("}");
    }
}
