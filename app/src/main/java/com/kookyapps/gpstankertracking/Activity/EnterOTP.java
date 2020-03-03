package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.Utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EnterOTP extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    EditText otpcode, editText_one, editText_two, editText_three, editText_four, editText_five, editText_six;
    TextView title, message, verify, resend;
    ImageView msg_icon;
    LinearLayout verifyLayout;
    RelativeLayout back, noti;
    String imageencoded ,bkngid,OTP;
    BookingListModal blmod;
    Bitmap leftbit;
    String init_type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        imageencoded = b.getString("Bitmap");
        blmod = b.getParcelable("Bookingdata");
        leftbit = Utils.decodeBase64(imageencoded);

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
                verifyLayout.setClickable(false);
                validateOTP();
                uploadBitmap();
                /*   i = new Intent(this, TripComplete.class);

                startActivity(i);*/
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



private  void validateOTP(){
       if (editText_one.getText().equals("")){
           editText_six.setError("entry the otp correctly");
       }else if (editText_two.getText().equals("")){
           editText_six.setError("entry the otp correctly");
       }else if (editText_three.getText().equals("")){
           editText_six.setError("entry the otp correctly");
       }else if (editText_four.getText().equals("")){
           editText_six.setError("entry the otp correctly");
       }else if (editText_five.getText().equals("")){
           editText_six.setError("entry the otp correctly");
       }else if (editText_six.getText().equals("")){
        editText_six.setError("entry the otp correctly");
       }




}

    private void uploadBitmap() {
        OTP = String.valueOf(editText_one.getText())+ String.valueOf(editText_two.getText())+String.valueOf(editText_three.getText())+ String.valueOf(editText_four.getText())+String.valueOf(editText_five.getText())+ String.valueOf(editText_six.getText());
        String url = URLs.BASE_URL + URLs.BOOKING_END+blmod.getBookingid();
        //url = "http://13.233.54.144:8080/api/user/document";
        //our custom volley request
       /* JSONObject params = new JSONObject();
        try {
            params.put("id", blmod.getBookingid());
            params.put("lat", currentlatlng.latitude);
            params.put("lng",currentlatlng.longitude);
        }catch (JSONException e){
            e.printStackTrace();
        }*/

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,url
                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj!=null){
                                if(obj.getInt("error")==0){
                                    SessionManagement.setOngoingBooking(EnterOTP.this,blmod.getBookingid());
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharedPrefUtil.deletePreference(EnterOTP.this,Constants.SHARED_PREF_BOOKING_TAG);
                                    Intent intent = new Intent(EnterOTP.this,TripComplete.class);
                                    intent.putExtra("Bookingdata",blmod);
                                    intent.putExtra("init_type", init_type);


                                    startActivity(intent);
                                    finish();
                                }else{
                                    RequestQueueService.showAlert(obj.getString("code"), EnterOTP.this);
                                    verifyLayout.setClickable(true);
                                    //   requestLayout.setBackgroundResource(R.drawable.straight_corners);
                                }
                            }else{
                                RequestQueueService.showAlert("Error! No data fetched", EnterOTP.this);
                                verifyLayout.setClickable(true);
                                //requestLayout.setBackgroundResource(R.drawable.straight_corners);

                            }
                        } catch (JSONException e) {
                            RequestQueueService.showAlert("Something went wrong", EnterOTP.this);
                            e.printStackTrace();
                            verifyLayout.setClickable(true);
                            //requestLayout.setBackgroundResource(R.drawable.straight_corners);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        verifyLayout.setClickable(true);
                        //requestLayout.setBackgroundResource(R.drawable.straight_corners);
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
           /* @Override
            protected java.util.Map1<String, String> getParams() throws AuthFailureError {
                java.util.Map1<String, String> params = new HashMap<>();
                return params;
            }*/

            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String, String> params = new HashMap<>();
                //params.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZGE5NTQ0M2QzY2U5NTU3MTRhNWY2MzQiLCJleHAiOjE1ODExODM0MjUsImlhdCI6MTU3ODU5MTQyNX0.U7xvdz6ZIwhqj_gGSx3bSfaxvhKoFQyenGdyd3oopgY");
                params.put("Authorization", "Bearer "+SessionManagement.getUserToken(EnterOTP.this));
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected java.util.Map<String,DataPart> getByteData() {
                //Map1<String, DataPart> params = new HashMap<>();
                Map<String,DataPart> imageMap = new HashMap<>();
                long imagename = System.currentTimeMillis();
                String imagename1 = "bill";
                DataPart dataPart= new DataPart(imagename1 + ".png", getFileDataFromDrawable(leftbit), "image/png");
                imageMap.put("image",dataPart);
                return imageMap;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("otp",OTP);
                /*params.put("lat",String.valueOf( currentlatlng.latitude));
                params.put("lng",String.valueOf(currentlatlng.longitude));*/
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}
