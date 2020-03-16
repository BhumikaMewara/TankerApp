package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.R;

public class TankerTripEndPic extends AppCompatActivity {

    ImageView img_retake,picture ,calender,clock;

    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day;
    LinearLayout retake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_trip_end_pic);
    }
    public void initView(){
        img_retake =        (ImageView)findViewById(R.id.iv_tnkr_strt_retake);
        picture=            (ImageView)findViewById(R.id.iv_tankr_strt_image_clicked);
        //picture.setImageBitmap(leftbit);
        captureImgBtn =     (ImageButton)findViewById(R.id.ib_tnkr_strt_capture);
        txt_retake=         (TextView) findViewById(R.id.tv_tankr_strt_retakeTxt);
        calender=           (ImageView)findViewById(R.id.iv_tankr_strt_calender);
        clock=              (ImageView)findViewById(R.id.iv_tankr_strt_clock);

        date=               (TextView) findViewById(R.id.tv_tankr_strt_date);
        time=               (TextView) findViewById(R.id.tv_tankr_strt_time_value);
        apmm=               (TextView) findViewById(R.id.tv_tankr_strt_time_ampm);
        lat=                (TextView) findViewById(R.id.tv_tankr_strt_lat);
        lon=                (TextView) findViewById(R.id.tv_tankr_strt_lon);
        retake= (LinearLayout)findViewById(R.id.ll_tanker_starting_pic_retake);



    }

}
