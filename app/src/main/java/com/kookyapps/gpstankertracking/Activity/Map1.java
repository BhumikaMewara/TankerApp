package com.kookyapps.gpstankertracking.Activity;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.CircleOptions;
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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.maps.android.PolyUtil;

import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

public class Map1 extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,TaskLoadedCallback{

    private GoogleMap mMap;
    LinearLayout tripLayout,logoutLayout,l;
    RelativeLayout notifications,bottom,seemore,details , redLayout,toolbarNotiCountLayout,toolbarmenuLayout,r;
    TextView title, seemoreText,bookingid,dropPoint,distance,contanctno,notificationCountText,trips,language,logout;
    TextView fullname,username;
    ImageView seemoreImg ;
    Double toLat , toLong,fromLat,fromLong,currentLat,currentLong,geofenceDist;

    Animation slideUp, slideDown;
    Boolean t = false,    permissionGranted = false,fromBuildMethod=false, locationCahnge1st=true;
    BookingListModal blmod;
    static String notificationCount;
    ArrayList<String> allpermissionsrequired;
    static ArrayList<LatLng> waypoints = null;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    long distance1=0,duration=0;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 20000;
    private LatLng currentlatlng=null;

    private LatLng pickupLatLng=null,dropLatLng=null;
    Polyline currentPolyline;
    private Marker currentmarker=null;
    Marker pickupMarker,dropMarker;
    MarkerOptions currentop;
    SupportMapFragment mapFragment;
    private Socket socket;
    boolean cameraAccepted;
    String imageencoded,can_accept,can_end,can_start;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    DrawerLayout navdrawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SwitchCompat switchCompat,onlineSwitch;
    GPSTracker gpsTracker;
    String  stringLatitude,stringLongitude;
    Locale locale;
    static boolean isTouched = true;
    ArrayList<LatLng> mapRoute=null;
    private List<BookingListModal> requestlist;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds

    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;
    private String provider_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        blmod = b.getParcelable("Bookingdata");
        //gpsTracker = new GPSTracker(this);
        switchCompat=(SwitchCompat)findViewById(R.id.switch2_map);
        if(SessionManagement.getLanguage(Map1.this).equals(Constants.HINDI_LANGUAGE)){
            switchCompat.setChecked(true);
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            switchCompat.setChecked(false);
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }

