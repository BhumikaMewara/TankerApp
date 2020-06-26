package com.kookyapps.gpstankertracking.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;



import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kookyapps.gpstankertracking.Modal.MapDataParserModal;
import com.kookyapps.gpstankertracking.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.content.ContextCompat;


public class PointsParser extends AsyncTask<String, Integer, MapDataParserModal> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";
    Context mContext;
    String mapdata;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
        this.mContext = mContext;
    }

    // Parsing the data in non-ui thread
    @Override
    protected MapDataParserModal doInBackground(String... jsonData) {

        JSONObject jObject;
        MapDataParserModal calculated = null;
        mapdata = jsonData[0];
        //List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            calculated = parser.parse(jObject);
            //routes = parser.parse(jObject);
            Log.d("mylog", "Executing routes");
            //Log.d("mylog", routes.toString());

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return calculated;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(MapDataParserModal calculated) {
        List<List<HashMap<String, String>>> result = null;
        ArrayList<LatLng> points=null;
        PolylineOptions lineOptions = null;
        long distance=0,duration=0;
        boolean deviated=false;
        LatLng curlatlng=null;
        if(calculated!=null){
            result = calculated.getRoutes();
            distance = calculated.getDistance();
            duration = calculated.getDuration();
        }

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }
        if(lineOptions!=null) {
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            } else {
                lineOptions.width(30);
                lineOptions.color(ContextCompat.getColor(mContext, R.color.greenLight));
            }
            taskCallback.onTaskDone(lineOptions,distance,duration,points,mapdata);
        }else {
            Log.d("mylog", "without Polylines drawn");
            taskCallback.onTaskDone();
        }
        // Drawing polyline in the Google Map for the i-th route
    }
}
