package com.justraspberry.jobdeal.view.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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
import com.justraspberry.jobdeal.rest.service.event.GetUnreadNotificationCountEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.main.MainActivity;
import com.justraspberry.jobdeal.view.rate.RateActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    int notificationType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        notificationType = getIntent().getIntExtra("notificationType", -1);

        viewPager.setAdapter(new NotificationViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });

        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorWhiteTrans), ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setTabIndicatorFullWidth(true);

        tabLayout.setupWithViewPager(viewPager);

        if(notificationType != -1){
            if(notificationType == 2 || notificationType == 4 || notificationType == 5){
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiRestClient.getInstance().getUnreadNotificationCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnreadNotificationCountEvent(GetUnreadNotificationCountEvent event){
        if(event.unreadCount != null){
            BadgeDrawable buyerBadge = tabLayout.getTabAt(0).getOrCreateBadge();
            BadgeDrawable doerBadge = tabLayout.getTabAt(1).getOrCreateBadge();

            buyerBadge.setBackgroundColor(ContextCompat.getColor(NotificationActivity.this, R.color.colorRed));
            buyerBadge.setNumber(event.unreadCount.getUnreadBuyerCount());

            if(event.unreadCount.getUnreadBuyerCount() == 0) {
                buyerBadge.setVisible(false);
                tabLayout.getTabAt(0).removeBadge();
            }

            doerBadge.setBackgroundColor(ContextCompat.getColor(NotificationActivity.this, R.color.colorRed));
            doerBadge.setNumber(event.unreadCount.getUnreadDoerCount());

            if(event.unreadCount.getUnreadDoerCount() == 0) {
                doerBadge.setVisible(false);
                tabLayout.getTabAt(1).removeBadge();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class NotificationViewPagerAdapter extends FragmentStatePagerAdapter {
        public NotificationViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
            return NotificationFragment.newInstance(1);
            else
                return NotificationFragment.newInstance(0);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            String[] titles = { getString(R.string.my_ads), getString(R.string.my_deals)};

            return titles[position];
        }
    }




}
