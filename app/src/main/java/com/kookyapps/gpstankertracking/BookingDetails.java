package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

public class BookingDetails extends AppCompatActivity {


    LinearLayout  lv_booking_det_first,lv_booking_det_bookg_and_value,lv_booking_det_estimated_distance,
            lv_booking_det_pickup,lv_booking_det_controller,lv_booking_det_customer,lv_booking_det_message,
            lh_booking_det_accept,lh_booking_det_start;
    RelativeLayout rl_booking_det_container,rl_booking_det_booking_id ,rl_booking_det_estimated ,
            rl_booking_det_pickup,rl_booking_det_drop,lv_booking_det_drop,rl_booking_det_controller,
            rl_booking_det_customer, rl_booking_det_message ;
    Toolbar toolbar_with_bk_arrow;
    ScrollView scrollView;
    TextView tv_booking_det_booking,tv_booking_det_booking_value,tv_booking_det_estimated_distance,
            tv_booking_det_estimated_distance_value,tv_booking_det_pickup_point,tv_booking_det_pickup_point_value,
            tv_booking_det_drop_point,tv_booking_det_drop_point_value,tv_booking_det_controller_name,
            tv_booking_det_controller_name_value,tv_booking_det_customer_contact_no,tv_booking_det_customer_contact_no_value,
            tv_booking_det_message,tv_booking_det_message_value,tv_booking_det_acceptText ;
    ImageView iv_booking_det_calling,iv_booking_det_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);




    }
}
