package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.R;

public class Map extends AppCompatActivity implements View.OnClickListener {


    RelativeLayout notifications,bottom,seemore,details , redLayout;
    TextView title, seemoreText;
    ImageView seemoreImg ;
    Animation slideUp, slideDown;
    Boolean t = false;
    //Transition transition = new Slide(Gravity.BOTTOM);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initViews();


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
                i = new Intent(this,EnterOTP.class);
                startActivity(i);
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





    public void initViews(){
        notifications = (RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        notifications.setOnClickListener(this);
        title=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        title.setText("Map");
        bottom=(RelativeLayout)findViewById(R.id.rl_map_bottomLayout_text);
        bottom.setOnClickListener(this);
        seemore = (RelativeLayout)findViewById(R.id.rl_map_seemore);
        seemore.setOnClickListener(this);
        seemoreImg=(ImageView)findViewById(R.id.iv_map_seemore_image);
        seemoreText=(TextView)findViewById(R.id.tv_map_seemore_text);
        details=(RelativeLayout)findViewById(R.id.rl_map_main);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);





    }
}
