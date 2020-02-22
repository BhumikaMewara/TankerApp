package com.kookyapps.gpstankertracking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kookyapps.gpstankertracking.Modal.NotificationModal;
import com.kookyapps.gpstankertracking.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;




public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    ArrayList<NotificationModal> notificationlist;
    Context context;
    private boolean isLoadingAdded = false;

    public NotificationsAdapter(Context context,ArrayList<NotificationModal> notificationlist){
        this.context = context;
        this.notificationlist = notificationlist;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_single_item,parent,false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.notificationheading.setText(notificationlist.get(position).getNotificationheading());
        holder.notificationheading.setText(notificationlist.get(position).getNotificationmsg());
    }

    @Override
    public int getItemCount() {
        return notificationlist.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout notificationlayout;
        TextView notificationheading,notificationmsg;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationlayout = (RelativeLayout)itemView.findViewById(R.id.rl_notificationitem_layout);
            notificationheading = (TextView)itemView.findViewById(R.id.tv_notificationitem_heading);
            notificationmsg = (TextView)itemView.findViewById(R.id.tv_notificationitem_data);
            notificationlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_notificationitem_layout:
                    break;
            }
        }

    }
    public void add(NotificationModal r) {
        notificationlist.add(r);
        notifyItemInserted(notificationlist.size() - 1);
    }
    public void addAll(List<NotificationModal> moveResults) {
        for (NotificationModal result : moveResults) {
            add(result);
        }
    }
    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new NotificationModal());
    }


}
