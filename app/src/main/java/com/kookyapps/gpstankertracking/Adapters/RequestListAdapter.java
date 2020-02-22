package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.kookyapps.gpstankertracking.Modal.BookingListModel;
import com.kookyapps.gpstankertracking.Modal.RequestListModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class RequestListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    private List<RequestListModel> requestlist;
    private boolean isLoadingAdded = false;
    FragmentActivity activity;


    public RequestListAdapter(Context context, FragmentActivity activity){
        this.context=context;
        this.activity = activity;
        this.requestlist = new ArrayList<RequestListModel>();
    }

    public void add(RequestListModel r) {
        requestlist.add(r);
        notifyItemInserted(requestlist.size() - 1);
    }
    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = requestlist.size() - 1;
        RequestListModel result = getItem(position);
        if (result != null) {
            requestlist.remove(position);
            notifyItemRemoved(position);
        }
    }



    public RequestListModel getItem(int position) {
        return requestlist.get(position);
    }


    public void addAll(List<RequestListModel> moveResults) {
        for (RequestListModel result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new RequestListModel());
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
