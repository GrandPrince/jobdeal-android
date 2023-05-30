package com.justraspberry.jobdeal.view.filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.maps.android.SphericalUtil;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.CategorySearchEvent;
import com.justraspberry.jobdeal.event.FragmentStackChangedEvent;
import com.justraspberry.jobdeal.event.LocationEvent;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.MapLocation;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetAddressEvent;
import com.justraspberry.jobdeal.rest.service.event.GetLocationFromAddressEvent;
import com.justraspberry.jobdeal.view.categories.CategoryFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MapFilterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.ivPin)
    ImageView ivPin;
    @BindView(R.id.etSearch)
    TextInputEditText etSearch;

    GoogleMap map;
    boolean isUser = false, isFirstMoveFinished = false;

    LatLng latLng;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        latLng = getIntent().getParcelableExtra("latLng");
        address = getIntent().getStringExtra("address");

        etSearch.clearFocus();

        if(address != null)
        etSearch.setText(address);

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

                if (latLng == null || latLng.latitude == 0)
                    map.moveCamera(CameraUpdateFactory.newLatLng(App.getInstance().getMapCenter()));
                else
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                //map.setPadding(0, Util.dpToPx(getContext(), 328), 0, 0);

                map.setMaxZoomPreference(19f);
                map.setMinZoomPreference(9f);

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                    }
                });

                map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            isUser = true;
                        }
                    }
                });

                map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLng centerMap = map.getCameraPosition().target;

                        if (isUser || !isFirstMoveFinished) {//is user move map ping server for address
                            etSearch.clearFocus();
                            ApiRestClient.getInstance().getAddress(centerMap);

                            isFirstMoveFinished = true;
                        }

                        isUser = false;
                    }
                });
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    if (etSearch.hasFocus()) {
                        //ping server with address
                        ApiRestClient.getInstance().getLocationFromAddress(s.toString());
                    }
                }

                if (s.length() == 0)
                    etSearch.clearFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("latLng", map.getCameraPosition().target);
        i.putExtra("address", etSearch.getText().toString());
        setResult(RESULT_OK, i);

        super.onBackPressed();
    }

    @OnClick(R.id.flFinish)
    public void onFinishClick(){
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @OnClick(R.id.flZoomIn)
    public void onZoomInClick() {
        if (map == null)
            return;

        map.animateCamera(CameraUpdateFactory.zoomIn());
    }

    @OnClick(R.id.flZoomOut)
    public void onZoomOutClick() {
        if (map == null)
            return;

        map.animateCamera(CameraUpdateFactory.zoomOut());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationFromAddress(GetLocationFromAddressEvent event) {
        if (event.latLng != null) {
            if (map == null)
                return;

            if (map.getCameraPosition().zoom < 16)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(event.latLng, 16));
            else
                map.animateCamera(CameraUpdateFactory.newLatLng(event.latLng));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddressEvent(GetAddressEvent event) {
        if (event.address != null) {
            etSearch.clearFocus();
            etSearch.setText(event.address);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationEvent event){
        if(event.location != null){
            if(map != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.location.getLatitude(), event.location.getLongitude())));
                ApiRestClient.getInstance().getAddress(new LatLng(event.location.getLatitude(), event.location.getLongitude()));
            }
        }
    }
}
