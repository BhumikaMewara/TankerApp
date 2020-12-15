package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.app.GPSTracker;
import com.kookyapps.gpstankertracking.fcm.Config;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.kookyapps.gpstankertracking.Adapters.ViewPagerAdapter;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.fcm.NotificationUtilsFcm;
import com.kookyapps.gpstankertracking.fragment.BookingList;
import com.kookyapps.gpstankertracking.fragment.RequestList;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout navdrawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    ViewPager viewPager;
    LinearLayout l ,tripLayout ,logoutLayout,cancelLayout;
    RelativeLayout r ,toolbarNotiCountLayout,toolbarmenuLayout,notificationLayout;
    TextView fullname,username,trip,language,logut,pagetitle,bookind_id,distance,from,to,view;
    ImageView toolBarImgMenu,toolBarImgNotification;
    static String notificationCount;
    Button rqstbtn , bkngbtn;
    static FragmentManager fragmentManager;
    ViewPagerAdapter adapter;
    int viewpos=0;
    private FusedLocationProviderClient mFusedLocationClient;
    Locale locale;
    Bundle b;
    public static final int RequestPermissionCode = 7;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView notificationCountText;
    SwitchCompat switchCompat, onlineSwitch;
    static Boolean isTouched = false;
    Location currloc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //toGetAllPermissions();
        switchCompat=(SwitchCompat)findViewById(R.id.switch2);
        if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)){
            Log.i("language",SessionManagement.getLanguage(this));
            switchCompat.setChecked(true);
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else{
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        onlineSwitch=(SwitchCompat)findViewById(R.id.switch1);
        if(SessionManagement.getUserStatus(FirstActivity.this).equals(Constants.IS_ONLINE)){
            onlineSwitch.setChecked(true);
        }else{
            onlineSwitch.setChecked(false);
        }
        onlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SessionManagement.setUserStatus(FirstActivity.this, Constants.IS_ONLINE);
                    updateLcationApiCalling(Constants.IS_ONLINE);
                    if(viewpos==0)
                        ((RequestList)adapter.getItem(viewpos)).requestReload();
                } else {
                    SessionManagement.setUserStatus(FirstActivity.this, Constants.IS_OFFLINE);
                    updateLcationApiCalling(Constants.IS_OFFLINE);
                    if(viewpos==0)
                        ((RequestList)adapter.getItem(viewpos)).requestReload();
                }
            }

        });
        toolbar =(Toolbar)findViewById(R.id.water_tanker_toolbar);
        viewPager=(ViewPager)findViewById(R.id.vp_first);
        fullname=(TextView)findViewById(R.id.tv_first_drawer_fullName);
        username=(TextView)findViewById(R.id.tv_first_drawer_username);
        trip=(TextView)findViewById(R.id.tv_drawer_tripText);
        language=(TextView)findViewById(R.id.tv_drawer_language);
        logut=(TextView)findViewById(R.id.tv_drawer_logout);
        toolBarImgMenu =(ImageView)findViewById(R.id.toolbarmenu);
        toolbarmenuLayout=(RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu);
        toolbarmenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerMenu(view);
            }
        });
        notificationLayout= (RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        toolBarImgNotification=  (ImageView)      findViewById(R.id.iv_booking_notification);
        notificationLayout.setOnClickListener(this);
        notificationCountText=(TextView)findViewById(R.id.tv_toolbar_notificationcount);
        toolbarNotiCountLayout=(RelativeLayout)findViewById(R.id.rl_toolbar_notificationcount);
        navdrawer= (DrawerLayout)   findViewById(R.id.dl_first) ;
        //actionBarDrawerToggle = new ActionBarDrawerToggle(this,navdrawer,R.string.drawer_open,R.string.drawer_close);
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
        r=(RelativeLayout)findViewById(R.id.rl_first_insideDL);
        l=(LinearLayout)findViewById(R.id.lv_first_drawer_firstLayout);
        tripLayout=(LinearLayout) findViewById(R.id. lh_first_triplayout);
        cancelLayout=(LinearLayout)findViewById(R.id.lh_first_cancellayout);
        logoutLayout=(LinearLayout)findViewById(R.id.lh_first_logoutLayout);
        rqstbtn= (Button)findViewById(R.id.btn_first_rqstbtn);
        bkngbtn= (Button)findViewById(R.id.btn_first_bookingbtn);
        bookind_id=(TextView)findViewById(R.id.tv_bookingitem_bookingid_title);
        distance=(TextView)findViewById(R.id.tv_bookingitem_distance_title);
        from=(TextView)findViewById(R.id.tv_bookingitem_fromtitle);
        to=(TextView)findViewById(R.id.tv_bookingitem_totitle);
        view=(TextView)findViewById(R.id.tv_bookingitem_viewaction);
        fullname.setText(SessionManagement.getName(FirstActivity.this));
        username.setText(SessionManagement.getUserId(FirstActivity.this));
        /*int noticount = Integer.parseInt(SessionManagement.getNotificationCount(this));
        if(noticount<=0){
            clearNotificationCount();
        }else{
            notificationCountText.setText(String.valueOf(noticount));
            toolbarNotiCountLayout.setVisibility(View.VISIBLE);
        }*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(FirstActivity.this));
                    setNotificationCount(count+1,false);
                    if(viewpos==0){
                        ((RequestList)adapter.getItem(viewpos)).requestReload();
                    }
                }else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)){
                        setAppLocale(Constants.HINDI_LANGUAGE);
                        languageChangeApi();
                        startActivity(getIntent());
                        finish();
                    }else{
                        setAppLocale(Constants.ENGLISH_LANGUAGE);
                        languageChangeApi();
                        startActivity(getIntent());
                        finish();
                    }
                }
            }
        };
        pagetitle = (TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        tripLayout.setOnClickListener(this);
        cancelLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
        rqstbtn.setOnClickListener(this);
        bkngbtn.setOnClickListener(this);
        viewPager = (ViewPager)findViewById(R.id.vp_first);
        fragmentManager = getSupportFragmentManager();
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    viewpos =0;
                    rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist_selected));
                    bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist));
                    pagetitle.setText(getString(R.string.request_list));
                    ((RequestList)adapter.getItem(position)).requestReload();
                }else{
                    viewpos=1;
                    rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist));
                    bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist_selected));
                    pagetitle.setText(getString(R.string.booking_list));
                    ((BookingList)adapter.getItem(position)).bookingReload();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setCurrentTab();
        toolbarmenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerMenu(view);
            }
        });

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
                        SessionManagement.setLanguage(FirstActivity.this, Constants.HINDI_LANGUAGE);
                    }else {
                        SessionManagement.setLanguage(FirstActivity.this, Constants.ENGLISH_LANGUAGE);
                    }
                }
                showlanguage();
                }

        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setCurrentTab();
        super.onNewIntent(intent);
    }

    public void updateLcationApiCalling(String currentStatus) {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            getLastLocation();
            if(currloc!=null) {
                jsonBodyObj.put("lat", currloc.getLatitude());
                jsonBodyObj.put("lng", currloc.getLongitude());
                jsonBodyObj.put("status", currentStatus);
                POSTAPIRequest postapiRequest = new POSTAPIRequest();
                String url = URLs.BASE_URL + URLs.UPDATE_LOCATION ;
                Log.i("url", String.valueOf(url));
                Log.i("Request", String.valueOf(postapiRequest));
                String token = SessionManagement.getUserToken(this);
                Log.i("Token:", token);
                HeadersUtil headparam = new HeadersUtil(token);
                postapiRequest.request(FirstActivity.this, updateLocationListner, url, headparam, jsonBodyObj);
            }else{
                Log.e("UpdateLocation","currloc is null");
            }
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
                        Log.i("UpdateLocationApi",message);
                        Toast.makeText(FirstActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void setCurrentTab(){
        b= getIntent().getExtras();
        if(b != null){
            int currentTab = b.getInt("curretTab", 0);
            viewPager.setCurrentItem(currentTab);
            if(currentTab==0){
                rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist_selected));
                bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist));
            }else{
                rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist));
                bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist_selected));
            }
        } else {
            rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist_selected));
            bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist));
            pagetitle.setText(getString(R.string.request_list));
        }
    }

    private  void setupViewPager(ViewPager viewPager){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RequestList(FirstActivity.this));
        adapter.addFragment(new BookingList(FirstActivity.this));
        viewPager.setAdapter(adapter);
    }
    public void drawerMenu (View view ){
        navdrawer.openDrawer(Gravity.LEFT);
    }
    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()) {
            case R.id.lh_first_triplayout:
                if (navdrawer.isDrawerOpen(GravityCompat.START)) {
                    navdrawer.closeDrawer(GravityCompat.START);
                }
                i = new Intent(this, TripDetails.class);
                i.putExtra("init_type", Constants.COMPLETED_TRIP);
                startActivity(i);
                break;
            case R.id.lh_first_logoutLayout:
                logoutLayout.setClickable(false);
                logutApiCalling();
                break;
            case R.id.btn_first_rqstbtn:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_first_bookingbtn:
                viewPager.setCurrentItem(1);
                break;
            case R.id.rl_water_tanker_toolbar_menu_notification:
                i = new Intent(this, Notifications.class);
                startActivity(i);
                break;
            case R.id.lh_first_cancellayout:
                if (navdrawer.isDrawerOpen(GravityCompat.START)) {
                    navdrawer.closeDrawer(GravityCompat.START);
                }
                i = new Intent(this, TripDetails.class);
                i.putExtra("init_type", Constants.CANCELLED_TRIP);
                startActivity(i);
                break;
        }
    }



    public void logutApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            if(SharedPrefUtil.hasKey(FirstActivity.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID)){
                RequestQueueService.showAlert("Cannot login while trip is ongoing.",FirstActivity.this);
                logoutLayout.setClickable(true);
            }else {
                POSTAPIRequest postapiRequest = new POSTAPIRequest();
                String url = URLs.BASE_URL + URLs.SIGN_OUT_URL;
                Log.i("url", String.valueOf(url));
                Log.i("Request", String.valueOf(postapiRequest));
                String token = SessionManagement.getUserToken(this);
                Log.i("Token:", token);
                HeadersUtil headparam = new HeadersUtil(token);
                postapiRequest.request(FirstActivity.this, logoutListner, url, headparam, jsonBodyObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            logoutLayout.setClickable(true);
        }

    }

    FetchDataListener logoutListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
           try {
               if (data!=null){
                   if (data.getInt("error") == 0) {
                       FirebaseAuth.getInstance().signOut();
                       SessionManagement.logout(logoutListner, FirstActivity.this);
                       Intent i = new Intent(FirstActivity.this, SelectServer.class);
                       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(i);
                       Toast.makeText(FirstActivity.this, "You are now logout", Toast.LENGTH_SHORT).show();
                       finish();
                   }
               }
           } catch (JSONException e){
                    e.printStackTrace();
               logoutLayout.setClickable(true);
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
            postapiRequest.request(FirstActivity.this,languageChangeListner,url,headparam,jsonBodyObj);

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

                       // SessionManagement.logout(logoutListner, FirstActivity.this);
                        Toast.makeText(FirstActivity.this, message, Toast.LENGTH_SHORT).show();

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
        notificationCount = SessionManagement.getNotificationCount(FirstActivity.this);
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
            SharedPrefUtil.setPreferences(FirstActivity.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
            boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if(b2)
                SharedPrefUtil.setPreferences(FirstActivity.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY,"no");
        }
    }
    public void clearNotificationCount(){
        notificationCountText.setText("");
        toolbarNotiCountLayout.setVisibility(View.GONE);
    }

    public void newNotification(){
        Log.i("newNotification","Notification");
        int count = Integer.parseInt(SharedPrefUtil.getStringPreferences(FirstActivity.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY));
        setNotificationCount(count+1,false);
    }

    @Override
    protected void onResume() {
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
        // clear the no04  ki[[vtification area when the app is opened
        getValidity();
        if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }

        String sc = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_COUNT_KEY);
        String vc = notificationCountText.getText().toString();
        int sharedCount,viewCount;
        if(sc.equals("")){
            sharedCount=0;
        }else{
            sharedCount = Integer.parseInt(SharedPrefUtil.getStringPreferences(this,
                    Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_COUNT_KEY));
        }
        boolean b1;
        if(vc.equals("")){
            viewCount=0;
            b1 = sharedCount != viewCount;
        }else if(vc.equals("99+")) {
            if (sharedCount > 99) {
                b1 = false;
            } else {
                b1 = true;
            }
        }else{
            viewCount = Integer.parseInt(vc);
            b1 = sharedCount != viewCount;
        }
        boolean b2 = SharedPrefUtil.getStringPreferences(this, Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
        if (b2) {
            newNotification();
        } else if (b1) {
            if(sharedCount<=0){
                notificationCountText.setText("");
                toolbarNotiCountLayout.setVisibility(View.GONE);
            }else if (sharedCount < 100 && sharedCount > 0) {
                notificationCountText.setText(String.valueOf(sharedCount));
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                toolbarNotiCountLayout.setVisibility(View.VISIBLE);
            }
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    @Override
    public void onBackPressed() {
        if (navdrawer.isDrawerOpen(GravityCompat.START)) {
            navdrawer.closeDrawer(GravityCompat.START);
        }else {
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(FirstActivity.this);
            builder.setMessage("Do you want to exit ?");
            builder.setTitle("Alert !");
            builder.setCancelable(false);
            builder.setPositiveButton(
                            "Yes",
                            new DialogInterface
                                    .OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {
                                    finish();
                                }
                            });
            builder.setNegativeButton(
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

    public void showlanguage(){
            /*SharedPrefUtil.setPreferences(getApplicationContext(), Constants.SHARED_LANGUAGE_LANGUAGE_TAG,
                    Constants.SHARED_LANGUAGE_CHANGED_KEY,"yes");*/
            if (!NotificationUtilsFcm.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent languageChange = new Intent(Config.LANGUAGE_CHANGE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(languageChange);
                //Toast.makeText(this, "language has been changed", Toast.LENGTH_SHORT).show();
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

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                currloc = task.getResult();
                            } else {
                                Log.w("Home Page", "Failed to get location.");
                                currloc = null;
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e("Home Page", "Lost location permission." + unlikely);
            currloc = null;
        }
    }

    private void getValidity(){
        try {
            GETAPIRequest postapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.CHECK_VALIDITY ;
            Log.i("url", String.valueOf(url));
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:", token);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(FirstActivity.this, validityListener, url, headparam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    FetchDataListener validityListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data!=null){
                    if (data.getInt("error") == 0) {
                        boolean b = data.getBoolean("valid");
                        if(b!=SessionManagement.getValidity(FirstActivity.this)) {
                            SessionManagement.setValidity(FirstActivity.this, b);
                            if(viewpos==0)
                                ((RequestList)adapter.getItem(viewpos)).requestReload();
                            else
                                ((BookingList)adapter.getItem(viewpos)).bookingReload();
                        }
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        @Override
        public void onFetchFailure(String msg) {
            Log.e("FirstActivity","Validity Api Failure");
        }

        @Override
        public void onFetchStart() {

        }
    };


}
