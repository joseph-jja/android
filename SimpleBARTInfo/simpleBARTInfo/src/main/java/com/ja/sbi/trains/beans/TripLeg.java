

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
}
