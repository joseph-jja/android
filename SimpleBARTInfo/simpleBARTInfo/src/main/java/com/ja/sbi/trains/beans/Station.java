package com.ja.sbi.trains.beans;

import java.util.List;


public class Station {

    private String shortName;
    private String stationName;
    private String stationTime;
    private List<Train> trains;

    /**
     * @param stationName the stationName to set
     */
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    /**
     * @return the stationName
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * @param stationTime the stationTime to set
     */
    public void setStationTime(String stationTime) {
        this.stationTime = stationTime;
    }

    /**
     * @return the stationTime
     */
    public String getStationTime() {
        return stationTime;
    }

    /**
     * @param trains the trains to set
     */
    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    /**
     * @return the trains
     */
    public List<Train> getTrains() {
        return trains;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }
}
