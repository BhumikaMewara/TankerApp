package com.kookyapps.gpstankertracking.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.Modal.SnappedPoint;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Services.TankerLocationCallback;
import com.kookyapps.gpstankertracking.Services.TankerLocationService;
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

public class EnterOTP extends AppCompatActivity implements View.OnClickListener, TankerLocationCallback{
    EditText otpcode, editText_one, editText_two, editText_three, editText_four, editText_five, editText_six;
    private EditText[] editTexts;
    TextView title, message, verify, resend,pageTitle;
    ImageView msg_icon;
    ProgressBar progressBar;
    LinearLayout verifyLayout;
    String imageencoded ,OTP;
    BookingListModal blmod;
    boolean isEndDialogShowing = false;
    Bitmap leftbit;
    String init_type;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<String> allpermissionsrequired;
    private boolean permissionGranted = false;
    private boolean mBound = false;
    private final int LOC_REQUEST = 10101;
    private TankerLocationService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TankerLocationService.LocalBinder binder = (TankerLocationService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
            mService.setServiceCallback(EnterOTP.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService.setServiceCallback(null);
            mService = null;
            mBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        imageencoded = SharedPrefUtil.getStringPreferences(EnterOTP.this, Constants.SHARED_PREF_IMAGE_TAG, Constants.SHARED_END_IMAGE_KEY);
        blmod = b.getParcelable("Bookingdata");
        leftbit = Utils.decodeBase64(imageencoded);
        allpermissionsrequired = new ArrayList<>();
        allpermissionsrequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allpermissionsrequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        initView();
    }

    public class GenericTextWatcher implements TextWatcher{
        private int currentIndex;
        private boolean isFirst = false,isLast = false;
        private String newTypedString="";

        GenericTextWatcher(int currentIndex){
            this.currentIndex = currentIndex;
            if(currentIndex == 0)
                this.isFirst = true;
            else if(currentIndex == editTexts.length-1){
                this.isLast = true;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            newTypedString = charSequence.subSequence(i,i+i2).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = newTypedString;
            if(text.length()>1)
                text= String.valueOf(text.charAt(0));

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if(text.length() == 1){
                moveToNext();
            }else if(text.length()==0)
                moveToPrvious();
        }
        private void moveToNext(){
            if(!isLast){
                editTexts[currentIndex+1].requestFocus();
            }

            if(isAllEditTextsFilled() && isLast){
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }
        private void moveToPrvious(){
            if(!isFirst)
                editTexts[currentIndex-1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts) {
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            }
            return true;
        }

        private void hideKeyboard(){
            if(getCurrentFocus() != null){
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        }
    }

    public class GenericOnKeyListener implements View.OnKeyListener{
        private int currentIndex;
        GenericOnKeyListener(int currentIndex){
            this.currentIndex = currentIndex;
        }
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP){
                if(editTexts[currentIndex].getText().toString().isEmpty() && currentIndex !=0) {
                    editTexts[currentIndex -1].setText("");
                    editTexts[currentIndex - 1].requestFocus();
                }
            }
            return false;
        }
    }
    public void initView() {
        pageTitle=(TextView)findViewById(R.id.tb_with_bck_arrow_title1);
        pageTitle.setText(R.string.enter_otp);
        title = (TextView) findViewById(R.id.tv_enterOtp_msgTitle);
        message = (TextView) findViewById(R.id.tv_enterOtp_msg);
        verify = (TextView) findViewById(R.id.tv_enterOtp_verifyText);
        msg_icon = (ImageView) findViewById(R.id.iv_enterOtp_message);
        otpcode = (EditText) findViewById(R.id.ed_enterOtp_otp);
        progressBar=(ProgressBar)findViewById(R.id.enterotp_progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        verifyLayout = (LinearLayout) findViewById(R.id.lh_enterOtp_verify);
        resend = (TextView) findViewById(R.id.tv_enterOtp_resendText);
        resend.setOnClickListener(this);
        verifyLayout.setOnClickListener(this);
        editText_one   = (EditText) findViewById(R.id.editText_one);
        editText_two   = (EditText) findViewById(R.id.editText_two);
        editText_three = (EditText) findViewById(R.id.editText_three);
        editText_four  = (EditText) findViewById(R.id.editText_four);
        editText_five  = (EditText) findViewById(R.id.editText_fifth);
        editText_six   = (EditText) findViewById(R.id.editText_sixth);
        editTexts = new EditText[]{editText_one,editText_two,editText_three,editText_four,editText_five,editText_six};
        editText_one.addTextChangedListener(new GenericTextWatcher(0));
        editText_two.addTextChangedListener(new GenericTextWatcher(1));
        editText_three.addTextChangedListener(new GenericTextWatcher(2));
        editText_four.addTextChangedListener(new GenericTextWatcher(3));
        editText_five.addTextChangedListener(new GenericTextWatcher(4));
        editText_six.addTextChangedListener(new GenericTextWatcher(5));
        editText_one.setOnKeyListener(new GenericOnKeyListener(0));
        editText_two.setOnKeyListener(new GenericOnKeyListener(1));
        editText_three.setOnKeyListener(new GenericOnKeyListener(2));
        editText_four.setOnKeyListener(new GenericOnKeyListener(3));
        editText_five.setOnKeyListener(new GenericOnKeyListener(4));
        editText_six.setOnKeyListener(new GenericOnKeyListener(5));
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
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
    protected void onStart() {
        checkAndRequestPermissions(EnterOTP.this,allpermissionsrequired);
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        // clear the notification area when the app is opened
        if(SessionManagement.getLanguage(EnterOTP.this).equals(Constants.HINDI_LANGUAGE)) {
            setAppLocale(Constants.HINDI_LANGUAGE);
        }else {
            setAppLocale(Constants.ENGLISH_LANGUAGE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));
    }
    private void checkAndRequestPermissions(Activity activity, ArrayList<String> permissions) {
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }else{
            permissionGranted = true;
            checkLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.MULTIPLE_PERMISSIONS_REQUEST_CODE:
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        permissionGranted = true;
                        if(!(grantResults[i]==PackageManager.PERMISSION_GRANTED)){
                            permissionGranted = false;
                            break;
                        }
                    }
                    if(permissionGranted){
                        checkLocation();
                    }else{
                        checkAndRequestPermissions(EnterOTP.this,allpermissionsrequired);
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void checkLocation() {
        boolean gpsenable=isLocationEnabled();
        if(!gpsenable){
            showLocationAlert();
        }else{
            if(!mBound)
                bindService(new Intent(EnterOTP.this, TankerLocationService.class).putExtra("booking_id",blmod.getBookingid()),mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void showLocationAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        EnterOTP.this.startActivityForResult(myIntent,LOC_REQUEST);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkLocation();
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled && isNetworkEnabled;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_enterOtp_verify:
                verifyLayout.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                validateOTP();
                if (blmod.getBookingid().equals(SharedPrefUtil.getStringPreferences(EnterOTP.this, Constants.SHARED_PREF_ONGOING_TAG, Constants.SHARED_ONGOING_BOOKING_ID))) {
                    uploadBitmap();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    verifyLayout.setClickable(true);
                }
                break;
            case R.id.tv_enterOtp_resendText:
                Intent intent = new Intent(EnterOTP.this,ResendOtp.class);
                intent.putExtra("booking_id",blmod.getBookingid());
                startActivity(intent);
        }
    }

    public void backAlert(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setMessage("You can't go back, Kindly End Trip.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isEndDialogShowing = false;
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        if(!isEndDialogShowing) {
            isEndDialogShowing = true;
            backAlert(EnterOTP.this);
        }
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
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("End Trip:",response.toString());
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj!=null){
                                if(obj.getInt("error")==0){
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EnterOTP.this,TripComplete.class);
                                    intent.putExtra("Bookingdata",blmod);
                                    intent.putExtra("init_type", init_type);
                                    mService.stopService();
                                    if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
                                        SharedPrefUtil.deletePreference(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG);
                                    if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_IMAGE_TAG,Constants.SHARED_END_IMAGE_KEY)){
                                        SharedPrefUtil.removePreferenceKey(EnterOTP.this,Constants.SHARED_PREF_IMAGE_TAG,Constants.SHARED_END_IMAGE_KEY);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    RequestQueueService.showAlert(obj.getString("message"), EnterOTP.this);
                                    verifyLayout.setClickable(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                RequestQueueService.showAlert("Error! No data fetched", EnterOTP.this);
                                verifyLayout.setClickable(true);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            RequestQueueService.showAlert("Something went wrong", EnterOTP.this);
                            e.printStackTrace();
                            verifyLayout.setClickable(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        verifyLayout.setClickable(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            try {
                                JSONObject obj = new JSONObject(new String(response.data));
                                Alert("This Trip has been cancelled",EnterOTP.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            RequestQueueService.showAlert("Something went wrong ", EnterOTP.this);
                        }
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
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    protected void onStop() {
        if(mBound){
            mService.setServiceCallback(null);
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void Alert(String message, final FragmentActivity context) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Alert!");
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
                        SharedPrefUtil.deletePreference(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG);
                    Intent intent = new Intent(EnterOTP.this, FirstActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void abortListener(int abortedBy) {
        if(SharedPrefUtil.hasKey(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG,Constants.SHARED_ONGOING_BOOKING_ID))
            SharedPrefUtil.deletePreference(EnterOTP.this,Constants.SHARED_PREF_ONGOING_TAG);
        if (abortedBy==1)
            abortAlert("This trip has been cancelled by admin",EnterOTP.this);
        else if(abortedBy==-1)
            abortAlert("This trip has been cancelled",EnterOTP.this);
        else
            abortAlert("This trip has been cancelled by controller",EnterOTP.this);
    }

    private void abortAlert(final String message, final FragmentActivity context) {
        try {
            new Thread()
            {
                public void run()
                {
                    EnterOTP.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            //Do your UI operations like dialog opening or Toast here
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setTitle("Alert!");
                            builder.setMessage(message);
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(EnterOTP.this, FirstActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }.start();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void newLocation(Location location) {
        Log.i("Enter OTP:","No Implementation for location updates.");
    }

    @Override
    public void geofenceEnter() {

    }

    @Override
    public void geofenceExit() {

    }
}
