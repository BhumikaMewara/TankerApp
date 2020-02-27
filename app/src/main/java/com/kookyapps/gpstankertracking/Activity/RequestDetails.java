package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Adapters.BookingListAdapter;
import com.kookyapps.gpstankertracking.Adapters.RequestListAdapter;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestDetails extends AppCompatActivity implements View.OnClickListener {

    TextView bookingid,distancetext,pickup,drop,drivername,contact_no,message,pagetitle,bottomtext;
    ImageView calltous;
    ImageView menunotification;
    RelativeLayout menuback,bottom;
    String init_type,bkngid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        initViews();
        bookingByIdApiCalling();

    }
    public void initViews() {
     /*   toolbar= (Toolbar)findViewById(R.id.toolbarmenu_backarrow);
       // container= (RelativeLayout)findViewById(R.id.rl_rqst_det_container);*/
       init_type = getIntent().getExtras().getString("init_type");
        bkngid =  getIntent().getExtras().getString("booking_id");

        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        bookingid = (TextView) findViewById(R.id.tv_bookingdetail_bookingid);
        distancetext = (TextView) findViewById(R.id.tv_bookingdetail_distance);
        pickup = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drop = (TextView) findViewById(R.id.tv_bookingdetail_drop);
        drivername = (TextView) findViewById(R.id.tv_bookingdetail_drivername);
        contact_no = (TextView) findViewById(R.id.tv_bookingdetail_contact);
        message = (TextView) findViewById(R.id.tv_bookingdetail_message);



        calltous = (ImageView) findViewById(R.id.iv_bookingdetail_bookingid_call);
        calltous.setOnClickListener(this);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
        menuback.setOnClickListener(this);
        menunotification = (ImageView) findViewById(R.id.iv_tb_with_bck_arrow_notification);
        menunotification.setOnClickListener(this);
        bottom= (RelativeLayout)findViewById(R.id.rl_result_details_bottomLayout);
        bottom.setOnClickListener(this);
        bottomtext=(TextView)findViewById(R.id.tv_result_details_bottomlayout_text);

        if (init_type.equals(Constants.REQUEST_DETAILS)) {
            pagetitle.setText("Request Details");
            bottomtext.setText("ACCEPT");

        } else if (init_type.equals(Constants.BOOKING_START)) {
            pagetitle.setText("Booking Details");
            bottomtext.setText("START");
        }

    }



    @Override
        public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
                case R.id.rl_toolbar_with_back_backLayout:
                    onBackPressed();
                    break;
                case R.id.iv_tb_with_bck_arrow_notification:

                    intent = new Intent(RequestDetails.this,Notifications.class);
                    startActivity(intent);
                    break;
                case R.id.iv_bookingdetail_bookingid_call:
                    break;
                case R.id.rl_result_details_bottomLayout:
                    if (init_type.equals(Constants.REQUEST_DETAILS)){
                        intent= new Intent(this,FirstActivity.class);

                        startActivity(intent);
                    }else if (init_type.equals(Constants.BOOKING_START)){

                        intent= new Intent(this, TankerStartingPic.class);
                        startActivity(intent);
                    }


            }
        }

    private void bookingByIdApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {

            GETAPIRequest getapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_BY_ID +bkngid ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(RequestDetails.this, bookingdetailsApiListner, url, headparam, jsonBodyObj);

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
                        BookingListModal blmod = new BookingListModal();
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

                            JSONObject distance = data.getJSONObject("distance");
                            if (distance != null) {
                                distance.getString("value");
                                blmod.setDistance(distance.getString("text"));
                                distancetext.setText(blmod.getDistance());
                            } else {
                                RequestQueueService.showAlert("Error! No Data in distance Found", RequestDetails.this);
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
                                        RequestQueueService.showAlert("Error! No Coordinates Found", RequestDetails.this); }
                                }else { RequestQueueService.showAlert("Error! No Data in geomaetry Found", RequestDetails.this); }
                            } else {
                                RequestQueueService.showAlert("Error! No Data in drop_point Found", RequestDetails.this);
                            }

                            JSONObject pickup_point = data.getJSONObject("pickup_point");
                            {
                                if (pickup_point != null) {
                                    blmod.setFromlocation(drop_point.getString("location"));
                                    //drop_point.getString("geofence_in_meter");
                                    blmod.setFromaddress(drop_point.getString("address"));
                                    pickup.setText(blmod.getFromaddress());


                                    JSONObject geomaetry = drop_point.getJSONObject("geometry");
                                    if (geomaetry != null) {
                                        geomaetry.getString("type");
                                        JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            blmod.setFromlongitude(coordinates.getString(0)); // lng
                                            blmod.setFromlongitude(coordinates.getString(1)); //lat
                                        } else {
                                            RequestQueueService.showAlert("Error! No Coordinates Found", RequestDetails.this);
                                        }
                                    } else {
                                        RequestQueueService.showAlert("Error! No Data in geomaetry Found", RequestDetails.this);
                                    }


                                } else {
                                    RequestQueueService.showAlert("Error! No Data in pick_point Found", RequestDetails.this);
                                }
                            }
                            /*JSONObject controller = data.getJSONObject("controller");
                            if (controller!=null){
                                controller.getString("_id");
                                controller.getString("name");
                            }else {
                                RequestQueueService.showAlert("Error! No data in controller found",RequestDetails.this);
                            }*/


                        } else {
                            RequestQueueService.showAlert("Error! No Data Found", RequestDetails.this);
                        }


                        // finish();
                    }
                    else {
                        RequestQueueService.showAlert("Error! Data is null",RequestDetails.this);
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",RequestDetails.this);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.showAlert(msg,RequestDetails.this);
        }

        @Override
        public void onFetchStart() {

        }

    };

}













