package com.justraspberry.jobdeal.view.rate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.NewNotificationEvent;
import com.justraspberry.jobdeal.model.Notification;
import com.justraspberry.jobdeal.model.Rate;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetNotificationEvent;
import com.justraspberry.jobdeal.rest.service.event.RateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RateActivity extends AppCompatActivity {

    @BindView(R.id.tvRated)
    TextView tvRatedAs;
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnRate)
    AppCompatButton btnRate;
    @BindView(R.id.tvAlreadyRated)
    TextView tvAlreadyRated;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    Float rating = 5.0f;
    Notification notification;
    int notificationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_buyer);
        ButterKnife.bind(this);

        notification = getIntent().getParcelableExtra("notification");
        notificationId = getIntent().getIntExtra("notificationId", -1);

        if (notification == null) {
            ApiRestClient.getInstance().getNotificationById(notificationId);
        } else {
            ApiRestClient.getInstance().getNotificationById(notification.getId());
        }
    }

    private void setViews() {
        if (notification.getType().equals(Consts.RATE_BUYER)) {

            if (notification.getRate() != null) {
                etComment.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                btnRate.setEnabled(false);
                tvAlreadyRated.setText(R.string.buyer_already_rated);
            }

            tvRatedAs.setText(getString(R.string.as_buyer, notification.getSender().getFullName()));
            Glide.with(this).load(notification.getSender().getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).circleCrop().dontAnimate())
                    .into(ivAvatar);
        } else if (notification.getType().equals(Consts.RATE_DOER)) {

            if (notification.getRate() != null) {
                etComment.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                btnRate.setEnabled(false);
                tvAlreadyRated.setText(R.string.doer_already_rated);
            }

            tvRatedAs.setText(getString(R.string.as_doer, notification.getSender().getFullName()));
            Glide.with(this).load(notification.getSender().getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder).circleCrop().dontAnimate())
                    .into(ivAvatar);
        }

        Glide.with(this).load(notification.getJob().getMainImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_job).centerCrop().dontAnimate())
                .into(ivImage);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            this.rating = rating;
        });
    }

    @OnClick(R.id.ivBack)
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.btnRate)
    public void onRateClick() {
        if (etComment.length() == 0) {
            etComment.setError(getString(R.string.error_empty_comment));
            return;
        }

        Rate rate = new Rate();

        if (notification.getType().equals(Consts.RATE_BUYER)) {
            rate.setBuyer(notification.getSender());
        } else {
            rate.setDoer(notification.getSender());
        }

        rate.setComment(etComment.getText().toString());
        rate.setFrom(App.getInstance().getCurrentUser());
        rate.setJob(notification.getJob());
        rate.setRate(Double.valueOf(rating));

        btnRate.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        ApiRestClient.getInstance().addRate(rate);
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
    public void onRateEvent(RateEvent event) {
        btnRate.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (event != null) {
            Intent intent = new Intent();
            intent.putExtra("notification", notification);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Snackbar.make(btnRate, getString(R.string.error_add_comment), Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationGetByIdEvent(NewNotificationEvent event) {
        if (event.notification != null) {
            notification = event.notification;
            setViews();
        } else {
            finish();
        }
    }
}
