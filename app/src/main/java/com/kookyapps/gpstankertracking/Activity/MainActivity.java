package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.URLs;
//import com.kookyapps.gpstankertracking.iid.FirebaseInstanceId;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleSignInText,usernameText,passwordText,signInText;
    EditText usernameET,passwordET;
    ImageView usernameImg,passwordImg;
    LinearLayout signIn;
    String username , password;

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
        signIn=(LinearLayout)      findViewById(R.id.lh_main_signIn);

        signIn.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.lh_main_signIn:

        Log.i("info","Sign In clicked");
        username = usernameET.getText().toString();
        password = passwordET.getText().toString();
                loginApiCalling();

        break;
        }
    }

    public void loginApiCalling(){
        JSONObject jsonBodyObj = new JSONObject();

        try {
            jsonBodyObj.put("username",username);
            jsonBodyObj.put("password",password);
            jsonBodyObj.put("device_type", "a");
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i("token",token);
            jsonBodyObj.put("device_token", token);
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            String url = URLs.BASE_URL+ URLs.SIGN_IN_URL;
            Log.i("url",String.valueOf(url));
            //Log.i("token",String.valueOf(token));
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
            //RequestQueueService.cancelProgressDialog();
            try {
                if (object != null) {
                    if (object.getInt("error")==0) {
                        Log.i("Login", "Login Successfull");
                        JSONObject userdetail = object.getJSONObject("data");
                        if(userdetail!=null) {
                            SessionManagement.createLoginSession(MainActivity.this,
                                    true, userdetail.getString("_id"),
                                    userdetail.getString("phone_country_code"),
                                    userdetail.getString("phone"),
                                    userdetail.getString("driver_name"),
                                    userdetail.getString("token"),
                                    userdetail.getJSONObject("settings").getString("language"));

                            Intent i = new Intent(MainActivity.this, FirstActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                            signIn.setClickable(true);
                        }
                    }
                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                    signIn.setClickable(true);}
            }catch (Exception e){
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                signIn.setClickable(true);
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            //RequestQueueService.cancelProgressDialog();
            RequestQueueService.showAlert(msg,MainActivity.this);
            signIn.setClickable(true);
        }

        @Override
        public void onFetchStart() {

            //RequestQueueService.showProgressDialog(Login.this);
        }
    };
}

