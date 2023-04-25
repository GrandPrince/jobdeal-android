package com.justraspberry.jobdeal.view.filter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.misc.JobDealRangeSeekbar;
import com.justraspberry.jobdeal.model.CurrentLocation;
import com.justraspberry.jobdeal.model.FilterBody;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.WishlistEvent;
import com.justraspberry.jobdeal.view.categories.CategoryActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    @BindView(R.id.cvCategories)
    CardView cvCategories;
    @BindView(R.id.tvPriceMin)
    TextView tvPriceMin;
    @BindView(R.id.tvPriceMax)
    TextView tvPriceMax;
    @BindView(R.id.tvPriceRange)
    TextView tvPriceRange;
    @BindView(R.id.tvDistanceRange)
    TextView tvDistanceRange;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvMyLocation)
    TextView tvMyLocation;
    @BindView(R.id.btnSave)
    MaterialButton btnSave;
    @BindView(R.id.tvReset)
    TextView tvReset;
    @BindView(R.id.rbDistance)
    JobDealRangeSeekbar rbDistance;
    @BindView(R.id.rbPrice)
    JobDealRangeSeekbar rbPrice;
    @BindView(R.id.tvCategoriesCount)
    TextView tvCategoriesCount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pbFilter)
    ProgressBar pbFilter;
    @BindView(R.id.smHelp)
    SwitchMaterial smHelp;
    @BindView(R.id.rlHelp)
            RelativeLayout rlHelp;
    @BindView(R.id.tvWishlistDescription)
            TextView tvWishlistDesc;

    FilterBody filterBody;

    LatLng latLng;
    String address;

    boolean isWishlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        isWishlist = getIntent().getBooleanExtra("isWishlist", false);

        if (!isWishlist) {
            toolbar.setTitle(getString(R.string.filter));
            filterBody = App.getInstance().getFilterBody();
            tvReset.setText(getString(R.string.reset_filters));
            tvWishlistDesc.setVisibility(View.GONE);
            Timber.e("Filters");
        } else {
            toolbar.setTitle(getString(R.string.wish_list));
            filterBody = App.getInstance().getWishlistBody();
            tvReset.setText(getString(R.string.reset_wishlist));
            rlHelp.setVisibility(View.GONE);
            tvWishlistDesc.setVisibility(View.VISIBLE);
            Timber.e("Wishlist - " + App.getInstance().getGson().toJson(App.getInstance().getWishlistBody()));
        }

        rbPrice.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                String range = "";
                String min = "";
                String max = "";

                if (maxValue.intValue() > 1000)
                    max = "1000+";
                else
                    max = String.valueOf(maxValue.intValue());

                min = String.valueOf(minValue);

                range = min + " - " + max + " " + App.getInstance().getInfo().getCurrency();

                tvPriceRange.setText(range);
            }
        });
        rbDistance.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                String range = "";
                String min = "";
                String max = "";

                if (minValue.intValue() < 1000)
                    min = minValue.toString() + " m";
                else
                    min = String.valueOf(minValue.intValue() / 1000) + " km";

                if (maxValue.intValue() < 1000)
                    max = maxValue.toString() + " m";
                else
                    max = String.valueOf(maxValue.intValue() / 1000) + " km";

                if (maxValue.intValue() == 100000)
                    max = String.valueOf(maxValue.intValue() / 1000) + "+ km";

                range = min + " - " + max;

                tvDistanceRange.setText(range);
            }
        });

        setupFilterViews();
    }

    public void setupFilterViews() {
        if (filterBody.getFilter().getFromPrice() != null)
            rbPrice.setMinStartValue(filterBody.getFilter().getFromPrice());
        else
            rbPrice.setMinStartValue(0);

        if (filterBody.getFilter().getToPrice() != null)
            rbPrice.setMaxStartValue(filterBody.getFilter().getToPrice());
        else
            rbPrice.setMaxStartValue(Integer.MAX_VALUE);

        if (filterBody.getFilter().getFromDistance() != null)
            rbDistance.setMinStartValue(filterBody.getFilter().getFromDistance());
        else
            rbDistance.setMinStartValue(0);

        if (filterBody.getFilter().getToDistance() != null)
            rbDistance.setMaxStartValue(filterBody.getFilter().getToDistance());
        else
            rbDistance.setMaxStartValue(Integer.MAX_VALUE);

        rbPrice.apply();
        rbDistance.apply();

        if (filterBody.getMyLocation()) {
            tvMyLocation.setActivated(true);
            tvAddress.setActivated(false);
        } else {
            tvMyLocation.setActivated(false);
            tvAddress.setActivated(true);
        }

        if (filterBody.getAddress() != null) {
            address = filterBody.getAddress();
            tvAddress.setText(filterBody.getAddress());
        } else {
            tvAddress.setText(getString(R.string.insert_address));
        }

        if (filterBody.getCurrentLocation() != null) {
            latLng = new LatLng(filterBody.getCurrentLocation().getLng(), filterBody.getCurrentLocation().getLng());
        }

        if(filterBody.getFilter().getHelpOnTheWay() != null){
            smHelp.setChecked(filterBody.getFilter().getHelpOnTheWay());
        }

        tvPriceMin.setText("0 " + App.getInstance().getInfo().getCurrency());
        tvPriceMax.setText("1000+ " + App.getInstance().getInfo().getCurrency());

        int countCategories = filterBody.getFilter().getCategoriesCount();

        Timber.e(App.getInstance().getGson().toJson(filterBody));

        if (countCategories > 0) {
            tvCategoriesCount.setVisibility(View.VISIBLE);
            tvCategoriesCount.setText(getString(R.string.categories_selected, countCategories));
        } else {
            tvCategoriesCount.setVisibility(View.GONE);
            tvCategoriesCount.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult");
        if (requestCode == Consts.REQ_MAP_FILTER && resultCode == RESULT_OK) {
            latLng = data.getParcelableExtra("latLng");
            address = data.getStringExtra("address");

            if (tvAddress.isActivated())
                tvAddress.setText(address);
        }
    }

    @OnCheckedChanged(R.id.smHelp)
    public void onHelpChecked(CompoundButton b, boolean cheked){
        filterBody.getFilter().setHelpOnTheWay(cheked);
    }

    @OnClick(R.id.tvReset)
    public void onResetClick() {
        filterBody = new FilterBody();
        filterBody.getFilter().clearCategories();
        setupFilterViews();
        if (!isWishlist)
            App.getInstance().setFilterBody(filterBody);
        else
            App.getInstance().setWishlistBody(filterBody);

        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.btnSave)
    public void onSaveClick() {
        filterBody.getFilter().setFromDistance(rbDistance.getSelectedMinValue().intValue());
        filterBody.getFilter().setToDistance(rbDistance.getSelectedMaxValue().intValue());

        filterBody.getFilter().setFromPrice(rbPrice.getSelectedMinValue().intValue());
        filterBody.getFilter().setToPrice(rbPrice.getSelectedMaxValue().intValue());

        filterBody.setMyLocation(tvMyLocation.isActivated());

        if (tvMyLocation.isActivated()) {//pick realtime location
            if(App.getInstance().getLocation() != null)
            filterBody.setCurrentLocation(new CurrentLocation(App.getInstance().getLocation().getLatitude(), App.getInstance().getLocation().getLongitude()));
        } else {
            filterBody.setCurrentLocation(new CurrentLocation(latLng.latitude, latLng.longitude));
        }

        filterBody.setAddress(address);

        if (!isWishlist) {
            App.getInstance().setFilterBody(filterBody);
            setResult(RESULT_OK);
            finish();
        } else {
            App.getInstance().setWishlistBody(filterBody);
            ApiRestClient.getInstance().updateWishlist(filterBody);
            showLoading(true);
        }

    }

    private void showLoading(boolean show){
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnSave.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(this, 44);
            btnSave.setLayoutParams(layoutParams);
            btnSave.setCornerRadius(Util.dpToPx(this, 32));
            btnSave.setText("");
            btnSave.setEnabled(false);
            pbFilter.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnSave.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnSave.setLayoutParams(layoutParams);
            btnSave.setText(getString(R.string.register));
            btnSave.setEnabled(true);
            btnSave.setCornerRadius(Util.dpToPx(this, 8));
            pbFilter.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.tvAddress, R.id.tvMyLocation})
    public void onLocationSelect(View v) {
        tvAddress.setActivated(false);
        tvMyLocation.setActivated(false);

        v.setActivated(!v.isActivated());

        if (v.getId() == R.id.tvAddress) {
            Intent i = new Intent(this, MapFilterActivity.class);
            i.putExtra("address", address);
            i.putExtra("latLng", latLng);
            startActivityForResult(i, Consts.REQ_MAP_FILTER);
        }
    }

    @OnClick(R.id.cvCategories)
    public void onCategoriesClick() {
        Intent i = new Intent(this, CategoryActivity.class);
        i.putExtra("isWishlist", isWishlist);
        i.putExtra("isFilterMultiSelect", true);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int countCategories = filterBody.getFilter().getCategoriesCount();

        if (countCategories > 0) {
            tvCategoriesCount.setVisibility(View.VISIBLE);
            tvCategoriesCount.setText(getString(R.string.categories_selected, countCategories));
        } else {
            tvCategoriesCount.setVisibility(View.GONE);
            tvCategoriesCount.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWishlistUpdate(WishlistEvent event){
        showLoading(false);
        if(event.filterBody != null){
            finish();
        } else {
            Snackbar.make(toolbar, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }
}
