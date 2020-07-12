package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.Activity.RequestDetails;
import com.kookyapps.gpstankertracking.Activity.TripDetails;
import com.kookyapps.gpstankertracking.Modal.TripDetailsModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class TripDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    BottomSheetDialog abortsheet;
    private List<TripDetailsModal> tripList;
    TripDetails activity;
    private String init_type;
    private static final int ITEM=0;
    private static final int LOADING=1;
    private boolean isLoadingAdded = false;




    public TripDetailsAdapter(Context context, TripDetails activity,String init_type) {
        this.context = context;
        this.activity = activity;
        this.init_type = init_type;
        this.tripList = new ArrayList<TripDetailsModal>();
    }


    /*public TripDetailsAdapter(Context context){
        this.context = context;
        this.tripList = new ArrayList<>();
    }
    public TripDetailsAdapter(Context context,ArrayList<TripDetailsModal> bookinglist,String init_type){
        this.tripList = bookinglist;
        this.context = context;
    }
*/
    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class TripDetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView bookingid,distance,fromlocation,fromtime,tolocation,totime,bookingactiontext,from_address,to_address;
        RelativeLayout ongoingView,ongoingAbort,bookingview;
        LinearLayout ongoingaction;
        ConstraintLayout itemlayout;






        public TripDetViewHolder(View view) {
            super(view);

            bookingid = (TextView)view.findViewById(R.id.tv_trip_details_bookingid);
            distance = (TextView)view.findViewById(R.id.tv_trip_details_distance1);
            from_address = (TextView)view.findViewById(R.id.tv_trip_details_fromlocation);
            fromtime=(TextView)view.findViewById(R.id.tv_currentDay);

            to_address = (TextView)view.findViewById(R.id.tv_trip_details_tolocation);
            totime = (TextView)view.findViewById(R.id.tv_trip_details_totime);
            itemlayout = (ConstraintLayout)view.findViewById(R.id.cl_triplist_itemlayout);

            bookingactiontext=(TextView)view.findViewById(R.id.tv_bookingitem_viewaction) ;
            bookingview = (RelativeLayout) view.findViewById(R.id.rl_bookingitem_view);


//            bookingview.setOnClickListener(this);
  //          bookingactiontext.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
           /* Intent i;
            switch (view.getId()) {
                case R.id.cl_triplist_itemlayout:
                    i = new Intent(context, RequestDetails.class);
                    i.putExtra("init_type", init_type);
                    i.putExtra("booking_id", current.getBookingid());
                    context.startActivity(i)
                    break;
            }*/
        }


    }

    @Override
    public int getItemCount() {
        return tripList==null?0:tripList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case ITEM:
                viewHolder = new TripDetViewHolder(inflater.inflate(R.layout.trip_details_single_item_list,parent,false));
                break;
            case LOADING:
                View v = inflater.inflate(R.layout.item_progress,parent,false);
                viewHolder = new LoadingViewHolder(v);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TripDetailsModal current = tripList.get(position);


        switch (getItemViewType(position)){



            case ITEM:
                final TripDetViewHolder mVH = (TripDetViewHolder) holder;
                //mVH.bookingid.setText(tripList.get(position).getBookingid());
                mVH.bookingid.setText(tripList.get(position).getTankerBookingid());
                mVH.distance.setText(tripList.get(position).getDistance());
                mVH.fromtime.setText(tripList.get(position).getFromtime());
                mVH.totime.setText(tripList.get(position).getTotime());
                mVH.from_address.setText(tripList.get(position).getFromlocation());
                mVH.to_address.setText(tripList.get(position).getTolocation());


                mVH.itemlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, RequestDetails.class);
                        i.putExtra("init_type", init_type);
                        i.putExtra("booking_id", current.getBookingid());
                        context.startActivity(i);
                    }
                });
                break;
            case LOADING:
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position==tripList.size()-1&&isLoadingAdded)?LOADING:ITEM ;
    }
    public void add(TripDetailsModal r) {
        tripList.add(r);
        notifyItemInserted(tripList.size() - 1);
    }


    public void addAll(List<TripDetailsModal> moveResults) {
        for (TripDetailsModal result : moveResults) {
            add(result);
        }
    }

    public void remove(TripDetailsModal r){
        int position = tripList.indexOf(r);
        if(position>-1){
            tripList.remove(position);
            notifyItemRemoved(position);
        }
    }



    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new TripDetailsModal());
    }
    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = tripList.size() - 1;
        TripDetailsModal result = getItem(position);
        if (result != null) {
            tripList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public TripDetailsModal getItem(int position) {
        return tripList.get(position);
    }

}
