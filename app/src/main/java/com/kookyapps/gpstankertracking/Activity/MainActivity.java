package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
//import com.kookyapps.gpstankertracking.iid.FirebaseInstanceId;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleSignInText,usernameText,passwordText,signInText;
    EditText usernameET,passwordET;
    ImageView usernameImg,passwordImg,eye;
    ConstraintLayout signIn;
    ProgressBar progressBar;
    String username , password;
    boolean pwd_visibility;

    RelativeLayout rl_eye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    public void initViews(){
        titleSignInText=(TextView) findViewById(R.id.tv_main_signInTitle);
        usernameText=(TextView)    findViewById(R.id.tv_main_username);
        passwordText=(TextView)    findViewById(R.id.tv_main_password);
        signInText=(TextView)      findViewById(R.id.tv_main_signInText);
        usernameET=(EditText)      findViewById(R.id.et_main_username);
        passwordET=(EditText)      findViewById(R.id.et_main_password);
        usernameImg=(ImageView)    findViewById(R.id.iv_main_usernameImg);
        passwordImg=(ImageView)    findViewById(R.id.iv_main_passwordImg);
        progressBar=(ProgressBar)  findViewById(R.id.main_progressBar);
        eye=(ImageView)            findViewById(R.id.iv_login_eye);
        rl_eye = (RelativeLayout)findViewById(R.id.rl_login_showpwwd);
        rl_eye.setOnClickListener(this);
        eye.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
        signIn=(ConstraintLayout)      findViewById(R.id.lh_main_signIn);
        signIn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pwd_visibility = false;
        passwordET.setTransformationMethod(new PasswordTransformationMethod());
        eye.setImageResource(R.drawable.ic_eye);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.lh_main_signIn:
                signIn.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                if (validate()) {
                    loginApiCalling();
                }
                break;
            case R.id.iv_login_eye:
                eye.setClickable(false);
                pwd_visibility = !pwd_visibility;
                if(pwd_visibility) {
                    passwordET.setTransformationMethod(null);
                    eye.setImageResource(R.drawable.ic_hidden);
                }
                else {
                    passwordET.setTransformationMethod(new PasswordTransformationMethod());
                    eye.setImageResource(R.drawable.ic_eye);
                }
                eye.setClickable(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(SharedPrefUtil.hasKey(MainActivity.this,Constants.SHARED_PREF_LOGIN_TAG,Constants.SERVER_IP))
            SharedPrefUtil.removePreferenceKey(MainActivity.this,Constants.SHARED_PREF_LOGIN_TAG,Constants.SERVER_IP);
        super.onBackPressed();
    }
    private boolean validate(){
        username = usernameET.getText().toString();
        password = passwordET.getText().toString();
        if (usernameET.getText().toString().equals("")) {
            progressBar.setVisibility(View.GONE);
            RequestQueueService.showAlert("Enter Username",MainActivity.this);
            signIn.setClickable(true);
            return false;

        }
        if (passwordET.getText().toString().equals("")) {
            progressBar.setVisibility(View.GONE);
            RequestQueueService.showAlert("Enter Password",MainActivity.this);
            signIn.setClickable(true);
            return false;
        }
        if (passwordET.getText().toString().length()<8){
            progressBar.setVisibility(View.GONE);
            RequestQueueService.showAlert("Password should be of atleast 8 letters long ",MainActivity.this);
            signIn.setClickable(true);
            return false;
        }

        return true;
    }
    public void loginApiCalling(){
        username = usernameET.getText().toString();
        password = passwordET.getText().toString();
        JSONObject jsonBodyObj = new JSONObject();
        try {
            jsonBodyObj.put("username",username);
            jsonBodyObj.put("password",password);
            jsonBodyObj.put("device_type", "a");
            String token= FirebaseInstanceId.getInstance().getToken();
            Log.d("FCMTOKEN:","token");
            jsonBodyObj.put("device_token", token);
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            String url = URLs.BASE_URL+ URLs.SIGN_IN_URL;
            Log.i("url",String.valueOf(url));
            Log.i("token",String.valueOf(token));
            Log.i("Request",username+", "+password );
            HeadersUtil headparam = new HeadersUtil();
            postapiRequest.request(this, loginApiListener,url,headparam,jsonBodyObj);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    FetchDataListener loginApiListener=new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject object) {
            try {
                if (object != null) {
                    if (object.getInt("error")==0) {
                        Log.i("Login", object.toString());
                        JSONObject userdetail = object.getJSONObject("data");
                        if(userdetail!=null) {
                            String ongoing  = userdetail.getString("booking_ongoing");
                            if(ongoing.equals("true")){
                                RequestQueueService.showAlert("Cannot Login while trip is ongoing", MainActivity.this);
                            }else {
                                if (userdetail.has("notification_count")) {
                                    if (userdetail.getString("notification_count").equals("")) {
                                        SessionManagement.createLoginSession(MainActivity.this,
                                                true, userdetail.getString("tanker_id"),
                                                userdetail.getString("phone_country_code"),
                                                userdetail.getString("phone"),
                                                userdetail.getString("driver_name"),
                                                userdetail.getString("token"),
                                                userdetail.getJSONObject("settings").getString("language"),
                                                userdetail.getString("location"),
                                                "1",
                                                "0",
                                                userdetail.getBoolean("valid"));
                                    } else {
                                        SessionManagement.createLoginSession(MainActivity.this,
                                                true, userdetail.getString("tanker_id"),
                                                userdetail.getString("phone_country_code"),
                                                userdetail.getString("phone"),
                                                userdetail.getString("driver_name"),
                                                userdetail.getString("token"),
                                                userdetail.getJSONObject("settings").getString("language"),
                                                userdetail.getString("location"),
                                                "1",
                                                userdetail.getString("notification_count"),
                                                userdetail.getBoolean("valid"));
                                    }
                                } else {
                                    SessionManagement.createLoginSession(MainActivity.this,
                                            true, userdetail.getString("tanker_id"),
                                            userdetail.getString("phone_country_code"),
                                            userdetail.getString("phone"),
                                            userdetail.getString("driver_name"),
                                            userdetail.getString("token"),
                                            userdetail.getJSONObject("settings").getString("language"),
                                            userdetail.getString("location"),
                                            "1",
                                            "0",
                                            userdetail.getBoolean("valid"));
                                }
                                SessionManagement.setUserStatus(MainActivity.this, userdetail.getString("activity_status"));
                                Intent i = new Intent(MainActivity.this, FirstActivity.class);
                                progressBar.setVisibility(View.GONE);
                                startActivity(i);
                                finish();
                            }
                        }
                        else {
                            RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                            signIn.setClickable(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                    signIn.setClickable(true);
                    progressBar.setVisibility(View.GONE);}
            }catch (Exception e){
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                signIn.setClickable(true);
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            //RequestQueueService.cancelProgressDialog();
            RequestQueueService.showAlert(msg,MainActivity.this);
            signIn.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFetchStart() {

            //RequestQueueService.showProgressDialog(Login.this);
        }
    };
}

