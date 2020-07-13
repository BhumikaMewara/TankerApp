package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (SessionManagement.checkSignIn(this)) {

            if (SharedPrefUtil.hasKey(SplashActivity.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID)){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("splash","open map");
                        String booking_id = SharedPrefUtil.getStringPreferences(SplashActivity.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID);
                        Intent i = new Intent(SplashActivity.this, RequestDetails.class);
                        i.putExtra("init_type", Constants.SPLASH_INIT);
                        i.putExtra("booking_id", booking_id);
                        startActivity(i);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }
}
