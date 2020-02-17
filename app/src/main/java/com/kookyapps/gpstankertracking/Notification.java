package com.kookyapps.gpstankertracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Notification extends AppCompatActivity {

    RecyclerView rv;
    ProgressBar pb;
    TextView no_noti;
    LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        rv=(RecyclerView)findViewById(R.id.rv_notification);
        no_noti=(TextView)findViewById(R.id.noNotificationText);
        pb=(ProgressBar)findViewById(R.id.pb_notification);

        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
       // rv.setAdapter(mAdapter);

    }
     /*recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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

        NotificationUtilsFcm.clearNotifications(getContext());
    notificationsApiCall();

        return root;
}*/
}
