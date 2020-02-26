package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Activity.BookingDetails;
import com.kookyapps.gpstankertracking.Activity.RequestDetails;
import com.kookyapps.gpstankertracking.Modal.BookingListModal;
import com.kookyapps.gpstankertracking.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class RequestListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<BookingListModal> requestlist;
    FragmentActivity activity;
    private String init_type;
    private static final int ITEM=0;
    private static final int LOADING=1;
    private boolean isLoadingAdded = false;


    public RequestListAdapter(Context context, FragmentActivity activity,String init_type) {
        this.context = context;
        this.activity = activity;
        this.init_type = init_type;
        this.requestlist = new ArrayList<BookingListModal>();

    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView bookingid, distance, fromlocation, fromtime, tolocation, totime, bookingactiontext;
        RelativeLayout bookingview;
        ConstraintLayout itemlayout;




        public BookingViewHolder(View view) {

            super(view);
            bookingid = (TextView) view.findViewById(R.id.tv_bookingitem_bookingid);
            distance = (TextView) view.findViewById(R.id.tv_bookingitem_distance);
            fromlocation = (TextView) view.findViewById(R.id.tv_bookingitem_fromlocation);
            fromtime = (TextView) view.findViewById(R.id.tv_bookingitem_fromtime);
            tolocation = (TextView) view.findViewById(R.id.tv_bookingitem_tolocation);
            totime = (TextView) view.findViewById(R.id.tv_bookingitem_totime);
            bookingactiontext=(TextView)view.findViewById(R.id.tv_bookingitem_viewaction) ;
            /*itemlayout = (ConstraintLayout)view.findViewById(R.id.cl_triplist_itemlayout);
            itemlayout.setOnClickListener(this);*/
            bookingview = (RelativeLayout) view.findViewById(R.id.rl_bookingitem_view);
            bookingview.setOnClickListener(this);
            bookingactiontext.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
          /*  switch (view.getId()) {

                case R.id.tv_bookingitem_viewaction:
                    Intent i = new Intent(context, RequestDetails.class);
                    i.putExtra("init_type", init_type);
                    context.startActivity(i);
                    break;
            }*/
        }
    }

    @Override
    public int getItemCount() {
        return requestlist==null?0:requestlist.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case ITEM:
                viewHolder = new BookingViewHolder(inflater.inflate(R.layout.bookinglist_single_item,parent,false));
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
        final BookingListModal current = requestlist.get(position);


        switch (getItemViewType(position)){



            case ITEM:
                final BookingViewHolder mVH = (BookingViewHolder)holder;
                mVH.bookingid.setText(requestlist.get(position).getBookingid());
                mVH.distance.setText(requestlist.get(position).getDistance());
                mVH.fromtime.setText(requestlist.get(position).getFromtime());
                mVH.totime.setText(requestlist.get(position).getTotime());
                mVH.fromlocation.setText(requestlist.get(position).getFromlocation());
                mVH.tolocation.setText(requestlist.get(position).getTolocation());
                mVH.bookingactiontext.setOnClickListener(new View.OnClickListener() {
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
        return (position==requestlist.size()-1&&isLoadingAdded)?LOADING:ITEM ;
    }

    public void add(BookingListModal r) {
            requestlist.add(r);
            notifyItemInserted(requestlist.size() - 1);
    }
    public void addAll(List<BookingListModal> moveResults) {
        for (BookingListModal result : moveResults) {
            add(result);
        }
    }

    public void remove(BookingListModal r){
        int position = requestlist.indexOf(r);
        if(position>-1){
            requestlist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new BookingListModal());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = requestlist.size() - 1;
        BookingListModal result = getItem(position);
        if (result != null) {
            requestlist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public BookingListModal getItem(int position) {
        return requestlist.get(position);
    }


}
