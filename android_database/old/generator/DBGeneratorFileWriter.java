package com.ja.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * simple class to write a file out
 * the idea is that a subclass can determine
 * what the data is that they want to write out
 * and override teh write method
 *
 * @author Joseph Acosta
 */
public abstract class DBGeneratorFileWriter {

    private BufferedWriter dbwriter = null;
    protected DBGeneratorTable table;
    private String tableClassName;
    private String factoryClassName;
    private String filename;

    public DBGeneratorFileWriter(DBGeneratorTable dbtable) {
        this.table = dbtable;
    }

    private String cammelCase(String in) {
        String parts[] = in.split("_");
        StringBuilder result = new StringBuilder();
        if ( parts != null && parts.length > 0 ) {
            for ( int i = 0; i < parts.length; i+= 1) {
               result.append(parts[i].substring(0,1).toUpperCase() + parts[i].substring(1));
            }
            return result.toString();
        }
        return null;
    }

    protected String getTableClassName() {
        String tname = this.table.getTableName();
        this.tableClassName = cammelCase(tname) + "Table";
        return this.tableClassName;
    }

    protected String getManagerClassName() {
        String tname = this.table.getTableName();
        this.factoryClassName = cammelCase(tname) + "Manager";
        return this.factoryClassName;
    }

    protected void setFilename(String fname) {
        this.filename = fname + ".java";
    }

    private void open() throws IOException {

        // output location of file needs to be obtained from package?

        dbwriter = new BufferedWriter(new FileWriter(new File(this.filename)));
    }

    protected void writeLine(String data) throws IOException {
        dbwriter.write(data);
        dbwriter.newLine();
    }

    public void generate() throws IOException {

        this.open();
        this.write();
        this.close();
    }

    protected abstract void write() throws IOException;

    private void close() throws IOException {

        dbwriter.close();
    }

}
