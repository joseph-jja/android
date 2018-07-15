package com.ja.sbi.trains.beans;

import java.util.List;

public class Trip {

    private String originTime;
    private String originDate;
    private String destinationTime;
    private String destinationDate;

    private String origin;
    private String destination;
    
    private List<TripLeg> legs;
    
    private Fare fareDetails;

    public Fare getFareDetails() {
      return this.fareDetails;
    }

    public void setFareDetails(Fare fareDetails) {
      this.fareDetails = fareDetails;
    }

    public List<TripLeg> getLegs() {
      return this.legs;
    }

    public void setLegs(List<TripLeg> legs) {
      this.legs = legs;
    }

    public String getDestination() {
      return this.destination;
    }

    public void setDestination(String destination) {
      this.destination = destination;
    }

    public String getOrigin() {
      return this.origin;
    }

    public void setOrigin(String origin) {
      this.origin = origin;
    }

    public String getDestinationDate() {
      return this.destinationDate;
    }

    public void setDestinationDate(String destinationDate) {
      this.destinationDate = destinationDate;
    } 

    public String getDestinationTime() {
      return this.destinationTime;
    }

    public void setDestinationTime(String destinationTime) {
      this.destinationTime = destinationTime;
    } 

    public String getOriginDate() {
      return this.originDate;
    }

    public void setOriginDate(String originDate) {
      this.originDate = originDate;
    } 

    public String getOriginTime() {
      return this.originTime;
    }

    public void setOriginTime(String originTime) {
      this.originTime = originTime;
    } 
}
