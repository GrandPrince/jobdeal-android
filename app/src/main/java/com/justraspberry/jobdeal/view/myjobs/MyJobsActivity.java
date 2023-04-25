package com.justraspberry.jobdeal.view.myjobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.JobAdapter;
import com.justraspberry.jobdeal.adapter.MyJobsAdapter;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.PostedAppliedJobsEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.main.MainActivity;
import com.justraspberry.jobdeal.view.main.MainListFragment;
import com.willy.ratingbar.BaseRatingBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MyJobsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.civProfilePicture)
    CircleImageView civProfilePicture;
    @BindView(R.id.tvUserFullName)
    TextView tvUserFullName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.rbProfileRating)
    BaseRatingBar rbProfileRating;
    @BindView(R.id.tvRating)
    TextView tvRating;
    @BindView(R.id.ivPremiumUser)
    ImageView ivPremiumUser;
    @BindView(R.id.cvProfileInfo)
    RelativeLayout cvProfileInfo;

    boolean isPostedJobs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        isPostedJobs = getIntent().getBooleanExtra("isPostedJobs", false);

        viewPager.setAdapter(new JobsPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        setupProfile();

        if (isPostedJobs) {
            toolbar.setTitle(R.string.job_bids);
            viewPager.setOffscreenPageLimit(1);
            cvProfileInfo.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        } else {
            toolbar.setTitle(R.string.applied_jobs);
            viewPager.setOffscreenPageLimit(2);
        }

        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorWhiteTrans), ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setTabIndicatorFullWidth(true);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setupProfile() {

        Glide.with(this)
                .load(App.getInstance().getCurrentUser().getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(civProfilePicture);
        tvUserFullName.setText(App.getInstance().getCurrentUser().getFullName());
        tvLocation.setText(App.getInstance().getCurrentUser().getCity());
        if (App.getInstance().getCurrentUser().getSubscription() != null) {
            ivPremiumUser.setVisibility(View.VISIBLE);
        } else {
            ivPremiumUser.setVisibility(View.GONE);
        }

        Float ratingToConvert = App.getInstance().getCurrentUser().getRateInfo().getAvgBuyer();
        Util.formatNumber(ratingToConvert);
        tvRating.setText(getString(R.string.rating_params, Util.formatNumber(ratingToConvert), App.getInstance().getCurrentUser().getRateInfo().getCountBuyer()));
        rbProfileRating.setRating(ratingToConvert.floatValue());
    }

    private class JobsPagerAdapter extends FragmentStatePagerAdapter {

        public JobsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (isPostedJobs) {
                return MyJobsFragment.newInstance(1, true);
            } else {
                if (position == 0)
                    return MyJobsFragment.newInstance(1, false);
                else if (position == 1)
                    return MyJobsFragment.newInstance(2, false);
            }


            return null;
        }

        @Override
        public int getCount() {
            if (isPostedJobs)
                return 1;
            else
                return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            String[] titles = {getString(R.string.active_jobs), getString(R.string.expired_jobs)};

            return titles[position];
        }
    }


}
