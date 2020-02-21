package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout navdrawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    ViewPager viewPager;
    LinearLayout l ,tripLayout ,logoutLayout;
    RelativeLayout r ;
    TextView fullname,username,trip,language,logut,toolBarTitle;
    ImageView tripImg,languageImg,logoutImg,flagImg,toolBarImgMenu,toolBarImgNotification;

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
    //toolBarTitle =           (TextView)       findViewById(R.id.toolbartitle);
    navdrawer=            (DrawerLayout)   findViewById(R.id.dl_first) ;
    actionBarDrawerToggle = new ActionBarDrawerToggle(this,navdrawer,R.string.drawer_open,R.string.drawer_close);
    r=                       (RelativeLayout) findViewById(R.id.rl_first_insideDL);
    l=                       (LinearLayout)   findViewById(R.id.lv_first_drawer_firstLayout);
    tripLayout =             (LinearLayout) findViewById(R.id. lh_first_triplayout);
    logoutLayout=            (LinearLayout)findViewById(R.id.lh_first_logoutLayout);
    tripLayout.setOnClickListener(this);
    logoutLayout.setOnClickListener(this);

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


}
