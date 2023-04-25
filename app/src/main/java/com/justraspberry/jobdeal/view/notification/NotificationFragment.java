package com.justraspberry.jobdeal.view.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.NotificationAdapter;
import com.justraspberry.jobdeal.event.NewNotificationEvent;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Notification;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetNotificationEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.main.MainListFragment;
import com.justraspberry.jobdeal.view.rate.RateActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class NotificationFragment extends Fragment {

    @BindView(R.id.rvNotifications)
    RecyclerView rvNotifications;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    NotificationAdapter notificationAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayList<Notification> notifications = new ArrayList<>();

    boolean isLoadingMore = false;
    boolean noMore = false;
    int page = 0;
    private int firstVisibleItem;
    private int type = 0;

    public NotificationFragment() {
        // Required empty public constructor
    }


    public static NotificationFragment newInstance(int type) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationAdapter = new NotificationAdapter(notifications, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvNotifications.setLayoutManager(linearLayoutManager);
        rvNotifications.setAdapter(notificationAdapter);
        getNotifications(0);
        notificationAdapter.notifyDataSetChanged();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications(0);
            }
        });

        rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (notifications.size() < 5)
                    return;

                if (dy > 0 && firstVisibleItem > notifications.size() - 5 && !isLoadingMore) {
                    loadMoreNotifications();
                }
            }
        });

        notificationAdapter.setOnNotificationClickListener(new NotificationAdapter.onNotificationClickListener() {
            @Override
            public void onNotificationClick(View itemView, int position, Job job, Notification notification) {
                notification.setSeen(true);
                notificationAdapter.notifyItemChanged(position);

                ApiRestClient.getInstance().readNotification(notification.getId());

                if (notification.getType().equals(Consts.RATE_BUYER) || notification.getType().equals(Consts.RATE_DOER)) {
                    Intent intent = new Intent(getActivity(), RateActivity.class);
                    intent.putExtra("notification", notification);
                    startActivityForResult(intent, Consts.REQ_NOTIF_RATE);
                } else if (notification.getType().equals(Consts.BUYER_ACCEPTED)
                        || notification.getType().equals(Consts.DOER_BID)
                        || notification.getType().equals(Consts.WISHLIST_JOB)) {
                    Intent i = new Intent(getActivity(), JobDetailsActivity.class);
                    i.putExtra("job", job);
                    startActivity(i);
                }
            }
        });

    }

    public void getNotifications(int page) {
        this.page = page;
        ApiRestClient.getInstance().getNotifications(this.page, type);

        if (page == 0)
            swipeRefresh.setRefreshing(true);
    }

    public void loadMoreNotifications() {
        if (!noMore) {
            isLoadingMore = true;
            page++;
            getNotifications(page);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Consts.REQ_NOTIF_RATE && resultCode == RESULT_OK) {

            Notification notification = data.getParcelableExtra("notification");

            int positionToDelete = -1;
            for (Notification n : notifications) {
                positionToDelete++;
                if (n.getId().equals(notification.getId())) {
                    break;
                }
            }

            if(positionToDelete != -1){
                notifications.remove(positionToDelete);
                notificationAdapter.notifyItemRemoved(positionToDelete);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getNotifications(GetNotificationEvent event) {
        if (type != event.type)
            return;

        isLoadingMore = false;
        swipeRefresh.setRefreshing(false);


        if (event.notifications != null) {
            for (Notification notification : notifications) {
                if (!notification.getSeen()) {
                    notification.setSeen(true);
                    if (App.getInstance().getCurrentUser().getNotificationCount() > 0)
                        App.getInstance().getCurrentUser().setNotificationCount(App.getInstance().getCurrentUser().getNotificationCount() - 1);
                }
            }

            if (page == 0) {
                notifications.clear();
                notifications.addAll(event.notifications);
                notificationAdapter.notifyDataSetChanged();
            } else {
                ArrayList<Notification> tmpNotifications = event.notifications;
                if (tmpNotifications.size() == 0) {
                    noMore = true;
                    return;
                }

                int prevSize = notifications.size();
                notifications.addAll(prevSize, tmpNotifications);
                notificationAdapter.notifyItemRangeChanged(prevSize, tmpNotifications.size());
            }
        } else {
            Util.displaySnackBar(Snackbar.make(rvNotifications, R.string.default_error, Snackbar.LENGTH_LONG), 12, 12);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewNotificationEvent(NewNotificationEvent event) {
        if (event.notification != null) {

            if ((type == 0 && (event.notification.getType() == 2 || event.notification.getType() == 4 || event.notification.getType() == 5))
                    ||
                    (type == 1 && (event.notification.getType() == 1 || event.notification.getType() == 3
                            || event.notification.getType() == 6 || event.notification.getType() == 7))) {

                notifications.add(0, event.notification);
                notificationAdapter.notifyItemInserted(0);
            }
        }
    }


}