package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.kookyapps.gpstankertracking.Adapters.TripDetailsAdapter;
import com.kookyapps.gpstankertracking.Modal.TripDetailsModal;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripDetails extends AppCompatActivity implements View.OnClickListener {
    ProgressBar tripDetProgressBar;
    TextView nodata,pageTitle,total_trip,totalKm,notificationCountText,bookingid,distance,from,to;
    RecyclerView trip_details_listView;
    TripDetailsAdapter adapter;
    String s;
    Context context;
    RelativeLayout back,distancelay;
    LinearLayoutManager mLayoutManager;
    Switch switch1;
   // TripListAdapter adapter;
    private final int PAGE_START  = 1;
    private int TOTAL_PAGES = 1;
    private static int page_size = 15;
    private int totaltxnCount;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    boolean isListNull = true;
    String init_type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        if(getIntent().hasExtra("init_type"))
            init_type = getIntent().getStringExtra("init_type");
        initViews();
//        createTrip();


    }


    public void initViews(){
        distancelay = (RelativeLayout)findViewById(R.id.rl_trip_details_relativeView);
        if(init_type.equals(Constants.COMPLETED_TRIP))
            distancelay.setVisibility(View.VISIBLE);
        else
            distancelay.setVisibility(View.GONE);
        tripDetProgressBar = (ProgressBar) findViewById(R.id.pb_trip_details);
        total_trip=(TextView)findViewById(R.id.tv_trip_details_totalTrip_value) ;
        totalKm=(TextView)findViewById(R.id.tv_trip_details_totalKM_value);
        nodata = (TextView)findViewById(R.id.tv_trip_details_nodata);
        nodata.setVisibility(View.GONE);
        bookingid=findViewById(R.id.tv_trip_details_bookingid_title);
        distance=findViewById(R.id.tv_trip_details_distance);
        from=findViewById(R.id.tv_trip_details_fromtitle);
        to=findViewById(R.id.tv_trip_details_totitle);
        back=(RelativeLayout)findViewById(R.id.rl_toolbarmenu_backimglayout);
        back.setOnClickListener(this);
        pageTitle=(TextView)findViewById(R.id.tb_with_bck_arrow_title);
        if(init_type.equals(Constants.COMPLETED_TRIP))
            pageTitle.setText(getString(R.string.trips));
        else
            pageTitle.setText(getString(R.string.cancel_trips));
        trip_details_listView=(RecyclerView)findViewById(R.id.rv_trip_details);
        adapter = new TripDetailsAdapter(this,TripDetails.this,init_type);
        mLayoutManager = new LinearLayoutManager(this);
        trip_details_listView.setLayoutManager(mLayoutManager);
        trip_details_listView.setItemAnimator(new DefaultItemAnimator());
        trip_details_listView.setAdapter(adapter);
        trip_details_listView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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
       tripDetailsListApiCalling();





    }

    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_toolbarmenu_backimglayout:
              onBackPressed();
                break;
        }
    }


    public void tripDetailsListApiCalling(){

        JSONObject jsonBodyObj = new JSONObject();
        try {
                GETAPIRequest getapiRequest = new GETAPIRequest();
                String url;
                if(init_type.equals(Constants.COMPLETED_TRIP))
                    url = URLs.BASE_URL+URLs.TRIP_DETAILS+"?page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
                else
                    url = URLs.BASE_URL+URLs.CANCELLED_TRIP_DETAILS+"?page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
//                Log.i("Success Url", String.valueOf(url));
                Log.i("url", String.valueOf(url));
                Log.i("Request", String.valueOf(getapiRequest));
                String token = SessionManagement.getUserToken(this);
                HeadersUtil headparam = new HeadersUtil(token);
                getapiRequest.request(this.getApplicationContext(),tripListener,url,headparam,jsonBodyObj);
            }catch (JSONException e){
                e.printStackTrace();
        }

    }
