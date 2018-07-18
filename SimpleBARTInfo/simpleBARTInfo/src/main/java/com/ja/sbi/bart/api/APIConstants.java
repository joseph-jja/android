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
	
	// route info 
	public static final String ROUTES_LIST = BART_API_HOST + "/api/route.aspx?cmd=routes" + KEY_STRING_API;
	public static final String ROUTE_INFO = BART_API_HOST + "/api/route.aspx?cmd=routeinfo&route=";
	
	// trip planning 
	public static final String SCHEDULE_DEPART = BART_API_HOST + "/api/sched.aspx?cmd=depart&b=0&a=3&orig=";
	public static final String SCHEDULE_ARRIVE = BART_API_HOST + "/api/sched.aspx?cmd=arrive&b=2&a=1&orig=";
	public static final String SCHEDULE_DEST = "&dest=";
	public static final String SCHEDULE_DATE = "&date=";  // mm/dd/yyyy
	public static final String SCHEDULE_TIME = "&time=";  //h:mm+am/pm
	
	// alerts api
	public static final String ALERTS_API = BART_API_HOST + "/api/bsa.aspx?cmd=bsa" + KEY_STRING_API;
}
