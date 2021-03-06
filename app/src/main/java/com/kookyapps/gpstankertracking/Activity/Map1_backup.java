package com.kookyapps.gpstankertracking.Activity;

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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.Modal.SnappedPoint;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchURL;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.TaskLoadedCallback;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.app.GPSTracker;
import com.kookyapps.gpstankertracking.fcm.Config;
import com.kookyapps.gpstankertracking.fcm.NotificationUtilsFcm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

/*import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;*/

public class Map1_backup extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback ,TaskLoadedCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap mMap;
    RelativeLayout bottom,seemore,details , redLayout,r;
    TextView title, seemoreText,bookingid,dropPoint,distance,contanctno,trips,language,logout;
    TextView fullname,username;
    ImageView seemoreImg ;
    ScrollView scrolldetails;
    Double toLat , toLong,fromLat,fromLong,currentLat,currentLong,geofenceDist;

    Animation slideUp, slideDown;
    Boolean t =false,    permissionGranted = false,fromBuildMethod=false, pathfetched=false;
    BookingListModal blmod;
    private String parserstring="";
    ArrayList<String> allpermissionsrequired;
    static ArrayList<LatLng> waypoints = null;

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
    String init_type,bkngid,tanker_id;
    boolean isMarkerRotating = false;

    private int OFFSET=0;
    private final int PAGINATION_OVERLAP = 3,PAGE_SIZE = 95;

    LatLng prevlatlng = null;
    int lowerbound=-1,upperbound=-1;
    double snappedDistance=0;
    JSONArray snappedArray;
    JSONObject finalsnap;
    //boolean path_snapped = false;
    Location droploc,curloc=null;


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
        //gpsTracker = new GPSTracker(this);

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

    public void initViews(){
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
                    if(SessionManagement.getLanguage(Map1_backup.this).equals(Constants.HINDI_LANGUAGE)){
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
                        checkAndRequestPermissions(Map1_backup.this,allpermissionsrequired);
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
            mapFragment.getMapAsync(Map1_backup.this);
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
                        Map1_backup.this.startActivityForResult(myIntent,LOC_REQUEST);
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
                //.setSmallestDisplacement(10);
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
                    //curloc = location;
                    double lt = Double.parseDouble(String.format("%.5f", location.getLatitude()));
                    double lg = Double.parseDouble(String.format("%.5f", location.getLongitude()));
                    currentlatlng = new LatLng(lt, lg);
                    MarkerOptions current = new MarkerOptions()
                            .position(currentlatlng)
                            .flat(true)
                            .anchor(.5f, 0f)
                            .icon(bitmapDescriptorFromVector(Map1_backup.this,R.drawable.ic_truck_icon));
                    if (currentmarker != null)
                        currentmarker.remove();
                    currentmarker = mMap.addMarker(current);
                    if (isRecentered) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 18));
                    }
                    JSONObject params = new JSONObject();
                    try {
                        params.put("id", blmod.getBookingid());
                        params.put("lat", currentlatlng.latitude);
                        params.put("lng", currentlatlng.longitude);
                        params.put("bearing", bearing);
                        socket.emit("locationUpdate:Booking", params);
                    } catch (Exception e) {
                        locationInProcess = false;
                        e.printStackTrace();
                    }
                    //Constants.travelled_path1 .add(location);
                    if (location.hasAccuracy()){
                        if(location.getAccuracy()<50){
                            curloc = location;
                            //double enddist = distance(currentlatlng.latitude,currentlatlng.longitude,dropLatLng.latitude,dropLatLng.longitude);
                        }
                    }
                    locationInProcess = false;
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
        mMap.setOnCameraMoveStartedListener(Map1_backup.this);
        mMap.setOnMarkerClickListener(Map1_backup.this);
        initSocket();
        if (!pathfetched) {
            new FetchURL(Map1_backup.this).execute(getUrl(pickupLatLng, dropLatLng, "driving"), "driving");
            locationInProcess = true;
            pathfetched = true;
        }
        droploc = new Location("");
        droploc.setLatitude(dropLatLng.latitude);
        droploc.setLongitude(dropLatLng.longitude);
       // Constants.travelled_path1.add(droploc);
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
                    params.put("lat", pickupLatLng.latitude);
                    params.put("lng", pickupLatLng.longitude);
                    if (parserstring != "") {
                        params.put("path", parserstring);
                        parserstring = "";
                    }
                    socket.emit("locationUpdate:Booking", params);
                    startLocationUpdates();
                }
            }
            locationInProcess = false;
            prevlatlng = pickupLatLng;
            //requestUpdate();
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



    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_map_bottomLayout_text:
                if (checkPermission()) {
                    cameraAccepted = true;
                } else {
                    requestPermission();
                }
                if (cameraAccepted) {
                    if(curloc!=null){
                        double enddist= curloc.distanceTo(droploc);
                        if (enddist<Integer.parseInt(blmod.getGeofence_in_meter())){
                            showEndAlert();
                        }else{
                            Toast.makeText(Map1_backup.this,"Not within Geofence Range",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(Map1_backup.this,"Not within Geofence Range",Toast.LENGTH_LONG).show();
                    }

                } else {
                    requestPermission();
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

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},Constants.CAMERA_PERMISSION_REQUEST);
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
                    stopUpdate();
                    //Constants.travelled_path.add(dropLatLng);
                    /*if(finalsnap!=null) {
                        intent.putExtra("snapped_path", finalsnap.toString());
                        intent.putExtra("snapped_distance", String.valueOf(snappedDistance));
                    }*/
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

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        if(SessionManagement.getLanguage(Map1_backup.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
        // clear the notification area when the app is opened
    }

    private Emitter.Listener onBookingAborted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            stopUpdate();
            Map1_backup.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
                    try {
                        Log.i("response","Booking Aborted "+response.getString("id"));
                        Integer abortedBy = response.getInt("aborted_by");
                        if (abortedBy==1)
                            Alert("This trip has been cancelled by admin", Map1_backup.this);
                        else
                            Alert("This trip has been cancelled by controller", Map1_backup.this);
                        if(SharedPrefUtil.hasKey(Map1_backup.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
                            SharedPrefUtil.deletePreference(Map1_backup.this,Constants.SHARED_PREF_ONGOING_TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        socket.disconnect();
        socket.off("aborted:Booking");
        stopUpdate();
        super.onDestroy();
    }

    public void Alert(String message, final FragmentActivity context) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Alert!");
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Map1_backup.this, FirstActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            });
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        RequestQueueService.showAlert("","Can not close in middle of trip", Map1_backup.this);
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

    public void showEndTrip(){ bottom.setVisibility(View.VISIBLE); }
    public void hideEndTrip(){
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

    public void showEndAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Map1_backup.this);
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
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                stopUpdate();
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
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}