FetchDataListener tripListener= new FetchDataListener() {
    @Override
    public void onFetchComplete(JSONObject data) {
        try {
            if (data != null) {
                if (data.getInt("error")==0) {
                    total_trip.setText(data.getString("total"));
                    totalKm.setText(data.getString("total_distance"));
                    ArrayList<TripDetailsModal> tripList=new ArrayList<>();
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
                            TripDetailsModal tdmod = new TripDetailsModal();
                            tdmod.setBookingid(jsonObject.getString("_id"));
                            tdmod.setFromtime(jsonObject.getString("trip_start_at"));
                            tdmod.setTotime(jsonObject.getString("trip_end_at"));
                            JSONObject dropPoint = jsonObject.getJSONObject("drop_point");
                            if (dropPoint != null) {
                                tdmod.setTolocation(dropPoint.getString("location"));
                                tdmod.setTo_address(dropPoint.getString("address"));

                                JSONObject geometry = dropPoint.getJSONObject("geometry");
                                if (geometry!=null){
                                    geometry.getString("type");
                                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                                    if (coordinates!=null) {
                                        coordinates.getString(0);//lon
                                        coordinates.getString(1);//lat
                                    }else {
                                        RequestQueueService.showAlert("Error! no value in coordinates", TripDetails.this);
                                    }
                                }else {
                                    RequestQueueService.showAlert("Error! no value in drop geometry", TripDetails.this);
                                }

                            } else {
                                RequestQueueService.showAlert("Error! no for for drop_point found", TripDetails.this);
                            }
                            tdmod.setTankerBookingid(jsonObject.getString("booking_id"));

                            JSONObject pickup = jsonObject.getJSONObject("pickup_point");
                            if (pickup != null) {
                                tdmod.setFromlocation(pickup.getString("location"));
                                tdmod.setFrom_address(pickup.getString("address"));
                                JSONObject geometry=pickup.getJSONObject("geometry");
                                if (geometry!=null){
                                    geometry.getString("type");
                                    JSONArray coordinates=geometry.getJSONArray("coordinates");
                                    if (coordinates!=null){
                                    coordinates.getString(0);//lon
                                    coordinates.getString(1);//lat
                                    }else{
                                        RequestQueueService.showAlert("no value in coordonates",TripDetails.this);
                                    }
                                }else {
                                    RequestQueueService.showAlert("no value in geometry",TripDetails.this);
                                }
                            } else {
                                RequestQueueService.showAlert("Error! no value found in pickup", TripDetails.this);
                            }
                            JSONObject distance = jsonObject.getJSONObject("distance");
                                if (distance != null) {
                                    tdmod.setDistance(distance.getString("text"));
                                } else {
                                    RequestQueueService.showAlert("Error! no data in distance  found", TripDetails.this);
                                }
                            tripList.add(tdmod);
                        }
                    }
                    Log.d("RequestList:", data.toString());
                    adapter.addAll(tripList);
                    setRecyclerView();
                    if (currentPage < TOTAL_PAGES)
                        adapter.addLoadingFooter();
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

        RequestQueueService.showAlert(msg,TripDetails.this);
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
            String url;
            if(init_type.equals(Constants.COMPLETED_TRIP))
                url = URLs.BASE_URL+URLs.TRIP_DETAILS+"?page_size="+page_size+"&page="+currentPage;
            else
                url = URLs.BASE_URL+URLs.CANCELLED_TRIP_DETAILS+"?page_size="+page_size+"&page="+currentPage;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(getapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            getapiRequest.request(this,nextListener,url,headparam,jsonBodyObj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    FetchDataListener nextListener = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data != null) {
                    if (data.getInt("error")==0) {

                        TripDetailsModal tdmod = new TripDetailsModal();
                        tdmod.setTotaltrip(data.getString("total"));
                        total_trip.setText(tdmod.getTotaltrip());
                        tdmod.setTotal_distance(data.getString("total_distance"));
                        totalKm.setText(tdmod.getTotal_distance());


                        ArrayList<TripDetailsModal> tripList=new ArrayList<>();
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

                                tdmod.setBookingid(jsonObject.getString("_id"));
                                tdmod.setFromtime(jsonObject.getString("trip_start_at"));
                                tdmod.setTotime(jsonObject.getString("trip_end_at"));
                                JSONObject dropPoint = jsonObject.getJSONObject("drop_point");
                                if (dropPoint != null) {
                                    tdmod.setTolocation(dropPoint.getString("location"));
                                    tdmod.setTo_address(dropPoint.getString("address"));

                                    JSONObject geometry = dropPoint.getJSONObject("geometry");
                                    if (geometry!=null){
                                        geometry.getString("type");
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates!=null) {
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        }else {
                                            RequestQueueService.showAlert("Error! no value in coordinates", TripDetails.this);
                                        }
                                    }else {
                                        RequestQueueService.showAlert("Error! no value in drop geometry", TripDetails.this);
                                    }

                                } else {
                                    RequestQueueService.showAlert("Error! no for for drop_point found", TripDetails.this);
                                }
                                JSONObject pickup = jsonObject.getJSONObject("pickup_point");
                                if (pickup != null) {


                                    tdmod.setFromlocation(pickup.getString("location"));
                                    tdmod.setFrom_address(pickup.getString("address"));
                                    JSONObject geometry=pickup.getJSONObject("geometry");
                                    if (geometry!=null){
                                        geometry.getString("type");
                                        JSONArray coordinates=geometry.getJSONArray("coordinates");
                                        if (coordinates!=null){
                                            coordinates.getString(0);//lon
                                            coordinates.getString(1);//lat
                                        }else{
                                            RequestQueueService.showAlert("no value in coordonates",TripDetails.this);
                                        }
                                    }else {
                                        RequestQueueService.showAlert("no value in geometry",TripDetails.this);
                                    }
                                } else {
                                    RequestQueueService.showAlert("Error! no value found in pickup", TripDetails.this);
                                }
                                JSONObject distance = jsonObject.getJSONObject("distance");
                                if (distance != null) {
                                    tdmod.setDistance(distance.getString("text"));
                                } else {
                                    RequestQueueService.showAlert("Error! no data in distance  found", TripDetails.this);
                                }
                                tripList.add(tdmod);
                            }
                        }



                        Log.d("RequestList:", data.toString());
                        adapter.removeLoadingFooter();
                        isLoading = false;
                        adapter.addAll(tripList);
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
            RequestQueueService.showAlert(msg,TripDetails.this);
        }

        @Override
        public void onFetchStart() {

        }
    };



    public void setRecyclerView(){
        if(isListNull){
            tripDetProgressBar.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
            trip_details_listView.setVisibility(View.GONE);
        }else{
            tripDetProgressBar.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            trip_details_listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
