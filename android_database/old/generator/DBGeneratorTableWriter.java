package com.ja.generator;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class DBGeneratorTableWriter extends DBGeneratorFileWriter {

    public DBGeneratorTableWriter(DBGeneratorTable dbtable) {
        super(dbtable);

        setFilename(super.getTableClassName());
    }

    public void write() throws IOException {

        writeLine("package " + table.getPkgName() + ";");
        writeLine("");
        writeLine("import com.ja.database.DBTable;");
        writeLine("");
        writeLine("public class " + super.getTableClassName() + " extends DBTable {");
        writeLine("");
        Map<String, DBGeneratorColumn> columns = super.table.getColumns();
        Set<Map.Entry<String, DBGeneratorColumn>> columnKeys = columns.entrySet();

        writeLine("  public static final String " + super.table.getTableName().toUpperCase().trim() + "_TABLE = \"" + super.table.getTableName().trim() + "\";");

        // generate the properties of this table object
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
            String col = cols.getKey();
            DBGeneratorColumn column = cols.getValue();
            writeLine("  public static final String " + column.getName().toUpperCase().trim() + "_COL = \"" + column.getName().trim() + "\";");
        }
        writeLine("");

        writeLine("  public " + super.getTableClassName() + "() { super.setTableName(" + super.getTableClassName() + "." + super.table.getTableName().toUpperCase().trim() + "_TABLE" + "); }");
        writeLine("");

        writeLine("  public " + super.getTableClassName() + "(DBTable table) { super.setTableName(" + super.getTableClassName() + "." + super.table.getTableName().toUpperCase().trim() + "_TABLE" + "); this.columnValues = table.getInternalData(); }");
        writeLine("");

        // generate the setters and getters of this table object
        columnKeys = columns.entrySet();
        for ( Map.Entry<String, DBGeneratorColumn> cols: columnKeys ) {
            DBGeneratorColumn column = cols.getValue();
            if ( column.getType().equalsIgnoreCase("Blob") ) {
                writeLine("  public void set" + column.getName().substring(0,1).toUpperCase() + column.getName().substring(1) + "(byte " + column.getName() + "[]) {");
                writeLine("    super.columnValues.put(" + super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL, (Object)" + column.getName() + ");");
                writeLine("  }");
                writeLine("");
                writeLine("  public byte[] get" + column.getName().substring(0,1).toUpperCase() + column.getName().substring(1) + "() {");
                writeLine("    return (byte[])super.columnValues.get(" + super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL);");
                writeLine("  }");
                writeLine("");
            } else {
                writeLine("  public void set" + column.getName().substring(0,1).toUpperCase() + column.getName().substring(1) + "(" + column.getType() + " " + column.getName() + ") {");
                writeLine("    super.columnValues.put(" + super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL, (Object)" + column.getName() + ");");
                writeLine("  }");
                writeLine("");
                writeLine("  public " + column.getType() + " get" + column.getName().substring(0,1).toUpperCase() + column.getName().substring(1) + "() {");
                writeLine("    return (" + column.getType() + ")super.columnValues.get(" + super.getTableClassName() + "." + column.getName().toUpperCase().trim() + "_COL);");
                writeLine("  }");
                writeLine("");
            }
        }
        writeLine("}");
    }
}
