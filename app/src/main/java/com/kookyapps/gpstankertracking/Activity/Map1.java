package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;


import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kookyapps.gpstankertracking.Modal.SnappedPoint;
import com.kookyapps.gpstankertracking.Services.TankerLocationCallback;
import com.kookyapps.gpstankertracking.Services.TankerLocationService;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kookyapps.gpstankertracking.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/*import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;*/



import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.FetchURL;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.app.GPSTracker;
import com.kookyapps.gpstankertracking.fcm.Config;
import com.kookyapps.gpstankertracking.fcm.NotificationUtilsFcm;


import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.maps.android.PolyUtil;
public class Map1 extends AppCompatActivity implements TankerLocationCallback, View.OnClickListener,OnMapReadyCallback ,TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {
    private GoogleMap mMap = null;
    RelativeLayout bottom,seemore,details ,r;
    TextView title, seemoreText,bookingid,dropPoint,distance,contanctno,trips,language,logout;
    ImageView seemoreImg ;
    ScrollView scrolldetails;
    Double toLat , toLong,fromLat,fromLong;
    Animation slideUp, slideDown;
    Boolean t =false,    permissionGranted = false,pathfetched=false;
    BookingListModal blmod;
    private String parserstring="";
    ArrayList<String> allpermissionsrequired;
    static ArrayList<LatLng> waypoints = null;
    private LocationManager locationManager;
    long distance1=0,duration=0;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private long UPDATE_INTERVAL = 3000;  /* 10 secs */
    private long FASTEST_INTERVAL = 3000;
    private LatLng currentlatlng=null;
    private boolean isRecentered = true;
    private float bearing=0;
    private LatLng pickupLatLng=null,dropLatLng=null;
    Polyline currentPolyline;
    private Marker currentmarker=null;
    SupportMapFragment mapFragment;
    boolean cameraAccepted;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList<LatLng> mapRoute=null;
    boolean isRequestingLocation = false;
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    ArrayList<LatLng> points;
    // flag for GPS Tracking is enabled
    private boolean locationInProcess = false;
    private final int LOC_REQUEST = 10101;
    private final int CAMERA_CAPTURE_REQUEST = 10102;
    String init_type,bkngid,tanker_id;
    LatLng prevlatlng = null;
    Location droploc,curloc=null;
    private boolean mBound = false;
    private TankerLocationService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TankerLocationService.LocalBinder binder = (TankerLocationService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
            mService.setServiceCallback(Map1.this);
            /*if(!mService.isSocketInitialized())
                mService.initSocket(blmod.getBookingid());*/
            curloc = mService.getCurrentLocation();
            if(mMap == null)
                mapFragment.getMapAsync(Map1.this);
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
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        blmod = b.getParcelable("Bookingdata");
        init_type = getIntent().getExtras().getString("init_type");
        bkngid = getIntent().getExtras().getString("booking_id");
        tanker_id=getIntent().getExtras().getString("tankerBookingId");
        Constants.isTripOngoing = true;
        fromLat=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getFromlatitude())));
        fromLong=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getFromlongitude())));
        toLat=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getTolatitude())));
        toLong=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getTologitude())));
       // geofenceDist=Double.parseDouble(blmod.getGeofence_in_meter());
        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        //allpermissionsrequired.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
       //initSocket();
       initViews();
    }
    private void initViews(){
        title=(TextView)findViewById(R.id.tb_with_bck_arrow_title1);
        title.setText(R.string.title_activity_maps);
        bottom=(RelativeLayout)findViewById(R.id.rl_map_bottomLayout_text);
        bottom.setOnClickListener(this);
        seemore = (RelativeLayout)findViewById(R.id.rl_map_seemore);
        seemore.setOnClickListener(this);
        seemoreImg=(ImageView)findViewById(R.id.iv_map_seemore_image);
        seemoreText=(TextView)findViewById(R.id.tv_map_seemore_text);
        details=(RelativeLayout)findViewById(R.id.rl_map_main);
        scrolldetails=(ScrollView)findViewById(R.id.sv_map_detailsScroll);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        bookingid=(TextView)findViewById(R.id.tv_map_bookingid_text);
        bookingid.setText(tanker_id);
        dropPoint=(TextView)findViewById(R.id.tv_map_drop_text);
        dropPoint.setText(blmod.getToaddress());
        contanctno=(TextView)findViewById(R.id.tv_map_contact_text);
        contanctno.setText("+" +blmod.getPhone_country_code()+blmod.getPhone());
        distance=(TextView)findViewById(R.id.tv_map_distance);
        distance.setText(blmod.getDistance());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fg_pickup_map);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(Map1.this).equals(Constants.HINDI_LANGUAGE)){
                        setAppLocale(Constants.HINDI_LANGUAGE);
                        finish();
                        startActivity(getIntent());
                    }else{
                        setAppLocale(Constants.ENGLISH_LANGUAGE);
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //checkAndRequestPermissions(this,allpermissionsrequired);
    }
    @Override
    protected void onStart() {
        checkAndRequestPermissions(Map1.this,allpermissionsrequired);
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        if(SessionManagement.getLanguage(Map1.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
    }
    private void checkAndRequestPermissions(Activity activity, ArrayList<String> permissions) {
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
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
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
                    }else{
                        checkAndRequestPermissions(Map1.this,allpermissionsrequired);
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void checkLocation() {
        boolean gpsenable=isLocationEnabled();
        if(!gpsenable){
            showLocationAlert();
        }else{
            if(!mBound)
                bindService(new Intent(Map1.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void showLocationAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Map1.this.startActivityForResult(myIntent,LOC_REQUEST);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkLocation();
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled && isNetworkEnabled;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pickupLatLng = new LatLng(fromLat,fromLong);
        dropLatLng = new LatLng(toLat,toLong);
        if(curloc==null)
            currentlatlng = pickupLatLng;
        else
            currentlatlng = new LatLng(curloc.getLatitude(),curloc.getLongitude());
        MarkerOptions pickupop,dropop,currentop;
        pickupop = new MarkerOptions()
                .position(pickupLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));
        dropop = new MarkerOptions()
                .position(dropLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));
        currentop = new MarkerOptions()
                .position(currentlatlng)
                .flat(true)
                .anchor(.5f, 0f)
                .icon(bitmapDescriptorFromVector(Map1.this,R.drawable.ic_truck_icon));
        mMap.addMarker(pickupop);
        mMap.addMarker(dropop);
        if (currentmarker != null)
            currentmarker.remove();
        currentmarker = mMap.addMarker(currentop);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 15));
        mMap.setOnCameraMoveStartedListener(Map1.this);
        mMap.setOnMarkerClickListener(Map1.this);
        droploc = new Location("");
        droploc.setLatitude(dropLatLng.latitude);
        droploc.setLongitude(dropLatLng.longitude);
        pathfetched = true;
        String encodedpoly = blmod.getPath();
        points = (ArrayList<LatLng>) decodePoly(encodedpoly);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions.width(30);
        polylineOptions.color(ContextCompat.getColor(Map1.this, R.color.greenLight));
        currentPolyline= mMap.addPolyline(polylineOptions);
        mService.setPath(encodedpoly);
        /*if (!pathfetched) {
            new FetchURL(Map1.this).execute(getUrl(pickupLatLng, dropLatLng, "driving"), "driving");
            pathfetched = true;
        }*/
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;

        String waypoint = "waypoints=";
        String parameters = "";
        try {
            if(waypoints!=null) {
                for (int i = 0; i < waypoints.size(); i++) {
                    LatLng temp = waypoints.get(i);
                    if (i == 0) {
                        waypoint = waypoint + "via:" + temp.latitude + "%2C" + temp.longitude;
                    } else {
                        waypoint = waypoint + "%7Cvia:" + temp.latitude + "%2C" + temp.longitude;
                    }
                }
                if (waypoints.size() <= 0) {
                    parameters = str_origin + "&" + str_dest + "&" + mode;
                } else {
                    parameters = str_origin + "&" + str_dest + "&" + waypoint + "&" + mode;
                }
            }else{
                parameters = str_origin + "&" + str_dest + "&" + mode;
            }
            //parameters = str_origin + "&" + str_dest + "&" + mode;
            // Building the parameters to the web service

        }catch (Exception e){
            e.printStackTrace();
            parameters = str_origin + "&" + str_dest + "&" + mode;
        }
        //String parameters = str_origin + "&" + str_dest + "&" + waypoint + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d("FetchUrl:",url);
        return url;
    }
    @Override
    public void onTaskDone(Object... values) {
        try {
            Log.i("Map1","InTaskDone");
            if (values != null) {
                if (values.length == 0) {
                } else {
                    if (currentPolyline != null)
                        currentPolyline.remove();
                    currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
                    distance1 = (long) values[1];
                    duration = (long) values[2];
                    mapRoute = (ArrayList<LatLng>) values[3];
                    parserstring = (String) values[4];
                    mService.setPath(parserstring);
                }
            }else{

            }
        }catch (Exception e){
            Log.e("Map1","TaskDone Exception");
            e.printStackTrace();
            pathfetched = false;
        }
    }
    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_map_bottomLayout_text:
                if (checkCameraPermission()) {
                    cameraAccepted = true;
                } else {
                    requestCameraPermission();
                }
                if (cameraAccepted) {
                    if(curloc!=null){
                        double enddist= curloc.distanceTo(droploc);
                        if (enddist<Integer.parseInt(blmod.getGeofence_in_meter())){
                            showEndAlert();
                        }else{
                            Toast.makeText(Map1.this,"Not within Geofence Range",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(Map1.this,"Not within Geofence Range",Toast.LENGTH_LONG).show();
                    }

                } else {
                    requestCameraPermission();
                }
                break;
            case R.id.rl_map_seemore:
                if (t) {
                    scrolldetails.setVisibility(View.VISIBLE);
                    seemoreText.setText(getString(R.string.seeless));
                    seemoreImg.setImageResource(R.drawable.see_fewer_map);
                    scrolldetails.animate().translationY(0);
                    t = false;
                }else{
                    seemoreText.setText(getString(R.string.seemore));
                    seemoreImg.setImageResource(R.drawable.see_more_map);
                    scrolldetails.animate().translationY(-1000);
                    scrolldetails.setVisibility(View.GONE);
                    t = true;
                }
                break;
        }
    }
    private boolean checkCameraPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},Constants.CAMERA_PERMISSION_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data)  {
        switch (requestCode) {
            case CAMERA_CAPTURE_REQUEST:
                if (resultCode != 0) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(this, TankerStartingPic.class);
                    intent.putExtra("Bitmap", bitmap);
                    intent.putExtra("Bookingdata", blmod);
                    intent.putExtra("init_type", Constants.TRIP_END_IMG);
                    mService.removeLocationUpdates();
                    startActivity(intent);
                    finish();
                }
                /*else{
                    requestUpdate();
                }*/
                break;
            case LOC_REQUEST:
                checkLocation();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    protected void onStop() {
        if(mBound){
            if(!(((Activity)Map1.this).isFinishing()))
                mService.setServiceCallback(null);
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        RequestQueueService.showAlert("","Can not close in middle of trip",Map1.this);
    }
    @Override
    public void onCameraMoveStarted(int i) {
        switch (i){
            case GoogleMap.OnCameraMoveStartedListener
                    .REASON_GESTURE:
                isRecentered =  false;
                break;
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(currentmarker)){
            isRecentered = true;
        }
        return false;
    }
    private void showEndTrip(){ bottom.setVisibility(View.VISIBLE); }
    private void hideEndTrip(){
        bottom.setVisibility(View.GONE);
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_truck_icon);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void showEndAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Map1.this);
        // Set the message show for the Alert time
        builder.setMessage("Do you want to end trip ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder
                .setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //mService.removeLocationUpdates();
                                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(camera_intent, CAMERA_CAPTURE_REQUEST);
                            }
                        });

        builder
                .setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void abortListener(int abortedBy) {
        if(SharedPrefUtil.hasKey(Map1.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
            SharedPrefUtil.deletePreference(Map1.this,Constants.SHARED_PREF_ONGOING_TAG);
        if(mService!=null)
            mService.stopService();
        if (abortedBy==1)
            abortAlert("This trip has been cancelled by admin",Map1.this);
        else if(abortedBy==-1)
            abortAlert("This trip has been cancelled",Map1.this);
        else
            abortAlert("This trip has been cancelled by controller",Map1.this);
    }
    private void abortAlert(final String message, final FragmentActivity context) {
        try {
            new Thread()
            {
                public void run()
                {
                    Map1.this.runOnUiThread(new Runnable()
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
                                    Intent intent = new Intent(Map1.this, FirstActivity.class);
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
        currentlatlng = new LatLng(location.getLatitude(),location.getLongitude());
        if(curloc==null)
            curloc = location;
        MarkerOptions current = new MarkerOptions()
                .position(currentlatlng)
                .flat(true)
                .anchor(.5f, 0f)
                .icon(bitmapDescriptorFromVector(Map1.this, R.drawable.ic_truck_icon));
        if (currentmarker != null)
            currentmarker.remove();
        currentmarker = mMap.addMarker(current);
        if (isRecentered) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 18));
        }
        if (location.hasAccuracy()) {
            if (location.getAccuracy() < 50) {
                curloc = location;
            }
        }
        locationInProcess = false;
    }

    @Override
    public void geofenceEnter() {
        geofenceAlert("You entered in Geofence area of drop location.",Map1.this);
    }

    @Override
    public void geofenceExit() {

    }

    public void geofenceAlert(String message, final Activity context) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Alert!");
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}