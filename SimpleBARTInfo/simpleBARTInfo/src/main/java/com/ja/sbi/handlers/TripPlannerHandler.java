package com.ja.sbi.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;
import com.ja.sbi.bart.api.APIConstants;
import com.ja.sbi.bart.api.BaseDownloader;
import com.ja.sbi.bart.api.StationDownloader;
import com.ja.sbi.trains.beans.Trip;
import com.ja.sbi.trains.beans.TripLeg;
import com.ja.sbi.trains.beans.Fare;
import com.ja.sbi.trains.beans.Station;
import com.ja.sbi.xml.TripParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TripPlannerHandler {

  private final String LOG_NAME = this.getClass().getName();


}
