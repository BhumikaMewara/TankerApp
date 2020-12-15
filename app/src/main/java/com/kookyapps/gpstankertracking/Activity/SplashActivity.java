package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Services.TankerLocationCallback;
import com.kookyapps.gpstankertracking.Services.TankerLocationService;
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
    private TankerLocationService mService = null;
    private boolean mBound = false;
    ConstraintLayout cl_NoInternet;
    ImageView iv_refresh;
    TextView tv_NoInternet;
    int refreshlevel = 0;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TankerLocationService.LocalBinder binder = (TankerLocationService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
            //mService.setServiceCallback(SplashActivity.this);
            /*if(!mService.isSocketInitialized())
                mService.initSocket(blmod.getBookingid());*/
            mService.stopService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cl_NoInternet = (ConstraintLayout)findViewById(R.id.cl_no_internet);
        tv_NoInternet = (TextView)findViewById(R.id.tv_no_internet);
        iv_refresh = (ImageView)findViewById(R.id.iv_refresh_icon);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_refresh.setClickable(false);
                cl_NoInternet.setVisibility(View.GONE);
                if(refreshlevel==0)
                    bookingByIdApiCalling();
                else if(refreshlevel==1)
                    getNotificationCount();
                iv_refresh.setClickable(true);
            }
        });
        NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        nm.cancelAll();
        if(SharedPrefUtil.hasKey(this,Constants.SHARED_PREF_LOGIN_TAG,Constants.SERVER_IP)) {
            URLs.BASE_URL = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_LOGIN_TAG,Constants.SERVER_IP)+"/api/tanker/";
            URLs.SOCKET_URL=SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_LOGIN_TAG,Constants.SERVER_IP)+"?token=";
            if (SessionManagement.checkSignIn(this)) {
                if (SharedPrefUtil.hasKey(SplashActivity.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID)) {
                    bookingByIdApiCalling();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNotificationCount();
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
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, SelectServer.class);
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
            refreshlevel = 0;
            tv_NoInternet.setText("An Error Occurred");
            cl_NoInternet.setVisibility(View.VISIBLE);
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
                        Log.d("Booking Detail",data.toString());
                        BookingListModal blmod = new BookingListModal();
                        if (data != null) {
                            blmod.setBookingid(data.getString("_id"));
                            String status = data.getString("status");
                            if(data.has("path"))
                                blmod.setPath(data.getString("path"));
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
                                bindService(new Intent(SplashActivity.this, TankerLocationService.class),mServiceConnection, Context.BIND_AUTO_CREATE);
                                getNotificationCount();
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                //RequestQueueService.showAlert("Error! No Data Found",SplashActivity.this);
                e.printStackTrace();
                refreshlevel = 0;
                tv_NoInternet.setText("An Error Occurred");
                cl_NoInternet.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            try {
                JSONObject er = new JSONObject(msg);
                String code = er.getString("code");
                Toast.makeText(SplashActivity.this,"Fetch Failure",Toast.LENGTH_LONG).show();
                refreshlevel = 0;
                tv_NoInternet.setText(msg);
                cl_NoInternet.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
                //RequestQueueService.showAlert(msg, SplashActivity.this);
                refreshlevel = 0;
                tv_NoInternet.setText(msg);
                cl_NoInternet.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFetchStart() {

        }

    };

    private void getNotificationCount() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            GETAPIRequest getapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.NOTIFICATION_COUNT;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this, notiCountListener, url, headparam, jsonBodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
            refreshlevel = 1;
            tv_NoInternet.setText("An Error Occurred");
            cl_NoInternet.setVisibility(View.VISIBLE);
        }
    }

    FetchDataListener notiCountListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject response) {
            Log.d("NotiCount:",response.toString());
            try {
                if (response != null) {
                    if (response.getInt("error") == 0) {
                        JSONObject data = response.getJSONObject("data");
                        String count = data.getString("count");
                        SessionManagement.setNotificationCount(SplashActivity.this,count);
                        Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                        SharedPrefUtil.deletePreference(SplashActivity.this,Constants.SHARED_PREF_ONGOING_TAG);
                        startActivity(i);
                        finish();
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",SplashActivity.this);
                e.printStackTrace();
                refreshlevel = 1;
                tv_NoInternet.setText("An Error Occurred");
                cl_NoInternet.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            try {
                Log.e("noticount",msg);
                //JSONObject er = new JSONObject(msg);
                //String code = er.getString("code");
                Toast.makeText(SplashActivity.this,"Fetch Failure",Toast.LENGTH_LONG).show();
                refreshlevel = 1;
                tv_NoInternet.setText(msg);
                cl_NoInternet.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
                //RequestQueueService.showAlert(msg, SplashActivity.this);
                refreshlevel = 1;
                tv_NoInternet.setText(msg);
                cl_NoInternet.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFetchStart() {

        }

    };
}
