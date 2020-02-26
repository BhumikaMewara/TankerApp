package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.R;

public class EnterOTP extends AppCompatActivity implements View.OnClickListener {

    EditText otpcode;
    TextView title,message,verify,resend;
    ImageView msg_icon ;
    LinearLayout verifyLayout;
    RelativeLayout back,noti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
      initView();

//



    }

    public void initView(){
        otpcode=        (EditText)findViewById(R.id.ed_enterOtp_otp);
        title=          (TextView)findViewById(R.id.tv_enterOtp_msgTitle);
        message=        (TextView)findViewById(R.id.tv_enterOtp_msg);
        verify=         (TextView)findViewById(R.id.tv_enterOtp_verifyText);
        msg_icon=       (ImageView)findViewById(R.id.iv_enterOtp_message);
        otpcode=        (EditText)findViewById(R.id.ed_enterOtp_otp);
        verifyLayout=   (LinearLayout)findViewById(R.id.lh_enterOtp_verify);
        resend=         (TextView)findViewById(R.id.tv_enterOtp_resendText);
        back=           (RelativeLayout)findViewById(R.id.rl_toolbarmenu_backimglayout);
        back.setOnClickListener(this);
        noti=           (RelativeLayout)findViewById(R.id.rl_toolbar_with_back_notification);
        noti.setOnClickListener(this);
        SpannableString content = new SpannableString("Resend OTP");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resend.setText(content);

        verifyLayout.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.lh_enterOtp_verify:
                i = new Intent(this, TripComplete.class);

                startActivity(i);
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;
            case R.id.rl_toolbar_with_back_notification:
                i = new Intent(this,Notifications.class);
                startActivity(i);
                break;
        }
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

}
