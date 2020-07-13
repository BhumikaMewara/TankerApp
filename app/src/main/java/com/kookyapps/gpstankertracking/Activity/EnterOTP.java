package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.Modal.SnappedPoint;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.Utils.Utils;
import com.kookyapps.gpstankertracking.Utils.VolleyMultipartRequest;
import com.kookyapps.gpstankertracking.fcm.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnterOTP extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    EditText otpcode, editText_one, editText_two, editText_three, editText_four, editText_five, editText_six;
    TextView title, message, verify, resend,pageTitle,notificationCountText;
    ImageView msg_icon;
    ProgressBar progressBar;
    LinearLayout verifyLayout;
    RelativeLayout back, noti,notificationCountLayout;
    String imageencoded ,bkngid,OTP;
    BookingListModal blmod;
    Bitmap leftbit;
    String init_type;
    private ArrayList<SnappedPoint> snappedPoints = null;
    static String notificationCount;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int lowerbound=-1,upperbound=-1;
    double snappedDistance=0;
    JSONArray snappedArray;
    JSONObject finalsnap;
    private int OFFSET=0;
    private final int PAGINATION_OVERLAP = 3,PAGE_SIZE = 70;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        imageencoded=SharedPrefUtil.getStringPreferences(EnterOTP.this,Constants.SHARED_PREF_IMAGE_TAG,Constants.SHARED_END_IMAGE_KEY);
        try {
            if(Constants.travelled_path==null) {
                if (SharedPrefUtil.hasKey(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG, Constants.SHARED_ONGOING_TRAVELLED_PATH)) {
                    if (blmod.getBookingid().equals(SharedPrefUtil.getStringPreferences(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))) {
                        Gson gson = new Gson();
                        String json = SharedPrefUtil.getStringPreferences(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG, Constants.SHARED_ONGOING_TRAVELLED_PATH);
                        Type type = new TypeToken<ArrayList<LatLng>>() {
                        }.getType();
                        Constants.travelled_path = gson.fromJson(json, type);
                        SharedPrefUtil.deletePreference(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    //    leftbit = (Bitmap) b.get("Bitmap");
        blmod = b.getParcelable("Bookingdata");
        /*if(b.containsKey("snapped_path")) {
            finalsnap = b.getString("snapped_path");
            snappedDistance = b.getString("snapped_distance");
        }*/
       leftbit = Utils.decodeBase64(imageencoded);
        initView();
    }

    public void initView() {
        //otpcode=        (EditText)findViewById(R.id.ed_enterOtp_otp);
        pageTitle=(TextView)findViewById(R.id.tb_with_bck_arrow_title);
        pageTitle.setText(R.string.enter_otp);
        title = (TextView) findViewById(R.id.tv_enterOtp_msgTitle);
        message = (TextView) findViewById(R.id.tv_enterOtp_msg);
        verify = (TextView) findViewById(R.id.tv_enterOtp_verifyText);
        msg_icon = (ImageView) findViewById(R.id.iv_enterOtp_message);
        otpcode = (EditText) findViewById(R.id.ed_enterOtp_otp);
        progressBar=(ProgressBar)findViewById(R.id.enterotp_progressbar);
        progressBar.setVisibility(View.GONE);
        verifyLayout = (LinearLayout) findViewById(R.id.lh_enterOtp_verify);
        resend = (TextView) findViewById(R.id.tv_enterOtp_resendText);
        back = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        back.setOnClickListener(this);
        noti = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_notification);
        noti.setOnClickListener(this);
        notificationCountLayout=(RelativeLayout)findViewById(R.id.rl_toolbar_notificationcount);
        notificationCountText=(TextView)findViewById(R.id.tv_toolbar_notificationcount);


        verifyLayout.setOnClickListener(this);
        editText_one   = (EditText) findViewById(R.id.editText_one);
        editText_two   = (EditText) findViewById(R.id.editText_two);
        editText_three = (EditText) findViewById(R.id.editText_three);
        editText_four  = (EditText) findViewById(R.id.editText_four);
        editText_five  = (EditText) findViewById(R.id.editText_fifth);
        editText_six   = (EditText) findViewById(R.id.editText_sixth);


        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);
        editText_five.addTextChangedListener(this);
        editText_six.addTextChangedListener(this);

        int noticount = Integer.parseInt(SessionManagement.getNotificationCount(this));
        if(noticount<=0){
            clearNotificationCount();
        }else{
            notificationCountText.setText(String.valueOf(noticount));
            notificationCountLayout.setVisibility(View.VISIBLE);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    int count = Integer.parseInt(SessionManagement.getNotificationCount(EnterOTP.this));
                    setNotificationCount(count+1,false);
                }else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(EnterOTP.this).equals(Constants.HINDI_LANGUAGE)){
                        setAppLocale(Constants.HINDI_LANGUAGE);
                        finish();
                    }else{
                        setAppLocale(Constants.ENGLISH_LANGUAGE);
                        finish();
                        startActivity(getIntent());
                    }
                }
            }
        };







    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.lh_enterOtp_verify:
                verifyLayout.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                validateOTP();
                if(blmod.getBookingid().equals(SharedPrefUtil.getStringPreferences(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID)))
                    if(Constants.isPathSnapped)
                        uploadBitmap();
                    else
                        snapToRoad();

                break;
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

        EditText e =(EditText)getCurrentFocus();
        if (e != null && e.length() > 0){
            View next = e.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
            if (next != null)
                next.requestFocus();
        }

    }


    @Override
    public void afterTextChanged(Editable editable) {

       // editText_one.setFocusable(false);
      //  editText_three.requestFocus();
//asdasdasd
//moveToNext();
        Log.i("setText",String.valueOf(editable.length()));
        if (editable.length() == 1) {

         if (editText_one.length() == 1) {
             editText_two.requestFocus(); }
         if (editText_two.length() == 1) {
             editText_three.requestFocus(); }
         if (editText_three.length() == 1) {
             editText_four.requestFocus();
         }
         if (editText_four.length() == 1) {
             editText_five.requestFocus();
         }
         if (editText_five.length() == 1) {
             editText_six.requestFocus();

         }
         if (editText_six.length() == 1) {
                return; }

        } else if (editable.length() == 0) {
            if (editText_six.length() == 0) {
                editText_five.requestFocus(); }

            if (editText_five.length() == 0) {
                editText_four.requestFocus(); }
            if (editText_four.length() == 0) {
                    editText_three.requestFocus(); }
                if (editText_three.length() == 0) {
                    editText_two.requestFocus(); }
                if (editText_two.length() == 0) {
                    editText_one.requestFocus(); }
            }

    }



    private void moveToNext(){
        editText_one.setFocusable(false);
        editText_three.requestFocus();
    }

