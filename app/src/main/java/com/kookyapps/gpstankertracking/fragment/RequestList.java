package com.kookyapps.gpstankertracking.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Activity.FirstActivity;
import com.kookyapps.gpstankertracking.Activity.MainActivity;
import com.kookyapps.gpstankertracking.Adapters.RequestListAdapter;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.kookyapps.gpstankertracking.Utils.FetchDataListener;
import com.kookyapps.gpstankertracking.Utils.GETAPIRequest;
import com.kookyapps.gpstankertracking.Utils.HeadersUtil;
import com.kookyapps.gpstankertracking.Utils.PaginationScrollListener;
import com.kookyapps.gpstankertracking.Utils.RequestQueueService;
import com.kookyapps.gpstankertracking.Utils.SessionManagement;
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
    RelativeLayout progressBar;
    TextView noRequest;
    Context context;
    SwipeRefreshLayout refreshLayout;
    ArrayList<BookingListModal> requestlist;


    ArrayList<BookingListModal> tripList;

    private RequestListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 7;
    private int totaltxnCount;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    boolean isListNull = true;

    public RequestList(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_request_list, container, false);

        recyclerView = (RecyclerView)root.findViewById(R.id.rv_fg_reqstlist);
        progressBar = (RelativeLayout) root.findViewById(R.id.fg_request_progresbar);
        refreshLayout=(SwipeRefreshLayout)root.findViewById(R.id.swipeRefresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                tripList.clear();

                mAdapter.clearAll();
                requestlistApiCalling();
            }
        });
        refreshLayout.setColorSchemeColors (getResources().getColor(R.color.colorPrimary),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_blue_dark));


        noRequest=(TextView)root.findViewById(R.id.tv_requestlist_nodata);
        mAdapter = new RequestListAdapter(context,getActivity(), Constants.REQUEST_DETAILS);
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


        requestlistApiCalling();
        return root;

    }




    private void requestlistApiCalling(){
        JSONObject jsonBodyObj = new JSONObject();
        try{
            GETAPIRequest getapiRequest=new GETAPIRequest();
            String url = URLs.BASE_URL+URLs.REQUEST_LIST+"?page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
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
        public void onFetchComplete(JSONObject data) {
            //RequestQueueService.cancelProgressDialog();
            try {
                if (data != null) {
                    if (data.getInt("error")==0) {
                        tripList=new ArrayList<>();
                        JSONArray array = data.getJSONArray("data");
                        totaltxnCount = data.getInt("total");
                        if (totaltxnCount > page_size) {
                            if (totaltxnCount % page_size == 0) {
                                TOTAL_PAGES = totaltxnCount / page_size;
                            } else {
                                TOTAL_PAGES = (totaltxnCount / page_size) + 1;
                            }
                        }
                        if(array!=null) {
                            isListNull = false;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                BookingListModal tdmod = new BookingListModal();
                                tdmod.setBookingid(jsonObject.getString("_id"));
                                JSONObject dropPoint = jsonObject.getJSONObject("drop_point");
                                if (dropPoint != null) {
                                    tdmod.setTolocation(dropPoint.getString("location"));
                                    tdmod.setToaddress(dropPoint.getString("address"));
                                    tdmod.setGeofence_in_meter(dropPoint.getString("geofence_in_meter"));
                                    JSONObject geometry = dropPoint.getJSONObject("geometry");
                                    if (geometry!=null) {
                                        geometry.getString("type");
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        }else {
                                            RequestQueueService.showAlert("Error! no data in coordinates",getActivity());
                                        }
                                    }else {
                                        RequestQueueService.showAlert("Error! no data in geometry",getActivity());
                                    }
                                    Log.i("dropPoint", "");
                                } else {
                                    RequestQueueService.showAlert("Error! no data in  drop_point", getActivity());
                                }


                                JSONObject pickup = jsonObject.getJSONObject("pickup_point");
                                if (pickup != null) {
                                    tdmod.setFromlocation(pickup.getString("location"));
                                    tdmod.setFromaddress(pickup.getString("address"));
                                    JSONObject geometry = pickup.getJSONObject("geometry");
                                    if (geometry!=null) {
                                        geometry.getString("type");
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        } else {
                                            RequestQueueService.showAlert("Error! no data in coordinates", getActivity());
                                        }

                                    }else {
                                        RequestQueueService.showAlert("Error! no data in geometry",getActivity());
                                    }

                                } else {
                                    RequestQueueService.showAlert("Error! no data in pick_up ", getActivity());
                                }
                                /*JSONObject distance = jsonObject.getJSONObject("distance");
                                if (distance != null) {
                                    tdmod.setDistance(distance.getString("text"));
                                } else {
                                    RequestQueueService.showAlert("Error! no data found", getActivity());
                                }*/


                                tripList.add(tdmod);
                            }
                        }
                        else {
                           noRequest.setText("No Request Found");
                        }
                        Log.d("RequestList:", data.toString());
                        setRecyclerView();
                        mAdapter.addAll(tripList);
                        if (currentPage < TOTAL_PAGES)
                            mAdapter.addLoadingFooter();
                        else
                            isLastPage = true;
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
            String url = URLs.BASE_URL+URLs.REQUEST_LIST+"?page_size="+page_size+"&page="+currentPage;
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
                        List<BookingListModal> modalList = new ArrayList<BookingListModal>();
                        if(array!=null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                BookingListModal tdmod = new BookingListModal();
                                tdmod.setBookingid(jsonObject.getString("_id"));
                                JSONObject dropPoint = jsonObject.getJSONObject("drop_point");
                                if (dropPoint != null) {
                                    tdmod.setTolocation(dropPoint.getString("location"));
                                    tdmod.setToaddress(dropPoint.getString("address"));
                                    tdmod.setGeofence_in_meter(dropPoint.getString("geofence_in_meter"));
                                    JSONObject geometry = dropPoint.getJSONObject("geometry");
                                    if (geometry!=null) {
                                        geometry.getString("type");
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        }else {
                                            RequestQueueService.showAlert("Error! no data in coordinates",getActivity());
                                        }
                                    }else {
                                        RequestQueueService.showAlert("Error! no data in geometry",getActivity());
                                    }

                                    Log.i("dropPoint", "");
                                } else {
                                    RequestQueueService.showAlert("Error! no data in  drop_point", getActivity());
                                }
                                JSONObject pickup = jsonObject.getJSONObject("pickup_point");
                                if (pickup != null) {
                                    tdmod.setFromlocation(pickup.getString("location"));
                                    tdmod.setFromaddress(pickup.getString("address"));
                                    JSONObject geometry = pickup.getJSONObject("geometry");
                                    if (geometry!=null) {
                                        geometry.getString("type");
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates != null) {
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        } else {
                                            RequestQueueService.showAlert("Error! no data in coordinates", getActivity());
                                        }

                                    }else {
                                        RequestQueueService.showAlert("Error! no data in geometry",getActivity());
                                    }

                                } else {
                                    RequestQueueService.showAlert("Error! no data in pick_up", getActivity());
                                }
                                /*JSONObject distance = jsonObject.getJSONObject("distance");
                                if (distance != null) {
                                    tdmod.setDistance(distance.getString("text"));
                                } else {
                                    RequestQueueService.showAlert("Error! no data found", getActivity());
                                }*/

                                tripList.add(tdmod);
                            }
                        }
                        Log.d("RequestList:", mydata.toString());
                        mAdapter.removeLoadingFooter();
                        isLoading = false;
                        mAdapter.addAll(tripList);
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


    public void setRecyclerView(){
        if(isListNull){
            progressBar.setVisibility(View.GONE);
            noRequest.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            noRequest.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}
