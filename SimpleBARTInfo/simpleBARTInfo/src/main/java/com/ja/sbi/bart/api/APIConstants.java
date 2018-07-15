package com.ja.sbi.bart.api;

public class APIConstants {

	// bart api host
	public static final String BART_API_HOST = "http://api.bart.gov"; 
	
	// key 
	public static final String BART_API_KEY = "MW9S-E7SL-26DU-VV8V";
	public static final String KEY_STRING_API = "&key=" + BART_API_KEY;
	
	// get all the stations
	public static final String GET_STATION_LIST_API = BART_API_HOST + "/api/stn.aspx?cmd=stns" + KEY_STRING_API;
	
	// get the trains at a particular station
	public static final String GET_TRAIN_LIST_API = BART_API_HOST + "/api/etd.aspx?cmd=etd&orig=";
	
	// used to calculate fair
	public static final String FAIR_API = BART_API_HOST + "/api/sched.aspx?cmd=fare&orig=";
	public static final String FAIR_DEST = "&dest=";
	
	// trip planning 
	public static final String SCHEDULE_DEPART= BART_API_HOST + "/api/sched.aspx?cmd=depart&orig=";
	public static final String SCHEDULE_ARRIVE= BART_API_HOST + "/api/sched.aspx?cmd=arrive&orig=";
	public static final String SCHEDULE_DEST = "&dest=";
	public static final String SCHEDULE_DATE = "&date=";
	
	// alerts api
	public static final String ALERTS_API = BART_API_HOST + "/api/bsa.aspx?cmd=bsa" + KEY_STRING_API;
}
