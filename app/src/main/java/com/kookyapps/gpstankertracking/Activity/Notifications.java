package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Adapters.NotificationsAdapter;
import com.kookyapps.gpstankertracking.Modal.NotificationModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.PaginationScrollListener;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
import com.kookyapps.gpstankertracking.Utils.SharedPrefUtil;
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity implements View.OnClickListener {


  /*  ArrayList<NotificationModal> notlist;
    RecyclerView notificationlistview;
    NotificationsAdapter adapter;
    String notificationCount;
    LinearLayoutManager mLayoutManager;
    private int totalNotificationCount;
    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 10;
    //private int page_no = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    boolean mIsVisibleToUser;
    boolean isListNull = true;
    ImageView menunotification;
    RelativeLayout menuback;
    TextView pagetitle,nodatadialog;
    ProgressBar notificationprogress;
    private int totalBookingCount;
*/




    RecyclerView notificationlistview;
    NotificationsAdapter adapter;

    RelativeLayout menuback,menunotification;
    TextView pagetitle,nodata;
    ProgressBar notificationprogress;

    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 7;
    //private int page_no = 1;
    LinearLayoutManager mLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int totalBookingCount;
    boolean isListNull = true;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initViews();



    }



    public  void initViews() {

        notificationlistview = (RecyclerView)findViewById(R.id.rv_notification);
        notificationlistview.setVisibility(View.GONE);
        nodata = (TextView)findViewById(R.id.tv_notificationitem_nodata);
        nodata.setVisibility(View.GONE);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbarmenu_backimglayout);
        menuback.setOnClickListener(this);

        menunotification = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_notification);
        pagetitle = (TextView)findViewById(R.id.tb_with_bck_arrow_title);
        pagetitle.setText(Constants.NOTIFICATION_PAGE_TITLE);
        notificationprogress = (ProgressBar)findViewById(R.id.pg_notification);
        notificationprogress.setVisibility(View.VISIBLE);

        adapter = new NotificationsAdapter(Notifications.this);
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
        createNotificationData();
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
        super.onBackPressed();
    }

    public void createNotificationData(){
       /* NotificationModal not1,not2,not3,not4,not5;
        not1 = new NotificationModal();
        not2 = new NotificationModal();
        not3 = new NotificationModal();
        not4 = new NotificationModal();
        not5 = new NotificationModal();

        not1.setNotifiactionid("1011");
        not1.setNotificationheading("The Standard Lorem Ipsum passage, used since 1500s");
        not1.setNotificationmsg("Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut dolore magna aliqua");

        not2.setNotifiactionid("1012");
        not2.setNotificationheading("The Standard Lorem Ipsum passage, used since 1500s");
        not2.setNotificationmsg("Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut dolore magna aliqua");

        not3.setNotifiactionid("1013");
        not3.setNotificationheading("The Standard Lorem Ipsum passage, used since 1500s");
        not3.setNotificationmsg("Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut dolore magna aliqua");

        not4.setNotifiactionid("1014");
        not4.setNotificationheading("The Standard Lorem Ipsum passage, used since 1500s");
        not4.setNotificationmsg("Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut dolore magna aliqua");

        not5.setNotifiactionid("1015");
        not5.setNotificationheading("The Standard Lorem Ipsum passage, used since 1500s");
        not5.setNotificationmsg("Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut dolore magna aliqua");

        notlist = new ArrayList<>();
        notlist.add(not1);
        notlist.add(not2);
        notlist.add(not3);
        notlist.add(not4);
        notlist.add(not5);

        setRecyclerView();*/


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
                        if(array!=null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                Log.i("Notification list", jsonObject.toString());
                                NotificationModal bmod = new NotificationModal();
                                bmod.setNotifiactionid(jsonObject.getString("_id"));
                                bmod.setBookingid(jsonObject.getJSONObject("data").getString("booking_id"));
                                bmod.setIsread("0");
                                bmod.setTankerid(jsonObject.getString("tanker_id"));
                                bmod.setText(jsonObject.getJSONObject("text").getString("en"));
                                bmod.setTitle(jsonObject.getJSONObject("title").getString("en"));
                                bmod.setNotificationtype(jsonObject.getString("type"));
                                tmodalList.add(bmod);
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
                setRecyclerView();
            }
        }
        @Override
        public void onFetchFailure(String msg) {
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
                                bmod.setText(jsonObject.getJSONObject("text").getString("en"));
                                bmod.setTitle(jsonObject.getJSONObject("title").getString("en"));
                                bmod.setNotificationtype(jsonObject.getString("type"));
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {
            //RequestQueueService.cancelProgressDialog();
            RequestQueueService.showAlert(msg,Notifications.this);
        }

        @Override
        public void onFetchStart() {

            //RequestQueueService.showProgressDialog(Login.this);
        }

    };

    public void readNotificationApiCall(String notificationId){
        try {
            GETAPIRequest getapiRequest = new GETAPIRequest();
            String url = URLs.BASE_URL + URLs.READ_NOTIFICATIONS+"?id="+notificationId;
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

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {

        }
    };





}