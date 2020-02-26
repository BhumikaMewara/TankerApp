package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.kookyapps.gpstankertracking.fragment.BookingList;
import com.kookyapps.gpstankertracking.fragment.RequestList;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout navdrawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    ViewPager viewPager;
    LinearLayout l ,tripLayout ,logoutLayout;
    RelativeLayout r ;
    TextView fullname,username,trip,language,logut,toolBarTitle,pagetitle;
    ImageView tripImg,languageImg,logoutImg,flagImg,toolBarImgMenu,toolBarImgNotification;
    static String notificationCount;
    Button rqstbtn , bkngbtn;
    static FragmentManager fragmentManager;
    ViewPagerAdapter adapter;
    final String [] tabTitle = {"Request List","Booking List"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initViews();

        toolBarImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerMenu(view);
            }
        });
    }
public void initViews(){
    toolbar =                (Toolbar)        findViewById(R.id.water_tanker_toolbar);
    viewPager=               (ViewPager)      findViewById(R.id.vp_first);
    fullname=                (TextView)       findViewById(R.id.tv_first_drawer_fullName);
    username=                (TextView)       findViewById(R.id.tv_first_drawer_username);
    trip=                    (TextView)       findViewById(R.id.tv_drawer_tripText);
    language=                (TextView)       findViewById(R.id.tv_drawer_language);
    logut=                   (TextView)       findViewById(R.id.tv_drawer_logout);
    toolBarImgMenu =         (ImageView)      findViewById(R.id.toolbarmenu);
    toolBarImgNotification=  (ImageView)      findViewById(R.id.iv_booking_notification);
    toolBarImgNotification.setOnClickListener(this);
    //toolBarTitle =           (TextView)       findViewById(R.id.toolbartitle);
    navdrawer=            (DrawerLayout)   findViewById(R.id.dl_first) ;
    actionBarDrawerToggle = new ActionBarDrawerToggle(this,navdrawer,R.string.drawer_open,R.string.drawer_close);
    r=                       (RelativeLayout) findViewById(R.id.rl_first_insideDL);
    l=                       (LinearLayout)   findViewById(R.id.lv_first_drawer_firstLayout);
    tripLayout =             (LinearLayout) findViewById(R.id. lh_first_triplayout);
    logoutLayout=            (LinearLayout)findViewById(R.id.lh_first_logoutLayout);
    rqstbtn= (Button)findViewById(R.id.btn_first_rqstbtn);
    bkngbtn= (Button)findViewById(R.id.btn_first_bookingbtn);
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
            pagetitle.setText(tabTitle[position]);
            if(position==0){
                rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist_selected));
                bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist));
            }else{
                rqstbtn.setBackground(getResources().getDrawable( R.drawable.bg_requestlist));
                bkngbtn.setBackground(getResources().getDrawable( R.drawable.bg_bookinglist_selected));
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    });

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
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.lh_first_triplayout:
                i = new Intent(this, TripDetails.class);
                startActivity(i);
                break;
            case R.id.lh_first_logoutLayout:
                logutApiCalling();
                break;
            case R.id.btn_first_rqstbtn:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_first_bookingbtn:
                viewPager.setCurrentItem(1);
                break ;
            case R.id.iv_booking_notification:
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

    /*public static void setNotificationCount(int count,boolean isStarted){
        notificationCount = SharedPrefUtil.getStringPreferences(context, Constants.SHARED_PREF_NOTICATION_TAG,"notification_count");
        if(Integer.parseInt(notificationCount)!=count || isStarted) {
            notificationCount = String.valueOf(count);
            if (count <= 0) {
                clearNotificationCount();
            } else if (count < 100) {
                bottomnavigation.setCount(1, String.valueOf(count));
            } else {
                bottomnavigation.setCount(1, "9+");
            }
            SharedPrefUtil.setPreferences(context,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
        }
        Fragment frg = fragmentManager.findFragmentById(R.id.firstviewpager);
        if(frg instanceof NotificationsFragment){
            ((NotificationsFragment) frg).reloadNotification();
        }
    }

    public static void clearNotificationCount(){
        bottomnavigation.clearCount(1);
    }
*/


}
