package com.ja.activity;

import android.app.Activity;

import com.ja.database.DatabaseAdapter;

/**
 * base activity class that contains the current view 
 * as well as the db adapter 
 * 
 * @author Joseph Acosta
 *
 */
public abstract class BaseActivity extends Activity {

	protected DatabaseAdapter dbAdapter = null;
	
	protected int currentContentView = -1;

	public int getCurrentContentView() {
		return this.currentContentView;
	}

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.currentContentView = layoutResID;
	}
	
	/**
	 * @param dbAdapter the dbAdapter to set
	 */
	public void setDbAdapter(DatabaseAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	/**
	 * @return the dbAdapter
	 */
	public DatabaseAdapter getDbAdapter() {
		return dbAdapter;
	}
	
	public abstract void initializeActivity();
}
