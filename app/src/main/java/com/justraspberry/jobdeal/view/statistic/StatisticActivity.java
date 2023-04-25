package com.justraspberry.jobdeal.view.statistic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetUserEvent;
import com.willy.ratingbar.BaseRatingBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StatisticActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.civProfilePicture)
    CircleImageView civProfilePicture;
    @BindView(R.id.tvUserFullName)
    TextView tvFullName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvJoinDate)
    TextView tvJoinDate;

    @BindView(R.id.cvRateAsBuyer)
    CardView cvRateAsBuyer;
    @BindView(R.id.rbBuyerRating)
    BaseRatingBar rbBuyerRating;
    @BindView(R.id.tvBuyerRating)
    TextView tvBuyerRating;
    @BindView(R.id.btnBuyerNext)
    ImageView btnBuyerNext;
    @BindView(R.id.tvBuyerJobsDone)
    TextView tvBuyerJobsDone;
    @BindView(R.id.tvBuyerSpent)
    TextView tvBuyerSpent;

    @BindView(R.id.cvRateAsDoer)
    CardView cvRateAsDoer;
    @BindView(R.id.rbDoerRating)
    BaseRatingBar rbDoerRating;
    @BindView(R.id.tvDoerRating)
    TextView tvDoerRating;
    @BindView(R.id.btnDoerNext)
    ImageView btnDoerNext;
    @BindView(R.id.tvDoerJobsDone)
    TextView tvDoerJobsDone;
    @BindView(R.id.tvDoerEarned)
    TextView tvDoerEarned;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        user = App.getInstance().getCurrentUser();

        ApiRestClient.getInstance().getUserById(user.getId());
    }

    public void setViews() {
        Glide.with(this).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(civProfilePicture);
        tvFullName.setText(user.getFullName());
        tvLocation.setText(user.getCity());
        tvJoinDate.setText(getString(R.string.user_since, user.getCreatedAt()));

        rbBuyerRating.setRating(user.getRateInfo().getAvgBuyer());
        rbDoerRating.setRating(user.getRateInfo().getAvgDoer());

        tvBuyerRating.setText(user.getRateInfo().getAvgBuyer() + "/" + user.getRateInfo().getCountBuyer());
        tvDoerRating.setText(user.getRateInfo().getAvgDoer() + "/" + user.getRateInfo().getCountDoer());

        if (user.getMyInfo() != null) {
            tvBuyerJobsDone.setText(getString(R.string.job_contract, user.getMyInfo().getBuyerContracts()));
            tvBuyerSpent.setText(getString(R.string.spent, Util.formatNumber(user.getMyInfo().getBuyerSpent().doubleValue()), user.getCurrency()));
            tvDoerJobsDone.setText(getString(R.string.jobs_done, user.getMyInfo().getDoerJobsDone()));
            tvDoerEarned.setText(getString(R.string.earned, Util.formatNumber(user.getMyInfo().getDoerEarned().doubleValue()), user.getCurrency()));
        } else {
            tvBuyerJobsDone.setText(getString(R.string.job_contract, 0));
            tvBuyerSpent.setText(getString(R.string.spent, "0", user.getCurrency()));
            tvDoerJobsDone.setText(getString(R.string.jobs_done, 0));
            tvDoerEarned.setText(getString(R.string.earned, "0", user.getCurrency()));
        }
    }

    @OnClick({R.id.cvRateAsDoer, R.id.btnDoerNext})
    public void onRateDoerClick() {
        Intent i = new Intent(StatisticActivity.this, BuyerDoerStatisticActivity.class);
        i.putExtra("user", user);
        i.putExtra("isBuyer", false);
        startActivity(i);
    }

    @OnClick({R.id.cvRateAsBuyer, R.id.btnBuyerNext})
    public void onRateBuyerClick() {
        Intent i = new Intent(StatisticActivity.this, BuyerDoerStatisticActivity.class);
        i.putExtra("user", user);
        i.putExtra("isBuyer", true);
        startActivity(i);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserEvent(GetUserEvent event) {
        if (event.user != null) {
            user = event.user;
            setViews();
        }
    }
}
