package com.ja.screenhandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ja.activity.BaseActivity;
import com.ja.database.DatabaseAdapter;
import com.ja.database.DatabaseAdapterException;
import com.ja.database.Table;
import com.ja.database.TableNotFoundException;

public class ScreenToDBMap {
	
	private List<ScreenInfo> fields = new ArrayList<ScreenInfo>();
	private String tableName;
	
	private final Class<?> _self = getClass();
    private final String STM_TAG = _self.getName();
    
	public ScreenToDBMap(String tblName) {
		this.fields.clear();
		this.tableName = tblName;
	}
	
	public void addScreenInfo(int viewName, String viewType, String dbFieldName) {
		final ScreenInfo si = new ScreenInfo();
		si.setViewName(viewName);
		si.setViewType(viewType);
		si.setDbFieldName(dbFieldName);
		this.fields.add(si);
	}
	
	public Map<String, Object> generateMap(BaseActivity act)  
		throws NumberFormatException, MalformedURLException
	{
		
		final Map<String, Object> insertFields = new HashMap<String, Object>();
		Log.d(STM_TAG, "Enter generateMap");
		for ( ScreenInfo field: fields ) {
			Log.d(STM_TAG, "Field is " + field.getDbFieldName());
			final View screenView = act.findViewById(field.getViewName());
			String data = "";
			if ( screenView instanceof EditText ) {
				data = ((EditText)screenView).getText().toString();			
			} else if ( screenView instanceof TextView ) {
				data = ((TextView)screenView).getText().toString();			
			}
			if ( data != null && data.trim().length() > 0 ) {
				// now we check the type of the field
				if ( field.getViewType().equalsIgnoreCase("string") ) {
					// pretty safe to do nothing
				} else if ( field.getViewType().equalsIgnoreCase("integer") ) {
					Integer.parseInt(data);
				} else if ( field.getViewType().equalsIgnoreCase("url") ) {
					final String urldata = ( ! data.startsWith("http://") ) ? "http://" + data : data;
					new URL(urldata);
				}
				final Object o = (Object)data;
				insertFields.put(field.getDbFieldName(), o);
			}
			Log.d(STM_TAG, "Data is " + data);
		}	
		return insertFields;
	}
	
    public long doDBUpdate(BaseActivity act, DatabaseAdapter db) 
    	throws NumberFormatException, MalformedURLException, 
    	DatabaseAdapterException, TableNotFoundException
    {
    	final Map<String, Object> insertFields = generateMap(act);
    	Log.v(STM_TAG, "Inserting or updating? " + insertFields.containsKey(Table.ID_COL));
    	long rc = -1;
    	if ( insertFields.containsKey(Table.ID_COL) ) {
			rc = db.update(this.tableName, insertFields);
		} else {
			rc = db.insert(this.tableName, insertFields);
		}
		return rc ;
	}
    
    public void mapTableToScreen(BaseActivity act, int dbid) { 
    	
    	final DatabaseAdapter db = act.getDbAdapter();
    	final Table table = db.findById(this.tableName, dbid);
		
		for ( ScreenInfo field: fields ) {
			final Object data = table.getColumnValue(field.getDbFieldName());
			String textToSet = "";
			if ( data != null ) {
				if ( field.getViewType().equalsIgnoreCase("string") && data instanceof String ) {
					textToSet = (String)data;
				} else if ( field.getViewType().equalsIgnoreCase("integer") && data instanceof Integer) {
					textToSet = ((Integer)data).toString();
				} else if ( field.getViewType().equalsIgnoreCase("url") ) {
					textToSet = (String)data;
				}
			}
			final View screenView = act.findViewById(field.getViewName());
			if ( screenView instanceof EditText ) {
				((EditText)screenView).setText(textToSet);			
			} else if ( screenView instanceof TextView ) {
				((TextView)screenView).setText(textToSet);			
			}
		}
    }	
}
