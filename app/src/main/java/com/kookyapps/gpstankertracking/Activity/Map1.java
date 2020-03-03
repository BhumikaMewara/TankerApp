package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
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



import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;


import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.kookyapps.gpstankertracking.Activity.TankerStartingPic.PERMISSION_REQUEST_CODE;

public class Map1 extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    RelativeLayout notifications,bottom,seemore,details , redLayout;
    TextView title, seemoreText,bookingid,dropPoint,distance,contanctno;
    ImageView seemoreImg ;
    Double toLat , toLong,fromLat,fromLong;
    Animation slideUp, slideDown;
    Boolean t = false,    permissionGranted = false,fromBuildMethod=false, locationCahnge1st=true;
    BookingListModal blmod;
    ArrayList<String> allpermissionsrequired;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 20000;
    private LatLng currentlatlng=null;
    private Marker currentmarker=null;
    SupportMapFragment mapFragment;
    private Socket socket;
    boolean cameraAccepted;
    String imageencoded,can_accept,can_end,can_start;

    /*{
        try{
            socket = IO.socket(URLs.SOCKET_URL+ SessionManagement.getUserToken(this));
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
    }*/

    //Transition transition = new Slide(Gravity.BOTTOM);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        blmod = b.getParcelable("Bookingdata");

        fromLat=Double.parseDouble(blmod.getFromlatitude());
        fromLong=Double.parseDouble(blmod.getFromlongitude());
        toLat=Double.parseDouble(blmod.getTolatitude());
        toLong=Double.parseDouble(blmod.getTologitude());


        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);

       initSocket();
       initViews();



    }
    public void initSocket(){
        try{
            socket = IO.socket(URLs.SOCKET_URL+ SessionManagement.getUserToken(this));
                    socket.connect();


            socket.on("aborted:Booking",onBookingAborted);
            JSONObject params = new JSONObject();

                params.put("booking_id", blmod.getBookingid());
/*                params.put("lat", currentlatlng.latitude);
                params.put("lng",currentlatlng.longitude);*/
                socket.emit("subscribe:Booking",params);


        }catch (URISyntaxException e){
            e.printStackTrace();

        }catch (JSONException e){
            e.printStackTrace();
        }

        /////////////////////////////////////////////////////////////
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng from = new LatLng(fromLat,fromLong);
        LatLng to = new LatLng(toLat,toLong);

        MarkerOptions op1 = new MarkerOptions()
                .position(from)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hydrant_pickuppoint_map));



        MarkerOptions op2 = new MarkerOptions()
                .position(to)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));


        mMap.addMarker(op1);
        mMap.addMarker(op2);
        if(currentlatlng!=null){
            MarkerOptions current = new MarkerOptions()
                    .position(currentlatlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_map));
            currentmarker = mMap.addMarker(current);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 12));
        }

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
                    seemoreText.setText("See Less");
                    seemoreImg.setImageResource(R.drawable.see_fewer_map);
                    details.animate().translationY(0);

                    t = false;
                }else{
                    seemoreText.setText("See More");
                    seemoreImg.setImageResource(R.drawable.see_more_map);
                    details.animate().translationY(-1000);
                    details.setVisibility(View.GONE);

                    //seemore.setVisibility(View.VISIBLE);
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
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if(resultCode!=0) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageencoded = Utils.encodeTobase64(bitmap);
//            if (can_start.equals("true")){
                Intent intent= new Intent(this, TankerStartingPic.class);
                intent.putExtra("Bitmap",imageencoded);
                intent.putExtra("Bookingdata",blmod);
                intent.putExtra("init_type",Constants.TRIP_END_IMG);
                startActivity(intent);
//            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }





    public void initViews(){
        notifications = (RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        notifications.setOnClickListener(this);
        title=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        title.setText("Map1");
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
        distance.setText(blmod.getDistance());
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fg_pickup_map);
        checkAndRequestPermissions(this,allpermissionsrequired);



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
            //createPickUpLocations();
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
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (!permissionGranted) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d("reque", "--->>>>");
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

    @Override
    public void onLocationChanged(Location location) {
        currentlatlng = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentlatlng!=null) {
            // createPickUpLocations();
            if (locationCahnge1st) {
                locationCahnge1st = false;

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


           //To be put in handler ////////////////////////////

            JSONObject params = new JSONObject();
            try {
                params.put("id", blmod.getBookingid());
                params.put("lat", currentlatlng.latitude);
                params.put("lng",currentlatlng.longitude);
            }catch (JSONException e){
                e.printStackTrace();
            }
            socket.emit("locationUpdate:Booking",params);
           /////////////////////////////////////////////////////////////


        } else
            Toast.makeText(Map1.this,"Current location not fetched",Toast.LENGTH_LONG).show();

    }
    /*public void createPickUpLocations(){
        try{

            String token = SessionManagement.getUserToken(Map1.this);
            GETAPIRequest pickuppointrequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.NEARBY_PICKUP_POINTS+"?lat="+currentlatlng.latitude+"&lng="+currentlatlng.longitude;
            Log.i("url",String.valueOf(url));
            Log.i("token",String.valueOf(token));
            HeadersUtil headparam = new HeadersUtil(token);
            pickuppointrequest.request(this, pickuppointrequestlistener,url,headparam);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }*/

    private Emitter.Listener onBookingAborted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Map1.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = (JSONObject)args[0];
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


}