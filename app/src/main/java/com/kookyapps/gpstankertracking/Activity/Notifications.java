package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kookyapps.gpstankertracking.Adapters.NotificationsAdapter;
import com.kookyapps.gpstankertracking.Modal.NotificationModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.POSTAPIRequest;
import com.kookyapps.gpstankertracking.Utils.PaginationScrollListener;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;
import com.kookyapps.gpstankertracking.fcm.Config;
import com.kookyapps.gpstankertracking.fcm.NotificationUtilsFcm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Notifications extends AppCompatActivity implements View.OnClickListener {

    RecyclerView notificationlistview;
    NotificationsAdapter adapter;

    RelativeLayout menuback;
    TextView pagetitle,nodata;
    ProgressBar notificationprogress;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    static Context context;

    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 15   ;
    //private int page_no = 1;
    LinearLayoutManager mLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int totalBookingCount;
    boolean isListNull = true,isPush=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        //createNotificationData();
        initViews();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        isPush = false;
        if(b!=null) {
            if (b.containsKey("ispush")) {
                if (b.getString("ispush").equals("1")) {
                    isPush = true;
                }
            }
        }

    }



    public  void initViews() {
        notificationlistview = (RecyclerView)findViewById(R.id.rv_notification);
        notificationlistview.setVisibility(View.GONE);
        nodata = (TextView)findViewById(R.id.tv_notificationitem_nodata);
        nodata.setVisibility(View.GONE);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        menuback.setOnClickListener(this);
        context=this;
        pagetitle = (TextView)findViewById(R.id.tb_with_bck_arrow_title);
        pagetitle.setText(R.string.notifications);
        notificationprogress = (ProgressBar)findViewById(R.id.pg_notification);
        notificationprogress.setVisibility(View.VISIBLE);
        adapter = new NotificationsAdapter(Notifications.this,Constants.NOTIFICATION_INIT);
        mLayoutManager = new LinearLayoutManager(this);
        notificationlistview.setLayoutManager(mLayoutManager);
        notificationlistview.setItemAnimator(new DefaultItemAnimator());
        notificationlistview.setAdapter(adapter);
        notificationlistview.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }
            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    reloadNotification();
                }/*else if(intent.getAction().equals(Config.LANGUAGE_CHANGE)){
                    if(SessionManagement.getLanguage(Notifications.this).equals(Constants.HINDI_LANGUAGE)){
                        Locale locale = new Locale(Constants.HINDI_LANGUAGE);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                    }else{
                        Locale locale = new Locale(Constants.ENGLISH_LANGUAGE);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                    }
                }*/
            }
        };
        NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        nm.cancelAll();
        createNotificationData();
    }
    @Override
    protected void onStart() {
        super.onStart();

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_toolbarmenu_backimglayout:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(isPush){
            Intent i = new Intent(this, FirstActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    public void createNotificationData(){

        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.NOTIFICATION_LIST+"?page_size="+String.valueOf(page_size)+"&page=1";
            Log.i("url", String.valueOf(url));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this.getApplicationContext(),getnotificationlistener,url,headparam);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    FetchDataListener getnotificationlistener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject response) {
            try {
                if (response != null) {
                    if (response.getInt("error")==0) {
                        ArrayList<NotificationModal> tmodalList=new ArrayList<>();
                        JSONArray array = response.getJSONArray("data");
                        totalBookingCount = response.getInt("total");
                        if(totalBookingCount>page_size) {
                            if (totalBookingCount % page_size == 0) {
                                TOTAL_PAGES = totalBookingCount / page_size;
                            } else {
                                TOTAL_PAGES = (totalBookingCount / page_size) + 1;
                            }
                        }
                        if(response.has("unread_count")){
                            SessionManagement.setNotificationCount(Notifications.this,response.getString("unread_count"));
                            //notiCount.setText(response.getString("unread_count"));
                        }
                        if(array!=null) {
                            if (array.length() != 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) array.get(i);
                                    Log.i("Notification list", jsonObject.toString());
                                    NotificationModal bmod = new NotificationModal();
                                    bmod.setNotifiactionid(jsonObject.getString("_id"));
                                    bmod.setBookingid(jsonObject.getJSONObject("data").getString("booking_id"));
                                    bmod.setIsread("0");
                                    bmod.setTankerid(jsonObject.getString("tanker_id"));
                                    bmod.setNotificationtype(jsonObject.getString("type"));
                                    if(SessionManagement.getLanguage(Notifications.this).equals(Constants.HINDI_LANGUAGE)){
                                        bmod.setText(jsonObject.getJSONObject("text").getString("hi"));
                                    }else{
                                        bmod.setText(jsonObject.getJSONObject("text").getString("en"));
                                    }
                                        if (jsonObject.has("title")) {
                                            if(SessionManagement.getLanguage(Notifications.this).equals(Constants.HINDI_LANGUAGE)){
                                            bmod.setTitle(jsonObject.getJSONObject("title").getString("hi"));
                                            }else{
                                            bmod.setTitle(jsonObject.getJSONObject("title").getString("en"));
                                        }
                                    } else {
                                        bmod.setTitle("No Title Recieved");
                                    }
                                    tmodalList.add(bmod);
                                }
                                isListNull = false;
                            }
                        }
                        Log.d("Notification List:",array.toString());
                        isListNull = false;
                        setRecyclerView();
                        //progressBar.setVisibility(View.GONE);
                        adapter.addAll(tmodalList);
                        if (currentPage < TOTAL_PAGES)
                            adapter.addLoadingFooter();
                        else
                            isLastPage = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                notificationprogress.setVisibility(View.GONE);
                setRecyclerView();
            }
        }
        @Override
        public void onFetchFailure(String msg) {
            notificationprogress.setVisibility(View.GONE);
            RequestQueueService.showAlert(msg, Notifications.this);
            setRecyclerView();
        }
        @Override
        public void onFetchStart() {
        }

    };

    public void setRecyclerView(){
        if(isListNull){
            notificationprogress.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
            notificationlistview.setVisibility(View.GONE);
        }else{
            notificationprogress.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            notificationlistview.setVisibility(View.VISIBLE);
        }
    }

    public void loadNextPage(){
        Log.d("loadNextPage: ", String.valueOf(currentPage));
        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.NOTIFICATION_LIST+"?page_size="+page_size+"&page="+currentPage;
            Log.i("url", String.valueOf(url));
            //Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this,nextListener,url,headparam);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    FetchDataListener nextListener=new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject mydata) {
            //RequestQueueService.cancelProgressDialog();

            try {
                if (mydata != null) {
                    if (mydata.getInt("error")==0) {
                        ArrayList<NotificationModal> tmodalList=new ArrayList<>();
                        JSONArray array = mydata.getJSONArray("data");
                        if(array!=null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                Log.i("Notification List", jsonObject.toString());
                                NotificationModal bmod = new NotificationModal();
                                bmod.setNotifiactionid(jsonObject.getString("_id"));
                                bmod.setBookingid(jsonObject.getJSONObject("data").getString("booking_id"));
                                bmod.setIsread("0");
                                bmod.setTankerid(jsonObject.getString("tanker_id"));
                                bmod.setNotificationtype(jsonObject.getString("type"));
                                if(SessionManagement.getLanguage(Notifications.this).equals(Constants.HINDI_LANGUAGE)) {
                                    bmod.setText(jsonObject.getJSONObject("text").getString("hi"));
                                }else{
                                    bmod.setText(jsonObject.getJSONObject("text").getString("en"));
                                }
                                if (jsonObject.has("title")) {
                                    if(SessionManagement.getLanguage(Notifications.this).equals(Constants.HINDI_LANGUAGE)){
                                        bmod.setTitle(jsonObject.getJSONObject("title").getString("hi"));
                                    }else{
                                        bmod.setTitle(jsonObject.getJSONObject("title").getString("en"));
                                    }
                                } else {
                                    bmod.setTitle("No Title Recieved");
                                }


                                tmodalList.add(bmod);
                            }
                        }
                        Log.d("Notification list", mydata.toString());
                        adapter.removeLoadingFooter();
                        isLoading = false;
                        adapter.addAll(tmodalList);
                        if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                }
                notificationprogress.setVisibility(View.GONE);

            } catch (JSONException e) {
                notificationprogress.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            //RequestQueueService.cancelProgressDialog();
            notificationprogress.setVisibility(View.GONE);
            RequestQueueService.showAlert(msg,Notifications.this);
        }

        @Override
        public void onFetchStart() {

            //RequestQueueService.showProgressDialog(Login.this);
        }

    };

    public void readNotificationApiCall(String notificationId){
        try {
            POSTAPIRequest getapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.READ_NOTIFICATIONS+notificationId;
            String token = SessionManagement.getUserToken(this);
            Log.i("Token:",token);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(Notifications.this,readListener,url,headparam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    FetchDataListener readListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data != null) {
                    if (data.getInt("error") == 0) {
                        String count = data.getString("unread_count");
                        SessionManagement.setNotificationCount(Notifications.this,count);
                        //notiCount.setText(count);
                        SharedPrefUtil.setPreferences(context,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,count);
                        adapter.setReadCalled(true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                adapter.setReadCalled(false);
            }
        }

        @Override
        public void onFetchFailure(String msg) {adapter.setReadCalled(false); }

        @Override
        public void onFetchStart() {

        }
    };
    public void reloadNotification(){
        if (SharedPrefUtil.getStringPreferences(this,Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_UPDATE_KEY).equals("yes")) {
            NotificationUtilsFcm.clearNotifications(this);
            adapter.clearNotifications();
            notificationprogress.setVisibility(View.VISIBLE);
            SharedPrefUtil.setPreferences(this, Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_UPDATE_KEY, "no");
            createNotificationData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        /*LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.LANGUAGE_CHANGE));*/
        //change the language of the the app when prompt

    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}