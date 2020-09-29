package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Services.TankerLocationCallback;
import com.kookyapps.gpstankertracking.Services.TankerLocationService;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class TankerStartingPic extends AppCompatActivity implements View.OnClickListener, TankerLocationCallback {
    ImageView img_retake,picture ,calender,clock;
    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day,fetchinglocation;
    RelativeLayout latLongLayout,dateAndTime,imgInfoLayout,progresslayout;
    LinearLayout retake,datalayout;
    String imageencoded ,bkngid,init_type,tankerbookingid;
    boolean photo_taken,cameraAccepted=false,permissionGranted = false;
    BookingListModal blmod;
    boolean tripStarted = false;
    Bitmap leftbit;
    ProgressBar progressBar;
    private LocationManager locationManager;
    //public static final int PERMISSION_REQUEST_CODE = 200;
    ArrayList<String> allpermissionsrequired;
    private final String backgroundpermission= Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    private TankerLocationService mService = null;
    private boolean mBound = false;
    private final int LOC_REQUEST = 10101;
    private final int BACKGROUND_LOCATION_REQUEST_CODE= 12010;
    private Location currentLocation=null;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TankerLocationService.LocalBinder binder = (TankerLocationService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
            mService.setServiceCallback(TankerStartingPic.this);
            currentLocation = mService.getCurrentLocation();
            if(currentLocation!=null) {
                hideFetchingLocation();
                String lati = String.format("%.5f", currentLocation.getLatitude());
                String longi = String.format("%.5f", currentLocation.getLongitude());
                lat.setText(lati);
                lon.setText(longi);
            }else{
                mService.requestCurrent();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService.setServiceCallback(null);
            mService = null;
            mBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_starting_pic);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        leftbit =(Bitmap) b.get("Bitmap");
        blmod = b.getParcelable("Bookingdata");
        if(i.hasExtra("init_type"))
            init_type=b.getString("init_type");
        tankerbookingid=b.getString("tankerBookingId");
        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        initView();
    }
    public void initView(){
        img_retake =        (ImageView)findViewById(R.id.iv_tnkr_strt_retake);
        picture=            (ImageView)findViewById(R.id.iv_tankr_strt_image_clicked);
        picture.setImageBitmap(leftbit);
        captureImgBtn =     (ImageButton)findViewById(R.id.ib_tnkr_strt_capture);
        captureImgBtn.setOnClickListener(this);
        captureImgBtn.setClickable(false);
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
        progressBar=        (ProgressBar)findViewById(R.id.tanker_starting_progressbar);
        progresslayout=     (RelativeLayout)findViewById(R.id.rl_tanker_starting_progress);
        fetchinglocation =  (TextView)findViewById(R.id.tv_tanker_starting_progress);
        if(!photo_taken) {
//            proceed.setText("Capture Bill");
            retake.setVisibility(View.GONE);
        }else{
//            proceed.setText("Proceed");
            retake.setVisibility(View.VISIBLE);
        }
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);
        String pattern = "HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String mytime = simpleDateFormat.format(new Date());
        time.setText(mytime);
        showFetchingLocation();
        retake.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //checkAndRequestPermissions(TankerStartingPic.this,allpermissionsrequired);
        if(isLocationEnabled()){
            if(!mBound)
                bindService(new Intent(TankerStartingPic.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
        }else{
            showAlert();
        }
    }
    @Override
    protected void onStop() {
        if(mBound){
            if(!(((Activity)TankerStartingPic.this).isFinishing()))
                mService.setServiceCallback(null);
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        switch (requestCode) {
            case LOC_REQUEST:
                if(isLocationEnabled()){
                    if(!mBound)
                        bindService(new Intent(TankerStartingPic.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
                }else{
                    showAlert();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()) {
            case R.id.ib_tnkr_strt_capture:
                showProgress();
                captureImgBtn.setClickable(false);
                leftbit = captureScreenShot();
                imageencoded=Utils.encodeTobase64(leftbit);
                SharedPrefUtil.setPreferences(TankerStartingPic.this,Constants.SHARED_PREF_IMAGE_TAG,Constants.SHARED_END_IMAGE_KEY,imageencoded);
                if (init_type!=null){
                    i= new Intent(TankerStartingPic.this,EnterOTP.class);
                    i.putExtra("Bookingdata",blmod);
                    hideProgress();
                    startActivity(i);
                    finish();
                } else{
                    showProgress();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        checkBackgroundLocation(TankerStartingPic.this);
                    else
                        uploadBitmap();
                }
                break;
            case R.id.ll_tanker_starting_pic_retake:
                picture.setImageResource(android.R.color.transparent);
                onBackPressed();
                break;
        }
    }
   private void checkBackgroundLocation(Context context) {
       if (ContextCompat.checkSelfPermission(context, backgroundpermission) == PERMISSION_GRANTED) {
           uploadBitmap();
       } else {
           if (ActivityCompat.shouldShowRequestPermissionRationale(TankerStartingPic.this,backgroundpermission)) {
               showBackgroundEducationalUI(TankerStartingPic.this);
           } else {
               ActivityCompat.requestPermissions(TankerStartingPic.this,new String[]{backgroundpermission}, BACKGROUND_LOCATION_REQUEST_CODE);
           }
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
        if(init_type!=null){
            if(init_type.equals(Constants.TRIP_START_IMG)){
                if(!tripStarted) {
                    photo_taken = false;
                    if (mService != null) {
                        if (mBound)
                            mService.stopService();
                    }
                    super.onBackPressed();
                }
            }else if(init_type.equals(Constants.TRIP_END_IMG)){
                super.onBackPressed();
            }
        }else{

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case BACKGROUND_LOCATION_REQUEST_CODE:
                if(grantResults.length>0){
                    if(grantResults[0]== PERMISSION_GRANTED){
                        uploadBitmap();
                    }else{
                        noBackgroundAlert(TankerStartingPic.this);
                    }
                }else{
                    noBackgroundAlert(TankerStartingPic.this);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void uploadBitmap() {
        String url = URLs.BASE_URL + URLs.BOOKING_START+blmod.getBookingid();
        Log.d("START_URL",url);
        tripStarted = true;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,url
                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d("Booking start",obj.toString());
                            if(obj!=null){
                                if(obj.getInt("error")==0){
                                    JSONObject data = obj.getJSONObject("data");
                                    if(data.has("path")) {
                                        String path = data.getString("path");
                                        blmod.setPath(path);
                                    }
                                    SharedPrefUtil.setPreferences(TankerStartingPic.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID,blmod.getBookingid());
                                    SharedPrefUtil.setPreferences(TankerStartingPic.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_DRIVER_ID,SessionManagement.getUserId(TankerStartingPic.this));
                                    Intent intent = new Intent(TankerStartingPic.this,Map1.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("Bookingdata",blmod);
                                    Log.i("tankerBookingId",tankerbookingid.toString());
                                    intent.putExtra("tankerBookingId", tankerbookingid);
                                    hideProgress();
                                    startActivity(intent);
                                    finish();
                                }else{
                                    RequestQueueService.showAlert(obj.getString("message"), TankerStartingPic.this);
                                    tripStarted = false;
                                    captureImgBtn.setClickable(true);
                                    hideProgress();
                                }
                            }else{
                                RequestQueueService.showAlert("Error! No data fetched", TankerStartingPic.this);
                                captureImgBtn.setClickable(true);
                                hideProgress();
                                tripStarted = false;
                            }
                        } catch (JSONException e) {
                            RequestQueueService.showAlert("Something went wrong", TankerStartingPic.this);
                            e.printStackTrace();
                            captureImgBtn.setClickable(true);
                            tripStarted = false;
                            hideProgress();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        captureImgBtn.setClickable(true);
                        tripStarted = false;
                        //progressBar.setVisibility(View.GONE);
                        hideProgress();
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            try {
                                JSONObject obj = new JSONObject(new String(response.data));
                                RequestQueueService.showAlert(obj.getString("message"),TankerStartingPic.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            RequestQueueService.showAlert("Something went wrong ", TankerStartingPic.this);
                        }
                    }
                }) {

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
    /*public void  checkAndRequestPermissions(Activity activity, ArrayList<String> permissions) {
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }else{
            permissionGranted = true;
            if(isLocationEnabled()){
                if(!mBound)
                    bindService(new Intent(TankerStartingPic.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
            }else{
                showAlert();
            }
            //checkLocation();
        }
    }*/
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
                        TankerStartingPic.this.startActivityForResult(myIntent,LOC_REQUEST);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if(isLocationEnabled()){
                            if(!mBound)
                                bindService(new Intent(TankerStartingPic.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
                        }else{
                            showAlert();
                        }
                        //checkLocation();
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void abortListener(int abortedBy) {
        if(SharedPrefUtil.hasKey(TankerStartingPic.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
            SharedPrefUtil.deletePreference(TankerStartingPic.this,Constants.SHARED_PREF_ONGOING_TAG);
        if (abortedBy==1)
            Alert("This trip has been cancelled by admin",TankerStartingPic.this);
        else if(abortedBy==-1)
            Alert("This trip has been cancelled",TankerStartingPic.this);
            else
                Alert("This trip has been cancelled by controller",TankerStartingPic.this);
    }
    public void Alert(final String message, final FragmentActivity context) {
        try {
            new Thread()
            {
                public void run()
                {
                    TankerStartingPic.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            //Do your UI operations like dialog opening or Toast here
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setTitle("Alert!");
                            builder.setMessage(message);
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(TankerStartingPic.this, FirstActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }.start();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void newLocation(Location location) {
        Log.i("TankerStartingPic","No Implementation for newLocation Method.");
        currentLocation = location;
        String lati = String.format("%.5f", currentLocation.getLatitude());
        String longi = String.format("%.5f", currentLocation.getLongitude());
        lat.setText(lati);
        lon.setText(longi);
        hideFetchingLocation();
    }

    @Override
    public void geofenceEnter() {

    }

    @Override
    public void geofenceExit() {

    }

    private void showProgress(){
        progresslayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgress(){
        progresslayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    private void showFetchingLocation(){
        progresslayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        fetchinglocation.setVisibility(View.VISIBLE);
        captureImgBtn.setVisibility(View.GONE);
        captureImgBtn.setClickable(false);
    }
    private void hideFetchingLocation(){
        progresslayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fetchinglocation.setVisibility(View.GONE);
        captureImgBtn.setVisibility(View.VISIBLE);
        captureImgBtn.setClickable(true);
    }
    public void showBackgroundEducationalUI(final FragmentActivity context) {
       final String message= "Your Location will be continously send to server so that your vehicle can be traced." +
               "Do you want to grant Background location Access?";
        try {
            new Thread()
            {
                public void run()
                {
                    TankerStartingPic.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            //Do your UI operations like dialog opening or Toast here
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setTitle("Alert!");
                            builder.setMessage(message);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{backgroundpermission}, BACKGROUND_LOCATION_REQUEST_CODE);
                                    }else{
                                        ActivityCompat.requestPermissions(TankerStartingPic.this,new String[]{backgroundpermission},BACKGROUND_LOCATION_REQUEST_CODE);
                                    }
                                }
                            });
                            builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    noBackgroundAlert(TankerStartingPic.this);
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void noBackgroundAlert(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Permission Not Granted")
                .setMessage("Sorry, booking can't be started without Allow all the time permission")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        captureImgBtn.setClickable(true);
                    }
                })
                .create()
                .show();
    }
}
