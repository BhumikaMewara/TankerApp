package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (SessionManagement.checkSignIn(this)) {

            if (SharedPrefUtil.hasKey(SplashActivity.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID)){
                bookingByIdApiCalling();
            }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }

    private void bookingByIdApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            GETAPIRequest getapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_BY_ID +SharedPrefUtil.getStringPreferences(SplashActivity.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID) ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this, bookingdetailsApiListner, url, headparam, jsonBodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    FetchDataListener bookingdetailsApiListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject mydata) {
            Log.d("RequestDetails:",mydata.toString());
            try {
                if (mydata != null) {
                    if (mydata.getInt("error") == 0) {
                        ArrayList<BookingListModal> bookingList = new ArrayList<>();
                        JSONObject data = mydata.getJSONObject("data");
                        BookingListModal blmod = new BookingListModal();
                        if (data != null) {
                            blmod.setBookingid(data.getString("_id"));
                            String status = data.getString("status");
                            if (status.equals("3")) {
                                blmod.setStatus(data.getInt("status"));
                                blmod.setMessage(data.getString("message"));
                                blmod.setPhone_country_code(data.getString("phone_country_code"));
                                blmod.setPhone(data.getString("phone"));
                                blmod.setController_name(data.getString("controller_name"));
                                blmod.setCan_accept(data.getString("can_accept"));
                                blmod.setCan_start(data.getString("can_start"));
                                blmod.setCan_end(data.getString("can_end"));
                                JSONObject distance = data.getJSONObject("distance");
                                if (distance != null) {
                                    blmod.setDistance(distance.getString("text"));
                                } else {
                                    Toast.makeText(SplashActivity.this, "No distance value", Toast.LENGTH_LONG).show();
                                }
                                JSONObject drop_point = data.getJSONObject("drop_point");
                                if (drop_point != null) {
                                    blmod.setToaddress(drop_point.getString("address"));
                                    blmod.setGeofence_in_meter(drop_point.getString("geofence_in_meter"));
                                    JSONObject geomaetry = drop_point.getJSONObject("geometry");
                                    if (geomaetry != null) {
                                        geomaetry.getString("type");
                                        JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            blmod.setTologitude(coordinates.getString(0)); // lng
                                            blmod.setTolatitude(coordinates.getString(1)); //lat
                                        } else {
                                            Toast.makeText(SplashActivity.this, "No drop point", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(SplashActivity.this, "No drop point", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(SplashActivity.this, "No drop point", Toast.LENGTH_LONG).show();
                                }
                                blmod.setTankerBookingid(data.getString("booking_id"));
                                JSONObject pickup_point = data.getJSONObject("pickup_point");
                                if (pickup_point != null) {
                                    blmod.setFromlocation(pickup_point.getString("location"));
                                    blmod.setFromaddress(pickup_point.getString("address"));
                                    JSONObject geomaetry = pickup_point.getJSONObject("geometry");
                                    if (geomaetry != null) {
                                        geomaetry.getString("type");
                                        JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            blmod.setFromlongitude(coordinates.getString(0)); // lng
                                            blmod.setFromlatitude(coordinates.getString(1)); //lat
                                        } else {
                                            Toast.makeText(SplashActivity.this, "No pickup point", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(SplashActivity.this, "No pickup point", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(SplashActivity.this, "No pickup point", Toast.LENGTH_LONG).show();
                                }
                                Intent i = new Intent(SplashActivity.this, Map1.class);
                                i.putExtra("Bookingdata", blmod);
                                i.putExtra("init_type", Constants.BOOKING_INIT);
                                i.putExtra("booking_id", blmod.getBookingid());
                                i.putExtra("tankerBookingId", blmod.getTankerBookingid());
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",SplashActivity.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            try {
                JSONObject er = new JSONObject(msg);
                String code = er.getString("code");
                Toast.makeText(SplashActivity.this,"Fetch Failure",Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
                RequestQueueService.showAlert(msg, SplashActivity.this);
            }
        }

        @Override
        public void onFetchStart() {

        }

    };
}
