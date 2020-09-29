package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.fcm.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class TripComplete extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {
    TextView tv_showmap,bookingid,distancetext,pickup,drop,controller_name,contact_no,message,pagetitle,notificationCountText,distanceTravelledTitleText;
    RelativeLayout back,bottom,showmaplayout;
    String init_type,bkngid,can_accept,can_end,can_start;
    BookingListModal blmod;
    ImageView iv_showmap;
    ScrollView scroll;
    Bundle b;
    SupportMapFragment mapFragment;
    RelativeLayout maplayout;
    ArrayList<LatLng>finalpath = null;
    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);
        initViews();
        bookingByIdApiCalling();
    }
    public void initViews() {
        init_type = getIntent().getExtras().getString("init_type");
        // bkngid = getIntent().getExtras().getString("booking_id");
        blmod = (BookingListModal) getIntent().getExtras().get("Bookingdata");
        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        back = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        notificationCountText = (TextView) findViewById(R.id.tv_toolbar_notificationcount);
        maplayout = (RelativeLayout) findViewById(R.id.rl_tripComplete_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fg_tripcomplete_map);
        bookingid = (TextView) findViewById(R.id.tv_tripcomplete_bookingid);
        distancetext = (TextView) findViewById(R.id.tv_tripcomplete_distance);
        pickup = (TextView) findViewById(R.id.tv_tripcomplete_pickup);
        drop = (TextView) findViewById(R.id.tv_tripcomplete_drop);
        controller_name = (TextView) findViewById(R.id.tv_tripcomplete_drivername);
        contact_no = (TextView) findViewById(R.id.tv_tripcomplete_contact);
        message = (TextView) findViewById(R.id.tv_tripcomplete_message);
        distanceTravelledTitleText = (TextView) findViewById(R.id.tv_tripcomplete_distance_title);
        bottom = (RelativeLayout) findViewById(R.id.rl_bottomLayout_text);
        showmaplayout = (RelativeLayout)findViewById(R.id.rl_trip_complete_showmap);
        tv_showmap = (TextView)findViewById(R.id.tv_trip_complete_showmap);
        iv_showmap = (ImageView) findViewById(R.id.iv_trip_complete_showmap);
        scroll = (ScrollView)findViewById(R.id.sv_tripcomplete);
        scroll.setVisibility(View.GONE);
        showmaplayout.setOnClickListener(this);
        tv_showmap.setText("Hide Map");
        iv_showmap.setImageResource(R.drawable.ic_minus);
        back.setOnClickListener(this);
        bottom.setOnClickListener(this);
        pagetitle.setText(getString(R.string.trip_complete));
    }
    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;
            case R.id.rl_bottomLayout_text:
                onBackPressed();
                break;
            case R.id.rl_trip_complete_showmap:
                if(maplayout.getVisibility()==View.VISIBLE){
                    maplayout.setVisibility(View.GONE);
                    scroll.setVisibility(View.VISIBLE);
                    tv_showmap.setText("Show Map");
                    iv_showmap.setImageResource(R.drawable.ic_plus);
                }else{
                    scroll.setVisibility(View.GONE);
                    maplayout.setVisibility(View.VISIBLE);
                    tv_showmap.setText("Hide Map");
                    iv_showmap.setImageResource(R.drawable.ic_minus);
                }
                break;
        }
    }

    private void bookingByIdApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            GETAPIRequest getapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_BY_ID +blmod.getBookingid() ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(TripComplete.this, bookingdetailsApiListner, url, headparam, jsonBodyObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    FetchDataListener bookingdetailsApiListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject mydata) {

            try {
                if (mydata != null) {
                    if (mydata.getInt("error") == 0) {
                        ArrayList<BookingListModal> bookingList = new ArrayList<>();
                        JSONObject data = mydata.getJSONObject("data");
                        blmod = new BookingListModal();
                        if (data != null) {
                            blmod.setBookingid(data.getString("_id"));
                            //bookingid.setText(blmod.getTankerBookingid());

                            if (data.getString("message").equals("")){
                                message.setText("No message");
                            }else {
                                blmod.setMessage(data.getString("message"));
                                message.setText(blmod.getMessage());
                            }


                            blmod.setPhone_country_code(data.getString("phone_country_code"));
                            blmod.setPhone(data.getString("phone"));
                            contact_no.setText("+" + blmod.getPhone_country_code() + blmod.getPhone());
                            blmod.setController_name(data.getString("controller_name"));
                            controller_name.setText(blmod.getController_name());
                            blmod.setCan_accept(data.getString("can_accept"));

                            can_accept=String.valueOf(data.getBoolean("can_accept"));
                            blmod.setCan_start(data.getString("can_start"));
                            can_start= String.valueOf(data.getBoolean("can_start"));
                            blmod.setCan_end(data.getString("can_end"));
                            can_end=String.valueOf(data.getBoolean("can_end"));


                            JSONObject distance = data.getJSONObject("distance");
                            if (distance != null) {
                                distance.getString("value");
                                blmod.setDistance(distance.getString("text"));
                               // distancetext.setText(blmod.getDistance());
                            } else {
                                RequestQueueService.showAlert("Error! No Data in distance Found", TripComplete.this);
                            }


                            JSONObject drop_point = data.getJSONObject("drop_point");
                            if (drop_point != null) {
                                drop_point.getString("location");

                                blmod.setGeofence_in_meter(drop_point.getString("geofence_in_meter"));
                                blmod.setToaddress(drop_point.getString("address"));
                                drop.setText(blmod.getToaddress());
                                JSONObject geomaetry = drop_point.getJSONObject("geometry");
                                if (geomaetry != null) {
                                    geomaetry.getString("type");
                                    JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                    if (coordinates != null) {
                                        blmod.setTologitude(coordinates.getString(0)); // lng
                                        blmod.setTolatitude(coordinates.getString(1)); //lat
                                    }else {
                                        RequestQueueService.showAlert("Error! No Coordinates Found", TripComplete.this); }
                                }else { RequestQueueService.showAlert("Error! No Data in geomaetry Found", TripComplete.this); }
                            } else {
                                RequestQueueService.showAlert("Error! No Data in drop_point Found", TripComplete.this);
                            }

                            blmod.setTankerBookingid(data.getString("booking_id"));
                            bookingid.setText(blmod.getTankerBookingid());


                                JSONObject distanceTravelled = data.getJSONObject("distance_traveled");
                                if (distanceTravelled != null) {
                                    blmod.setDistanceTravelled(distanceTravelled.getString("text"));
                                    distanceTravelled.getString("value");
                                    distanceTravelledTitleText.setText(R.string.final_distance);
                                    distancetext.setText(blmod.getDistanceTravelled());
                                }else {
                                    RequestQueueService.showAlert("Error! No Data in distanceTravelled Found", TripComplete.this);
                                  //  progressBar.setVisibility(View.GONE);
                                    bottom.setVisibility(View.VISIBLE);
                                    bottom.setClickable(true); }



                            JSONObject pickup_point = data.getJSONObject("pickup_point");
                            {
                                if (pickup_point != null) {
                                    blmod.setFromlocation(pickup_point.getString("location"));
                                    //drop_point.getString("geofence_in_meter");
                                    blmod.setFromaddress(pickup_point.getString("address"));
                                    pickup.setText(blmod.getFromaddress());


                                    JSONObject geomaetry = pickup_point.getJSONObject("geometry");
                                    if (geomaetry != null) {
                                        geomaetry.getString("type");
                                        JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            blmod.setFromlongitude(coordinates.getString(0)); // lng
                                            blmod.setFromlatitude(coordinates.getString(1)); //lat
                                        } else {
                                            RequestQueueService.showAlert("Error! No Coordinates Found", TripComplete.this);
                                        }
                                    } else {
                                        RequestQueueService.showAlert("Error! No Data in geomaetry Found", TripComplete.this);
                                    }


                                } else {
                                    RequestQueueService.showAlert("Error! No Data in pick_point Found", TripComplete.this);
                                }
                            }
                        } else {
                            RequestQueueService.showAlert("Error! No Data Found", TripComplete.this);
                        }

                        if(data.has("snapped_path")){
                            String snapstring = data.getString("snapped_path");
                            JSONObject snap = new JSONObject(snapstring);
                            JSONArray snaparray = snap.getJSONArray("snappedPoints");
                            if(finalpath == null)
                                finalpath = new ArrayList<>();
                            for(int i=0;i<snaparray.length();i++){
                                JSONObject point = snaparray.getJSONObject(i);
                                JSONObject location = point.getJSONObject("location");
                                double lat = Double.parseDouble(location.getString("latitude"));
                                double longi = Double.parseDouble(location.getString("longitude"));
                                LatLng temp = new LatLng(lat,longi);
                                finalpath.add(temp);
                            }
                            mapFragment.getMapAsync(TripComplete.this);
                        }


                        // finish();
                    }
                    else {
                        RequestQueueService.showAlert("Error! Data is null",TripComplete.this);
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",TripComplete.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.showAlert(msg,TripComplete.this);
        }

        @Override
        public void onFetchStart() {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, FirstActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        maplayout.setVisibility(View.VISIBLE);
        PolylineOptions op = new PolylineOptions();
        op.addAll(finalpath);
        op.width(30);
        op.color(ContextCompat.getColor(TripComplete.this,R.color.greenLight));
        mMap.addPolyline(op);
        LatLng pickupLatLng = finalpath.get(0);
        LatLng dropLatLng = finalpath.get(finalpath.size()-1);
        MarkerOptions pickupop,dropop,currentop;
        pickupop = new MarkerOptions()
                .position(pickupLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));
        dropop = new MarkerOptions()
                .position(dropLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));
        mMap.addMarker(pickupop);
        mMap.addMarker(dropop);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 18));
    }
}




