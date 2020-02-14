package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ViewPager viewPager;
    LinearLayout l;
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
    toolBarTitle =           (TextView)       findViewById(R.id.toolbartitle);
    drawerLayout=            (DrawerLayout)   findViewById(R.id.dl_first) ;
    r=                       (RelativeLayout) findViewById(R.id.rl_first_insideDL);
    l=                       (LinearLayout)   findViewById(R.id.lv_first_drawer_firstLayout);
    }

    public void drawerMenu (View view ){
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(i);
        finish();
    }
}
