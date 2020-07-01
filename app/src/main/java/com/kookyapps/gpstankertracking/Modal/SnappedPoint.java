package com.kookyapps.gpstankertracking.Modal;

public class SnappedPoint {
    private float latitude;
    private float longitude;
    private String placeid;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String toString(){
        String point = "";
        try {
            point = String.valueOf(latitude) + "<|>" + String.valueOf(longitude) + "<|>" + placeid;
        }catch (Exception e){
            return null;
        }
        return point;
    }

    public SnappedPoint parseSnappedPoint(String point){
        SnappedPoint spoint = new SnappedPoint();
        String[] parts = point.split("<|>");
        if(parts.length!=3)
            return null;
        spoint.setLatitude(Float.parseFloat(parts[0]));
        spoint.setLongitude(Float.parseFloat(parts[1]));
        spoint.setPlaceid(parts[2]);
        return spoint;
    }
}
