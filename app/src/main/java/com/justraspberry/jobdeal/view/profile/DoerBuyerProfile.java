package com.justraspberry.jobdeal.view.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.RateAdapter;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Rate;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.ChooseApplicantEvent;
import com.justraspberry.jobdeal.rest.service.event.GetRatesEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.payment.PaymentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoerBuyerProfile extends AppCompatActivity {

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
    @BindView(R.id.tvDoerOfferBuyerContract)
    TextView tvDoerOfferBuyerContract;
    @BindView(R.id.btnNext)
    ImageView btnNext;
    @BindView(R.id.tvValue)
    TextView tvValue;
    @BindView(R.id.tvMobileNumber)
    TextView tvMobileNumber;

    @BindView(R.id.rvRatings)
    RecyclerView rvRatings;
    @BindView(R.id.rlNoReviews)
    RelativeLayout rlNoReviews;
    @BindView(R.id.tvRatedAsBuyerDoer)
    TextView tvRatedAsBuyerDoer;
    @BindView(R.id.vSeparator)
    View vSeparator;
    @BindView(R.id.cvRatingList)
    CardView cvRatingList;

    @BindView(R.id.tvApply)
    TextView tvApply;
    @BindView(R.id.ivMessage)
    ImageView ivMessage;
    @BindView(R.id.ivPhone)
    ImageView ivPhone;
    @BindView(R.id.mcActions)
    MaterialCardView mcActions;
    @BindView(R.id.tvAboutMe)
    TextView tvAboutMe;

    User user;
    User currentUser;
    boolean isBuyer = false;
    Applicant applicant;
    Job job;

    //ratings
    RateAdapter rateAdapter;
    ArrayList<Rate> rates = new ArrayList<>();
    boolean noMore = false, isLoading = false;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doer_buyer_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        user = getIntent().getParcelableExtra("user");
        isBuyer = getIntent().getBooleanExtra("isBuyer", false);
        applicant = getIntent().getParcelableExtra("applicant");
        job = getIntent().getParcelableExtra("job");
      currentUser= App.getInstance().getCurrentUser();

        if (user == null)
            user = applicant.getUser();

        setViews();

        rateAdapter = new RateAdapter(this, rates);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRatings.setLayoutManager(linearLayoutManager);
        rvRatings.setAdapter(rateAdapter);

        rvRatings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !isLoading) {
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!noMore && rates.size() >= 10 && lastVisibleItem > rates.size() - 10) {
                        page++;
                        getComments(page);
                    }
                }
            }
        });

        getComments(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getComments(int page) {
        this.page = page;

        if (!noMore) {
            isLoading = true;
            if (isBuyer)
                ApiRestClient.getInstance().getRateByBuyerId(user.getId(), page);
            else
                ApiRestClient.getInstance().getRateByDoerId(user.getId(), page);
        }
    }

    public void setViews() {
        Glide.with(this).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(civProfilePicture);
        tvFullName.setText(user.getName() + " " + user.getSurname());
        tvLocation.setText(user.getCity());
        tvJoinDate.setText(getString(R.string.user_since, user.getCreatedAt()));

        if(user.getAboutMe() != null){
            tvAboutMe.setText(user.getAboutMe());
            tvAboutMe.setVisibility(View.VISIBLE);
        } else {
            tvAboutMe.setVisibility(View.GONE);
        }


        if (isBuyer) {
            dummySeparator.setVisibility(View.GONE);
            tvMobileNumber.setVisibility(View.GONE);

            tvRatedAsBuyerDoer.setText(user.getName() +  " " + getString(R.string.rated_as_buyer));
            vSeparator.setVisibility(View.GONE);

            tvDoerOfferBuyerContract.setVisibility(View.GONE);
            tvValue.setVisibility(View.GONE);
            toolbar.setTitle(getString(R.string.buyer_profile));

            mcActions.setVisibility(View.GONE);
        } else {
            dummySeparator.setVisibility(View.VISIBLE);
            tvMobileNumber.setText(user.getMobile());
            tvDoerOfferBuyerContract.setVisibility(View.VISIBLE);
            tvValue.setVisibility(View.VISIBLE);
            toolbar.setTitle(getString(R.string.doer_profile));

            tvRatedAsBuyerDoer.setText(getString(R.string.rated_as_doer));
            vSeparator.setVisibility(View.VISIBLE);

            tvDoerOfferBuyerContract.setText(getString(R.string.doer_offer_for_job));
            tvValue.setText(Util.formatNumber(applicant.getPrice()) + " " + user.getCurrency());

            mcActions.setVisibility(View.VISIBLE);
            if (applicant.getChoosed()) {
                tvApply.setVisibility(View.GONE);
                ivMessage.setVisibility(View.VISIBLE);
                ivPhone.setVisibility(View.VISIBLE);
                tvMobileNumber.setVisibility(View.VISIBLE);
            } else {
                tvApply.setText(getString(R.string.choose_doer));
                tvApply.setVisibility(View.VISIBLE);
                ivMessage.setVisibility(View.VISIBLE);
                ivPhone.setVisibility(View.VISIBLE);
                tvMobileNumber.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.ivPhone)
    public void onPhoneClick() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", user.getMobile(), null)));
    }

    @OnClick(R.id.ivMessage)
    public void onMessageClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", user.getMobile(), null)));
    }

    @OnClick(R.id.tvApply)
    public void onApplyClick() {
        Intent intent = new Intent(DoerBuyerProfile.this, ConfirmChooseActivity.class);
        startActivityForResult(intent, Consts.REQ_CHOOSE);
    }

    @OnClick(R.id.civProfilePicture)
    public void onAvatarClick(){
        if(job == null)
            return;

        Intent i = new Intent(DoerBuyerProfile.this, ImageActivity.class);
        if(isBuyer)
            i.putExtra("imageUrl", job.getUser().getAvatar());
        else if (applicant != null)
            i.putExtra("imageUrl", applicant.getUser().getAvatar());
        startActivity(i);
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
        if (requestCode == Consts.REQ_PAYMENT) {
            if (resultCode == RESULT_OK) {
                applicant = data.getParcelableExtra("applicant");
                applicant.setJob(job);
                user = applicant.getUser();
                Intent intent = new Intent();
                intent.putExtra("applicant", applicant);
                setResult(RESULT_OK, intent);
                ApiRestClient.getInstance().chooseApplicant(applicant, job.getId());
            }
        } else if (requestCode == Consts.REQ_CHOOSE){
            if(resultCode == RESULT_OK && data != null){
                job.setHelpOnTheWay(data.getBooleanExtra("helpOnWay", true));
                applicant.setJob(job);
              /*  if (job.getPrice() > applicant.getPrice()) {
                    Intent i = new Intent(DoerBuyerProfile.this, PaymentActivity.class);
                    i.putExtra("job", job);
                    i.putExtra("applicant", applicant);
                    i.putExtra("type", Consts.PAY_CHOOSE);
                    startActivityForResult(i, Consts.REQ_PAYMENT);
                } else {*/
                    user = applicant.getUser();
                    Intent intent = new Intent();
                    intent.putExtra("applicant", applicant);
                    setResult(RESULT_OK, intent);
                    tvApply.setEnabled(false);
                    ApiRestClient.getInstance().chooseApplicant(applicant, job.getId());
               // }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRatesEvent(GetRatesEvent event) {
        isLoading = false;

        if (event.rates != null) {
            if (page == 0) {
                rates.clear();
                rates.addAll(event.rates);
                rateAdapter.notifyDataSetChanged();

                if (rates.size() == 0)
                    cvRatingList.setVisibility(View.INVISIBLE);
                else
                    cvRatingList.setVisibility(View.VISIBLE);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseApplicant(ChooseApplicantEvent event) {
        tvApply.setEnabled(true);
        if (event.job == null) {//error happen
            Snackbar.make(toolbar, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(toolbar, getString(R.string.choose_ok, applicant.getUser().getFullName()), Snackbar.LENGTH_LONG).show();
            applicant.setChoosed(true);
            setViews();
        }
    }
}
