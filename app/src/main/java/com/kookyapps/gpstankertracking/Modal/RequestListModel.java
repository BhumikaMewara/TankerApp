package com.kookyapps.gpstankertracking.Modal;

public class RequestListModel {
    String requestid , distance,  from ,to;

    public String getBookingid() {
        return requestid;
    }

    public void setBookingid(String bookingid) {
        this.requestid = bookingid;
    }



    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }



    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


}
