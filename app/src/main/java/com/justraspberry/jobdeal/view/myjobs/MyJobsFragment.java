package com.justraspberry.jobdeal.view.myjobs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.JobAdapter;
import com.justraspberry.jobdeal.adapter.MyJobsAdapter;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.PostedAppliedJobsEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyJobsFragment extends Fragment {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.rvJobList)
    RecyclerView rvJobsList;

    ArrayList<Job> jobs = new ArrayList<>();

    JobAdapter myJobsAdapter;
    MyJobsAdapter postedJobsAdapter;

    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;

    int page = 0;
    boolean isLoadingMore = false;
    boolean noMore = false;
    int firstVisibleItem;



    private int type = 1;
    private boolean isPostedJobs;

    public MyJobsFragment() {
        // Required empty public constructor
    }

    public static MyJobsFragment newInstance(int type, boolean isPostedJobs) {
        MyJobsFragment fragment = new MyJobsFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putBoolean("isPostedJobs", isPostedJobs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            isPostedJobs = getArguments().getBoolean("isPostedJobs");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getJobs(App.getInstance().getCurrentUser().getId(), 0, type);

        if (isPostedJobs) {
            postedJobsAdapter = new MyJobsAdapter(getContext(), jobs);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rvJobsList.setLayoutManager(linearLayoutManager);
            rvJobsList.setAdapter(postedJobsAdapter);
            postedJobsAdapter.notifyDataSetChanged();

            postedJobsAdapter.setOnJobClickListener(new MyJobsAdapter.onJobClickListener() {
                @Override
                public void onJobClicked(View view, int position, Job job) {
                    Intent i = new Intent(getActivity(), JobDetailsActivity.class);
                    i.putExtra("job", job);
                    startActivity(i);
                }
            });
        } else {
            myJobsAdapter = new JobAdapter(getContext(), jobs);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rvJobsList.setLayoutManager(gridLayoutManager);
            rvJobsList.setAdapter(myJobsAdapter);
            myJobsAdapter.notifyDataSetChanged();

            myJobsAdapter.setOnJobClickListener(new JobAdapter.OnJobClickListener() {
                @Override
                public void onJobClick(View v, int position, Job job) {
                    Intent i = new Intent(getActivity(), JobDetailsActivity.class);
                    i.putExtra("job", job);
                    startActivity(i);
                }
            });
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getJobs(App.getInstance().getCurrentUser().getId(), 0, type);
            }
        });

        rvJobsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isPostedJobs)
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                else
                    firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (jobs.size() < 5)
                    return;

                if (firstVisibleItem > jobs.size() - 5 && !isLoadingMore) {
                    loadMoreJobs();
                }
            }
        });
    }

    public void getJobs(int userId, int page, int type) {
        this.page = page;

        if (isPostedJobs)
            ApiRestClient.getInstance().getPostedJobs(userId, this.page);
        else
            ApiRestClient.getInstance().getAppliedJobs(this.page, type);

        if (page == 0)
            swipeRefresh.setRefreshing(true);
    }

    public void loadMoreJobs() {
        if (!noMore) {
            isLoadingMore = true;
        }
        page++;
        getJobs(App.getInstance().getCurrentUser().getId(), page, type);
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
    public void getPostedJobsEvent(PostedAppliedJobsEvent event) {
        if(event.type != type)
            return;

        isLoadingMore = false;
        swipeRefresh.setRefreshing(false);

        if (event.jobs != null) {
            if (page == 0) {
                jobs.clear();
                jobs.addAll(event.jobs);

                if (isPostedJobs)
                    postedJobsAdapter.notifyDataSetChanged();
                else
                    myJobsAdapter.notifyDataSetChanged();
            } else {
                ArrayList<Job> tmpJobs = event.jobs;
                if (tmpJobs.size() == 0) {
                    swipeRefresh.setRefreshing(false);
                    noMore = true;
                    return;
                }

                int prevSize = jobs.size();
                jobs.addAll(prevSize, tmpJobs);

                if (isPostedJobs)
                    postedJobsAdapter.notifyItemRangeInserted(prevSize, tmpJobs.size());
                else
                    myJobsAdapter.notifyItemRangeChanged(prevSize, tmpJobs.size());
            }
        } else {
            Util.displaySnackBar(Snackbar.make(rvJobsList, R.string.default_error, Snackbar.LENGTH_LONG), 12, 12);
        }
    }
}