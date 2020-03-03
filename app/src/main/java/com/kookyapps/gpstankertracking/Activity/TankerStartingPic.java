package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.Utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TankerStartingPic extends AppCompatActivity implements View.OnClickListener {

    ImageView img_retake,picture ,calender,clock;
    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day;
    LinearLayout retake;
    String imageencoded ,bkngid,init_type;
    boolean photo_taken,cameraAccepted=false;
    BookingListModal blmod;
    Bitmap leftbit;
    private LatLng currentlatlng=null;
    public static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_starting_pic);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        imageencoded = b.getString("Bitmap");
        blmod = b.getParcelable("Bookingdata");
        init_type=b.getString("init_type");
        leftbit = Utils.decodeBase64(imageencoded);
        initView();



        captureImgBtn.setOnClickListener(this);

        if(!photo_taken) {
//            proceed.setText("Capture Bill");
            retake.setVisibility(View.GONE);

        }else{
//            proceed.setText("Proceed");
            retake.setVisibility(View.VISIBLE);
        }
//        back.setOnClickListener(this);




    }
    public void initView(){
        img_retake =        (ImageView)findViewById(R.id.iv_tnkr_strt_retake);
        picture=            (ImageView)findViewById(R.id.iv_tankr_strt_image_clicked);
        picture.setImageBitmap(leftbit);
        captureImgBtn =     (ImageButton)findViewById(R.id.ib_tnkr_strt_capture);
        txt_retake=         (TextView) findViewById(R.id.tv_tankr_strt_retakeTxt);
        calender=           (ImageView)findViewById(R.id.iv_tankr_strt_calender);
        clock=              (ImageView)findViewById(R.id.iv_tankr_strt_clock);
        day=                (TextView) findViewById(R.id.tv_tankr_strt_day);
        date=               (TextView) findViewById(R.id.tv_tankr_strt_date);
        time=               (TextView) findViewById(R.id.tv_tankr_strt_time_value);
        apmm=               (TextView) findViewById(R.id.tv_tankr_strt_time_ampm);
        lat=                (TextView) findViewById(R.id.tv_tankr_strt_lat);
        lon=                (TextView) findViewById(R.id.tv_tankr_strt_lon);
        retake= (LinearLayout)findViewById(R.id.ll_tanker_starting_pic_retake);

        retake.setOnClickListener(this);

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if(resultCode!=0) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageencoded = Utils.encodeTobase64(bitmap);
            picture.setImageBitmap(bitmap);
            retake.setVisibility(View.VISIBLE);
            photo_taken = true;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/




    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.ib_tnkr_strt_capture:
                captureImgBtn.setClickable(false);
                if (init_type!=null){
                i= new Intent(TankerStartingPic.this,EnterOTP.class);
                i.putExtra("Bitmap",imageencoded);
                i.putExtra("Bookingdata",blmod);
                startActivity(i);
                finish();
                }else{
                uploadBitmap();
                }
                /*i = new Intent(this, RequestDetails.class);
                startActivity(i);*/
                break;

           /*case R.id.ll_tanker_starting_pic_retake:
                photo_taken = false;
                retake.setVisibility(View.GONE);
                picture.setImageResource(android.R.color.transparent);
                captureImgBtn.performClick();
                onResume();
                break;*/


        }
    }

    @Override
    public void onBackPressed() {
    photo_taken=false;
        super.onBackPressed();



    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
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







    private void uploadBitmap() {

        String url = URLs.BASE_URL + URLs.BOOKING_START+blmod.getBookingid();
        //url = "http://13.233.54.144:8080/api/user/document";
        //our custom volley request
       /* JSONObject params = new JSONObject();
        try {
            params.put("id", blmod.getBookingid());
            params.put("lat", currentlatlng.latitude);
            params.put("lng",currentlatlng.longitude);
        }catch (JSONException e){
            e.printStackTrace();
        }*/

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,url
                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj!=null){
                                if(obj.getInt("error")==0){
                                    SessionManagement.setOngoingBooking(TankerStartingPic.this,blmod.getBookingid());
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(TankerStartingPic.this,Map1.class);
                                    intent.putExtra("Bookingdata",blmod);

                                    startActivity(intent);
                                    finish();
                                }else{
                                    RequestQueueService.showAlert(obj.getString("code"), TankerStartingPic.this);
                                    captureImgBtn.setClickable(true);
                                 //   requestLayout.setBackgroundResource(R.drawable.straight_corners);
                                }
                            }else{
                                RequestQueueService.showAlert("Error! No data fetched", TankerStartingPic.this);
                                captureImgBtn.setClickable(true);
                                //requestLayout.setBackgroundResource(R.drawable.straight_corners);

                            }
                        } catch (JSONException e) {
                            RequestQueueService.showAlert("Something went wrong", TankerStartingPic.this);
                            e.printStackTrace();
                            captureImgBtn.setClickable(true);
                            //requestLayout.setBackgroundResource(R.drawable.straight_corners);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        captureImgBtn.setClickable(true);
                        //requestLayout.setBackgroundResource(R.drawable.straight_corners);
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
           /* @Override
            protected java.util.Map1<String, String> getParams() throws AuthFailureError {
                java.util.Map1<String, String> params = new HashMap<>();
                return params;
            }*/

            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String, String> params = new HashMap<>();
                //params.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZGE5NTQ0M2QzY2U5NTU3MTRhNWY2MzQiLCJleHAiOjE1ODExODM0MjUsImlhdCI6MTU3ODU5MTQyNX0.U7xvdz6ZIwhqj_gGSx3bSfaxvhKoFQyenGdyd3oopgY");
                params.put("Authorization", "Bearer "+SessionManagement.getUserToken(TankerStartingPic.this));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected java.util.Map<String,DataPart> getByteData() {
                //Map1<String, DataPart> params = new HashMap<>();
                Map<String,DataPart> imageMap = new HashMap<>();
                long imagename = System.currentTimeMillis();
                String imagename1 = "bill";
                DataPart dataPart= new DataPart(imagename1 + ".png", getFileDataFromDrawable(leftbit), "image/png");
                imageMap.put("image",dataPart);
                return imageMap;
            }

            /*@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("lat",String.valueOf( currentlatlng.latitude));
                params.put("lng",String.valueOf(currentlatlng.longitude));
                return params;
            }*/
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

  /*  @Override
    public void onLocationChanged(Location location) {
        currentlatlng = new LatLng(location.getLatitude(), location.getLongitude());

    }*/
}
