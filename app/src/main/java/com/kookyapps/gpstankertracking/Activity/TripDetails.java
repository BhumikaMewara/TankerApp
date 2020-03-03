package com.kookyapps.gpstankertracking.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.kookyapps.gpstankertracking.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TripDetails extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout navdrawer;
    ImageView toolbarmenu  ;
    ProgressBar tripDetProgressBar;
    TextView nodata,pageTitle,trip,language,logutText,total_trip,totalKm;
    RecyclerView trip_details_listView;
    TripDetailsAdapter adapter;
    String s;
    Context context;
    RelativeLayout notification ,back;
    ActionBarDrawerToggle actionBarDrawerToggle;
    LinearLayout l,logout,tripLayout;
    ArrayList<TripDetailsModal>tripDetailList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        initViews();
//        createTrip();


    }


    public void initViews(){
        tripDetProgressBar = (ProgressBar) findViewById(R.id.pb_trip_details);
        total_trip=(TextView)findViewById(R.id.tv_trip_details_totalTrip_value) ;
        totalKm=(TextView)findViewById(R.id.tv_trip_details_totalKM_value);
        nodata = (TextView)findViewById(R.id.tv_trip_details_nodata);
        nodata.setVisibility(View.GONE);
        pageTitle=(TextView)findViewById(R.id.tb_with_bck_arrow_title);
        trip_details_listView=(RecyclerView)findViewById(R.id.rv_trip_details);
        notification=(RelativeLayout)findViewById(R.id.rl_toolbar_with_back_notification);
        back=(RelativeLayout)findViewById(R.id.rl_toolbarmenu_backimglayout);
        back.setOnClickListener(this);
        notification.setOnClickListener(this);

        trip=(TextView)findViewById(R.id.tv_tripdetails_tripText);
        language=(TextView)findViewById(R.id.tv_tripdetails_language);

        logutText=(TextView)findViewById(R.id.tv_tripdetails_logout);
        logout=(LinearLayout)findViewById(R.id.lh_tripdetails_logoutLayout);

        navdrawer=            (DrawerLayout)   findViewById(R.id.dl_trip_details) ;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,navdrawer,R.string.drawer_open,R.string.drawer_close);

        l=                       (LinearLayout)   findViewById(R.id.lv_tripdetails_drawer_firstLayout);
        tripLayout =             (LinearLayout) findViewById(R.id. lh_tripdetails_triplayout);

        tripLayout.setOnClickListener(this);
        logout.setOnClickListener(this);



        adapter = new TripDetailsAdapter(this,TripDetails.this,Constants.TRIP_DETAILS);

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

    public void drawerMenu (View view ){
        navdrawer.openDrawer(Gravity.LEFT);
    }



    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_water_tanker_toolbar_menu_notification:
                Intent intent;
                intent = new Intent(TripDetails.this,Notifications.class);
                startActivity(intent);
                break;
            case R.id.rl_water_tanker_toolbar_menu:
              onBackPressed();
                break;
            case R.id.lh_tripdetails_logoutLayout:
                logutApiCalling();
                break;


        }
    }

    public void logutApiCalling() {
        JSONObject jsonBodyObj = new JSONObject();
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            String url = URLs.BASE_URL + URLs.SIGN_OUT_URL;
            Log.i("url", String.valueOf(url));
            Log.i("Request", String.valueOf(postapiRequest));
            String token = SessionManagement.getUserToken(this);
            HeadersUtil headparam = new HeadersUtil(token);
            postapiRequest.request(TripDetails.this,logoutListner,url,headparam,jsonBodyObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    FetchDataListener logoutListner = new FetchDataListener() {
        @Override
        public void onFetchComplete(JSONObject data) {
            try {
                if (data!=null){
                    if (data.getInt("error") == 0) {
                        FirebaseAuth.getInstance().signOut();
                        SessionManagement.logout(logoutListner, TripDetails.this);
                        Intent i = new Intent(TripDetails.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        Toast.makeText(TripDetails.this, "You are now logout", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }


        @Override
        public void onFetchFailure(String msg) {
            logout.setClickable(true);
        }

        @Override
        public void onFetchStart() {

        }
    };


    public void tripDetailsListApiCalling(){

        JSONObject jsonBodyObj = new JSONObject();
        try {

                GETAPIRequest getapiRequest = new GETAPIRequest();
                String url = URLs.BASE_URL+URLs.TRIP_DETAILS+"?page_size="+String.valueOf(page_size)+"&page="+String.valueOf(PAGE_START);
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
            String url = URLs.BASE_URL+URLs.TRIP_DETAILS+"?page_size="+page_size+"&page="+currentPage;
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
                        JSONArray array = data.getJSONArray("data");
                        List<TripDetailsModal> modalList = new ArrayList<TripDetailsModal>();
                        if(array!=null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = (JSONObject) array.get(i);
                                TripDetailsModal tdmod = new TripDetailsModal();
                                tdmod.setBookingid(jsonObject.getString("_id"));
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
                                    Log.i("dropPoint", "");
                                } else {
                                    RequestQueueService.showAlert("Error! no data found", TripDetails.this);
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
                                    RequestQueueService.showAlert("Error! no data found", TripDetails.this);
                                }
                                JSONObject distance = jsonObject.getJSONObject("distance");
                                if (distance != null) {
                                    tdmod.setDistance(distance.getString("text"));
                                } else {
                                    RequestQueueService.showAlert("Error! no data found", TripDetails.this);
                                }

                                modalList.add(tdmod);
                            }
                        }
                        Log.d("RequestList:", data.toString());
                        adapter.removeLoadingFooter();
                        isLoading = false;
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
    public void onBackPressed() {
       Intent i=new Intent(this,FirstActivity.class);
               i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
               startActivity(i);

    }




}
