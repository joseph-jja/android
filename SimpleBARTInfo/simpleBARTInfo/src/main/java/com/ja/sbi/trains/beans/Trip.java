
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
