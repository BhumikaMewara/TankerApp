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
import com.kookyapps.gpstankertracking.Modal.SnappedPoint;
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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.maps.android.PolyUtil;

import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

public class Map1 extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback ,TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap mMap;
    LinearLayout tripLayout,logoutLayout,l;
    RelativeLayout notifications,bottom,seemore,details , redLayout,toolbarNotiCountLayout,toolbarmenuLayout,r;
    TextView title, seemoreText,bookingid,dropPoint,distance,contanctno,notificationCountText,trips,language,logout;
    TextView fullname,username;
    ImageView seemoreImg ;
    ScrollView scrolldetails;
    Double toLat , toLong,fromLat,fromLong,currentLat,currentLong,geofenceDist;

    Animation slideUp, slideDown;
    Boolean t =false,    permissionGranted = false,fromBuildMethod=false, pathfetched=false;
    BookingListModal blmod;
    private String parserstring="";
    static String notificationCount;
    ArrayList<String> allpermissionsrequired;
    static ArrayList<LatLng> waypoints = null;
    ArrayList<LatLng> travelledpath =null;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    long distance1=0,duration=0;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private long UPDATE_INTERVAL = 3000;  /* 10 secs */
    private long FASTEST_INTERVAL = 3000;
    private LatLng currentlatlng=null;
    private boolean isRecentered = true;
    private float bearing=0;

    Polyline travelled_polyline = null;
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
    boolean isRequestingLocation = false;
    private ArrayList<SnappedPoint> snappedPoints = null;
    double travelled_distance=0;
    PolylineOptions travelledoptions = null;
    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 2; // 10 seconds

    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;
    private String provider_info;
    private boolean locationInProcess = false;
    private final int LOC_REQUEST = 10101;
    private final int CAMERA_CAPTURE_REQUEST = 10102;
    String init_type,bkngid;
    boolean isMarkerRotating = false;

    private int OFFSET=0;
    private final int PAGINATION_OVERLAP = 3,PAGE_SIZE = 95;

    LatLng prevlatlng = null;
    int lowerbound=-1,upperbound=-1;
    double snappedDistance=0;
    JSONArray snappedArray;
    JSONObject finalsnap;
    boolean path_snapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        blmod = b.getParcelable("Bookingdata");
        init_type = getIntent().getExtras().getString("init_type");
        bkngid = getIntent().getExtras().getString("booking_id");
        //gpsTracker = new GPSTracker(this);
        switchCompat=(SwitchCompat)findViewById(R.id.switch2_map);
        if(SessionManagement.getLanguage(Map1.this).equals(Constants.HINDI_LANGUAGE)){
            switchCompat.setChecked(true);
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            switchCompat.setChecked(false);
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }

        fromLat=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getFromlatitude())));
        fromLong=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getFromlongitude())));
        toLat=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getTolatitude())));
        toLong=Double.parseDouble(String.format("%.5f",Double.parseDouble(blmod.getTologitude())));
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
        scrolldetails=(ScrollView)findViewById(R.id.sv_map_detailsScroll);
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
            showAlert();
        }else{
            mapFragment.getMapAsync(Map1.this);
        }
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

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setInterval(FASTEST_INTERVAL);

        if (!permissionGranted) {
            return;
        }
        requestUpdate();
    }

    private LocationCallback mlocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {

            if (!locationInProcess) {
                locationInProcess = true;
                if (locationResult == null) {
                    locationInProcess = false;
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (!location.hasAccuracy()) {
                        locationInProcess = false;
                        return;
                    }
                    if (location.getAccuracy() > 50) {
                        locationInProcess = false;
                        return;
                    }
                    if (location.hasBearing())
                        bearing = location.getBearing()+90;

                    double lt = Double.parseDouble(String.format("%.5f", location.getLatitude()));
                    double lg = Double.parseDouble(String.format("%.5f", location.getLongitude()));
                    currentlatlng = new LatLng(lt, lg);
                    double enddist = distance(currentlatlng.latitude,currentlatlng.longitude,dropLatLng.latitude,dropLatLng.longitude);
                    if(enddist<1000)
                        showEndTrip();
                    else
                        hideEndTrip();

                    if (currentlatlng != null)
                        prevlatlng = currentlatlng;

                    double dist = 0;
                    if(pathfetched)
                        dist = distance(prevlatlng.latitude, prevlatlng.longitude, currentlatlng.latitude, currentlatlng.longitude);
                    if (dist > 150 || !pathfetched) {
                        travelled_distance = travelled_distance + dist;
                        if (travelledpath == null) {
                            travelledpath = new ArrayList<>();
                        }
                        travelledpath.add(currentlatlng);
                        MarkerOptions current = new MarkerOptions()
                                .position(currentlatlng)
                                .flat(true)
                                .rotation(bearing)
                                .anchor(.5f, .5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_map));
                        if (currentmarker != null)
                            currentmarker.remove();
                        currentmarker = mMap.addMarker(current);
                        if (isRecentered) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 18));
                        }

                        if (!pathfetched) {
                            new FetchURL(Map1.this).execute(getUrl(pickupLatLng, dropLatLng, "driving"), "driving");
                            stopUpdate();
                            pathfetched = true;
                        } else {
                            JSONObject params = new JSONObject();
                            try {
                                params.put("id", blmod.getBookingid());
                                params.put("lat", currentlatlng.latitude);
                                params.put("lng", currentlatlng.longitude);
                                params.put("bearing", bearing);
                            } catch (JSONException e) {
                                locationInProcess = false;
                                e.printStackTrace();
                            }
                            socket.emit("locationUpdate:Booking", params);
                            locationInProcess = false;
                        }

                    }
                }
            }
        }
    };



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng jodhpur = new LatLng(26.283779, 73.021964);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jodhpur));
        pickupLatLng = new LatLng(fromLat,fromLong);
        dropLatLng = new LatLng(toLat,toLong);
        MarkerOptions pickupop,dropop,currentop;
        pickupop = new MarkerOptions()
                .position(pickupLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));
        dropop = new MarkerOptions()
                .position(dropLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));
        mMap.addMarker(pickupop);
        mMap.addMarker(dropop);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 15));
        initSocket();
    }

    @Override
    public void onTaskDone(Object... values) {
        try {
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
                    JSONObject params = new JSONObject();
                    params.put("id", blmod.getBookingid());
                    params.put("lat", currentlatlng.latitude);
                    params.put("lng", currentlatlng.longitude);
                    params.put("bearing", bearing);
                    if (parserstring != "") {
                        params.put("path", parserstring);
                        parserstring = "";
                    }
                    socket.emit("locationUpdate:Booking", params);
                }
            }
            locationInProcess = false;
            requestUpdate();
        }catch (Exception e){
            e.printStackTrace();
            locationInProcess = false;
            requestUpdate();
        }
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

    public void initSocket(){
        try {
            socket = IO.socket(URLs.SOCKET_URL + SessionManagement.getUserToken(this));
            socket.connect();
            socket.on("aborted:Booking", onBookingAborted);
            JSONObject params = new JSONObject();
            params.put("booking_id", blmod.getBookingid());
            socket.emit("subscribe:Booking", params);
            startLocationUpdates();
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
                    stopUpdate();
                    snapToRoad();
                } else {
                    requestPermission();
                }
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

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        switch (requestCode) {
            case CAMERA_CAPTURE_REQUEST:
                if (resultCode != 0) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(this, TankerStartingPic.class);
                    intent.putExtra("Bitmap", bitmap);
                    intent.putExtra("Bookingdata", blmod);
                    intent.putExtra("init_type", Constants.TRIP_END_IMG);
                    intent.putExtra("snapped_path",finalsnap.toString());
                    intent.putExtra("snapped_distance",String.valueOf(snappedDistance));
                    startActivity(intent);
                    finish();
                    //break;
                }else{
                    requestUpdate();
                }
                break;
            case LOC_REQUEST:
                checkLocation();
                break;
        }
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

    private Emitter.Listener onBookingAborted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            stopUpdate();
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
        socket.disconnect();
        socket.off("aborted:Booking");
        stopUpdate();
        super.onDestroy();
    }


    public void showlanguage(){
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


    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(Map1.this,RequestDetails.class);
        intent.putExtra("init_type", Constants.BOOKING_START);
        intent.putExtra("booking_id", bkngid);
        startActivity(intent);
        finish();*/
        if(path_snapped){
            RequestQueueService.showAlert("","Procced to end activity",this);
        }else {
            super.onBackPressed();
        }
    }

    public void requestUpdate(){
        if(!isRequestingLocation) {
            try {
                isRequestingLocation = true;
                fusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mlocationCallback,
                        Looper.getMainLooper());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void stopUpdate(){
        if(isRequestingLocation) {
            isRequestingLocation = false;
            fusedLocationClient.removeLocationUpdates(mlocationCallback);
        }
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

    private void snapToRoad() {
        try {
            if(!path_snapped) {
                if (OFFSET > 0)
                    OFFSET -= PAGINATION_OVERLAP;
                lowerbound = OFFSET;
                upperbound = Math.min(OFFSET + PAGE_SIZE, travelledpath.size());
                GETAPIRequest getapiRequest = new GETAPIRequest();
                String url = getSnapUrl(lowerbound, upperbound, false);
                HeadersUtil headparam = new HeadersUtil();
                getapiRequest.request(Map1.this, snapToRoadListener, url, headparam);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    FetchDataListener snapToRoadListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            Log.d("SnapRoadResponse:",data.toString());
            try {
                if (data.has("error")) {
                    Toast.makeText(Map1.this,"Error in snap to road.",Toast.LENGTH_LONG);
                } else {
                    if(snappedPoints==null)
                        snappedPoints = new ArrayList<SnappedPoint>();
                    if(snappedArray == null)
                        snappedArray = new JSONArray();
                    JSONArray snaps = data.getJSONArray("snappedPoints");
                    boolean passedOverlap = false;
                    for(int i=0;i<snaps.length();i++){
                        SnappedPoint point = new SnappedPoint();
                        JSONObject snap = snaps.getJSONObject(i);
                        JSONObject location = snap.getJSONObject("location");
                        point.setLatitude(Float.parseFloat(location.getString("latitude")));
                        point.setLongitude(Float.parseFloat(location.getString("longitude")));
                        point.setPlaceid(snap.getString("placeId"));
                        if(snap.has("originalIndex"))
                            point.setOriginalindex(Integer.parseInt(snap.getString("originalIndex")));
                        if (OFFSET == 0 || point.getOriginalindex() >= PAGINATION_OVERLAP - 1) {
                            passedOverlap = true;
                        }
                        if (passedOverlap) {
                            snappedPoints.add(point);
                            snappedArray.put(snap);
                            int size = snappedPoints.size();
                            if(size>1)
                                snappedDistance = snappedDistance+distance(snappedPoints.get(size-1).getLatitude(),snappedPoints.get(size-1).getLongitude(),snappedPoints.get(size).getLatitude(),snappedPoints.get(size).getLongitude());
                        }
                    }
                    OFFSET = upperbound;
                    if(OFFSET<travelledpath.size())
                        snapToRoad();
                    else{
                        JSONObject params = new JSONObject();
                        finalsnap = new JSONObject();
                        finalsnap.put("snappedpoints",snappedArray);
                        /*try {
                            params.put("id", blmod.getBookingid());
                            params.put("snap",finalsnap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        path_snapped=true;
                        //socket.emit("locationUpdate:Booking", params);
                        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(camera_intent, CAMERA_CAPTURE_REQUEST);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(Map1.this,"Error in snap to road",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onFetchFailure(String msg) {
        }

        @Override
        public void onFetchStart() {
        }
    };

    private String getSnapUrl(int lowerbound,int upperbound,boolean isInterpolate) {
        String path = "path=";
        String interpolate = "interpolate=";
        if(isInterpolate)
            interpolate = interpolate+"true";
        else
            interpolate = interpolate+"false";
        String parameters = "";
        parameters = path + "&" + interpolate;
        for(int i=lowerbound;i<upperbound;i++){
            if(i==lowerbound)
                path = path+travelledpath.get(i).latitude+","+travelledpath.get(i).longitude;
            else
                path = path+"|"+travelledpath.get(i).latitude+","+travelledpath.get(i).longitude;
        }
        String url = "https://roads.googleapis.com/v1/snapToRoads?"+ parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d("FetchUrl:",url);
        return url;
    }
    public void showEndTrip(){}
    public void hideEndTrip(){}
}