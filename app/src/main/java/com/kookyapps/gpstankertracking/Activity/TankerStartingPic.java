package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.kookyapps.gpstankertracking.app.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TankerStartingPic extends AppCompatActivity implements View.OnClickListener {
    ImageView img_retake,picture ,calender,clock;
    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day;
    RelativeLayout latLongLayout,dateAndTime,imgInfoLayout;
    LinearLayout retake;
    String imageencoded ,bkngid,init_type;
    boolean photo_taken,cameraAccepted=false,permissionGranted = false;
    BookingListModal blmod;
    Bitmap leftbit;
    private LocationManager locationManager;
    private LatLng currentlatlng=null;
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static final int CALL_PERMISSION_REQUEST_CODE = 300;
    ArrayList<String> allpermissionsrequired;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_starting_pic);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        leftbit =(Bitmap) b.get("Bitmap");
        //leftbit=addStampToImage(leftbit);
        blmod = b.getParcelable("Bookingdata");
        init_type=b.getString("init_type");
        /*if(b.containsKey("snapped_path")) {
            finalsnap = b.getString("snapped_path");
            snappedDistance = b.getString("snapped_distance");
        }*/
        gpsTracker = new GPSTracker(this);
        //leftbit = Utils.decodeBase64(imageencoded);
       // addPermission();
        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
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


        /*Date timenow = Calendar.getInstance().getTime();*/
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);

        String pattern = " HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String mytime = simpleDateFormat.format(new Date());
        time.setText(mytime);



