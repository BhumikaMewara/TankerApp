package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TankerStartingPic extends AppCompatActivity implements View.OnClickListener {

    ImageView img_retake,picture ,calender,clock;
    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_starting_pic);
        initView();
    }
    public void initView(){
        img_retake =        (ImageView)findViewById(R.id.iv_tnkr_strt_retake);
        picture=            (ImageView)findViewById(R.id.iv_tankr_strt_image_clicked);
        captureImgBtn =     (ImageButton)findViewById(R.id.ib_tnkr_strt_capture);
        txt_retake=         (TextView) findViewById(R.id.tv_tankr_strt_retakeTxt);
        calender=           (ImageView)findViewById(R.id.iv_tankr_strt_calender);
        clock=              (ImageView)findViewById(R.id.iv_tankr_strt_clock);
        day=                (TextView) findViewById(R.id.tv_tankr_strt_day);
        date=               (TextView) findViewById(R.id.tv_tankr_strt_date);
        time=               (TextView) findViewById(R.id.tv_tankr_strt_time_value);
        apmm=               (TextView) findViewById(R.id.tv_tankr_strt_time_ampm);
        lat=                (TextView) findViewById(R.id.tv_tankr_strt_lat);
        lon=                (TextView) findViewById(R.id.tv_tankr_strt_lon);
        captureImgBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.ib_tnkr_strt_capture:
                i = new Intent(TankerStartingPic.this,EnterOTP.class);
                startActivity(i);
                finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
