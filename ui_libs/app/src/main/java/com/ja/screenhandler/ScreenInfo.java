package com.ja.screenhandler;

public class ScreenInfo {

	private int viewName;   // screen view id
	private String viewType;  // string, int, url
	private String dbFieldName;
	/**
	 * @param viewName the viewName to set
	 */
	public void setViewName(int viewName) {
		this.viewName = viewName;
	}
	/**
	 * @return the viewName
	 */
	public int getViewName() {
		return viewName;
	}
	/**
	 * @param viewType the viewType to set
	 */
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
	/**
	 * @return the viewType
	 */
	public String getViewType() {
		return viewType;
	}
	/**
	 * @param dbFieldName the dbFieldName to set
	 */
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	/**
	 * @return the dbFieldName
	 */
	public String getDbFieldName() {
		return dbFieldName;
	}
}
