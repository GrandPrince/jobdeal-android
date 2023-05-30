package com.justraspberry.jobdeal.view.job;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.ApplyJobEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ApplyJobActivity extends AppCompatActivity {

    @BindView(R.id.etPrice)
    TextInputEditText etPrice;
    @BindView(R.id.btnApply)
    RelativeLayout btnApply;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));

        setFinishOnTouchOutside(false);

        job = getIntent().getParcelableExtra("job");

        etPrice.setText(Util.formatNumber(Util.roundNumber(job.getPrice(), 2)));
    }

    private void showLoading(boolean show){
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            btnApply.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btnApply.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnApply)
    public void onApplyClick(){
        showLoading(true);

        Applicant applicant = new Applicant();
        applicant.setPrice(Double.parseDouble(etPrice.getText().toString()));
        applicant.setUser(App.getInstance().getCurrentUser());
        applicant.setJob(job);

        ApiRestClient.getInstance().applyJob(applicant);
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
    public void onApplyEvent(ApplyJobEvent event){
        showLoading(false);
        if(event.job != null){
            Intent i = new Intent();
            i.putExtra("job", event.job);
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_FIRST_USER);
        }

        finish();
    }
}
