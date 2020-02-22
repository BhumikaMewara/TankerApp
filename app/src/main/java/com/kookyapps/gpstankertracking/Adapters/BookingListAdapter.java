package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Modal.BookingListModel;
import com.kookyapps.gpstankertracking.Modal.RequestListModel;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.fragment.BookingList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private List<BookingListModel> bookinglist;
    private boolean isLoadingAdded = false;
    FragmentActivity activity;


    public BookingListAdapter(Context context, FragmentActivity activity){
        this.context=context;
        this.activity = activity;
        this.bookinglist = new ArrayList<BookingListModel>();
    }

    public void add(BookingListModel r) {
        bookinglist.add(r);
        notifyItemInserted(bookinglist.size() - 1);
    }

    public void addAll(List<BookingListModel> moveResults) {
        for (BookingListModel result : moveResults) {
            add(result);
        }
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = bookinglist.size() - 1;
        BookingListModel result = getItem(position);
        if (result != null) {
            bookinglist.remove(position);
            notifyItemRemoved(position);
        }
    }
    public BookingListModel getItem(int position) {
        return bookinglist.get(position);
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new BookingListModel());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}


