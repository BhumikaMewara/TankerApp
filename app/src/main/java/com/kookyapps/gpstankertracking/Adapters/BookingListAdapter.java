package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.kookyapps.gpstankertracking.Modal.BookingListModal;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private List<BookingListModal> bookinglist;
    private boolean isLoadingAdded = false;
    FragmentActivity activity;


    public BookingListAdapter(Context context, FragmentActivity activity){
        this.context=context;
        this.activity = activity;
        this.bookinglist = new ArrayList<BookingListModal>();
    }

    public void add(BookingListModal r) {
        bookinglist.add(r);
        notifyItemInserted(bookinglist.size() - 1);
    }

    public void addAll(List<BookingListModal> moveResults) {
        for (BookingListModal result : moveResults) {
            add(result);
        }
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = bookinglist.size() - 1;
        BookingListModal result = getItem(position);
        if (result != null) {
            bookinglist.remove(position);
            notifyItemRemoved(position);
        }
    }
    public BookingListModal getItem(int position) {
        return bookinglist.get(position);
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new BookingListModal());
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


