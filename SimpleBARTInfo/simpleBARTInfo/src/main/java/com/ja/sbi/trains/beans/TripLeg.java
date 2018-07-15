

public class TripLeg {

    private String order;
    private String transferCode;
    private String origin;
    private String destination;
    private String routeLine;
    private String trainHeadStation;

    // times
    private String originTime;
    private String originDate;
    private String destinationTime;
    private String destinationDate;

    public String getTrainHeadStation() {
      return this.trainHeadStation;
    }

    public void setTrainHeadStation(String trainHeadStation) {
      this.trainHeadStation = trainHeadStation;
    }

    public void setRouteLine(String routeLine) {
      this.routeLine = routeLine;
    }

    public String getRouteLine() {
      return this.routeLine;
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

    public String getTransferCode() {
      return this.transferCode;
    }

    public void setTransferCode(String transferCode) {
      this.transferCode = transferCode;
    }

    public String getOrder() {
      return this.order;
    }

    public void setOrder(String order) {
      this.order = order;
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
