package com.kookyapps.gpstankertracking.Modal;

import org.json.JSONException;
import org.json.JSONObject;

public class SnappedPoint {
    private float latitude;
    private float longitude;
    private String placeid;
    private int originalindex;

    public SnappedPoint(){
        this.originalindex = -1;
    }
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

    public int getOriginalindex() {
        return originalindex;
    }

    public void setOriginalindex(int originalindex) {
        this.originalindex = originalindex;
    }

    public String toString(){
        String point = "";
        try {
            if(originalindex<0)
                point = String.valueOf(latitude) + "<|>" + String.valueOf(longitude) + "<|>" + placeid;
            else
                point = String.valueOf(latitude) + "<|>" + String.valueOf(longitude) + "<|>" + placeid+"<|>"+originalindex;
        }catch (Exception e){
            e.printStackTrace();
        }
        return point;
    }

    public SnappedPoint parseSnappedPoint(String point){
        SnappedPoint spoint = new SnappedPoint();
        String[] parts = point.split("<|>");
        int len = parts.length;
        if(len<3 || len>4)
            return null;
        spoint.setLatitude(Float.parseFloat(parts[0]));
        spoint.setLongitude(Float.parseFloat(parts[1]));
        spoint.setPlaceid(parts[2]);
        if(len==4)
            spoint.setOriginalindex(Integer.parseInt(parts[3]));
        return spoint;
    }

    public JSONObject toJson(){
        JSONObject point = new JSONObject();
        try {
            JSONObject location = new JSONObject();
            location.put("latitude", String.valueOf(latitude));
            location.put("latitude", String.valueOf(longitude));
            point.put("location",location);
            point.put("placeId",placeid);
            point.put("originalIndex",originalindex);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return point;
    }

    public SnappedPoint fromJson(JSONObject point){
        SnappedPoint spoint = new SnappedPoint();
        try{
            JSONObject location = point.getJSONObject("location");
            spoint.setLatitude(Float.parseFloat(location.getString("latitude")));
            spoint.setLongitude(Float.parseFloat(location.getString("longitude")));
            spoint.setPlaceid(point.getString("placeId"));
            if(point.has("originalIndex"))
                spoint.setOriginalindex(Integer.parseInt(point.getString("originalIndex")));
            else
                spoint.setOriginalindex(-1);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return spoint;
    }
}