private  void validateOTP(){
       if (editText_one.getText().equals("")){
           editText_six.setError(getString(R.string.wrongOtp));
       }else if (editText_two.getText().equals("")){
           editText_six.setError(getString(R.string.wrongOtp));
       }else if (editText_three.getText().equals("")){
           editText_six.setError(getString(R.string.wrongOtp));
       }else if (editText_four.getText().equals("")){
           editText_six.setError(getString(R.string.wrongOtp));
       }else if (editText_five.getText().equals("")){
           editText_six.setError(getString(R.string.wrongOtp));
       }else if (editText_six.getText().equals("")){
        editText_six.setError(getString(R.string.wrongOtp));
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

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("End Trip:",response.toString());
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj!=null){
                                if(obj.getInt("error")==0){
                                    //SessionManagement.setOngoingBooking(EnterOTP.this,blmod.getBookingid());
                                    //SessionManagement.setOngoingBooking(EnterOTP.this,"");
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EnterOTP.this,TripComplete.class);
                                    intent.putExtra("Bookingdata",blmod);
                                    intent.putExtra("init_type", init_type);
                                    Constants.isTripOngoing = false;
                                    Constants.ongoingBookingId = "";
                                    Constants.isPathSnapped = false;
                                    Constants.travelled_path = null;
                                    if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
                                        SharedPrefUtil.deletePreference(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG);
                                    if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_TRIP_TAG,Constants.SHARED_ONGOING_TRAVELLED_PATH))
                                        SharedPrefUtil.deletePreference(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(intent);
                                    finish();
                                }else{

                                    //RequestQueueService.showAlert(obj.getString("code"), EnterOTP.this);
                                    RequestQueueService.showAlert("Error in response", EnterOTP.this);
                                    verifyLayout.setClickable(true);
                                    progressBar.setVisibility(View.GONE);
                                    //   requestLayout.setBackgroundResource(R.drawable.straight_corners);
                                }
                            }else{
                                RequestQueueService.showAlert("Error! No data fetched", EnterOTP.this);
                                verifyLayout.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                                //requestLayout.setBackgroundResource(R.drawable.straight_corners);

                            }
                        } catch (JSONException e) {
                            RequestQueueService.showAlert("Something went wrong", EnterOTP.this);
                            e.printStackTrace();
                            verifyLayout.setClickable(true);
                            progressBar.setVisibility(View.GONE);
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
                        progressBar.setVisibility(View.GONE);
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
                if(finalsnap!=null) {
                    params.put("snapped_path", finalsnap.toString());
                    params.put("distance_traveled", String.valueOf(snappedDistance));
                }
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





    public void setNotificationCount(int count,boolean isStarted){
        notificationCount = SessionManagement.getNotificationCount(EnterOTP.this);
        if(Integer.parseInt(notificationCount)!=count) {
            notificationCount = String.valueOf(count);
            if (count <= 0) {
                clearNotificationCount();
            } else if (count < 100) {
                notificationCountText.setText(String.valueOf(count));
                notificationCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                notificationCountLayout.setVisibility(View.VISIBLE);
            }
            SharedPrefUtil.setPreferences(EnterOTP.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
            boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
            if(b2)
                SharedPrefUtil.setPreferences(EnterOTP.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY,"no");
        }
    }
    public void newNotification(){
        Log.i("newNotification","Notification");
        int count = Integer.parseInt(SharedPrefUtil.getStringPreferences(EnterOTP.this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY));
        setNotificationCount(count+1,false);
    }
    public void clearNotificationCount(){
        notificationCountText.setText("");
        notificationCountLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(Constants.travelled_path==null) {
                if (SharedPrefUtil.hasKey(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG, Constants.SHARED_ONGOING_TRAVELLED_PATH)) {
                    if (blmod.getBookingid().equals(SharedPrefUtil.getStringPreferences(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))) {
                        Gson gson = new Gson();
                        String json = SharedPrefUtil.getStringPreferences(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG, Constants.SHARED_ONGOING_TRAVELLED_PATH);
                        Type type = new TypeToken<ArrayList<LatLng>>() {
                        }.getType();
                        Constants.travelled_path = gson.fromJson(json, type);
                        SharedPrefUtil.deletePreference(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened

        if(SessionManagement.getLanguage(EnterOTP.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
        //change the language when prompt
        int sharedCount =Integer.parseInt(SessionManagement.getNotificationCount(this));
        String viewCount =notificationCountText.getText().toString();
        boolean b1 = String.valueOf("sharedCount")!=viewCount;

        boolean b2 = SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes");
        if(b2){
            newNotification();
        }else if (b1){
            if (sharedCount < 100 && sharedCount>0) {
                notificationCountText.setText(String.valueOf(sharedCount));
                notificationCountLayout.setVisibility(View.VISIBLE);
            } else {
                notificationCountText.setText("99+");
                notificationCountLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    private void snapToRoad() {
        try {
            if(!Constants.isPathSnapped) {
                if (OFFSET > 0)
                    OFFSET -= PAGINATION_OVERLAP;
                lowerbound = OFFSET;
                upperbound = Math.min(OFFSET + PAGE_SIZE, Constants.travelled_path.size());
                GETAPIRequest getapiRequest = new GETAPIRequest();
                String url = getSnapUrl(lowerbound, upperbound, false);
                HeadersUtil headparam = new HeadersUtil();
                getapiRequest.request(EnterOTP.this, snapToRoadListener, url, headparam);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    FetchDataListener snapToRoadListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            Log.d("SnapRoadResponse:",data.toString());
            try {
                if (data.has("error")) {
                    Toast.makeText(EnterOTP.this,"Error in snap to road.",Toast.LENGTH_LONG);
                } else {
                    if(snappedPoints==null)
                        snappedPoints = new ArrayList<SnappedPoint>();
                    if(snappedArray == null)
                        snappedArray = new JSONArray();
                    JSONArray snaps = data.getJSONArray("snappedPoints");
                    boolean passedOverlap = false;
                    for(int i=0;i<snaps.length();i++){
                        SnappedPoint point = new SnappedPoint();
                        JSONObject snap = snaps.getJSONObject(i);
                        JSONObject location = snap.getJSONObject("location");
                        point.setLatitude(Float.parseFloat(location.getString("latitude")));
                        point.setLongitude(Float.parseFloat(location.getString("longitude")));
                        point.setPlaceid(snap.getString("placeId"));
                        if(snap.has("originalIndex"))
                            point.setOriginalindex(Integer.parseInt(snap.getString("originalIndex")));
                        if (OFFSET == 0 || point.getOriginalindex() >= PAGINATION_OVERLAP - 1) {
                            passedOverlap = true;
                        }
                        if (passedOverlap) {
                            snappedPoints.add(point);
                            snappedArray.put(snap);
                            int size = snappedPoints.size();
                            if(size>1)
                                snappedDistance = snappedDistance+distance(snappedPoints.get(size-2).getLatitude(),snappedPoints.get(size-2).getLongitude(),snappedPoints.get(size-1).getLatitude(),snappedPoints.get(size-1).getLongitude());
                        }
                    }
                    OFFSET = upperbound;
                    if(OFFSET<Constants.travelled_path.size())
                        snapToRoad();
                    else{
                        finalsnap = new JSONObject();
                        finalsnap.put("snappedpoints",snappedArray);
                        Constants.isPathSnapped=true;
                        uploadBitmap();

                        //socket.emit("locationUpdate:Booking", params);

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(EnterOTP.this,"Error in snap to road",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onFetchFailure(String msg) {
        }

        @Override
        public void onFetchStart() {
        }
    };

    private String getSnapUrl(int lowerbound,int upperbound,boolean isInterpolate) {
        String path = "path=";
        String interpolate = "interpolate=";
        if(isInterpolate)
            interpolate = interpolate+"true";
        else
            interpolate = interpolate+"false";
        String parameters = "";
        for(int i=lowerbound;i<upperbound;i++){
            if(i==lowerbound)
                path = path+Constants.travelled_path.get(i).latitude+"%2C"+Constants.travelled_path.get(i).longitude;
            else
                path = path+"%7C"+Constants.travelled_path.get(i).latitude+"%2C"+Constants.travelled_path.get(i).longitude;
        }
        parameters = path + "&" + interpolate;
        String url = "https://roads.googleapis.com/v1/snapToRoads?"+ parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d("FetchUrl:",url);
        return url;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist=dist/0.62137;
        dist=dist*1609.34;


        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    protected void onPause() {
        if(Constants.travelled_path!=null) {
            Gson gson = new Gson();
            String json = gson.toJson(Constants.travelled_path);
            SharedPrefUtil.setPreferences(EnterOTP.this, Constants.SHARED_PREF_TRIP_TAG, Constants.SHARED_ONGOING_TRAVELLED_PATH, json);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
