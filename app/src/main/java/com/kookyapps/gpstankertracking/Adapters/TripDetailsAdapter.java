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
import com.kookyapps.gpstankertracking.Modal.TripDetailsModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripDetailsAdapter extends RecyclerView.Adapter<TripDetailsAdapter.TripDetViewHolder> {
    ArrayList<TripDetailsModal> bookinglist;
    Context context;
    String init_type;
    public TripDetailsAdapter(Context context,ArrayList<TripDetailsModal> bookinglist,String init_type){
        this.bookinglist = bookinglist;
        this.context = context;
        this.init_type = init_type;
    }

    public class TripDetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView bookingid,distance,fromlocation,fromtime,tolocation,totime,bookingactiontext;
        RelativeLayout ongoingView,ongoingAbort,bookingview;
        LinearLayout ongoingaction;

        public TripDetViewHolder(View view) {
            super(view);

            bookingid = (TextView)view.findViewById(R.id.tv_trip_details_bookingid);
            distance = (TextView)view.findViewById(R.id.tv_trip_details_distance);
            fromlocation = (TextView)view.findViewById(R.id.tv_trip_details_fromlocation);
            fromtime = (TextView)view.findViewById(R.id.tv_trip_details_fromtime);
            tolocation = (TextView)view.findViewById(R.id.tv_trip_details_tolocation);
            totime = (TextView)view.findViewById(R.id.tv_trip_details_totime);
            bookingactiontext = (TextView)view.findViewById(R.id.tv_trip_details_viewaction);
            ongoingaction = (LinearLayout)view.findViewById(R.id.ll_trip_details_ongoing);
            ongoingView = (RelativeLayout)view.findViewById(R.id.rl_trip_details_ongoing_view);
            ongoingView.setOnClickListener(this);
            /*ongoingAbort = (RelativeLayout)view.findViewById(R.id.rl_trip_details_ongoing_abort);
            ongoingAbort.setOnClickListener(this);*/
            bookingview = (RelativeLayout)view.findViewById(R.id.rl_trip_details_view);
            bookingview.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.rl_trip_details_ongoing_view:
                    break;
                /*case R.id.rl_trip_details_ongoing_abort:
                    abortsheet = new BottomSheetDialog(context);
                    View sheetView = LayoutInflater.from(context).inflate(R.layout.activity_abort_dialog,null);
                    abortsheet.setContentView(sheetView);
                    abort_delete = (TextView)sheetView.findViewById(R.id.tv_abortdialog_delete);
                    abort_cancel = (TextView)sheetView.findViewById(R.id.tv_abortdialog_cancel);
                    abort_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    abort_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            abortsheet.dismiss();
                        }
                    });
                    abortsheet.show();
                    break;*/
                case R.id.rl_trip_details_view:
                    intent = new Intent(context, BookingDetails.class);
                    intent.putExtra("init_type",init_type);
                    context.startActivity(intent);
                    /* if(init_type.equals(Constants.PENDING_CALL)){

                        abortsheet = new BottomSheetDialog(context);
                        View sheetView2 = LayoutInflater.from(context).inflate(R.layout.activity_abort_dialog,null);
                        abortsheet.setContentView(sheetView2);
                        abort_delete = (TextView)sheetView2.findViewById(R.id.tv_abortdialog_delete);
                        abort_cancel = (TextView)sheetView2.findViewById(R.id.tv_abortdialog_cancel);
                        abort_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        abort_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                abortsheet.dismiss();
                            }
                        });

                        abortsheet.show();
                    }else{
                        intent = new Intent(context, BookingDetails.class);
                        intent.putExtra("init_type",init_type);
                        context.startActivity(intent);
                    }*/
                    break;
            }
        }


    }

    @Override
    public int getItemCount() {
        return bookinglist.size();
    }
    @NonNull
    @Override
    public TripDetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_details_single_item_list,parent,false);
        return new TripDetViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull TripDetViewHolder holder, int position) {
        if(init_type.equals(Constants.ONGOING_CALL)){
            holder.ongoingaction.setVisibility(View.VISIBLE);
            holder.bookingview.setVisibility(View.GONE);
        }else{
            if(init_type.equals(Constants.PENDING_CALL)){
                holder.bookingactiontext.setText("Abort");
            }else{
                holder.bookingactiontext.setText("View");
            }
            holder.ongoingaction.setVisibility(View.GONE);
            holder.bookingview.setVisibility(View.VISIBLE);
        }
        holder.bookingid.setText(bookinglist.get(position).getBookingid());
        holder.distance.setText(bookinglist.get(position).getDistance());
        holder.fromtime.setText(bookinglist.get(position).getFromtime());
        holder.totime.setText(bookinglist.get(position).getTotime());
        holder.fromlocation.setText(styleLocation(bookinglist.get(position).getFromlocation()));
        holder.tolocation.setText(styleLocation(bookinglist.get(position).getTolocation()));
    }





    private SpannableStringBuilder styleLocation(String location){
        int index = location.indexOf(",");
        final SpannableStringBuilder sb = new SpannableStringBuilder(location);
        final StyleSpan bss = new StyleSpan(Typeface.BOLD);
        sb.setSpan(new AbsoluteSizeSpan(12,true),0,index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(10,true),index+1,location.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss,0,index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }
}
