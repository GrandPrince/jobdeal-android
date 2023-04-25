package com.justraspberry.jobdeal.view.job;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Report;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.ReportEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.btnReport)
    MaterialButton btnReport;
    @BindView(R.id.etReport)
    EditText etReport;
    @BindView(R.id.pbReport)
    ProgressBar pbReport;

    Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        job = getIntent().getParcelableExtra("job");
    }

    @OnClick(R.id.btnReport)
    public void onReportClick() {
        showLoading(true);

        Report report = new Report();
        report.setJob(job);
        if (!etReport.getText().toString().equalsIgnoreCase(""))
            report.setReportText(etReport.getText().toString());
        else
            report.setReportText(getString(R.string.offending));
        report.setUser(App.getInstance().getCurrentUser());

        ApiRestClient.getInstance().reportJob(report);
    }

    public void showLoading(boolean show) {
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnReport.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(this, 44);
            btnReport.setLayoutParams(layoutParams);
            btnReport.setCornerRadius(Util.dpToPx(this, 32));
            btnReport.setText("");
            btnReport.setEnabled(false);
            pbReport.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnReport.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnReport.setLayoutParams(layoutParams);
            btnReport.setText(getString(R.string.register));
            btnReport.setEnabled(true);
            btnReport.setCornerRadius(Util.dpToPx(this, 8));
            pbReport.setVisibility(View.INVISIBLE);
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
    public void onReportEvent(ReportEvent event) {
        showLoading(false);
        if (event.report != null) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
