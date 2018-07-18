package com.ja.sbi.beans;

public class Fare {

    private String fare;
    private String clipperDiscount;
    private String seniorDisabledClipper;
    private String youthClipper;

    public String getSeniorDisabledClipper() {
        return seniorDisabledClipper;
    }

    public void setSeniorDisabledClipper(String seniorDisabledClipper) {
        this.seniorDisabledClipper = seniorDisabledClipper;
    }

    public String getYouthClipper() {
        return youthClipper;
    }

    public void setYouthClipper(String youthClipper) {
        this.youthClipper = youthClipper;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getClipperDiscount() {
        return clipperDiscount;
    }

    public void setClipperDiscount(String clipperDiscount) {
        this.clipperDiscount = clipperDiscount;
    }

}
