package com.kookyapps.gpstankertracking.Modal;

public class TripDetailsModal {
    String bookingid,distance,fromlocation,tolocation,fromtime,totime;
    String fromlatitude,fromlongitude, tolatitude, tologitude;


    public String getFromlatitude() { return this.fromlatitude; }

    public void setFromlatitude(String fromlatitude) { this.fromlatitude = fromlatitude; }

    public String getFromlongitude() { return this.fromlongitude; }

    public void setFromlongitude(String fromlongitude) { this.fromlongitude = fromlongitude; }

    public String getTolatitude() { return this.tolatitude; }

    public void setTolatitude(String tolatitude) { this.tolatitude = tolatitude; }

    public String getTologitude() { return this.tologitude; }

    public void setTologitude(String tologitude) { this.tologitude = tologitude; }




    public String getBookingid() {
        return this.bookingid;
    }

    public void setBookingid(String bookingid) {
        this.bookingid = bookingid;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFromlocation() {
        return this.fromlocation;
    }

    public void setFromlocation(String fromlocation) {
        this.fromlocation = fromlocation;
    }

    public String getTolocation() {
        return this.tolocation;
    }

    public void setTolocation(String tolocation) {
        this.tolocation = tolocation;
    }

    public String getFromtime() {
        return this.fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getTotime() {
        return this.totime;
    }

    public void setTotime(String totime) {
        this.totime = totime;
    }
}
