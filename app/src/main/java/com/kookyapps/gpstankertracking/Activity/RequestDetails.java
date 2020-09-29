package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RequestDetails extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    TextView bookingid, distanceTitle, distancetext, pickup, drop, controllername, contact_no, message, pagetitle, bottomtext,tv_showmap;
    ImageView calltous,iv_showmap;
    ProgressBar progressBar;
    ScrollView scrollview;
    SupportMapFragment mapFragment;
    RelativeLayout maplayout,mapshowlayout;
    ArrayList<LatLng>finalpath = null;
    GoogleMap mMap;
    RelativeLayout menuback, bottom;
    String init_type, bkngid;
    BookingListModal blmod;
    String dial_no = null;
    String can_accept, can_end, can_start,currentPhotoPath;
    boolean cameraAccepted, callaccepted;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_TAKE_PHOTO = 3;
    String location_permission = Manifest.permission.ACCESS_FINE_LOCATION;
    String camera_permission = Manifest.permission.CAMERA;
    String call_permission = Manifest.permission.CALL_PHONE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        initViews();
        bookingByIdApiCalling();
    }
    public void initViews() {
        Bundle b = getIntent().getExtras();
        init_type = b.getString("init_type");
        bkngid = b.getString("booking_id");
        progressBar=(ProgressBar) findViewById(R.id.pb_requestDetails_progressbar);
        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        bookingid = (TextView) findViewById(R.id.tv_bookingdetail_bookingid);
        distancetext = (TextView) findViewById(R.id.tv_bookingdetail_distance);
        distanceTitle =(TextView)findViewById(R.id. tv_bookingdetail_distance_title);
        pickup = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drop = (TextView) findViewById(R.id.tv_bookingdetail_drop);
        controllername = (TextView) findViewById(R.id.tv_bookingdetail_drivername);
        contact_no = (TextView) findViewById(R.id.tv_bookingdetail_contact);
        message = (TextView) findViewById(R.id.tv_bookingdetail_message);
        mapshowlayout = (RelativeLayout)findViewById(R.id.rl_request_detail_showmap);
        tv_showmap = (TextView)findViewById(R.id.tv_request_detail_showmap);
        iv_showmap = (ImageView)findViewById(R.id.iv_request_detail_showmap);
        mapshowlayout.setOnClickListener(this);
        scrollview = (ScrollView)findViewById(R.id.sv_request_details);
        calltous = (ImageView) findViewById(R.id.iv_bookingdetail_bookingid_call);
        calltous.setOnClickListener(this);
        maplayout = (RelativeLayout)findViewById(R.id.rl_requestDetail_map);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fg_requesttDeatils_map);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        menuback.setOnClickListener(this);
        bottom = (RelativeLayout) findViewById(R.id.rl_result_details_bottomLayout);
        bottom.setOnClickListener(this);
        bottomtext = (TextView) findViewById(R.id.tv_result_details_bottomlayout_text);
        if(init_type.equals("notification")){
            readNotificationApiCall(b.getString("notification_id"));
            NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            nm.cancelAll();
        }
        hideMapBar();
    }
    public void showMapBar(){mapshowlayout.setVisibility(View.VISIBLE);}
    public void hideMapBar(){mapshowlayout.setVisibility(View.GONE);}
    @Override
        public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;
            case R.id.rl_result_details_bottomLayout:
                bottom.setClickable(false);
                if (blmod.getStatus()==1&&can_accept.equals("true")) {
                    bookingacceptedapiCalling();
                    progressBar.setVisibility(View.VISIBLE);
                } else if (blmod.getStatus()==2) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (can_start.equals("true")) {
                        checkRequestCameraPermission(RequestDetails.this);
                    }else{
                        Toast.makeText(RequestDetails.this, "Booking can't be started", Toast.LENGTH_SHORT).show();
                        bottom.setClickable(true);
                    }
                }
                else if(blmod.getStatus()==3){
                    progressBar.setVisibility(View.VISIBLE);
                    intent = new Intent(RequestDetails.this, Map1.class);
                    intent.putExtra("Bookingdata", blmod);
                    intent.putExtra("init_type", Constants.BOOKING_INIT);
                    intent.putExtra("booking_id", bkngid);
                    intent.putExtra("tankerBookingId", blmod.getTankerBookingid());
                    progressBar.setVisibility(View.GONE);
                    bottom.setClickable(true);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.rl_request_detail_showmap:
                if(maplayout.getVisibility()==View.VISIBLE){
                    maplayout.setVisibility(View.GONE);
                    scrollview.setVisibility(View.VISIBLE);
                    tv_showmap.setText("Show Map");
                    iv_showmap.setImageResource(R.drawable.ic_plus);
                }else{
                    scrollview.setVisibility(View.GONE);
                    maplayout.setVisibility(View.VISIBLE);
                    tv_showmap.setText("Hide Map");
                    iv_showmap.setImageResource(R.drawable.ic_minus);
                }
                break;
            case R.id.iv_bookingdetail_bookingid_call:
                calltous.setClickable(false);
                String phoneNumber = contact_no.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    dial_no = "tel:" + phoneNumber;
                    checkRequestCallPermission(RequestDetails.this);
                } else {
                    Toast.makeText(RequestDetails.this, "No Phone Number Available", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkRequestCameraPermission(final Activity activity){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),camera_permission)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,camera_permission)){
                new AlertDialog.Builder(activity)
                        .setTitle("Camera Permission")
                        .setMessage("We need to capture the tanker Image.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,new String[]{camera_permission},Constants.CAMERA_PERMISSION_REQUEST);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bottom.setClickable(true);
                                if(progressBar.getVisibility()==View.VISIBLE)
                                    progressBar.setVisibility(View.GONE);
                                noCameraLocationAlert(RequestDetails.this,camera_permission);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(activity,new String[]{camera_permission},Constants.CAMERA_PERMISSION_REQUEST);
            }
        }else{
            cameraAccepted = true;
            checkRequestLocationPermission(RequestDetails.this);
        }
    }

    public void checkRequestCallPermission(final Activity activity){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),call_permission)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,call_permission)){
                new AlertDialog.Builder(activity)
                        .setTitle("Call Permission")
                        .setMessage("We need call permission to place a call.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,new String[]{call_permission},Constants.CALL_PERMISSION_REQUEST);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                calltous.setClickable(true);
                                noCallAlert(RequestDetails.this);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(activity,new String[]{call_permission},Constants.CALL_PERMISSION_REQUEST);
            }
        }else{
            callaccepted = true;
            calltous.setClickable(true);
            if(dial_no!=null)
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial_no)));
        }
    }

    public void checkRequestLocationPermission(final Activity activity){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),location_permission)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,location_permission)){
                new AlertDialog.Builder(activity)
                        .setTitle("Location Permission")
                        .setMessage("Location Permission is required for showing your location on  the map.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,new String[]{location_permission},Constants.LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bottom.setClickable(true);
                                if(progressBar.getVisibility()==View.VISIBLE)
                                    progressBar.setVisibility(View.GONE);
                                noCameraLocationAlert(RequestDetails.this,location_permission);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(activity,new String[]{location_permission},Constants.LOCATION_PERMISSION_REQUEST);
            }
        }else{
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (camera_intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void noCameraLocationAlert(final Activity activity,String permission){
        String msg = "";
        if(permission.equals(camera_permission)){
            msg = "camera permission";
            cameraAccepted = false;
        }else if(permission.equals(location_permission)){
            msg = "location permission";
        }
        new AlertDialog.Builder(activity)
                .setTitle("Permission Not Granted")
                .setMessage("Sorry, we can not proceed without "+msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public void noCallAlert(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Permission Not Granted")
                .setMessage("Sorry, call could not be placed.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.CAMERA_PERMISSION_REQUEST:
                if(grantResults.length>0){
                    cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        checkRequestLocationPermission(RequestDetails.this);
                    }else{
                        bottom.setClickable(true);
                        if(progressBar.getVisibility()==View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                        noCameraLocationAlert(RequestDetails.this,camera_permission);
                    }
                }
                break;
            case Constants.CALL_PERMISSION_REQUEST:
                callaccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                calltous.setClickable(true);
                if (callaccepted){
                    if(dial_no!=null)
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial_no)));
                }else {
                    noCallAlert(RequestDetails.this);
                }
                break;
            case Constants.LOCATION_PERMISSION_REQUEST:
                if(progressBar.getVisibility()==View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (camera_intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
                    }
                }else{
                    bottom.setClickable(true);
                    noCameraLocationAlert(RequestDetails.this,camera_permission);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        switch(requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK && data.hasExtra("data")){
                    progressBar.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (can_start.equals("true")) {
                        Intent intent = new Intent(this, TankerStartingPic.class);
                        intent.putExtra("Bitmap", bitmap);
                        intent.putExtra("init_type",Constants.TRIP_START_IMG);
                        intent.putExtra("Bookingdata", blmod);
                        intent.putExtra("tankerBookingId", blmod.getTankerBookingid());
                        progressBar.setVisibility(View.GONE);
                        bottom.setClickable(true);
                        startActivity(intent);
                        //finish();
                    }
                }else{
                    Toast.makeText(RequestDetails.this,"Image not captured successfully",Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    private Bitmap addStampToImage(Bitmap originalBitmap) {
        int extraHeight = (int) (originalBitmap.getHeight() * 0.15);
        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                originalBitmap.getHeight() , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        String text = "Friday 3 march 2020";
        //Paint pText = new Paint();
        Paint pText = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        pText.setColor(Color.WHITE);
        pText.setTextSize(12);
        pText.setAntiAlias(true);
       // setTextSizeForWidth(pText,(int) (originalBitmap.getHeight() * 0.04),text);
        Rect bounds = new Rect();
        pText.getTextBounds(text, 0, text.length(), bounds);
        Rect textHeightWidth = new Rect();
        pText.getTextBounds(text, 0, text.length(), textHeightWidth);
        canvas.drawText(text, (canvas.getWidth() / 4) - (textHeightWidth.width() / 2),
                originalBitmap.getHeight()  - textHeightWidth.height(),
                pText);
       //imageView.setImageBitmap(newBitmap);
        return newBitmap;
    }


    private void setTextSizeForWidth(Paint paint, float desiredHeight, String text) {
        final float testTextSize = 48f;
        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredHeight / bounds.height();
        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
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
                        progressBar.setVisibility(View.GONE);
                        bottom.setVisibility(View.VISIBLE);
                        bottom.setClickable(true);
                        Intent intent = new Intent(getApplicationContext() , FirstActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("curretTab",1);
                        startActivity(intent);
                        Toast.makeText(RequestDetails.this, getString(R.string.request_accepted), Toast.LENGTH_SHORT).show();
                    } else {
                        RequestQueueService.showAlert("Error! Data is null",RequestDetails.this);
                        progressBar.setVisibility(View.GONE);
                        bottom.setVisibility(View.VISIBLE);
                        bottom.setClickable(true);
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",RequestDetails.this);
                progressBar.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                bottom.setClickable(true);
                e.printStackTrace();
            }
        }
        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.showAlert(msg,RequestDetails.this);
            progressBar.setVisibility(View.GONE);
            bottom.setVisibility(View.VISIBLE);
            bottom.setClickable(true);
        }
        @Override
        public void onFetchStart() { }
    };
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
            Log.d("RequestDetails:",mydata.toString());
            try {
                if (mydata != null) {
                    if (mydata.getInt("error") == 0) {
                        ArrayList<BookingListModal> bookingList = new ArrayList<>();
                        JSONObject data = mydata.getJSONObject("data");
                        blmod = new BookingListModal();
                        if (data != null) {
                            blmod.setBookingid(data.getString("_id"));
                            String status = data.getString("status");
                            blmod.setStatus(data.getInt("status"));
                            if(data.has("path"))
                                blmod.setPath(data.getString("path"));
                            if (init_type.equals(Constants.REQUEST_INIT)) {
                                pagetitle.setText(getString(R.string.request_details));
                            } else if (init_type.equals(Constants.COMPLETED_TRIP)) {
                                pagetitle.setText(getString(R.string.trip_details));
                            } else if(init_type.equals(Constants.COMPLETED_TRIP)) {
                                pagetitle.setText(getString(R.string.cancel_trips));
                            }else {
                                pagetitle.setText(getString(R.string.booking_details));
                            }
                            if (data.getString("message").equals("")) {
                                message.setText("No message");
                            } else {
                                blmod.setMessage(data.getString("message"));
                                message.setText(blmod.getMessage());
                            }
                            blmod.setPhone_country_code(data.getString("phone_country_code"));
                            blmod.setPhone(data.getString("phone"));
                            contact_no.setText("+" + blmod.getPhone_country_code() + blmod.getPhone());
                            blmod.setController_name(data.getString("controller_name"));
                            controllername.setText(blmod.getController_name());
                            blmod.setCan_accept(data.getString("can_accept"));
                            can_accept = String.valueOf(data.getBoolean("can_accept"));
                            blmod.setCan_start(data.getString("can_start"));
                            can_start = String.valueOf(data.getBoolean("can_start"));
                            blmod.setCan_end(data.getString("can_end"));
                            can_end = String.valueOf(data.getBoolean("can_end"));
                            if (status.equals("0")) {
                                if (SharedPrefUtil.hasKey(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID))
                                    SharedPrefUtil.deletePreference(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG);
                                if (init_type.equals(Constants.SPLASH_INIT)) {
                                    Intent intent = new Intent(RequestDetails.this, FirstActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    RequestDetails.this.finish();
                                }
                                bottom.setVisibility(View.GONE);
                            } else if (status.equals("1") && can_accept.equals("true")) {
                                bottomtext.setText(getString(R.string.accept));
                                bottom.setVisibility(View.VISIBLE);
                            } else if (status.equals("2") && can_start.equals("true")) {
                                bottomtext.setText(getString(R.string.start));
                                bottom.setVisibility(View.VISIBLE);
                            } else if (status.equals("3")) {
                                bottomtext.setText(getString(R.string.view_map));
                                bottom.setVisibility(View.VISIBLE);
                                if (!SharedPrefUtil.hasKey(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID)) {
                                    SharedPrefUtil.setPreferences(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID, blmod.getBookingid());
                                    SharedPrefUtil.setPreferences(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_DRIVER_ID, SessionManagement.getUserId(RequestDetails.this));
                                }
                            } else if (status.equals("4")) {
                                bottom.setVisibility(View.GONE);
                            } else if (status.equals("5")) {
                                if (SharedPrefUtil.hasKey(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID))
                                    SharedPrefUtil.deletePreference(RequestDetails.this, Constants.SHARED_PREF_ONGOING_TAG);
                                if (data.has("snapped_path")) {
                                    String snapstring = data.getString("snapped_path");
                                    JSONObject snap = new JSONObject(snapstring);
                                    JSONArray snaparray;
                                    if (snap.has("snappedPoints")) {
                                        //maplayout.setVisibility(View.VISIBLE);
                                        snaparray = snap.getJSONArray("snappedPoints");
                                        if (finalpath == null)
                                            finalpath = new ArrayList<>();
                                        for (int i = 0; i < snaparray.length(); i++) {
                                            JSONObject point = snaparray.getJSONObject(i);
                                            JSONObject location = point.getJSONObject("location");
                                            double lat = Double.parseDouble(location.getString("latitude"));
                                            double longi = Double.parseDouble(location.getString("longitude"));
                                            LatLng temp = new LatLng(lat, longi);
                                            finalpath.add(temp);
                                        }
                                        mapFragment.getMapAsync(RequestDetails.this);
                                    }
                                }
                                if (init_type.equals(Constants.SPLASH_INIT)) {
                                    Intent intent = new Intent(RequestDetails.this, FirstActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    RequestDetails.this.finish();
                                }
                                bottom.setVisibility(View.GONE);
                            }
                            JSONObject distance = data.getJSONObject("distance");
                            if (distance != null) {
                                distance.getString("value");
                                blmod.setDistance(distance.getString("text"));
                                distancetext.setText(blmod.getDistance());
                            } else {
                                Toast.makeText(RequestDetails.this,"No distance value",Toast.LENGTH_LONG).show();
                            }
                            JSONObject drop_point = data.getJSONObject("drop_point");
                            if (drop_point != null) {
                                drop_point.getString("location");
                                blmod.setToaddress(drop_point.getString("address"));
                                drop.setText(blmod.getToaddress());
                                blmod.setGeofence_in_meter(drop_point.getString("geofence_in_meter"));
                                JSONObject geomaetry = drop_point.getJSONObject("geometry");
                                if (geomaetry != null) {
                                    geomaetry.getString("type");
                                    JSONArray coordinates = geomaetry.getJSONArray("coordinates");
                                    if (coordinates != null) {
                                        blmod.setTologitude(coordinates.getString(0)); // lng
                                        blmod.setTolatitude(coordinates.getString(1)); //lat
                                    } else {
                                        Toast.makeText(RequestDetails.this,"No drop point",Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(RequestDetails.this,"No drop point",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(RequestDetails.this,"No drop point",Toast.LENGTH_LONG).show();
                            }
                            blmod.setTankerBookingid(data.getString("booking_id"));
                            bookingid.setText(blmod.getTankerBookingid());
                            JSONObject pickup_point = data.getJSONObject("pickup_point");
                            if (pickup_point != null) {
                                blmod.setFromlocation(pickup_point.getString("location"));
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
                                        Toast.makeText(RequestDetails.this,"No pickup point",Toast.LENGTH_LONG).show();
                                        //progressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    Toast.makeText(RequestDetails.this,"No pickup point",Toast.LENGTH_LONG).show();
                                    //progressBar.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(RequestDetails.this,"No pickup point",Toast.LENGTH_LONG).show();
                                //RequestQueueService.showAlert("Error! No Data in pick_point Found", RequestDetails.this);
                                //progressBar.setVisibility(View.GONE);
                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
                            RequestQueueService.showAlert("Error! No Data Found", RequestDetails.this);
                            progressBar.setVisibility(View.GONE);
                            bottom.setVisibility(View.VISIBLE);
                            bottom.setClickable(true);
                        }

                    }
                    else {
                        RequestQueueService.showAlert("Error! Data is null",RequestDetails.this);
                        progressBar.setVisibility(View.GONE);
                        bottom.setVisibility(View.VISIBLE);
                        bottom.setClickable(true);
                    }
                }
            } catch (JSONException e) {
                RequestQueueService.showAlert("Error! No Data Found",RequestDetails.this);
                progressBar.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                bottom.setClickable(true);
                e.printStackTrace();
            }

        }

        @Override
        public void onFetchFailure(String msg) {
            try {
                JSONObject er = new JSONObject(msg);
                String code = er.getString("code");
                if(code.equals("not_found")){
                    //SessionManagement.removeOngoingBooking(RequestDetails.this);
                    SharedPrefUtil.deletePreference(RequestDetails.this,Constants.SHARED_PREF_ONGOING_TAG);
                    Intent intent = new Intent(RequestDetails.this,FirstActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    RequestDetails.this.finish();

                }else {
                    RequestQueueService.showAlert(msg, RequestDetails.this);
                    progressBar.setVisibility(View.GONE);
                    bottom.setVisibility(View.VISIBLE);
                    bottom.setClickable(true);
                }
            }catch (Exception e){
                e.printStackTrace();
                RequestQueueService.showAlert(msg, RequestDetails.this);
                progressBar.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                bottom.setClickable(true);
            }
        }

        @Override
        public void onFetchStart() {

        }

    };

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


   @Override
   public void onBackPressed() {
       if(SharedPrefUtil.hasKey(this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID)){
           RequestQueueService.showAlert("", "Can not close in middle of trip", this);
       }else {
           if (init_type.equals(Constants.SPLASH_INIT)) {
               AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetails.this);
               builder.setTitle("Alert !");
               builder.setMessage("Do you want to exit ?");
               builder.setCancelable(false);
               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       finish();
                   }
               });
               builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               AlertDialog alertDialog = builder.create();
               alertDialog.show();
           } else {
               Intent intent = new Intent(this, FirstActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
               finish();
           }
       }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMapBar();
        //maplayout.setVisibility(View.VISIBLE);
        PolylineOptions op = new PolylineOptions();
        op.addAll(finalpath);
        op.width(30);
        op.color(ContextCompat.getColor(RequestDetails.this,R.color.greenLight));
        mMap.addPolyline(op);
        LatLng pickupLatLng = finalpath.get(0);
        LatLng dropLatLng = finalpath.get(finalpath.size()-1);
        MarkerOptions pickupop,dropop,currentop;
        pickupop = new MarkerOptions()
                .position(pickupLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));
        dropop = new MarkerOptions()
                .position(dropLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));
        mMap.addMarker(pickupop);
        mMap.addMarker(dropop);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 18));
    }

    public void readNotificationApiCall(String notificationId){
        try {
            POSTAPIRequest getapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.READ_NOTIFICATIONS+notificationId;
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:",token);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(RequestDetails.this,readListener,url,headparam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    FetchDataListener readListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data != null) {
                    if (data.getInt("error") == 0) {
                        String count = data.getString("unread_count");
                        SessionManagement.setNotificationCount(RequestDetails.this,count);
                        SharedPrefUtil.setPreferences(RequestDetails.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,count);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {Log.e("Read Error",msg);}

        @Override
        public void onFetchStart() {

        }
    };
}