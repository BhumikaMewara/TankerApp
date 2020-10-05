package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ResendOtp extends AppCompatActivity {
    TextView tv_title,tv_message,tv_cancel;
    RelativeLayout rl_cancel,progress;
    Button retry;
    String booking_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_otp);
        setFinishOnTouchOutside(false);
        if (getIntent().hasExtra("booking_id"))
            booking_id = getIntent().getStringExtra("booking_id");
        progress = (RelativeLayout) findViewById(R.id.rl_progress_resend);
        tv_title = (TextView)findViewById(R.id.tv_resend_title);
        tv_title.setText("Please Wait");
        tv_message = (TextView)findViewById(R.id.tv_resend_text);
        tv_message.setText("Sending OTP..");
        tv_cancel = (TextView)findViewById(R.id.tv_resend_cancel);
        rl_cancel = (RelativeLayout) findViewById(R.id.rl_resend_cancel);
        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendOtp.this.finish();
            }
        });
        retry = (Button)findViewById(R.id.btn_retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.setVisibility(View.GONE);
                resendOtpApi();
            }
        });
        disableCancel();
        resendOtpApi();
    }
    public void setTitle(String title){
        tv_title.setText(title);
    }
    public void setMessage(String message){
        tv_message.setText(message);
    }
    public void enableCancel(){
        tv_cancel.setTextColor(getResources().getColor(R.color.colorblack));
        rl_cancel.setClickable(true);
    }
    public void disableCancel(){
        tv_cancel.setTextColor(getResources().getColor(R.color.grey));
        rl_cancel.setClickable(false);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    public void resendOtpApi(){
        if(booking_id!=null) {
            try {
                POSTAPIRequest getapiRequest = new POSTAPIRequest();
                String url = URLs.BASE_URL + URLs.RESEND_OTP + booking_id;
                String token = SessionManagement.getUserToken(this);
                Log.i("Url:", url);
                HeadersUtil headparam = new HeadersUtil(token);
                getapiRequest.request(ResendOtp.this, resendListener, url, headparam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            setMessage("No Booking Id Found.");
        }
    }
    FetchDataListener resendListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data != null) {
                    Log.i("Response:", data.toString());
                    if (data.getInt("error") == 0) {
                        setTitle("Completed");
                        setMessage("OTP sent successfully.");
                        progress.setVisibility(View.GONE);
                        enableCancel();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setMessage("JSON Exception");
                retry.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            setMessage(msg);
            retry.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFetchStart() {

        }
    };


}