        fromLat=Double.parseDouble(blmod.getFromlatitude());
        fromLong=Double.parseDouble(blmod.getFromlongitude());
        toLat=Double.parseDouble(blmod.getTolatitude());
        toLong=Double.parseDouble(blmod.getTologitude());
       // geofenceDist=Double.parseDouble(blmod.getGeofence_in_meter());

        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);

       //initSocket();
       initViews();
    }


    public void initViews(){
        notifications = (RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        notifications.setOnClickListener(this);
        toolbarNotiCountLayout=(RelativeLayout)findViewById(R.id.rl_toolbar_notificationcount);
        notificationCountText=(TextView)findViewById(R.id.tv_toolbar_notificationcount);
        title=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        title.setText(R.string.title_activity_maps);

        trips=(TextView)findViewById(R.id.tv_map_tripText);
        language=(TextView)findViewById(R.id.tv_map_language);
        logout=(TextView)findViewById(R.id.tv_map_logout);

        bottom=(RelativeLayout)findViewById(R.id.rl_map_bottomLayout_text);
        bottom.setOnClickListener(this);
        seemore = (RelativeLayout)findViewById(R.id.rl_map_seemore);
        seemore.setOnClickListener(this);
        seemoreImg=(ImageView)findViewById(R.id.iv_map_seemore_image);
        seemoreText=(TextView)findViewById(R.id.tv_map_seemore_text);
        details=(RelativeLayout)findViewById(R.id.rl_map_main);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        bookingid=(TextView)findViewById(R.id.tv_map_bookingid_text);
        bookingid.setText(blmod.getBookingid());
        dropPoint=(TextView)findViewById(R.id.tv_map_drop_text);
        dropPoint.setText(blmod.getToaddress());
        contanctno=(TextView)findViewById(R.id.tv_map_contact_text);
        contanctno.setText("+" +blmod.getPhone_country_code()+blmod.getPhone());
        distance=(TextView)findViewById(R.id.tv_map_distance);
        navdrawer=(DrawerLayout)findViewById(R.id.dl_trip_details);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, navdrawer,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        navdrawer.setDrawerElevation(0f);
        navdrawer.setScrimColor(Color.TRANSPARENT);
        navdrawer.addDrawerListener(actionBarDrawerToggle);


        distance.setText(blmod.getDistance());
        toolbarmenuLayout=(RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu);
        toolbarmenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerMenu(view);
            }
        });
        l=(LinearLayout)findViewById(R.id.lv_map_drawer_firstLayout);
        tripLayout=(LinearLayout) findViewById(R.id. lh_map_triplayout);
        tripLayout.setOnClickListener(this);
        logoutLayout=(LinearLayout)findViewById(R.id.lh_map_logoutLayout);
        logoutLayout.setOnClickListener(this);
        fullname=(TextView)findViewById(R.id.tv_map_drawer_fullName);
        username=(TextView)findViewById(R.id.tv_map_drawer_username);

        fullname.setText(SessionManagement.getName(Map1.this));
        username.setText(SessionManagement.getUserId(Map1.this));


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fg_pickup_map);


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
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(Map1.this));
                    setNotificationCount(count+1,false);
                }
                else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
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


        switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isTouched) {
                    isTouched = false;
                    if (isChecked) {
                        SessionManagement.setLanguage(Map1.this,Constants.HINDI_LANGUAGE);
                    }
                    else {
                        SessionManagement.setLanguage(Map1.this,Constants.ENGLISH_LANGUAGE);
                    }
                    showlanguage();
                }
            }
        });


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
            buildGoogleApiClient();
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
                        buildGoogleApiClient();
                        //createPickUpLocations();
                    }else{
                        checkAndRequestPermissions(Map1.this,allpermissionsrequired);
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        //getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Try to get location if you GPS Service is enabled
        if (isGPSEnabled) {
            this.isGPSTrackingEnabled = true;
            Log.d("MAP1:", "Application use GPS Service");
            /*
             * This provider determines location using
             * satellites. Depending on conditions, this provider may take a while to return
             * a location fix.
             */
            provider_info = LocationManager.GPS_PROVIDER;
        } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
            this.isGPSTrackingEnabled = true;
            Log.d("MAP1:", "Application use Network State to get GPS coordinates");
            /*
             * This provider determines location based on
             * availability of cell tower and WiFi access points. Results are retrieved
             * by means of a network lookup.
             */
            provider_info = LocationManager.NETWORK_PROVIDER;
        }
        return isGPSTrackingEnabled;
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        fromBuildMethod = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!permissionGranted) {
            return;
        }
        if(fromBuildMethod){
            startLocationUpdates();
            fromBuildMethod = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("PICKUP ACTIVITY:", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("PICKUP ACTIVITY:", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        try {
            if (!permissionGranted) {
                return;
            }
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(provider_info);

                if(location!=null)
                    currentlatlng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            locationManager.requestLocationUpdates(provider_info, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }catch(SecurityException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        //double lt = Double.parseDouble(String.format("%.6f", location.latitude));
        //double lg = Double.parseDouble(String.format("%.6f", currentlatlng.longitude));
        currentlatlng = new LatLng(Double.parseDouble(String.format("%.6f", location.getLatitude())),Double.parseDouble(String.format("%.6f", location.getLongitude())));
        //currentlatlng = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentlatlng!=null) {
            // createPickUpLocations();
            if (locationCahnge1st) {
                mapFragment.getMapAsync(this);
            }else {
                MarkerOptions current = new MarkerOptions()
                        .position(currentlatlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_map));
                if (currentmarker != null)
                    currentmarker.remove();
                currentmarker = mMap.addMarker(current);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 18));
            }
            if (mapRoute == null) {
                if (waypoints == null)
                    waypoints = new ArrayList<>();
                waypoints.add(currentlatlng);
            } else if (!PolyUtil.isLocationOnPath(currentlatlng, mapRoute, true, 10)&&(!locationCahnge1st)) {
                if (waypoints == null)
                    waypoints = new ArrayList<>();
                if (waypoints.size() >= 10)
                    waypoints.remove(0);
                double lt = Double.parseDouble(String.format("%.6f", currentlatlng.latitude));
                double lg = Double.parseDouble(String.format("%.6f", currentlatlng.longitude));
                LatLng t = new LatLng(lt, lg);
                waypoints.add(currentlatlng);
                locationManager.removeUpdates(Map1.this);
                new FetchURL(Map1.this).execute(getUrl(pickupLatLng, dropLatLng, "driving"), "driving");
            }else{
                JSONObject params = new JSONObject();
                try {
                    params.put("id", blmod.getBookingid());
                    params.put("lat", currentlatlng.latitude);
                    params.put("lng",currentlatlng.longitude);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                socket.emit("locationUpdate:Booking",params);
            }
        } else {
            Log.d("Map1:", "Current location not fetched");
            Toast.makeText(Map1.this, "Current location not fetched", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationCahnge1st = false;
        pickupLatLng = new LatLng(Double.parseDouble(String.format("%.6f", new Double(blmod.getFromlatitude()))),Double.parseDouble(String.format("%.6f", new Double(blmod.getFromlongitude()))));
        dropLatLng = new LatLng(Double.parseDouble(String.format("%.6f", new Double(blmod.getTolatitude()))),Double.parseDouble(String.format("%.6f", new Double(blmod.getFromlongitude()))));
        MarkerOptions pickupop,dropop,currentop;
        pickupop = new MarkerOptions()
                .position(pickupLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));

        dropop = new MarkerOptions()
                .position(dropLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));

        mMap.addMarker(pickupop);
        mMap.addMarker(dropop);
        if(currentlatlng!=null){
            currentop = new MarkerOptions()
                    .position(currentlatlng)
                    .flat(true)
                    .alpha(.6f)
                    .anchor(0.5f,0.5f)
                    .rotation(90)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_map));
            currentmarker = mMap.addMarker(currentop);
            pickupMarker = mMap.addMarker(pickupop);
            dropMarker = mMap.addMarker(dropop);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 12));
        }
        initSocket();
        if (mapRoute == null) {
            locationManager.removeUpdates(Map1.this);
            new FetchURL(Map1.this).execute(getUrl(pickupLatLng, dropLatLng, "driving"), "driving");
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        String s = "";
        if(values!=null) {
            if (values.length == 0){

            }else {
                if (currentPolyline != null)
                    currentPolyline.remove();
                //int size = values.length;
                currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
                distance1 = (long) values[1];
                duration = (long) values[2];
                mapRoute = (ArrayList<LatLng>) values[3];
                s = (String)values[4];
            }
        }
        JSONObject params = new JSONObject();
        try {
            params.put("id", blmod.getBookingid());
            params.put("lat", currentlatlng.latitude);
            params.put("lng",currentlatlng.longitude);
            if(s!="") {
                params.put("path", s);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        socket.emit("locationUpdate:Booking",params);
        startLocationUpdates();
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
                        waypoint = waypoint + "%7C" + temp.latitude + "%2C" + temp.longitude;
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

    public void initSocket(){
        try {
            socket = IO.socket(URLs.SOCKET_URL + SessionManagement.getUserToken(this));
            socket.connect();
            socket.on("aborted:Booking", onBookingAborted);
            JSONObject params = new JSONObject();
            params.put("booking_id", blmod.getBookingid());
            socket.emit("subscribe:Booking", params);
        }catch (URISyntaxException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////////
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist=dist/0.62137;
        dist=dist*1609.34;


        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void drawerMenu (View view ){
        navdrawer.openDrawer(Gravity.LEFT);
    }
    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_water_tanker_toolbar_menu_notification:
                i = new Intent(this,Notifications.class);
                startActivity(i);
                break;
            case R.id.rl_map_bottomLayout_text:

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

                /*i = new Intent(this,EnterOTP.class);
                startActivity(i);*/
            case R.id.rl_map_seemore:
                if (t) {
                    details.setVisibility(View.VISIBLE);
                    seemoreText.setText(getString(R.string.seeless));
                    seemoreImg.setImageResource(R.drawable.see_fewer_map);
                    details.animate().translationY(0);
                    t = false;
                }else{
                    seemoreText.setText(getString(R.string.seemore));
                    seemoreImg.setImageResource(R.drawable.see_more_map);
                    details.animate().translationY(-1000);
                    details.setVisibility(View.GONE);
                    //seemore.setVisibility(View.VISIBLE);
                    t = true;
                }
                break;
            case R.id.lh_map_triplayout:
                Log.i("trip","clicked");
                i = new Intent(this, TripDetails.class);
                startActivity(i);

                break;
            case R.id.lh_map_logoutLayout:
                Log.i("logout","clicked");
                logoutLayout.setClickable(false);
                logutApiCalling();
                break;
        }
    }

    public void logutApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.SIGN_OUT_URL;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:",token);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(Map1.this,logoutListner,url,headparam,jsonBodyObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    FetchDataListener logoutListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data!=null){
                    if (data.getInt("error") == 0) {
                        FirebaseAuth.getInstance().signOut();
                        SessionManagement.logout(logoutListner, Map1.this);
                        Intent i = new Intent(Map1.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        Toast.makeText(Map1.this, "You are now logout", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }


        @Override
        public void onFetchFailure(String msg) {
            logoutLayout.setClickable(true);
        }

        @Override
        public void onFetchStart() {

        }
    };

    public void updateLcationApiCalling(String currentStatus) {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("lat", stringLatitude);
            jsonBodyObj.put("lng", stringLongitude);
            jsonBodyObj.put("status", currentStatus);
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.UPDATE_LOCATION ;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:", token);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(Map1.this, updateLocationListner, url, headparam, jsonBodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    FetchDataListener updateLocationListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data!=null){
                    if (data.getInt("error") == 0) {
                        String message=   data.getString("message");
                        Toast.makeText(Map1.this, message, Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        @Override
        public void onFetchFailure(String msg) {
            logoutLayout.setClickable(true);
        }

        @Override
        public void onFetchStart() {

        }
    };







    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        //switch (requestCode) {
          //  case PERMISSION_REQUEST_CODE:
                if (resultCode != 0) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(this, TankerStartingPic.class);
                    intent.putExtra("Bitmap", bitmap);
                    intent.putExtra("Bookingdata", blmod);
                    intent.putExtra("init_type", Constants.TRIP_END_IMG);
                    startActivity(intent);
                    finish();
            //        break;
                }
        //}
        super.onActivityResult(requestCode, resultCode, data);
    }




    public void languageChangeApi() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("lang",locale);
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.LANGUAGE_CHANGED;

            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:",token);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(Map1.this,languageChangeListner,url,headparam,jsonBodyObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    FetchDataListener languageChangeListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data!=null){
                    if (data.getInt("error") == 0) {
                        String message=   data.getString("message");
                        Toast.makeText(Map1.this, message, Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e){
                e.printStackTrace();

            }

        }
        @Override
        public void onFetchFailure(String msg) {
            logoutLayout.setClickable(true);
        }

        @Override
        public void onFetchStart() {

        }
    };






    public void setNotificationCount(int count,boolean isStarted){
        notificationCount = SessionManagement.getNotificationCount(Map1.this);
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
            SharedPrefUtil.setPreferences(Map1.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
            boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if(b2)
                SharedPrefUtil.setPreferences(Map1.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY,"no");
        }
    }

    public void newNotification(){
        Log.i("newNotification","Notification");
        int count = Integer.parseInt(SharedPrefUtil.getStringPreferences(Map1.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY));
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        if(SessionManagement.getLanguage(Map1.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
        // clear the notification area when the app is opened
        int sharedCount =Integer.parseInt(SessionManagement.getNotificationCount(this));
        String viewCount =notificationCountText.getText().toString();
        boolean b1 = String.valueOf("sharedCount")!=viewCount;
        boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
        if(b2){
            newNotification();
        }else if (b1){
            if (sharedCount < 100 && sharedCount>0) {
                notificationCountText.setText(String.valueOf(sharedCount));
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            }
        }
    }





















    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private Emitter.Listener onBookingAborted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            locationManager.removeUpdates(Map1.this);
            Map1.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    try {
                        Log.i("response","Booking Aborted "+response.getString("id"));
                        SessionManagement.removeOngoingBooking(Map1.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };












    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("aborted:Booking");
    }


    public void showlanguage(){
            /*SharedPrefUtil.setPreferences(getApplicationContext(), Constants.SHARED_LANGUAGE_LANGUAGE_TAG,
                    Constants.SHARED_LANGUAGE_CHANGED_KEY,"yes");*/
        if (!NotificationUtilsFcm.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent languageChange = new Intent(Config.LANGUAGE_CHANGE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(languageChange);
            Toast.makeText(this, "language has been changed", Toast.LENGTH_SHORT).show();
        }
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


}