package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.R;

public class EnterOTP extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    EditText otpcode, editText_one, editText_two, editText_three, editText_four, editText_five, editText_six;
    TextView title, message, verify, resend;
    ImageView msg_icon;
    LinearLayout verifyLayout;
    RelativeLayout back, noti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        initView();

//


    }

    public void initView() {
        //otpcode=        (EditText)findViewById(R.id.ed_enterOtp_otp);
        title = (TextView) findViewById(R.id.tv_enterOtp_msgTitle);
        message = (TextView) findViewById(R.id.tv_enterOtp_msg);
        message.setText("Please ask the customer to enter 6 digit verification code");
        verify = (TextView) findViewById(R.id.tv_enterOtp_verifyText);
        msg_icon = (ImageView) findViewById(R.id.iv_enterOtp_message);
        otpcode = (EditText) findViewById(R.id.ed_enterOtp_otp);
        verifyLayout = (LinearLayout) findViewById(R.id.lh_enterOtp_verify);
        resend = (TextView) findViewById(R.id.tv_enterOtp_resendText);
        back = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        back.setOnClickListener(this);
        noti = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_notification);
        noti.setOnClickListener(this);
        SpannableString content = new SpannableString("Resend OTP");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resend.setText(content);

        verifyLayout.setOnClickListener(this);
        editText_one = (EditText) findViewById(R.id.editText_one);
        editText_two = (EditText) findViewById(R.id.editText_two);
        editText_three = (EditText) findViewById(R.id.editText_three);
        editText_four = (EditText) findViewById(R.id.editText_four);
        editText_five = (EditText) findViewById(R.id.editText_fifth);
        editText_six = (EditText) findViewById(R.id.editText_sixth);


        editText_one.addTextChangedListener(this);
        editText_one.addTextChangedListener(this);
        editText_one.addTextChangedListener(this);
        editText_one.addTextChangedListener(this);
        editText_one.addTextChangedListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.lh_enterOtp_verify:
                i = new Intent(this, TripComplete.class);

                startActivity(i);
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;
            case R.id.rl_toolbar_with_back_notification:
                i = new Intent(this, Notifications.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus(); }
            if (editText_two.length() == 1) {
                    editText_three.requestFocus(); }
            if (editText_three.length() == 1) {
                editText_four.requestFocus(); }
        } else if (editable.length() == 0) {
                if (editText_four.length() == 0) {
                    editText_three.requestFocus(); }
                if (editText_three.length() == 0) {
                    editText_two.requestFocus(); }
                if (editText_two.length() == 0) {
                    editText_one.requestFocus(); }
            }

        }

}
