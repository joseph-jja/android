package com.ja.database;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    /* database context */
    private final Context context;

    /* internal database helper class */
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /* things needed to operate */
    private String dbname;
    private int dbver;
    private List<TableManager> dbTables;

    private final Class<?> _self = getClass();
	private final String DB_TAG = _self.getName();
	
	private TableManager tableMgr;
	private boolean dbIsOpen = false;
	
    public DatabaseAdapter(Context ctx, List<TableManager> tables, String dbname, Integer dbVer) {
        this.context = ctx;

        this.dbname = dbname;
        this.dbTables = tables;
        this.dbver = dbVer;

        dbHelper = new DatabaseHelper(context, this.dbname, this.dbver);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String dbName, int dbVersion) {
            super(context, dbName, null,  dbVersion);
        }

        public void onCreate(SQLiteDatabase db) {
            for ( final TableManager table : dbTables ) {
               try { 	
            	   Log.v(DB_TAG, "Table SQL = " + table.createTable());
            	   db.execSQL( table.createTable() );
               } catch (Exception ex) {
            	   Log.v(DB_TAG, "Table already exists: " + table.getTableName() + ". " + ex.getMessage());
               }
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Log.v(DB_TAG, "Entering database upgrade verion from " + oldVersion + " to " + newVersion);
            for ( final TableManager table : dbTables ) {
            	final List<String> updateSQL = table.upgradeTable(oldVersion, newVersion);
            	for ( String sql : updateSQL ) {
                	if ( ! sql.equals("") ) {
            			db.execSQL( sql );
            		}
            	}
            }
            onCreate(db);
        }
    }

    private TableManager findTable(String name) {
        for ( final TableManager table : dbTables ) {
            if ( table.getTableName().equalsIgnoreCase(name) ) {
                return table;
            }
         }
        throw new TableNotFoundException("DBAdapeter failed to insert, table " + name + " not found!");
    }

    /* insert a record into a table */
    public long insert(String tableName, Map<String, Object> values)
                 throws DatabaseAdapterException, TableNotFoundException {

        tableMgr = findTable(tableName);

        Log.v(DB_TAG, "Table is " + tableName);
        return db.insert( tableName, null, tableMgr.insert(values) );
    }

    /* update a record in a table */
    public long update(String tableName, Map<String, Object> values)
                 throws DatabaseAdapterException, TableNotFoundException {

        tableMgr = findTable(tableName);

        final Object dbid = values.get(Table.ID_COL);
        final Integer id = ( dbid instanceof Integer ) ? (Integer)dbid : Integer.parseInt((String)dbid);
        // reset id in case it was a string
        values.put(Table.ID_COL, id);
        
        Log.v(DB_TAG, "Key is = " + id);
        final int rowcount = db.update( tableName, tableMgr.update(values), Table.ID_COL + " = " + id, null );
        if ( rowcount != 1 ) { 
        	throw new DatabaseAdapterException("Invalid result set count! Expected 1 got " + rowcount + ".");
        }
        return id;
    }

    /* delete a record from a table */
    public int deleteById(String tableName, int id) throws DatabaseAdapterException, TableNotFoundException {

        return db.delete(tableName, Table.ID_COL + " = " + id, null);
    }

    /* delete a record from a table */
    public int deleteWhere(String tableName, String where, String args[]) throws DatabaseAdapterException, TableNotFoundException {

        return db.delete(tableName, where, args);
    }

    /* find a record into a table */
    public Table findById(String tableName, int id) throws DatabaseAdapterException, TableNotFoundException {

        tableMgr = findTable(tableName);

        final Cursor resultset = db.query(tableName, null, "_id = " + id, null, null, null, null, null);

        // map the column back
        if ( resultset.getCount() != 1 ) {
        	throw new DatabaseAdapterException("Invalid result set count! Expected 1 got " + resultset.getCount() + ".");
        }

       // move to the first row, to make sure we are at the start
        resultset.moveToFirst();
        
        final Table table = tableMgr.mapTableRow(resultset);
        
        if ( ! resultset.isClosed() ) {
        	resultset.close();
        }
        
        // call the method to map the table row and return that row
        return table;
    }

    /* find a record into a table */
    public List<Table> find(String tableName, String columnName, Object value) throws DatabaseAdapterException {

        // we only do the lookup for the table so that we do not try to
        // delete from a table that does not exist
        tableMgr = findTable(tableName);

        final Cursor resultset = db.query(tableName, null, 
        		( ( columnName != null && value != null ) ? columnName + " = " + value : null ), 
        		null, null, null, null, null);

        // map the column back
        if ( resultset.getCount() < 1 ) {
            return null;
        }

        // move to the first row
        resultset.moveToFirst();

        // total number of rows
        final int rowcount = resultset.getCount();

        // loop through rows and get results
        final List<Table> results = new ArrayList<Table>(rowcount);       
        for ( int i = 0; i < rowcount; i+=1 ) {
            results.add( tableMgr.mapTableRow(resultset) );
            resultset.moveToNext();
        }
        
        if ( ! resultset.isClosed() ) {
        	resultset.close();
        }
        
        return results;
    }

    /* find a record into a table */
    public List<Table> findAll(String tableName) throws DatabaseAdapterException {
    	return find(tableName, null, null);
    }
    
    public List<Table> query(String sql) {

    	final Cursor resultset = db.rawQuery(sql, null);

        // map the column back
        if ( resultset.getCount() < 1 ) {
            return null;
        }
        Log.v(DB_TAG, "Got results of size = " + resultset.getCount());
        // move to the first row
        resultset.moveToFirst();

        // total number of rows
        final int rowcount = resultset.getCount();
        final String names[] = resultset.getColumnNames();
        Log.v(DB_TAG, "Result row = " + names[0]);

        // loop through rows and get results
        final List<Table> results = new ArrayList<Table>(rowcount);
        String name = null;
        Object value = null;
        for ( int i = 0; i < rowcount; i+=1 ) {
            // process columns
        	final Table table = new Table();
            int nl = names.length;
            for ( int j =0; j < nl; j+=1 ) {
                name = names[j];
                try { 
                	value = (Object)resultset.getString(j);
                } catch ( Exception exs ) {
                	// not a string
                	try { 
                    	value = (Object)resultset.getInt(j);
                    } catch ( Exception exi ) {
                    	// not an int
                    	try { 
                        	value = (Object)resultset.getBlob(j);
                        } catch ( Exception exb ) {
                        	// not an blog
                        	Log.e(DB_TAG, "Datatype for column named " + name + " is unknown!");
                        }
                    }
                }
                Log.v(DB_TAG, "names = " + name + " => value = " + value);
                table.setColumnValue(name, value);
            }
            results.add(table);
            resultset.moveToNext();
        }
        
        if ( ! resultset.isClosed() ) {
        	resultset.close();
        }

        return results;
    }

     //---opens the database---
    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        if ( db != null ) { 
        	this.dbIsOpen = true;
        }
    }

    //---closes the database---
    public void close()
    {
    	dbHelper.close();
    	this.dbIsOpen = false;
    }
	/**
	 * @return the isOpen
	 */
	public boolean isDbIsOpen() {
		return dbIsOpen;
	}
	
	public void beginTransaction() { 
		this.db.beginTransaction();
	}
	
	public void endTransaction() { 
		this.db.endTransaction();
	}
	
	public void setTransactionSuccessful() { 
		this.db.setTransactionSuccessful();
	}
}
