package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.R;

public class Map extends AppCompatActivity implements View.OnClickListener {


    RelativeLayout notifications,bottom;
    TextView title;

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

        }
    }

    public void initViews(){
        notifications = (RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        notifications.setOnClickListener(this);
        title=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        title.setText("Map");
        bottom=(RelativeLayout)findViewById(R.id.rl_map_bottomLayout_text);
        bottom.setOnClickListener(this);
    }
}
