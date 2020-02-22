package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.AppCompatActivity;
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


    ArrayList<NotificationModal> notlist;
    RecyclerView notificationlistview;
    NotificationsAdapter adapter;
    String notificationCount;
    LinearLayoutManager mLayoutManager;
    private int totalNotificationCount;
    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 15;
    //private int page_no = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    boolean mIsVisibleToUser;

    ImageView menunotification;
    RelativeLayout menuback;
    TextView pagetitle,nodatadialog;
    ProgressBar notificationprogress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initViews();


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

       // NotificationUtilsFcm.clearNotifications(getContext());
        notificationsApiCall();

    }
    public  void initViews() {

        notificationlistview = (RecyclerView) findViewById(R.id.rv_notification);
        notificationlistview.setVisibility(View.GONE);
        nodatadialog = (TextView) findViewById(R.id.tv_notificationitem_nodata);
        nodatadialog.setVisibility(View.GONE);
        menuback = (RelativeLayout) findViewById(R.id.rl_toolbar_with_back_backLayout);
        menuback.setOnClickListener(this);
        menunotification = (ImageView) findViewById(R.id.iv_tb_with_bck_arrow_notification);
        pagetitle = (TextView) findViewById(R.id.tb_with_bck_arrow_title);
        pagetitle.setText(Constants.NOTIFICATION_PAGE_TITLE);
        notificationprogress = (ProgressBar) findViewById(R.id.pg_notification);
        createNotificationData();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_toolbar_with_back_backLayout:
                onBackPressed();

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void createNotificationData(){
        NotificationModal not1,not2,not3,not4,not5;
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

        setRecyclerView();
    }
    public void setRecyclerView(){
        if(notlist==null){
            notificationprogress.setVisibility(View.GONE);
            nodatadialog.setVisibility(View.VISIBLE);
            notificationlistview.setVisibility(View.GONE);
        }else{
            notificationprogress.setVisibility(View.GONE);
            nodatadialog.setVisibility(View.GONE);
            notificationlistview.setVisibility(View.VISIBLE);
            adapter = new NotificationsAdapter(Notifications.this, notlist);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            notificationlistview.setLayoutManager(mLayoutManager);
            notificationlistview.setAdapter(adapter);
        }
    }


    private void notificationsApiCall(){
        JSONObject jsonBodyObj = new JSONObject();
        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.NOTIFICATION_LIST+"page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this.getApplicationContext(),fetchListener,url,headparam,jsonBodyObj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    FetchDataListener fetchListener=new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject mydata) {
            //RequestQueueService.cancelProgressDialog();
            try {
                if (mydata != null) {
                    if (mydata.getInt("error")==0) {
                        JSONArray array = mydata.getJSONArray("data");
                        if (array.isNull(0)){
                            //SharedPrefUtil.setPreferences(getContext(), Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
                            notificationCount = String.valueOf(0);
                            totalNotificationCount = 0;
                            TOTAL_PAGES= 0;

                            nodatadialog.setVisibility(View.VISIBLE);
                            notificationlistview.setVisibility(View.GONE);
                            notificationprogress.setVisibility(View.GONE);

                        }else {
                            notificationCount = mydata.getString("unread_count");
                            totalNotificationCount = mydata.getInt("total");
                            if (totalNotificationCount > page_size) {
                                if (totalNotificationCount % page_size == 0) {
                                    TOTAL_PAGES = totalNotificationCount / page_size;
                                } else {
                                    TOTAL_PAGES = (totalNotificationCount / page_size) + 1;
                                }
                            }
                        }
                        Log.d("Total_Pages",String.valueOf(TOTAL_PAGES));

                        //SharedPrefUtil.setPreferences(this, Constants.SHARED_PREF_NOTICATION_TAG,Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
                        //FirstActivity.setNotificationCount(Integer.parseInt(notificationCount),false);
                        List<NotificationModal> modalList = new ArrayList<NotificationModal>();

                        if(array!=null) {

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                NotificationModal mod = new NotificationModal();
                                mod.setNotifiactionid(jsonObject.getString("id"));
                                mod.setNotificationmsg(jsonObject.getString(("text")).replaceAll("[\\n]", ""));

                                mod.setNotificationheading(jsonObject.getString("heading"));
                                modalList.add(mod);
                                Log.i("Mod", mod.toString());
                            }
                        }//setNotification
                        Log.d("Notify", mydata.toString());
                        notificationprogress.setVisibility(View.GONE);
                        adapter.addAll(modalList);
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

        }

    };
    public void loadNextPage(){
        Log.d("loadNextPage: ", String.valueOf(currentPage));
        JSONObject jsonBodyObj = new JSONObject();
        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.NOTIFICATION_LIST+"page_size="+page_size+"&page="+currentPage;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this.getApplicationContext().getApplicationContext(),nextListener,url,headparam,jsonBodyObj);
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
                        JSONArray array = mydata.getJSONArray("data");
                        List<NotificationModal> modalList = new ArrayList<NotificationModal>();
                        if(array!=null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                NotificationModal mod = new NotificationModal();
                                mod.setNotifiactionid(jsonObject.getString("id"));
                                mod.setNotificationmsg(jsonObject.getString(("text")).replaceAll("[\\n]", ""));

                                mod.setNotificationheading(jsonObject.getString("heading"));
                                modalList.add(mod);
                                Log.i("Mod", mod.toString());
                            }
                        }
                        //setNotification
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
        }
    };
}
