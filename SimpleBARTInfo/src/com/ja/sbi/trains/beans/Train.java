package com.ja.sbi.trains.beans;

public class Train {

	private String trainName;
	private String trainTime;
	private String length; 
	private String platform;
	
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	/**
	 * @param trainName the trainName to set
	 */
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	/**
	 * @return the trainName
	 */
	public String getTrainName() {
		return trainName;
	}
	/**
	 * @param trainTime the trainTime to set
	 */
	public void setTrainTime(String trainTime) {
		this.trainTime = trainTime;
	}
	/**
	 * @return the trainTime
	 */
	public String getTrainTime() {
		return trainTime;
	}
}
