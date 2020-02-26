package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.Utils;


public class TankerStartingPic extends AppCompatActivity implements View.OnClickListener {

    ImageView img_retake,picture ,calender,clock;
    ImageButton captureImgBtn ;
    TextView txt_retake,date,time,lat,lon,apmm,day;
    LinearLayout retake;
    String imageencoded;
    boolean photo_taken,cameraAccepted;
    public static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_starting_pic);
        initView();

        if(checkPermission( )) {
            /*Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, 0);*/
            cameraAccepted = true;
        }else{
            requestPermission();
        }

        captureImgBtn.setOnClickListener(this);

        if(!photo_taken) {
//            proceed.setText("Capture Bill");
            retake.setVisibility(View.GONE);

        }else{
//            proceed.setText("Proceed");
            retake.setVisibility(View.VISIBLE);
        }
//        back.setOnClickListener(this);




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
        retake= (LinearLayout)findViewById(R.id.ll_tanker_starting_pic_retake);

        retake.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if(resultCode!=0) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageencoded = Utils.encodeTobase64(bitmap);
            picture.setImageBitmap(bitmap);
            retake.setVisibility(View.VISIBLE);
            photo_taken = true;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.ib_tnkr_strt_capture:
                if(photo_taken) {
                    if(SharedPrefUtil.hasKey(this, Constants.SHARED_PREF_CAPTURE_IMAGE_BEFORE,Constants.CAPTURE_IMAGE)){
                        SharedPrefUtil.removePreferenceKey(this,Constants.SHARED_PREF_CAPTURE_IMAGE_BEFORE,Constants.CAPTURE_IMAGE);
                        SharedPrefUtil.setPreferences(this, Constants.SHARED_PREF_CAPTURE_IMAGE_BEFORE,Constants.CAPTURE_IMAGE,imageencoded);
                    }else{
                        SharedPrefUtil.setPreferences(this, Constants.SHARED_PREF_CAPTURE_IMAGE_BEFORE,Constants.CAPTURE_IMAGE,imageencoded);
                    }
                    i = new Intent(this, Map.class);
                    startActivity(i);
                }else{
                    if(cameraAccepted) {
                        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(camera_intent, 0);
                    }else{
                        requestPermission();
                    }
                }
                break;
            case R.id.ll_tanker_starting_pic_retake:
                photo_taken = false;
                retake.setVisibility(View.GONE);
                picture.setImageResource(android.R.color.transparent);
                captureImgBtn.performClick();
                onResume();

                break;


        }
    }

    @Override
    public void onBackPressed() {
    photo_taken=false;
        super.onBackPressed();



    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0){
                    cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        cameraAccepted = true;
                    }else{
                        cameraAccepted = false;
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
