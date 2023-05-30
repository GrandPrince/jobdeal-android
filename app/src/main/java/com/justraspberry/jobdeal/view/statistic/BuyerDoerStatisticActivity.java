package com.justraspberry.jobdeal.view.statistic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.RateAdapter;
import com.justraspberry.jobdeal.model.Rate;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetRatesEvent;
import com.justraspberry.jobdeal.view.job.AddJobActivity;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.payment.PaymentActivity;
import com.justraspberry.jobdeal.view.myjobs.MyJobsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class BuyerDoerStatisticActivity extends AppCompatActivity {

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

    //active doer part
    @BindView(R.id.vDummySeparator)
    View dummySeparator;
    @BindView(R.id.tvActiveDoerBuyer)
    TextView tvActiveDoerBuyer;
    @BindView(R.id.ivIconBeenHere)
    ImageView ivActiveIcon;

    @BindView(R.id.tvJobsDone)
    TextView tvJobsDoneContracts;
    @BindView(R.id.tvEarnedPlaceholder)
    TextView tvEarnedSpent;

    @BindView(R.id.rvRatings)
    RecyclerView rvRatings;
    @BindView(R.id.tvRatedAsBuyerDoer)
    TextView tvRatedAsBuyerDoer;
    @BindView(R.id.btnCheckJobOffers)
    AppCompatButton btnCheckCreateJob;
    @BindView(R.id.tvNumberOfJobs)
    TextView tvJobsDoneValue;
    @BindView(R.id.tvEarnedSpent)
    TextView tvEarnedSpentValue;

    @BindView(R.id.rlNoReviews)
    RelativeLayout rlNoReviews;

    User user;
    boolean isBuyer = false;
    boolean isSubscribed = true;

    //ratings
    RateAdapter rateAdapter;
    ArrayList<Rate> rates = new ArrayList<>();
    boolean noMore = false, isLoading = false;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_doer_statistic);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        user = getIntent().getParcelableExtra("user");
        isBuyer = getIntent().getBooleanExtra("isBuyer", false);
        isSubscribed = getIntent().getBooleanExtra("isSubscribed", true);

        setViews();

        rateAdapter = new RateAdapter(this, rates);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRatings.setLayoutManager(linearLayoutManager);
        rvRatings.setAdapter(rateAdapter);

        rvRatings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0 && !isLoading){
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!noMore && rates.size() >= 10 && lastVisibleItem > rates.size() - 10){
                        page++;
                        getComments(page);
                    }
                }
            }
        });

        getComments(0);
    }

    public void getComments(int page){
        this.page = page;

        if(!noMore){
            isLoading = true;
            if(isBuyer)
                ApiRestClient.getInstance().getRateByBuyerId(user.getId(), page);
            else
                ApiRestClient.getInstance().getRateByDoerId(user.getId(), page);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (App.getInstance().getCurrentUser().getSubscription() == null) {
            tvActiveDoerBuyer.setText(getString(R.string.become_active_doer));
        } else {
            tvActiveDoerBuyer.setText(getString(R.string.cancel_subscribtion));
        }
    }

    @OnClick(R.id.btnCheckJobOffers)
    public void goToMyJobs() {
        if (isBuyer) {
            if (App.getInstance().getCurrentUser() == null) {//guest
                new AlertDialog.Builder(this).setTitle(getString(R.string.login_required_title).toUpperCase())
                        .setMessage(getString(R.string.login_required_body))
                        .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i = new Intent(BuyerDoerStatisticActivity.this, LoginActivity.class);
                                i.setAction("logout");
                                startActivity(i);
                                finish();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return;
            }

            Intent i = new Intent(this, AddJobActivity.class);
            startActivityForResult(i, Consts.REQ_ADD_JOB);
        } else {
            Intent i = new Intent(this, MyJobsActivity.class);
            startActivity(i);
        }
    }

    @OnClick(R.id.tvActiveDoerBuyer)
    public void onBecomeActiveClicked() {
        if (App.getInstance().getCurrentUser().getSubscription() == null) {
            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("type", Consts.PAY_SUBSCRIBE);
            startActivity(i);
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.cancel_subscription_dialog_message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ApiRestClient.getInstance().klarnaCancelSubscribe();
                            tvActiveDoerBuyer.setText(getString(R.string.become_active_doer));
                            Toast.makeText(getApplication(), "You are canceled your subscription!", Toast.LENGTH_LONG).show();

                        }
                    })
//                        .setIcon(R.drawable.logo)
                    .show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setViews() {
        Glide.with(this).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(civProfilePicture);
        tvFullName.setText(user.getFullName());
        tvLocation.setText(user.getCity());
        tvJoinDate.setText(getString(R.string.user_since, user.getCreatedAt()));

        if (isBuyer) {
            dummySeparator.setVisibility(View.GONE);
            tvActiveDoerBuyer.setVisibility(View.GONE);
            ivActiveIcon.setVisibility(View.GONE);

            tvJobsDoneContracts.setText(getText(R.string.job_contract_simple));
            tvEarnedSpent.setText(getText(R.string.spent_simple));
            tvRatedAsBuyerDoer.setText(getString(R.string.rated_as_buyer));
            btnCheckCreateJob.setText(getString(R.string.create_job));
            toolbar.setTitle(getString(R.string.buyer_statistic));

            if (user.getMyInfo() != null) {
                tvJobsDoneValue.setText(String.valueOf(user.getMyInfo().getBuyerContracts()));
                tvEarnedSpentValue.setText(Util.formatNumber(user.getMyInfo().getBuyerSpent()) + " " +  user.getCurrency());
            }
        } else {
            //disabled subcription
            dummySeparator.setVisibility(View.GONE);
            tvActiveDoerBuyer.setVisibility(View.GONE);
            ivActiveIcon.setVisibility(View.GONE);

            tvJobsDoneContracts.setText(getText(R.string.jobs_done_simple));
            tvEarnedSpent.setText(getText(R.string.earned_simple));
            tvRatedAsBuyerDoer.setText(getString(R.string.rated_as_doer));
            btnCheckCreateJob.setText(getString(R.string.check_job_offers));
            toolbar.setTitle(getString(R.string.doer_statistic));

            if (user.getMyInfo() != null) {
                tvJobsDoneValue.setText(String.valueOf(user.getMyInfo().getDoerJobsDone()));
                tvEarnedSpentValue.setText(Util.formatNumber(user.getMyInfo().getDoerEarned()) + " " + user.getCurrency());
            }
        }
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
    public void onRatesEvent(GetRatesEvent event){
        isLoading = false;

        if(event.rates != null) {
            if (page == 0) {
                rates.clear();
                rates.addAll(event.rates);
                rateAdapter.notifyDataSetChanged();

                if(rates.size() == 0)
                    rlNoReviews.setVisibility(View.VISIBLE);
                else
                    rlNoReviews.setVisibility(View.GONE);
            } else {
                ArrayList<Rate> tmp = event.rates;
                if (tmp.size() == 0) {
                    noMore = true;
                    return;
                }
                int size = rates.size();
                rates.addAll(size, tmp);
                rateAdapter.notifyItemRangeInserted(size, tmp.size());

            }
        }
    }
}
