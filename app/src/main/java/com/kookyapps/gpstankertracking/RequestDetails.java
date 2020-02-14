package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RequestDetails extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout l;
    RelativeLayout container;
    TextView tv_rqst_det_booking;
    ImageView iv_reqst_det_calling;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        initViews();


    }
    public void initViews(){
     /*   toolbar= (Toolbar)findViewById(R.id.toolbarmenu_backarrow);
       // container= (RelativeLayout)findViewById(R.id.rl_rqst_det_container);*/



    }
}
