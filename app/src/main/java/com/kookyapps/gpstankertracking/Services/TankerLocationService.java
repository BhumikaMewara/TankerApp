package com.kookyapps.gpstankertracking.Services;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.NetworkUtils;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TankerLocationService extends Service {
    private static final String PACKAGE_NAME = "com.kookyapps.gpstankertracking.Services";
    private static final String TAG = TankerLocationService.class.getSimpleName();
    private static final String CHANNEL_ID = "tanker_channel_02";
    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private TankerLocationCallback serviceCallback;
    /**
     * The current location.
     */
    private Location mLocation;
    private Socket socket;
    private String bookingId="";
    private boolean socketInitialized = false;
    private boolean bookingAborted = false;
    private boolean locationInProcess = false;
    private boolean pathSent = false;
    private String parserString = "";
    boolean requestingCurrentLocation = false;
    boolean oncreatecalled = false;
    private BroadcastReceiver networkStateReceiver;
    boolean networkstatechanged = false;
    int networkstatus;
    public TankerLocationService() {
    }
    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        locationInProcess = false;
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (!locationInProcess) {
                            locationInProcess = true;
                        if(requestingCurrentLocation) {
                            removeLocationUpdates();
                            requestingCurrentLocation = false;
                            locationInProcess = false;
                            onNewLocation(location);
                        }
                        else {
                            if(networkstatus==NetworkUtils.NETWORK_INTERNET||networkstatus==NetworkUtils.WIFI_INTERNET) {
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("id", bookingId);
                                    params.put("lat", location.getLatitude());
                                    params.put("lng", location.getLongitude());
                                    params.put("provider", location.getProvider());
                                    if (location.hasSpeed())
                                        params.put("speed", location.getSpeed());
                                    if (location.hasBearing())
                                        params.put("bearing", location.getBearing());
                                    if (location.hasAccuracy())
                                        params.put("accuracy", location.getAccuracy());
                                    Log.i("OnLocationUpdate", "Emitting Socket");
                                    socket.emit("locationUpdate:Booking", params);
                                } catch (Exception e) {
                                    locationInProcess = false;
                                    e.printStackTrace();
                                }
                            }
                            if (location.hasAccuracy()) {
                                if (location.getAccuracy() < 50) {
                                    mLocation = location;
                                }
                            }
                            locationInProcess = false;
                            onNewLocation(location);
                        }
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("Broadcast:","Network State");
                networkstatechanged = true;
                networkstatus = NetworkUtils.getConnectivityStatus(getApplicationContext());
                if(networkstatus==NetworkUtils.NETWORK_INTERNET||networkstatus==NetworkUtils.WIFI_INTERNET){
                    Log.i("Network State:","Internet Available");
                    resetSocket();
                    networkstatechanged = false;
                }else{
                    Log.i("Network State:","No Internet");
                }
            }
        };
        registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        if(intent.getAction().equals(Constants.STOP_SERVICE_ACTION)){
            stopService();
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        if(bookingId.equals(""))
            bookingId = intent.getStringExtra("booking_id");
        mChangingConfiguration = false;
        if(bookingAborted){
            onAbort(-1);
        }
        getLastLocation();
        if(socket == null || !socketInitialized) {
            initSocket();
            //oncreatecalled = false;
        }
        return mBinder;
    }
    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        if(bookingId.equals(""))
            bookingId = intent.getStringExtra("booking_id");
        mChangingConfiguration = false;
        if(bookingAborted){
            onAbort(-1);
        }
        if(socket == null || !socketInitialized) {
            initSocket();
            //oncreatecalled = false;
        }
        getLastLocation();
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.isRequestingLocation(this)) {
            Log.i(TAG, "Starting foreground service");
            // TODO(developer). If targeting O, use the following code.
           /* if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }*/
           startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }
    @Override
    public void onDestroy() {
        if(serviceIsRunningInForeground(this))
            stopForeground(true);
        mServiceHandler.removeCallbacksAndMessages(null);
    }
    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), TankerLocationService.class).setAction(Constants.START_SERVICE_ACTION));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }
    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            if(requestingCurrentLocation)
                requestingCurrentLocation = false;
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }
    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, TankerLocationService.class);
        String title = "Tanker GPS",text = "Trip Ongoing";
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(text)
                .setContentTitle(title)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        return builder.build();
    }
    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Class used for the client Binder.Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public TankerLocationService getService() {
            return TankerLocationService.this;
        }
    }
    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
    private Emitter.Listener onBookingAborted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("OnAbortListener","Booking aborted");
            if(serviceIsRunningInForeground(TankerLocationService.this))
                stopForeground(true);
            removeLocationUpdates();
            bookingAborted = true;
            int abortedBY = -1;
            if (SharedPrefUtil.hasKey(TankerLocationService.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID))
                SharedPrefUtil.deletePreference(TankerLocationService.this, Constants.SHARED_PREF_ONGOING_TAG);
            JSONObject response = (JSONObject) args[0];
            try {
                Log.i("response", "Booking Aborted " + response.getString("id"));
                abortedBY = response.getInt("aborted_by");
            } catch (JSONException e) {
                e.printStackTrace();
                //onAbort(-1);
            }
            onAbort(abortedBY);
        }
    };

    private Emitter.Listener onGeofenceEnter = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(serviceCallback!=null)
                serviceCallback.geofenceEnter();
        }
    };

    private Emitter.Listener onGeofenceExit = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(serviceCallback!=null)
                serviceCallback.geofenceExit();
        }
    };



    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }
    private void onNewLocation(Location location) {
        if(serviceCallback!=null){
            serviceCallback.newLocation(location);
        }
    }
    public void setServiceCallback(TankerLocationCallback callback){
        serviceCallback = callback;
    }
    public void initSocket(){
        try {
            socket = IO.socket(URLs.SOCKET_URL + SessionManagement.getUserToken(this));
            socket.connect();
            socketInitialized = true;
            socket.on("aborted:Booking", onBookingAborted);
            socket.on("enter:Geofence",onGeofenceEnter);
            socket.on("exit:Geofence",onGeofenceExit);
            JSONObject params = new JSONObject();
            params.put("booking_id", bookingId);
            socket.emit("subscribe:Booking", params);
        }catch (URISyntaxException e){
            e.printStackTrace();
            socketInitialized = false;
        }catch (JSONException e){
            e.printStackTrace();
            socketInitialized = false;
        }
    }
    private void onAbort(int abortedBY){
        if(serviceCallback!=null)
            serviceCallback.abortListener(abortedBY);
        bookingAborted = false;
    }
    public Location getCurrentLocation(){
        getLastLocation();
        return mLocation;
    }
    public boolean isSocketInitialized(){
        return socketInitialized;
    }

    public void stopService(){
        removeLocationUpdates();
        unregisterReceiver(networkStateReceiver);
        closeSocket();
        bookingId = null;
        if(serviceIsRunningInForeground(this))
            stopForeground(true);
        stopSelf();
    }
    public void setPath(String path){
        parserString = path;
        requestLocationUpdates();
    }
    public void requestCurrent(){
        requestingCurrentLocation = true;
        requestLocationUpdates();
    }

    public void resetSocket(){
        closeSocket();
        initSocket();
    }
    public void closeSocket(){
        if(socket!=null){
            Log.i("Socket:","Disconnecting");
            if(socket!=null) {
                if (socket.hasListeners("aborted:Booking"))
                    socket.off("aborted:Booking", onBookingAborted);
                if (socket.connected())
                    socket.disconnect();
                socket.close();
            }
            socketInitialized = false;
            socket=null;
        }
    }
}