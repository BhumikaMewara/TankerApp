package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

public class BookingDetails extends AppCompatActivity implements View.OnClickListener {


    TextView bookingid, distance, pickup, drop, drivername, contact_no, message, pagetitle;
    ImageView calltous;
    ImageView menunotification;
    RelativeLayout menuback;
    String init_type,bkngid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        initViews();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            bkngid = bundle.getString("booking_id");
        }
    }

    public void initViews() {
        //toolbar = (Toolbar) findViewById(R.layout.toolbar_with_bk_arrow);
        init_type = getIntent().getExtras().getString("init_type");
        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        bookingid = (TextView) findViewById(R.id.tv_bookingdetail_bookingid);
        distance = (TextView) findViewById(R.id.tv_bookingdetail_distance);
        pickup = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drop = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drivername = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        contact_no = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        message = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        calltous = (ImageView) findViewById(R.id.iv_bookingdetail_bookingid_call);
        calltous.setOnClickListener(this);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
        menuback.setOnClickListener(this);
        menunotification = (ImageView) findViewById(R.id.iv_tb_with_bck_arrow_notification);
        menunotification.setOnClickListener(this);
        if (init_type.equals(Constants.COMPLETED_CALL)) {
            pagetitle.setText("Completed Booking Details");
        } else if (init_type.equals(Constants.ABORTED_CALL)) {
            pagetitle.setText("Aborted Booking Details");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_toolbar_with_back_backLayout:
                onBackPressed();
                break;
            case R.id.iv_tb_with_bck_arrow_notification:
                Intent intent;
                intent = new Intent(BookingDetails.this, Notifications.class);
                startActivity(intent);
                break;
            case R.id.iv_bookingdetail_bookingid_call:
                break;
        }
    }

    private void bookingByIdApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_BY_ID +bkngid ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(BookingDetails.this, bookingdetailsApiListner, url, headparam, jsonBodyObj);

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
                        JSONObject data = mydata.getJSONObject("data");
                        if (data!=null){
                            data.getString("_id");
                            data.getString("message");
                            data.getString("phone_country_code");
                            data.getString("phone");
                            data.getString("controller_name");


                            JSONObject distance=    data.getJSONObject("distance");
                            {
                                if (distance!=null){
                                    distance.getString("value");
                                    distance.getString("text");
                                }
                                 else {
                                   RequestQueueService.showAlert("Error! No Data Found",BookingDetails.this);
                                 }
                            }

                        }else {
                            RequestQueueService.showAlert("Error! No Data Found",BookingDetails.this);
                        }

                        /*FirebaseAuth.getInstance().signOut();
                        SessionManagement.logout(bookingdetailsApiListner, BookingDetails.this);
                        Intent i = new Intent(BookingDetails.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        Toast.makeText(BookingDetails.this, "You are now logout", Toast.LENGTH_SHORT).show();*/
                        finish();
                    }else {
                        RequestQueueService.showAlert("Error! No Data Found",BookingDetails.this);
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",BookingDetails.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
RequestQueueService.showAlert(msg,BookingDetails.this);
        }

        @Override
        public void onFetchStart() {

        }

    };

}
