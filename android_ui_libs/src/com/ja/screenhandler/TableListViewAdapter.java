package com.ja.screenhandler;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ja.activity.BaseActivity;
import com.ja.database.Table;

public abstract class TableListViewAdapter extends ArrayAdapter<Table> {

	private List<Table> items;
	private BaseActivity activity;
	private int viewId;
	private LayoutInflater inflator;
	
	/**
	 * how to map the table to the view
	 * 
	 * @param table
	 * @param currentView
	 */
	protected abstract void mapTable(Table table, View currentView);
	
	public TableListViewAdapter(Context context, int resource, List<Table> objects) {
		super(context, resource, objects);
		this.activity = (BaseActivity)context;
		this.items = objects;
		this.viewId = resource;
	}
	
	public int getCount() { 
		return ( this.items != null ) ? this.items.size() : 0;
	}

	private LayoutInflater getInflator() { 
		if ( this.inflator == null ) { 
			this.inflator = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return this.inflator;
	}
	
	@Override
    public View getView(int position, View currentView, ViewGroup parent) {
		
		final View rView = ( currentView != null ) ? currentView : getInflator().inflate(this.viewId, null);    
		final Table table = items.get(position);
		if (table != null) {
			// callback method to map a row of data to a list view row
			mapTable(table, rView);
		}
		return rView;
    }
}
