package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

public class BookingDetails extends AppCompatActivity implements View.OnClickListener {


    LinearLayout  lv_booking_det_first,lv_booking_det_bookg_and_value,lv_booking_det_estimated_distance,
            lv_booking_det_pickup,lv_booking_det_controller,lv_booking_det_customer,lv_booking_det_message,
            start_layout,lh_booking_det_start;
    RelativeLayout rl_booking_det_container,rl_booking_det_booking_id ,rl_booking_det_estimated ,
            rl_booking_det_pickup,rl_booking_det_drop,lv_booking_det_drop,rl_booking_det_controller,
            rl_booking_det_customer, rl_booking_det_message ;
    Toolbar toolbar;
    ScrollView scroll;
    TextView booking,booking_value,est_dist,est_dist_val,pick_pt,pick_pt_value, drp_pt,drp_pt_value,
            ctrl_name,ctrl_name_value,customer_no,customer_no_value, message,message_value,accept ;
    ImageView phone_icn,forward_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        initViews();
    }
    public void initViews(){
       //toolbar = (Toolbar) findViewById(R.layout.toolbar_with_bk_arrow);
      scroll= (ScrollView)findViewById(R.id.sv_booking_det);
        booking =(TextView) findViewById(R.id.tv_booking_det_booking);
        booking_value=(TextView)findViewById(R.id.tv_booking_det_booking_value);
        est_dist=(TextView)findViewById(R.id.tv_booking_det_estimated_distance);
        est_dist_val=(TextView)findViewById(R.id.tv_booking_det_estimated_distance_value);
        pick_pt=(TextView)findViewById(R.id.tv_booking_det_pickup_point);
        pick_pt_value=(TextView)findViewById(R.id.tv_booking_det_pickup_point_value);
        drp_pt=(TextView)findViewById(R.id.tv_booking_det_drop_point);
        drp_pt_value=(TextView)findViewById(R.id.tv_booking_det_drop_point_value);
        ctrl_name=(TextView)findViewById(R.id.tv_booking_det_controller_name);
        ctrl_name_value=(TextView)findViewById(R.id.tv_booking_det_controller_name_value);
        customer_no=(TextView)findViewById(R.id.tv_booking_det_customer_contact_no);
        customer_no_value=(TextView)findViewById(R.id.tv_booking_det_customer_contact_no_value);
        message=(TextView)findViewById(R.id.tv_booking_det_message);
        message_value=(TextView)findViewById(R.id.tv_booking_det_message_value);
        accept=(TextView)findViewById(R.id.tv_booking_det_acceptText);

        phone_icn=(ImageView)findViewById(R.id.iv_booking_det_calling);
        forward_arrow = (ImageView)findViewById(R.id.iv_booking_det_arrow);

        start_layout = (LinearLayout)findViewById(R.id.lh_booking_det_accept);
start_layout.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.lh_booking_det_accept:
                i = new Intent(this,TripComplete.class);
                startActivity(i);
                break;
        }
    }
}
