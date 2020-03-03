package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.Utils.VolleyMultipartRequest2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

public class RequestDetails extends AppCompatActivity implements View.OnClickListener {

    TextView bookingid,distancetext,pickup,drop,controllername,contact_no,message,pagetitle,bottomtext;
    ImageView calltous;
    ImageView menunotification;
    RelativeLayout menuback,bottom;
    String init_type,bkngid;
    BookingListModal blmod;
    ArrayList<String> imagearray;
    String imageencoded,can_accept,can_end,can_start;
    boolean cameraAccepted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        initViews();
        bookingByIdApiCalling();

    }
    public void initViews() {
        init_type = getIntent().getExtras().getString("init_type");
        bkngid = getIntent().getExtras().getString("booking_id");

        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        bookingid = (TextView) findViewById(R.id.tv_bookingdetail_bookingid);
        distancetext = (TextView) findViewById(R.id.tv_bookingdetail_distance);
        pickup = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drop = (TextView) findViewById(R.id.tv_bookingdetail_drop);
        controllername = (TextView) findViewById(R.id.tv_bookingdetail_drivername);
        contact_no = (TextView) findViewById(R.id.tv_bookingdetail_contact);
        message = (TextView) findViewById(R.id.tv_bookingdetail_message);


        calltous = (ImageView) findViewById(R.id.iv_bookingdetail_bookingid_call);
        calltous.setOnClickListener(this);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
        menuback.setOnClickListener(this);
        menunotification = (ImageView) findViewById(R.id.iv_tb_with_bck_arrow_notification);
        menunotification.setOnClickListener(this);
        bottom = (RelativeLayout) findViewById(R.id.rl_result_details_bottomLayout);
        bottom.setOnClickListener(this);
        bottomtext = (TextView) findViewById(R.id.tv_result_details_bottomlayout_text);

//        if (init_type.equals(Constants.REQUEST_DETAILS)) {
//            pagetitle.setText("Request Details");
//            bottomtext.setText("ACCEPT");
//
//        } else if (init_type.equals(Constants.BOOKING_START)) {
//            if (can_start.equals("true")){
//            pagetitle.setText("Booking Details");
//            bottomtext.setText("START");
//            }else {
//                bottomtext.setText("View Map");
//            }
//        }

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
                        bookingacceptedapiCalling();
                    }else if (init_type.equals(Constants.BOOKING_START)) {
                        if (can_start.equals("true")) {
                            if (checkPermission()) {
                                cameraAccepted = true;
                            } else {
                                requestPermission();
                            }
                            if (cameraAccepted) {
                                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(camera_intent, 0);
                            } else {
                                requestPermission();
                            }
                        }
                        else{
                            intent = new Intent(RequestDetails.this,Map1.class);
                            intent.putExtra("Bookingdata",blmod);
                            startActivity(intent);
                        }
                    }
            }
        }


    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0){
                    cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        cameraAccepted = true;
                    }else{
                        cameraAccepted = false;
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if(resultCode!=0) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageencoded = Utils.encodeTobase64(bitmap);
            if (can_start.equals("true")){
                Intent intent= new Intent(this, TankerStartingPic.class);
                intent.putExtra("Bitmap",imageencoded);
                intent.putExtra("Bookingdata",blmod);
                startActivity(intent);
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bookingacceptedapiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {

            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.BOOKING_ACCEPTED + bkngid;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(RequestDetails.this, bookingacceptApiListner, url, headparam, jsonBodyObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    FetchDataListener bookingacceptApiListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject mydata) {

            try {
                if (mydata != null) {
                    if (mydata.getInt("error") == 0) {

                        Intent intent = new Intent(getApplicationContext() , FirstActivity.class);
                        intent.putExtra("curretTab",1);
                        startActivity(intent);

                    } else {
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






   /* public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
*/











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
                            controllername.setText(blmod.getController_name());
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

                            if (init_type.equals(Constants.REQUEST_DETAILS)) {
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
                            }
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













