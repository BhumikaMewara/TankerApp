package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleSignInText,usernameText,passwordText,signInText;
    EditText usernameET,passwordET;
    ImageView usernameImg,passwordImg;
    LinearLayout signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

getSupportActionBar().hide();
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
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    public void signInClicked(View View){
        Log.i("info","Sign In clicked");
        Intent i=new Intent(this, TankerStartingPic.class);

        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {

        }
    }

