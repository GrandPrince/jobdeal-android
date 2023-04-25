package com.justraspberry.jobdeal.view.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.JobAdapter;
import com.justraspberry.jobdeal.event.FilterJobEvent;
import com.justraspberry.jobdeal.event.ListScrollEvent;
import com.justraspberry.jobdeal.event.LocationEvent;
import com.justraspberry.jobdeal.event.MapActivatedEvent;
import com.justraspberry.jobdeal.event.TotalJobCountEvent;
import com.justraspberry.jobdeal.model.Filter;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.MapLocation;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.JobFilterEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class MainListFragment extends Fragment {

    @BindView(R.id.rvJobs)
    RecyclerView rvJobs;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.cvJob)
    MaterialCardView cvJob;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    //Detail map view
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.ivBoost)
    ImageView ivBoost;

    GoogleMap map;
    boolean isSpeedy = false;
    boolean isDelivery = false;

    ArrayList<Job> jobs = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();

    Marker selectedMarker;
    Job selectedJob;

    JobAdapter jobAdapter;
    GridLayoutManager gridLayoutManager;
    private boolean isLoadingMore = false;
    private boolean noMore = false;
    private int firstVisibleItem;
    private boolean isLocationSet = false;
    int page = 0;

    LatLng previousCenter = new LatLng(0, 0);

    public MainListFragment() {
        // Required empty public constructor
    }


    public static MainListFragment newInstance(boolean isSpeedy, boolean isDelivery) {
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putBoolean("isSpeedy", isSpeedy);
        args.putBoolean("isDelivery", isDelivery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSpeedy = getArguments().getBoolean("isSpeedy", false);
            isDelivery = getArguments().getBoolean("isDelivery", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_list, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().register(this);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setIndoorLevelPickerEnabled(false);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);

                if(!isLocationSet && App.getInstance().getLocation() == null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(App.getInstance().getMapCenter(), 11f));
                } else if (!isLocationSet && App.getInstance().getLocation() != null){
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.getInstance().getLocation().getLatitude(), App.getInstance().getLocation().getLongitude()), 11f));
                }

                //map.setPadding(0, Util.dpToPx(getContext(), 328), 0, 0);

                map.setMaxZoomPreference(19f);
                //map.setMinZoomPreference(11f);

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        deselectAll();
                    }
                });

                map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            if (cvJob.getVisibility() == View.VISIBLE)
                                deselectAll();
                        }
                    }
                });

                map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLng centerMap = map.getCameraPosition().target;
                        LatLng northeast = map.getProjection().getVisibleRegion().latLngBounds.northeast;

                        App.getInstance().setMapCenter(centerMap);

                        int distance = (int) SphericalUtil.computeDistanceBetween(centerMap, northeast);

                        int centerOffset = (int) SphericalUtil.computeDistanceBetween(centerMap, previousCenter);

                        if (centerOffset > distance / 3) {//if user moved map more than 100m, get new users
                            previousCenter = centerMap;

                            App.getInstance().getFilterBody().getFilter().setMapLocation(new MapLocation(centerMap.latitude, centerMap.longitude, distance));

                            ApiRestClient.getInstance().filterJob(isSpeedy, isDelivery, true, page);
                        }
                    }
                });

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        populateJob(marker);
                        return false;
                    }
                });

            }
        });

        if (App.getInstance().isMapActivated) {
            rvJobs.setVisibility(View.GONE);
        } else {
            rvJobs.setVisibility(View.VISIBLE);
        }

        jobAdapter = new JobAdapter(getContext(), jobs);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvJobs.setLayoutManager(gridLayoutManager);
        rvJobs.setAdapter(jobAdapter);

        jobAdapter.setOnJobClickListener(new JobAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(View v, int position, Job job) {
                Intent i = new Intent(getActivity(), JobDetailsActivity.class);
                i.putExtra("job", job);
                startActivityForResult(i, Consts.REQ_JOB_DETAIL);
            }
        });

        rvJobs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 10)
                    EventBus.getDefault().post(new ListScrollEvent(true));
                else if (dy < -10)
                    EventBus.getDefault().post(new ListScrollEvent(false));

                firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                if (jobs.size() < 5)
                    return;

                if (dy > 0 && firstVisibleItem > jobs.size() - 5 && !isLoadingMore) {
                    loadMoreJobs();
                }
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getJobs(0);
            }
        });

        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorGreen));
        getJobs(0);
    }

    public void deselectAll() {
        Timber.d("deselectAll");
        cvJob.setVisibility(View.GONE);
        selectedMarker = null;
        selectedJob = null;
    }

    private void getJobs(int page) {
        this.page = page;

        if (this.page == 0)
            noMore = false;

        isLoadingMore = true;
        swipeRefresh.setRefreshing(true);
        ApiRestClient.getInstance().filterJob(isSpeedy, isDelivery, false, this.page);

    }

    private void populateJob(Marker marker) {
        selectedJob = (Job) marker.getTag();
        selectedMarker = marker;

        if (selectedJob.getImages().size() > 0)
            Glide.with(this).load(selectedJob.getImages().get(0).getFullUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_job)
                            .transforms(new CenterCrop(), new RoundedCorners(12)))
                    .into(ivImage);

        tvTitle.setText(selectedJob.getName());
        tvDuration.setText(selectedJob.getExpireAt());

        if (selectedJob.getSpeedy()) {
            tvPrice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_price));
            tvPrice.setCompoundDrawables(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_av_timer_small), null);
            tvDuration.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
        } else {
            tvPrice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_price_green));
            tvPrice.setCompoundDrawables(null, null, null, null);
            tvDuration.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTextSecondary));
        }

        if (selectedJob.getBoost())
            ivBoost.setVisibility(View.VISIBLE);
        else
            ivBoost.setVisibility(View.GONE);

        tvPrice.setText(Util.formatNumber(selectedJob.getPrice()) + " " + selectedJob.getCurrency());

        if (selectedJob.getDistance() != null) {
            if (selectedJob.getDistance() > 1000)
                tvLocation.setText(String.valueOf(selectedJob.getDistance() / 1000) + "km");
            else
                tvLocation.setText(String.valueOf(selectedJob.getDistance()) + "m");
        }

        cvJob.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.REQ_JOB_DETAIL) {
            if (resultCode == Consts.RESULT_DELETE) {
                Job job = data.getParcelableExtra("job");
                int i = 0;
                for (Job j : jobs) {
                    if (j.getId().equals(job.getId())) {
                        jobAdapter.notifyItemRemoved(i);
                        jobs.remove(i);
                        break;
                    }
                    i++;
                }

                mapView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cvJob.setVisibility(View.GONE);
                        clearMap();
                        markers.clear();
                        addMarkersForJobs(jobs);
                    }
                }, 1000);

            }
        }
    }

    @OnClick(R.id.cvJob)
    public void onMapJobClick() {
        if (selectedJob == null) {
            deselectAll();
            return;
        }

        Intent i = new Intent(getActivity(), JobDetailsActivity.class);
        i.putExtra("job", selectedJob);
        startActivity(i);
    }

    private void loadMoreJobs() {
        //Timber.e("LOAD MORE (" + String.valueOf(isSpeedy) + "): " + String.valueOf(this.page));
        if (!noMore) {
            isLoadingMore = true;
            page++;
            getJobs(page);
        }
    }

    private void clearMap() {
        for (Marker m : markers) {
            m.remove();
        }

        map.clear();
        selectedJob = null;
        selectedMarker = null;
    }

    private void addMarkersForJobs(ArrayList<Job> jobs) {
        if (map == null)
            return;

        clearMap();

        IconGenerator iconGenerator = new IconGenerator(getActivity());

        for (Job job : jobs) {
            iconGenerator.setBackground(null);
            TextView textPrice = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.item_map_price, null, false);
            iconGenerator.setContentView(textPrice);


            if (job.getSpeedy())
                textPrice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_price));
            else
                textPrice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_price_green));

            textPrice.setText(Util.formatNumber(job.getPrice()) + " " + job.getCurrency());

            Bitmap b = iconGenerator.makeIcon();
            Marker marker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(b)).position(new LatLng(job.getLatitude(), job.getLongitude())));
            marker.setTag(job);
            markers.add(marker);
            b.recycle();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (map != null)
            map.moveCamera(CameraUpdateFactory.newLatLng(App.getInstance().getMapCenter()));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /*----------------------------- SUBSCRIBES ------------------------------------------------*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationEvent event){
        if(event.location != null){
            if(!isLocationSet && map != null)
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(event.location.getLatitude(), event.location.getLongitude())));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapActivatedEvent(MapActivatedEvent event) {
        if (event.isMapActivated) {
            //mapView.setVisibility(View.VISIBLE);
            swipeRefresh.setVisibility(View.GONE);
        } else {
            //mapView.setVisibility(View.GONE);
            swipeRefresh.setVisibility(View.VISIBLE);
        }
    }

    //called when user change sort or filters from main activity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterJob(FilterJobEvent event) {
        getJobs(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJobsEvent(JobFilterEvent event) {
        if (event.isSpeedy != isSpeedy)
            return;

        if (event.isDelivery != isDelivery)
            return;

        isLoadingMore = false;
        swipeRefresh.setRefreshing(false);

        if (event.jobResponse != null) {

            if (event.isMap) {
                addMarkersForJobs(event.jobResponse.getJobs());
                EventBus.getDefault().post(new TotalJobCountEvent(event.jobResponse.getTotal(), event.jobResponse.getJobs().size(), isSpeedy, isDelivery, event.isMap));
            } else {
                //if (event.jobResponse.getJobs().size() > 0) {
                if (page == 0) {
                    jobs.clear();
                    jobs.addAll(event.jobResponse.getJobs());
                    jobAdapter.notifyDataSetChanged();
                } else {
                    ArrayList<Job> tmpUsers = event.jobResponse.getJobs();
                    if (tmpUsers.size() == 0) {
                        noMore = true;
                        return;
                    }
                    int size = jobs.size();
                    jobs.addAll(size, tmpUsers);
                    jobAdapter.notifyItemRangeInserted(size, tmpUsers.size());
                }
                /*} else {
                    noMore = true;
                    if (this.page == 0) {
                        jobs.clear();
                        jobAdapter.notifyDataSetChanged();
                    }
                }*/

                EventBus.getDefault().post(new TotalJobCountEvent(event.jobResponse.getTotal(), jobs.size(), isSpeedy, isDelivery, event.isMap));
            }
        }
    }
}
