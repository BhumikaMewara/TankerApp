package com.kookyapps.gpstankertracking.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kookyapps.gpstankertracking.Activity.Notifications;
import com.kookyapps.gpstankertracking.Activity.RequestDetails;
import com.kookyapps.gpstankertracking.Modal.NotificationModal;
import com.kookyapps.gpstankertracking.R;
import com.kookyapps.gpstankertracking.Utils.Constants;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<NotificationModal> notificationlist;
    Context context;
    private String init_type;
    public static int readPos = -1;
    private static boolean readCalled = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    String detail_init_type = "";

    public NotificationsAdapter(Context context){
        this.context = context;
        this.notificationlist = new ArrayList<>();
    }
    public NotificationsAdapter(Context context,String init_type){
        this.context = context;
        this.init_type = init_type;
        this.notificationlist = new ArrayList<NotificationModal>();
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
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
                    if(context instanceof Notifications && !readCalled) {
                        readCalled = true;
                        readPos = getAdapterPosition();
                        if (readPos >= 0) {
                            String type = notificationlist.get(readPos).getNotificationtype();
                            if (type.equals("BOOKING_REQUEST")) {
                                detail_init_type = Constants.REQUEST_INIT;
                            } else {
                                detail_init_type = Constants.BOOKING_INIT;
                            }
                            ((Notifications) context).readNotificationApiCall(notificationlist.get(getAdapterPosition()).getNotifiactionid());
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationlist == null ? 0 : notificationlist.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = new NotificationViewHolder(inflater.inflate(R.layout.notification_single_item,parent,false));
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(v2);
                break;
        }
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NotificationModal current = notificationlist.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final NotificationViewHolder mVH = (NotificationViewHolder) holder;
                mVH.notificationheading.setText(notificationlist.get(position).getTitle());
                mVH.notificationmsg.setText(notificationlist.get(position).getText());
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == notificationlist.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
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


    public void remove(NotificationModal r) {
        int position = notificationlist.indexOf(r);
        if (position > -1) {
            notificationlist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new NotificationModal());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = notificationlist.size() - 1;
        NotificationModal result = getItem(position);
        if (result != null) {
            notificationlist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public NotificationModal getItem(int position) {
        return notificationlist.get(position);
    }


    public void clearNotifications(){
    notificationlist.clear();
        notifyDataSetChanged();
    }

    public void setReadCalled(boolean t){
        readCalled = false;
        if(readPos!=-1&&t){
            if(init_type!="") {
                Intent i = new Intent(context, RequestDetails.class);
                i.putExtra("init_type", detail_init_type);
                i.putExtra("booking_id", notificationlist.get(readPos).getBookingid());
                notificationlist.remove(readPos);
                notifyItemRemoved(readPos);
                readPos =-1;
                detail_init_type="";
                context.startActivity(i);
            }
        }
    }

}



