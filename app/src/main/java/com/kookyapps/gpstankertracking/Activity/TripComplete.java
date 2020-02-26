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

public class TripComplete extends AppCompatActivity implements View.OnClickListener {

    TextView bookingid,distance,pickup,drop,drivername,contact_no,message,pagetitle;
    //ImageView calltous;
    ImageView menunotification;
    RelativeLayout back,noti,bottom;
    String init_type,bkngid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);
        initViews();

    }
    public void initViews(){

            pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
            back = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
            noti=(RelativeLayout)findViewById(R.id.rl_toolbar_with_back_notification);
            bookingid = (TextView) findViewById(R.id.tv_tripcomplete_bookingid);
            distance = (TextView) findViewById(R.id.tv_tripcomplete_distance);
            pickup = (TextView) findViewById(R.id.tv_tripcomplete_pickup);
            drop = (TextView) findViewById(R.id.tv_tripcomplete_drop);
            drivername = (TextView) findViewById(R.id.tv_tripcomplete_drivername);
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

    private void bookingByIdApiCalling() {
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

}
