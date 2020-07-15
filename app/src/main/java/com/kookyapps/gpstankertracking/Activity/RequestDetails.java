package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.Utils.VolleyMultipartRequest2;
import com.kookyapps.gpstankertracking.fcm.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.CALL_PERMISSION_REQUEST_CODE;
import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

public class RequestDetails extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    TextView bookingid, distancetext, pickup, drop, controllername, contact_no, message, pagetitle, bottomtext;
    ImageView calltous;
    ImageView menunotification;
    ProgressBar progressBar;

    SupportMapFragment mapFragment;
    RelativeLayout maplayout;
    ArrayList<LatLng>finalpath = null;
    static Context context;
    GoogleMap mMap;

    RelativeLayout menuback, bottom, notificationLayout, toolbarNotiCountLayout;
    String init_type, bkngid,tanker_id;
    static String notificationCount;
    BookingListModal blmod;
    ArrayList<String> imagearray;

    String imageencoded, can_accept, can_end, can_start,currentPhotoPath;
    boolean cameraAccepted, callaccepted;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView notificationCountText;
    Button change;

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    static final int REQUEST_TAKE_PHOTO = 3;
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
        progressBar=(ProgressBar) findViewById(R.id.pb_requestDetails_progressbar);
        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        bookingid = (TextView) findViewById(R.id.tv_bookingdetail_bookingid);
        distancetext = (TextView) findViewById(R.id.tv_bookingdetail_distance);
        pickup = (TextView) findViewById(R.id.tv_bookingdetail_pickup);
        drop = (TextView) findViewById(R.id.tv_bookingdetail_drop);
        controllername = (TextView) findViewById(R.id.tv_bookingdetail_drivername);
        contact_no = (TextView) findViewById(R.id.tv_bookingdetail_contact);
        message = (TextView) findViewById(R.id.tv_bookingdetail_message);
        toolbarNotiCountLayout = (RelativeLayout) findViewById(R.id.rl_toolbar_notificationcount);
        notificationLayout = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_notification);
        notificationCountText = (TextView) findViewById(R.id.tv_toolbar_notificationcount);
        calltous = (ImageView) findViewById(R.id.iv_bookingdetail_bookingid_call);
        calltous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = contact_no.getText().toString();

                if (!TextUtils.isEmpty(phoneNumber)) {
                    if (checkCall(Manifest.permission.CALL_PHONE)) {
                        String dial = "tel:" + phoneNumber;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(RequestDetails.this, "Permission Call Phone denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RequestDetails.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (checkCall(Manifest.permission.CALL_PHONE)) {
            calltous.setEnabled(true);
        } else {
            calltous.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        }

        maplayout = (RelativeLayout)findViewById(R.id.rl_requestDetail_map);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fg_requesttDeatils_map);

        if (init_type.equals(Constants.TRIP_INIT)){
            maplayout.setVisibility(View.VISIBLE);
        }else {
            maplayout.setVisibility(View.GONE);
        }


        menuback = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        menuback.setOnClickListener(this);
        menunotification = (ImageView) findViewById(R.id.iv_tb_with_bck_arrow_notification);
        notificationLayout.setOnClickListener(this);
        bottom = (RelativeLayout) findViewById(R.id.rl_result_details_bottomLayout_text);
        bottom.setOnClickListener(this);
        //bottom.setClickable(false);
        bottomtext = (TextView) findViewById(R.id.tv_result_details_bottomlayout_text);


        int noticount = Integer.parseInt(SessionManagement.getNotificationCount(this));
        if(noticount<=0){
            clearNotificationCount();
        }else{
            notificationCountText.setText(String.valueOf(noticount));
            toolbarNotiCountLayout.setVisibility(View.VISIBLE);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(RequestDetails.this));
                    setNotificationCount(count+1,false);
                }else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(RequestDetails.this).equals(Constants.HINDI_LANGUAGE)){
                        setAppLocale(Constants.HINDI_LANGUAGE);
                        finish();
                    }else{
                        setAppLocale(Constants.ENGLISH_LANGUAGE);
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        };

    }



    @Override
        public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;

            case R.id.rl_toolbar_with_back_notification:
                intent = new Intent(RequestDetails.this, Notifications.class);
                startActivity(intent);
                break;
            case R.id.rl_result_details_bottomLayout_text:
                if (init_type.equals(Constants.REQUEST_INIT)) {
                    bookingacceptedapiCalling();
                    bottom.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                } else if (!init_type.equals(Constants.TRIP_INIT)) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (can_start.equals("true")) {
                        if (checkPermission()) {
                            cameraAccepted = true;
                        } else {
                            requestPermission();
                        }
                        if (cameraAccepted) {
                            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (camera_intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        if (blmod.getStatus()==0){
                            Alert("This trip has been cancelled",RequestDetails.this);
                        }else {
                            intent = new Intent(RequestDetails.this, Map1.class);
                            intent.putExtra("Bookingdata", blmod);
                            intent.putExtra("init_type", Constants.BOOKING_INIT);
                            intent.putExtra("booking_id", bkngid);
                            intent.putExtra("tankerBookingId", blmod.getTankerBookingid());
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
                break;
        }
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED;

        //
    }

    private boolean checkCall(String permission){
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
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
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                callaccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if (callaccepted){
                    callaccepted=true;
                }else {
                    callaccepted=false;
                }
                calltous.setEnabled(true);
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (resultCode!=0) {
            progressBar.setVisibility(View.VISIBLE);

            Bundle b = data.getExtras();
            if (resultCode != REQUEST_IMAGE_CAPTURE && b.containsKey("data")) {

                Bitmap bitmap = (Bitmap) b.get("data");

                // bitmap = addStampToImage(bitmap);

                //imageencoded = Utils.encodeTobase64(bitmap);
                if (can_start.equals("true")) {
                    Intent intent = new Intent(this, TankerStartingPic.class);
                    intent.putExtra("Bitmap", bitmap);
                    intent.putExtra("Bookingdata", blmod);
                    startActivity(intent);
                    finish();
                }
            }
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


    private void setTextSizeForWidth(Paint paint, float desiredHeight,
                                     String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
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

    public void Alert(String message, final FragmentActivity context) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Alert!");
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();

                }
            });

            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        public void onFetchStart() {

        }

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
                            bottom.setVisibility(View.VISIBLE);
                            bottom.setClickable(true);
                            progressBar.setVisibility(View.GONE);
                            blmod.setBookingid(data.getString("_id"));
                            String status = data.getString("status");
                            if((status.equals("0") || status.equals("5")||status.equals("6"))){
                                if(SharedPrefUtil.hasKey(RequestDetails.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
                                    SharedPrefUtil.deletePreference(RequestDetails.this,Constants.SHARED_PREF_ONGOING_TAG);
                                if(SharedPrefUtil.hasKey(RequestDetails.this,Constants.SHARED_PREF_TRIP_TAG,Constants.SHARED_ONGOING_TRAVELLED_PATH))
                                    SharedPrefUtil.deletePreference(RequestDetails.this,Constants.SHARED_PREF_TRIP_TAG);
                                if(init_type.equals(Constants.SPLASH_INIT)) {
                                    Intent intent = new Intent(RequestDetails.this, FirstActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    RequestDetails.this.finish();
                                }
                            }

                            blmod.setStatus(data.getInt("status"));
                            //bookingid.setText(blmod.getBookingid());
                            if (data.getString("message").equals("")){
                                message.setText("No message");
                            }else {
                                blmod.setMessage(data.getString("message"));
                                message.setText(blmod.getMessage());
                            }blmod.setPhone_country_code(data.getString("phone_country_code"));
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
                            }else {
                                RequestQueueService.showAlert("Error! No Data in distance Found", RequestDetails.this);
                                progressBar.setVisibility(View.GONE);
                                bottom.setVisibility(View.VISIBLE);
                                bottom.setClickable(true);
                            }JSONObject drop_point = data.getJSONObject("drop_point");
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
                                    }else {
                                        RequestQueueService.showAlert("Error! No Coordinates Found", RequestDetails.this);
                                    }progressBar.setVisibility(View.GONE);
                                    bottom.setVisibility(View.VISIBLE);
                                    bottom.setClickable(true);
                                }else {
                                    RequestQueueService.showAlert("Error! No Data in geomaetry Found", RequestDetails.this);
                                }progressBar.setVisibility(View.GONE);
                                bottom.setVisibility(View.VISIBLE);
                                bottom.setClickable(true);
                            } else {
                                RequestQueueService.showAlert("Error! No Data in drop_point Found", RequestDetails.this);
                                progressBar.setVisibility(View.GONE);
                                bottom.setVisibility(View.VISIBLE);
                                bottom.setClickable(true);
                            }

                            blmod.setTankerBookingid(data.getString("booking_id"));
                            bookingid.setText(blmod.getTankerBookingid());

                            JSONObject pickup_point = data.getJSONObject("pickup_point");
                            { if (pickup_point != null) {
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
                                            RequestQueueService.showAlert("Error! No Coordinates Found", RequestDetails.this);
                                            progressBar.setVisibility(View.GONE);
                                            bottom.setVisibility(View.VISIBLE);
                                            bottom.setClickable(true);
                                        }
                                    } else {
                                        RequestQueueService.showAlert("Error! No Data in geomaetry Found", RequestDetails.this);
                                        progressBar.setVisibility(View.GONE);
                                        bottom.setVisibility(View.VISIBLE);
                                        bottom.setClickable(true);
                                    }
                                } else {
                                    RequestQueueService.showAlert("Error! No Data in pick_point Found", RequestDetails.this);
                                    progressBar.setVisibility(View.GONE);
                                    bottom.setVisibility(View.VISIBLE);
                                    bottom.setClickable(true);
                                }
                            }
                            if (init_type.equals(Constants.REQUEST_INIT)) {
                                pagetitle.setText(getString(R.string.request_details));
                                bottomtext.setText(getString(R.string.accept));
                            }else if (init_type.equals(Constants.TRIP_INIT)){
                                bottom.setVisibility(View.GONE);
                                pagetitle.setText(getString(R.string.trip_details));
                            }else{
                                if (can_start.equals("true")){
                                    pagetitle.setText(getString(R.string.booking_details));
                                    bottomtext.setText(getString(R.string.start));
                                }else if(can_end.equals("true")){
                                    pagetitle.setText(getString(R.string.booking_details));
                                    bottomtext.setText(getString(R.string.view_map));
                                }else {
                                    bottom.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            RequestQueueService.showAlert("Error! No Data Found", RequestDetails.this);
                            progressBar.setVisibility(View.GONE);
                            bottom.setVisibility(View.VISIBLE);
                            bottom.setClickable(true);
                        }

                        if(data.has("snapped_path")){
                            String snapstring = data.getString("snapped_path");
                            JSONObject snap = new JSONObject(snapstring);
                            JSONArray snaparray = snap.getJSONArray("snappedPoints");
                            if(finalpath == null)
                                finalpath = new ArrayList<>();
                            for(int i=0;i<snaparray.length();i++){
                                JSONObject point = snaparray.getJSONObject(i);
                                JSONObject location = point.getJSONObject("location");
                                double lat = Double.parseDouble(location.getString("latitude"));
                                double longi = Double.parseDouble(location.getString("longitude"));
                                LatLng temp = new LatLng(lat,longi);
                                finalpath.add(temp);
                            }
                            mapFragment.getMapAsync(RequestDetails.this);
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
    public void setNotificationCount(int count,boolean isStarted){
        notificationCount = SessionManagement.getNotificationCount(RequestDetails.this);
        if(Integer.parseInt(notificationCount)!=count) {
            notificationCount = String.valueOf(count);
            if (count <= 0) {
                clearNotificationCount();
            } else if (count < 100) {
                notificationCountText.setText(String.valueOf(count));
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            }
            SharedPrefUtil.setPreferences(RequestDetails.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
            boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if(b2)
                SharedPrefUtil.setPreferences(RequestDetails.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY,"no");
        }
    }
    public void newNotification(){
        Log.i("newNotification","Notification");
        int count = Integer.parseInt(SharedPrefUtil.getStringPreferences(RequestDetails.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY));
        setNotificationCount(count+1,false);
    }
    public void clearNotificationCount(){
        notificationCountText.setText("");
        toolbarNotiCountLayout.setVisibility(View.GONE);
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
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.PUSH_NOTIFICATION));
            // clear the notification area when the app is opened
            int sharedCount = Integer.parseInt(SharedPrefUtil.getStringPreferences(this,
                    Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_COUNT_KEY));
            int viewCount = Integer.parseInt(notificationCountText.getText().toString());
            boolean b1 = sharedCount != viewCount;
            boolean b2 = SharedPrefUtil.getStringPreferences(this, Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if (b2) {
                newNotification();
            } else if (b1) {
                if (sharedCount < 100 && sharedCount > 0) {
                    notificationCountText.setText(String.valueOf(sharedCount));
                    toolbarNotiCountLayout.setVisibility(View.VISIBLE);
                } else {
                    notificationCountText.setText("99+");
                    toolbarNotiCountLayout.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }


   @Override
   public void onBackPressed() {
       if (init_type.equals(Constants.SPLASH_INIT)){
           AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetails.this);
           builder.setTitle("Alert !");
           builder.setMessage("Do you want to exit ?");
           builder.setCancelable(false);
           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which)
               { finish();
               }
           });
           builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which)
               {dialog.cancel(); }
           });
           AlertDialog alertDialog = builder.create();
           alertDialog.show();
       }else {
           super.onBackPressed();
       }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        maplayout.setVisibility(View.VISIBLE);
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
}