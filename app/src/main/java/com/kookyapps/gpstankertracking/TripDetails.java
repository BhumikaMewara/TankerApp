package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.kookyapps.gpstankertracking.Adapters.TripDetailsAdapter;
import com.kookyapps.gpstankertracking.Modal.TripDetailsModal;
import com.kookyapps.gpstankertracking.Utils.Constants;

import java.util.ArrayList;

public class TripDetails extends AppCompatActivity implements View.OnClickListener {

    ImageView toolbarmenu ;
    ProgressBar tripDetProgressBar;
    TextView nodata,pageTitle;
    RecyclerView trip_details;
    TripDetailsAdapter adapter;
    String s;
    RelativeLayout menu ,menunotifications ;
    ArrayList<TripDetailsModal>tripDetailList;
   // TripListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        initViews();

    }
    public void initViews(){
        tripDetProgressBar = findViewById(R.id.pb_trip_details);
        nodata = (TextView)findViewById(R.id.tv_trip_details_nodata);
        nodata.setVisibility(View.GONE);
        pageTitle=(TextView)findViewById(R.id.tv_water_tanker_toolbartitle);
        trip_details=(RecyclerView)findViewById(R.id.rv_trip_details);
        menunotifications=(RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu_notification);
        menu=(RelativeLayout)findViewById(R.id.rl_water_tanker_toolbar_menu);
        menunotifications.setOnClickListener(this);
        menu.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {
        Intent i ;
        switch (view.getId()){
            case R.id.rl_water_tanker_toolbar_menu_notification:

                break;
            case R.id.rl_water_tanker_toolbar_menu:

                break;

        }
    }
     public void createTrip(){
         TripDetailsModal data1 = new TripDetailsModal();
         TripDetailsModal data2 = new TripDetailsModal();
         TripDetailsModal data3 = new TripDetailsModal();

         data1.setBookingid("1234567890");
         data1.setDistance("15 KM");
         data1.setFromlocation("Boranada, Summer Nagar, 115");
         data1.setFromtime("Saturday, 24 January, 04:20 PM");
         data1.setTolocation("Chopasani Housing Board, Shree Krishna Nagar, 161");
         data1.setTotime("Saturday, 24 January, 04:40 PM");

         data2.setBookingid("1234567891");
         data2.setDistance("16 KM");
         data2.setFromlocation("Koranada, Summer Nagar, 115");
         data2.setFromtime("Katurday, 24 January, 04:20 PM");
         data2.setTolocation("Khopasani Housing Board, Shree Krishna Nagar, 161");
         data2.setTotime("Katurday, 24 January, 04:40 PM");

         data3.setBookingid("1234567892");
         data3.setDistance("17 KM");
         data3.setFromlocation("Moranada, Summer Nagar, 115");
         data3.setFromtime("Maturday, 24 January, 04:20 PM");
         data3.setTolocation("Mhopasani Housing Board, Shree Krishna Nagar, 161");
         data3.setTotime("Maturday, 24 January, 04:40 PM");

         tripDetailList = new ArrayList<>();
         tripDetailList.add(data1);
         tripDetailList.add(data2);
         tripDetailList.add(data3);
         setRecyclerView();
     }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setRecyclerView(){
        if(tripDetailList==null){
            tripDetProgressBar.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
            tripDetProgressBar.setVisibility(View.GONE);
        }else{
            tripDetProgressBar.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            trip_details.setVisibility(View.VISIBLE);
            adapter = new TripDetailsAdapter(TripDetails.this, tripDetailList, Constants.TRIP_DETAILS);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            trip_details.setLayoutManager(mLayoutManager);
            trip_details.setAdapter(adapter);
        }
    }
    public void tripDetailsListApiCalling(){

    }

}
