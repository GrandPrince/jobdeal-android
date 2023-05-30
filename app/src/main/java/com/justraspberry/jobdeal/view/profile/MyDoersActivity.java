package com.justraspberry.jobdeal.view.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.adapter.ApplicantsAdapter;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.ChooseApplicantEvent;
import com.justraspberry.jobdeal.rest.service.event.GetJobApplicantsEvent;
import com.justraspberry.jobdeal.view.payment.PaymentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MyDoersActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivJobImage)
    ImageView ivJobImage;
    @BindView(R.id.tvJobName)
    TextView tvJobName;
    @BindView(R.id.tvJobPrice)
    TextView tvJobPrice;
    @BindView(R.id.tvJobDescription)
    TextView tvJobDescription;
    @BindView(R.id.tvJobTime)
    TextView tvJobTime;
    @BindView(R.id.ivNoApplicants)
    ImageView ivNoApplicants;
    @BindView(R.id.rvApplicants)
    RecyclerView rvApplicants;
    @BindView(R.id.rlUnderbidder)
    RelativeLayout rlUnderbidder;


    Job job;
    ArrayList<Applicant> jobApplicants = new ArrayList<>();
    ApplicantsAdapter applicantsAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doers);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        job = getIntent().getParcelableExtra("job");

        if (job != null) {
            setupView(job);
        }

        getAllJobApplicants(job);

        applicantsAdapter = new ApplicantsAdapter(this, jobApplicants, job);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvApplicants.setLayoutManager(linearLayoutManager);
        rvApplicants.setAdapter(applicantsAdapter);

        applicantsAdapter.setOnDoerClickListener(new ApplicantsAdapter.onDoerClickListener() {
            @Override
            public void onDoerClick(View view, int position, Applicant applicant) {
                if (job.getExpired())
                    return;

                if(applicant.getPrice() < job.getPrice() && !job.getUnderbidderListed()){
                    onUnlockClick();
                    return;
                }

                /*if (applicant.getChoosed()) {*/
                Intent i = new Intent(MyDoersActivity.this, DoerBuyerProfile.class);
                i.putExtra("isBuyer", false);
                i.putExtra("applicant", applicant);
                i.putExtra("job", job);
                startActivityForResult(i, Consts.REQ_DOER_PROFILE);
               /* } else {
                    if (job.getPrice() > applicant.getPrice()) {
                        Intent i = new Intent(MyDoersActivity.this, PaymentActivity.class);
                        i.putExtra("job", job);
                        i.putExtra("applicant", applicant);
                        i.putExtra("type", Consts.PAY_CHOOSE);
                        startActivityForResult(i, Consts.REQ_PAYMENT);
                    } else {
                        int i = 0;
                        for (Applicant a : jobApplicants) {
                            if (a.getId().equals(applicant.getId())) {
                                a.setChoosed(true);
                                applicantsAdapter.notifyItemChanged(i);
                                break;
                            }
                            i++;
                        }

                        ApiRestClient.getInstance().chooseApplicant(applicant, job.getId());
                    }
                }*/
            }

            @Override
            public void onAvatarClick(View view, int position, Applicant applicant) {
                Intent i = new Intent(MyDoersActivity.this, ImageActivity.class);
                i.putExtra("imageUrl", applicant.getUser().getAvatar());
                startActivity(i);
            }
        });

        applicantsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.REQ_PAYMENT) {
            if (resultCode == RESULT_OK) {
                Applicant applicant = data.getParcelableExtra("applicant");

                int i = 0;
                for (Applicant a : jobApplicants) {
                    if (a.getId().equals(applicant.getId())) {
                        a.setChoosed(true);
                        applicantsAdapter.notifyItemChanged(i);
                        break;
                    }
                    i++;
                }

                ApiRestClient.getInstance().chooseApplicant(applicant, job.getId());
            }
        } else if (requestCode == Consts.REQ_DOER_PROFILE && resultCode == RESULT_OK) {
            Applicant applicant = data.getParcelableExtra("applicant");

            int i = 0;
            for (Applicant a : jobApplicants) {
                if (a.getId().equals(applicant.getId())) {
                    a.setChoosed(true);
                    applicantsAdapter.notifyItemChanged(i);
                    break;
                }
                i++;
            }
        } else if (requestCode == Consts.REQ_PAY_UNDERBIDDER) {
            if (resultCode == RESULT_OK) {
                job.setUnderbidderListed(true);
                Intent i = new Intent();
                i.putExtra("job", job);
                setResult(RESULT_OK, i);
                setupView(job);
                applicantsAdapter.updateJob(job);
            } else {
                Snackbar.make(rvApplicants, getString(R.string.error_payment_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getAllJobApplicants(Job job) {
        ApiRestClient.getInstance().getJobApplicants(job.getId());
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

    public void setupView(Job job) {
        Glide.with(this).load(job.getMainImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_job).transforms(new CenterCrop(), new RoundedCorners(16)))
                .into(ivJobImage);
        tvJobName.setText(job.getName());
        tvJobPrice.setText(Util.formatNumber(Util.roundNumber(job.getPrice(), 2)) + " " + job.getCurrency());
        tvJobDescription.setText(job.getDescription());
        if (job.getExpired())
            tvJobTime.setText(getString(R.string.job_expired));
        else
            tvJobTime.setText(job.getExpireAt());

        if(job.getUnderbidderListed()){
            rlUnderbidder.setVisibility(View.GONE);
        } else {
            rlUnderbidder.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tvUnlock)
    public void onUnlockClick(){
        Intent i = new Intent(this, PaymentActivity.class);
        i.putExtra("job", job);
        i.putExtra("type", Consts.PAY_UNDERBIDDER);
        startActivityForResult(i, Consts.REQ_PAY_UNDERBIDDER);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getJobApplicants(GetJobApplicantsEvent event) {
        if (event.jobApplicants != null && event.jobApplicants.size() > 0) {
            jobApplicants.clear();
            ivNoApplicants.setVisibility(View.GONE);
            jobApplicants.addAll(event.jobApplicants);
            applicantsAdapter.notifyDataSetChanged();
        } else {
            ivNoApplicants.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseApplicant(ChooseApplicantEvent event) {
        if (event.job == null) {//error happen
            getAllJobApplicants(job);
            Snackbar.make(toolbar, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }
}
