package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TripComplete extends AppCompatActivity implements View.OnClickListener {

    TextView bookingid,distancetext,pickup,drop,controller_name,contact_no,message,pagetitle;
    //ImageView calltous;
    ImageView menunotification;
    RelativeLayout back,noti,bottom;
    String init_type,bkngid,can_accept,can_end,can_start;
    BookingListModal blmod;
    Bundle b;
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
            back = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
            noti=(RelativeLayout)findViewById(R.id.rl_toolbar_with_back_notification);
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
            pagetitle.setText("Trip Complete");





    }
    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.rl_toolbar_with_back_backLayout:
                onBackPressed();
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




  /*  private void bookingByIdApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_BY_ID +bkngid ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(TripComplete.this, bookingdetailsApiListner, url, headparam, jsonBodyObj);

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
                                    RequestQueueService.showAlert("Error! No Data Found",TripComplete.this);
                                }
                            }

                        }else {
                            RequestQueueService.showAlert("Error! No Data Found",TripComplete.this);
                        }

                        finish();
                    }else {
                        RequestQueueService.showAlert("Error! No Data Found",TripComplete.this);
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
*/



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


                           /* if (init_type.equals(Constants.REQUEST_DETAILS)) {
                                pagetitle.setText("Request Details");
                                bottomtext.setText("ACCEPT");

                            } else if (init_type.equals(Constants.BOOKING_START)) {
                                if (can_start.equals("true")){
                                    pagetitle.setText("Booking Details");
                                    bottomtext.setText("START");
                                }else {
                                    pagetitle.setText("Booking Details");
                                    bottomtext.setText("View Map");
                                }
                            }*/
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

}




