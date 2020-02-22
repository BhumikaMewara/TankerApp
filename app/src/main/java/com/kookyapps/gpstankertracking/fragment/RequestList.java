package com.kookyapps.gpstankertracking.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.kookyapps.gpstankertracking.Activity.FirstActivity;
import com.kookyapps.gpstankertracking.Adapters.BookingListAdapter;
import com.kookyapps.gpstankertracking.Adapters.RequestListAdapter;
import com.kookyapps.gpstankertracking.Modal.BookingListModel;
import com.kookyapps.gpstankertracking.Modal.RequestListModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestList extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noRequest;

    private RequestListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    private int totalNotificationCount;
    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 15;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    boolean mIsVisibleToUser;
    private String requestCount;


    public RequestList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_request_list, container, false);

        recyclerView = (RecyclerView)root.findViewById(R.id.rv_fg_bookinglist);
        progressBar = (ProgressBar)root.findViewById(R.id.fg_booking_progresbar);
        noRequest=(TextView)root.findViewById(R.id.tv_bookinglist_nodata);

        mAdapter = new RequestListAdapter(getActivity(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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


        bookinglistApiCalling();

        return root;
    }


    private void bookinglistApiCalling(){
        JSONObject jsonBodyObj = new JSONObject();
        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.REQUEST_LIST+"page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(getContext());
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(getActivity().getApplicationContext(),fetchListener,url,headparam,jsonBodyObj);
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
                            requestCount = String.valueOf(0);
                            totalNotificationCount = 0;
                            TOTAL_PAGES= 0;

                            noRequest.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                        }else {
                            requestCount = mydata.getString("unread_count");
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
                        List<RequestListModel>modalList=new ArrayList<RequestListModel>();


                        /*SharedPrefUtil.setPreferences(getContext(), Constants.SHARED_PREF_NOTICATION_TAG, Constants.SHARED_NOTIFICATION_COUNT_KEY,notificationCount);
                        FirstActivity.setNotificationCount(Integer.parseInt(notificationCount),false);
                        List<NotificationModel> modalList = new ArrayList<NotificationModel>();*/

                        if (array!=null){
                            for (int i =0 ;i<array.length();i++){
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                RequestListModel rmod = new RequestListModel();
                                rmod.setBookingid(jsonObject.getString("id"));
                                modalList.add(rmod);
                                Log.i("Mod", rmod.toString());
                            }
                        }
                        //setNotification
                        Log.d("request", mydata.toString());
                        progressBar.setVisibility(View.GONE);
                        mAdapter.addAll(modalList);
                        if (currentPage < TOTAL_PAGES) mAdapter.addLoadingFooter();
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
            RequestQueueService.showAlert(msg,getActivity());
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
            String url = URLs.BASE_URL+URLs.REQUEST_LIST+"page_size="+page_size+"&page="+currentPage;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(getContext());
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(getActivity().getApplicationContext(),nextListener,url,headparam,jsonBodyObj);
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
                        List<RequestListModel> modalList = new ArrayList<RequestListModel>();

                        if (array!=null){
                            for (int i =0 ;i<array.length();i++){
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                RequestListModel rmod = new RequestListModel();
                                rmod.setBookingid(jsonObject.getString("id"));
                                modalList.add(rmod);
                                Log.i("Mod", rmod.toString());
                            }
                        }//setNotification
                        mAdapter.removeLoadingFooter();
                        isLoading = false;
                        Log.d("Notify", mydata.toString());
                        mAdapter.addAll(modalList);
                        if (currentPage < TOTAL_PAGES) mAdapter.addLoadingFooter();
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
            RequestQueueService.showAlert(msg,getActivity());
        }

        @Override
        public void onFetchStart() {

        }

    };


}
