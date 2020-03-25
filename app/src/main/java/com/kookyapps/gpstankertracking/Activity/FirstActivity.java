package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.kookyapps.gpstankertracking.Utils.LocaleHelper;
import com.kookyapps.gpstankertracking.app.GPSTracker;
import com.kookyapps.gpstankertracking.fcm.Config;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout navdrawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    ViewPager viewPager;
    LinearLayout l ,tripLayout ,logoutLayout;
    RelativeLayout r ,toolbarNotiCountLayout,toolbarmenuLayout,notificationLayout;
    TextView fullname,username,trip,language,logut,toolBarTitle,pagetitle,bookind_id,distance,from,to,view;
    ImageView tripImg,languageImg,logoutImg,flagImg,toolBarImgMenu,toolBarImgNotification;
    static String notificationCount;
    Button rqstbtn , bkngbtn;
    static FragmentManager fragmentManager;
    ViewPagerAdapter adapter;
     String [] tabTitle ;
    Locale locale;
    Bundle b;
    Context newContext; ;
    public static final int RequestPermissionCode = 7;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView notificationCountText;
    SwitchCompat switchCompat, onlineSwitch;
    static Boolean isTouched = false,isTouched2=true;
    Boolean x=true,permissionGranted = false;
    GPSTracker gpsTracker;
    ArrayList<String> allpermissionsrequired;
    String  stringLatitude,stringLongitude,s12;
    private LocationManager locationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
       // pagetitle=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        toGetAllPermissions();
        gpsTracker = new GPSTracker(this);
        switchCompat=(SwitchCompat)findViewById(R.id.switch2);
        if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)){
            Log.i("language",SessionManagement.getLanguage(this));
            switchCompat.setChecked(true);
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else{
            setAppLocale(Constants.ENGLISH_LANGUAGE);

        }

        onlineSwitch=(SwitchCompat)findViewById(R.id.switch1);
        if(SessionManagement.getUserStatus(FirstActivity.this).equals(Constants.IS_ONLINE)){
            onlineSwitch.setChecked(true);
        }else{
            onlineSwitch.setChecked(false);
        }
        if (gpsTracker.getIsGPSTrackingEnabled()){
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);
        }else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
        onlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    SessionManagement.setUserStatus(FirstActivity.this, Constants.IS_ONLINE);
                    updateLcationApiCalling(Constants.IS_ONLINE);
                } else {
                    SessionManagement.setLanguage(FirstActivity.this, Constants.IS_OFFLINE);
                    updateLcationApiCalling(Constants.IS_OFFLINE);
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
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(FirstActivity.this));
                    setNotificationCount(count+1,false);
                }else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)){
                        setAppLocale(Constants.HINDI_LANGUAGE);
                        languageChangeApi();
                        finish();
                        startActivity(getIntent());

                    }else{
                         setAppLocale(Constants.ENGLISH_LANGUAGE);
                        languageChangeApi();

                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        };
             pagetitle = (TextView)findViewById(R.id.tv_water_tanker_toolbartitle);



        tripLayout.setOnClickListener(this);
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
                //highLightCurrentTab(position);

                //pagetitle.setText(tabTitle[position]);
                if(position==0){
                    rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist_selected));
                    bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist));
                    pagetitle.setText(getString(R.string.request_list));
                }else{
                    rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist));
                    bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist_selected));
                    pagetitle.setText(getString(R.string.booking_list));
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



    public void toGetAllPermissions(){
        // Adding if condition inside button.

        // If All permission is enabled successfully then this block will execute.
        if(CheckingPermissionIsEnabledOrNot())
        {
            Toast.makeText(FirstActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        }
        // If, If permission is not enabled then else condition will execute.
        else {

            //Calling method to enable permission.
            RequestMultiplePermission();

        }
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(FirstActivity.this, new String[]
                {
                        CAMERA,
                        ACCESS_FINE_LOCATION,
                        INTERNET,
                        CALL_PHONE,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }
    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessFineLoaction = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean CallPhonePermisission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStoragePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && AccessFineLoaction && InternetPermission && CallPhonePermisission  && ReadExternalStoragePermission  && WriteExternalStoragePermission) {

                        Toast.makeText(FirstActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(FirstActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int FivthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int SixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FivthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SixthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
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
            postapiRequest.request(FirstActivity.this, updateLocationListner, url, headparam, jsonBodyObj);

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
        switch (view.getId()){
            case R.id.lh_first_triplayout:
                onBackPressed();
                i = new Intent(this, TripDetails.class);
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
                break ;
            case R.id.rl_water_tanker_toolbar_menu_notification:
                i = new Intent(this,Notifications.class);
                startActivity(i);
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
            postapiRequest.request(FirstActivity.this,logoutListner,url,headparam,jsonBodyObj);

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
                       SessionManagement.logout(logoutListner, FirstActivity.this);
                       Intent i = new Intent(FirstActivity.this, MainActivity.class);
                       i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(i);
                       Toast.makeText(FirstActivity.this, "You are now logout", Toast.LENGTH_SHORT).show();
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
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the no04  ki[[vtification area when the app is opened

        if(SessionManagement.getLanguage(FirstActivity.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));


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
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    @Override
    public void onBackPressed() {

        if (navdrawer.isDrawerOpen(GravityCompat.START)) {
            navdrawer.closeDrawer(GravityCompat.START);
        }else {


            // Create the object of
            // AlertDialog Builder class
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(FirstActivity.this);

            // Set the message show for the Alert time
            builder.setMessage("Do you want to exit ?");

            // Set Alert Title
            builder.setTitle("Alert !");

            // Set Cancelable false
            // for when the user clicks on the outside
            // the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the positive button with yes name
            // OnClickListener method is use of
            // DialogInterface interface.

            builder
                    .setPositiveButton(
                            "Yes",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {

                                    // When the user click yes button
                                    // then app will close
                                    finish();
                                }
                            });

            // Set the Negative button with No name
            // OnClickListener method is use
            // of DialogInterface interface.
            builder
                    .setNegativeButton(
                            "No",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {

                                    // If user click no
                                    // then dialog box is canceled.
                                    dialog.cancel();
                                }
                            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
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


}
