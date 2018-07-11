package com.ja.sbi.trains.beans;

import java.util.Comparator;

public class Train implements Comparator<Train>, Comparable<Train> {

    private String trainName;
    private String trainTime;
    private String length;
    private String platform;
    private String direction;

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

    /**
     * @param direction the direction to set
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * @return the direction
     */
    public String getDirection() {
        return direction;
    }

    public int compareTo(Train a) {
        return this.direction.compareTo(a.getDirection());
    }

    public int compare(Train a, Train b) {
        return a.getDirection().compareTo(b.getDirection());
    }
}
