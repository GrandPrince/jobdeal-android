package com.justraspberry.jobdeal.view.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.adapter.JobAdapter;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetBookmarkedJobsEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.experimental.BitwiseOperationsKt;

public class BookmarksActivity extends AppCompatActivity {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.rvBookmarkedJobs)
    RecyclerView rvBookmarkedJobs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    ArrayList<Job> bookmarkedJobs = new ArrayList<>();
    JobAdapter jobAdapter;
    GridLayoutManager gridLayoutManager;


    int page = 0;
    public boolean isLoadingMore = false;
    public boolean noMore = false;
    private int firstVisibleItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));


        jobAdapter = new JobAdapter(this, bookmarkedJobs);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvBookmarkedJobs.setLayoutManager(gridLayoutManager);
        rvBookmarkedJobs.setAdapter(jobAdapter);

        getBookmarkedJobs(0);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookmarkedJobs(0);
            }
        });

        jobAdapter.setOnJobClickListener(new JobAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(View v, int position, Job job) {
                Intent i = new Intent(BookmarksActivity.this, JobDetailsActivity.class);
                i.putExtra("job", job);
                startActivityForResult(i, Consts.REQ_BOOKMARK_JOB);
            }
        });

        rvBookmarkedJobs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                if (bookmarkedJobs.size() < 10)
                    return;

                if (firstVisibleItem > bookmarkedJobs.size() - 10 && !isLoadingMore) {
                    loadMoreBookmarkedJobs();
                }
            }
        });


    }

    public void getBookmarkedJobs(int page) {
        this.page = page;
        ApiRestClient.getInstance().getBookmarkedJobs(this.page);

        if (page == 0)
            swipeRefresh.setRefreshing(true);
    }

    public void loadMoreBookmarkedJobs() {
        if (!noMore) {
            isLoadingMore = true;
            page++;
            getBookmarkedJobs(page);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Consts.REQ_BOOKMARK_JOB && resultCode == RESULT_OK) {
            Job job = data.getParcelableExtra("job");

            if (!job.getBookmarked()) {//remove job from list if user remove bookmark
                int i = 0;
                for (Job j : bookmarkedJobs) {
                    if (j.getId().equals(job.getId())) {
                        jobAdapter.notifyItemRemoved(i);
                        bookmarkedJobs.remove(i);
                    }
                    i++;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBookmarkedJobs(GetBookmarkedJobsEvent event) {
        swipeRefresh.setRefreshing(false);
        isLoadingMore = false;

        if (event.bookmarkedJobs != null) {
            if (page == 0) {
                bookmarkedJobs.clear();
                bookmarkedJobs.addAll(event.bookmarkedJobs);
                jobAdapter.notifyDataSetChanged();
            } else {
                ArrayList<Job> tmpJobs = event.bookmarkedJobs;
                if (tmpJobs.size() == 0) {
                    noMore = true;
                    return;
                }

                int prevSize = bookmarkedJobs.size();
                bookmarkedJobs.addAll(prevSize, tmpJobs);
                jobAdapter.notifyItemRangeChanged(prevSize, tmpJobs.size());
            }
        } else {
            Toast.makeText(this, getString(R.string.default_error), Toast.LENGTH_LONG).show();
        }
    }
}


