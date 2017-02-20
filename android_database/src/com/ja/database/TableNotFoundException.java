package com.ja.database;

@SuppressWarnings("serial")
public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String message) { 
        super(message);
    }

}