//        if (gpsTracker.getIsGPSTrackingEnabled()){
//
//            String stringLatitude = String.valueOf(gpsTracker.latitude);
//            lat.setText(stringLatitude);
//
//            String stringLongitude = String.valueOf(gpsTracker.longitude);
//            lon.setText(stringLongitude);
//
//        }
//        else
//        {
//            // can't get location
//            // GPS or Network is not enabled
//            // Ask user to enable GPS/network in settings
//            gpsTracker.showSettingsAlert();
//        }

    }
    public void initView(){
        img_retake =        (ImageView)findViewById(R.id.iv_tnkr_strt_retake);
        picture=            (ImageView)findViewById(R.id.iv_tankr_strt_image_clicked);
        picture.setImageBitmap(leftbit);
        captureImgBtn =     (ImageButton)findViewById(R.id.ib_tnkr_strt_capture);
        txt_retake=         (TextView) findViewById(R.id.tv_tankr_strt_retakeTxt);
        calender=           (ImageView)findViewById(R.id.iv_tankr_strt_calender);
        clock=              (ImageView)findViewById(R.id.iv_tankr_strt_clock);
        date=               (TextView) findViewById(R.id.tv_tankr_strt_date);
        time=               (TextView) findViewById(R.id.tv_tankr_strt_time_value);
        apmm=               (TextView) findViewById(R.id.tv_tankr_strt_time_ampm);
        lat=                (TextView) findViewById(R.id.tv_tankr_strt_lat);
        lon=                (TextView) findViewById(R.id.tv_tankr_strt_lon);
        dateAndTime=        (RelativeLayout)findViewById(R.id.rl_tankr_strt_dateTime);
        latLongLayout=      (RelativeLayout)findViewById(R.id.rl_tankr_strt_latLon);
        retake=             (LinearLayout)findViewById(R.id.ll_tanker_starting_pic_retake);
        imgInfoLayout=      (RelativeLayout)findViewById(R.id.image_with_infoLayout);
        retake.setOnClickListener(this);


        if (gpsTracker.getIsGPSTrackingEnabled()){


            String stringLatitude = String.valueOf(gpsTracker.latitude);
            lat.setText(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            lon.setText(stringLongitude);

        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }

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



    private Bitmap addStampToImage(Bitmap originalBitmap) {

        int extraHeight = (int) (originalBitmap.getHeight() * 0.15);

        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                originalBitmap.getHeight() , Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;

        String text = "Friday 3 march 2020";
        String time = "10pm";
        String lat = "27.00000";
        String lon = "72.00000";
        drawString(originalBitmap,canvas,0,originalBitmap.getHeight() - 30,text);
        drawString(originalBitmap,canvas,0,originalBitmap.getHeight() - 15,time);

        return newBitmap;
    }


    private void drawString( Bitmap bitmap , Canvas canvas ,int x , int y,String text ){
        //Paint pText = new Paint();
        Paint pText = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        pText.setColor(Color.WHITE);

        pText.setAntiAlias(true);

        setTextSizeForWidth(pText,(int) (bitmap.getWidth()),text);




        Rect bounds = new Rect();
        pText.getTextBounds(text, 0, text.length(), bounds);


        Rect textHeightWidth = new Rect();
        pText.getTextBounds(text, 0, text.length(), textHeightWidth);

        canvas.drawText(text, 5, y, pText);

    }

    private void setTextSizeForWidth(Paint paint, float desiredHeight,
                                     String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize =10f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize ;

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }






    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.ib_tnkr_strt_capture:
                leftbit = captureScreenShot();

                //store(leftbit,blmod.getBookingid()+ ".png");
                imageencoded=Utils.encodeTobase64(leftbit);
                SharedPrefUtil.setPreferences(TankerStartingPic.this,Constants.SHARED_PREF_IMAGE_TAG,Constants.SHARED_END_IMAGE_KEY,imageencoded);
                captureImgBtn.setClickable(false);
                if (init_type!=null){
                i= new Intent(TankerStartingPic.this,EnterOTP.class);

              //  i.putExtra("Bitmap",imageencoded);
                i.putExtra("Bookingdata",blmod);
                /*if(finalsnap!=null) {
                    i.putExtra("snapped_path", finalsnap);
                    i.putExtra("snapped_distance", snappedDistance);
                }*/

                startActivity(i);
                finish();
                }
                else{
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

    private Bitmap captureScreenShot() {
        imgInfoLayout.setDrawingCacheEnabled(true);
        imgInfoLayout.buildDrawingCache();
        Bitmap bm = imgInfoLayout.getDrawingCache();
        return bm;
    }

    @Override
    public void onBackPressed() {
    photo_taken=false;
        super.onBackPressed();




    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
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
            case Constants.MULTIPLE_PERMISSIONS_REQUEST_CODE:
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        permissionGranted = true;
                        if(!(grantResults[i]==PackageManager.PERMISSION_GRANTED)){
                            permissionGranted = false;
                            break;
                        }
                    }
                    if(permissionGranted){
                        checkLocation();
                        if (gpsTracker.getIsGPSTrackingEnabled()){
                            String stringLatitude = String.valueOf(gpsTracker.latitude);
                            lat.setText(stringLatitude);
                            String stringLongitude = String.valueOf(gpsTracker.longitude);
                            lon.setText(stringLongitude);
                        }
                        else
                        {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gpsTracker.showSettingsAlert();
                        }
                       // buildGoogleApiClient();
                        //createPickUpLocations();
                    }else{
                        checkAndRequestPermissions(TankerStartingPic.this,allpermissionsrequired);
                    }
                }
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
                                    //requestLayout.setBackgroundResource(R.drawable.straight_corners);
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



    public void addPermission (){
        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        checkAndRequestPermissions(this,allpermissionsrequired);
    }







    public void checkAndRequestPermissions(Activity activity, ArrayList<String> permissions) {
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }else{
            permissionGranted = true;
            checkLocation();
            gpsTracker.getLocation();
            if (gpsTracker.getIsGPSTrackingEnabled()){
                String stringLatitude = String.valueOf(gpsTracker.latitude);
                lat.setText(stringLatitude);
                String stringLongitude = String.valueOf(gpsTracker.longitude);
                lon.setText(stringLongitude);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }
        }
    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


}
