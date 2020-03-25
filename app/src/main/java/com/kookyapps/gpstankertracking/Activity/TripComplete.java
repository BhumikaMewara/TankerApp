package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class TripComplete extends AppCompatActivity implements View.OnClickListener {

    TextView bookingid,distancetext,pickup,drop,controller_name,contact_no,message,pagetitle,notificationCountText;
    //ImageView calltous;
    ImageView menunotification;
    RelativeLayout back,noti,bottom,notificationCountLayout;
    String init_type,bkngid,can_accept,can_end,can_start;
    BookingListModal blmod;
    static String notificationCount;
    Bundle b;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);
        initViews();
        bookingByIdApiCalling();

    }
    public void initViews(){

            init_type = getIntent().getExtras().getString("init_type");
           // bkngid = getIntent().getExtras().getString("booking_id");
            blmod= (BookingListModal) getIntent().getExtras().get("Bookingdata");
            pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
            back = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
            noti=(RelativeLayout)findViewById(R.id.rl_toolbar_with_back_notification);

            notificationCountLayout=(RelativeLayout)findViewById(R.id.rl_toolbar_notificationcount);
            notificationCountText=(TextView)findViewById(R.id.tv_toolbar_notificationcount);

        int noticount = Integer.parseInt(SessionManagement.getNotificationCount(this));
        if(noticount<=0){
            clearNotificationCount();
        }else{
            notificationCountText.setText(String.valueOf(noticount));
            notificationCountLayout.setVisibility(View.VISIBLE);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(TripComplete.this));
                    setNotificationCount(count+1,false);
                }
            }
        };

        bookingid = (TextView) findViewById(R.id.tv_tripcomplete_bookingid);
            distancetext = (TextView) findViewById(R.id.tv_tripcomplete_distance);
            pickup = (TextView) findViewById(R.id.tv_tripcomplete_pickup);
            drop = (TextView) findViewById(R.id.tv_tripcomplete_drop);
            controller_name = (TextView) findViewById(R.id.tv_tripcomplete_drivername);
            contact_no = (TextView) findViewById(R.id.tv_tripcomplete_contact);
            message = (TextView) findViewById(R.id.tv_tripcomplete_message);
            bottom=(RelativeLayout)findViewById(R.id.rl_bottomLayout_text);
            back.setOnClickListener(this);
            noti.setOnClickListener(this);
            bottom.setOnClickListener(this);
            pagetitle.setText(getString(R.string.trip_complete));



    }
    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.rl_toolbarmenu_backimglayout:
                back.setClickable(false);
                i=new Intent(TripComplete.this,FirstActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.iv_tb_with_bck_arrow_notification:
                i = new Intent(TripComplete.this,Notifications.class);
                startActivity(i);
                break;
            case R.id.rl_bottomLayout_text:
                i=new Intent(this,FirstActivity.class);
                startActivity(i);
                finish();
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
                            bookingid.setText(blmod.getBookingid());

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
                                distancetext.setText(blmod.getDistance());
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


    public void setNotificationCount(int count,boolean isStarted){
        notificationCount = SessionManagement.getNotificationCount(TripComplete.this);
        if(Integer.parseInt(notificationCount)!=count) {
            notificationCount = String.valueOf(count);
            if (count <= 0) {
                clearNotificationCount();
            } else if (count < 100) {
                notificationCountText.setText(String.valueOf(count));
                notificationCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                notificationCountLayout.setVisibility(View.VISIBLE);
            }
            SharedPrefUtil.setPreferences(TripComplete.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
            boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if(b2)
                SharedPrefUtil.setPreferences(TripComplete.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY,"no");
        }
    }
    public void newNotification(){
        Log.i("newNotification","Notification");
        int count = Integer.parseInt(SharedPrefUtil.getStringPreferences(TripComplete.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY));
        setNotificationCount(count+1,false);
    }
    public void clearNotificationCount(){
        notificationCountText.setText("");
        notificationCountLayout.setVisibility(View.GONE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
        //change the language when prompt
        int sharedCount =Integer.parseInt(SessionManagement.getNotificationCount(this));
        String viewCount =notificationCountText.getText().toString();
        boolean b1 = String.valueOf("sharedCount")!=viewCount;

        boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
        if(b2){
            newNotification();
        }else if (b1){
            if (sharedCount < 100 && sharedCount>0) {
                notificationCountText.setText(String.valueOf(sharedCount));
                notificationCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                notificationCountLayout.setVisibility(View.VISIBLE);
            }
        }
    }


}




