package com.ja.sbi.bart.api;

public class APIConstants {

	// key 
	public static final String BART_API_KEY = "MW9S-E7SL-26DU-VV8V";
	public static final String KEY_STRING_API = "&key=" + BART_API_KEY;
	
	// get all the stations
	public static final String GET_STATION_LIST_API = "http://api.bart.gov/api/stn.aspx?cmd=stns" + KEY_STRING_API;
	
	// get the trains at a particular station
	public static final String GET_TRAIN_LIST_API = "http://api.bart.gov/api/etd.aspx?cmd=etd&orig=";
	
	// used to calculate fair
	public static final String FAIR_API = "http://api.bart.gov/api/sched.aspx?cmd=fare&orig=";
	public static final String FAIR_DEST = "&dest=";
	
	// alerts api
	public static final String ALERTS_API = "http://api.bart.gov/api/bsa.aspx?cmd=bsa" + KEY_STRING_API;

}
