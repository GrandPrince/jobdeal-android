package com.justraspberry.jobdeal.view.payment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.KlarnaRequestEvent;
import com.justraspberry.jobdeal.rest.service.event.PaymentEvent;
import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class KlarnaActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rlKlarna)
    RelativeLayout rlKlarna;


    KlarnaCheckout klarnaCheckout;
    String refId;
    int paymentReqRetry = 0;
    Job job;
    int type = -1;
    Applicant applicant;
    Uri confirmationUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klarna);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        klarnaCheckout = new KlarnaCheckout(KlarnaActivity.this, "https://dev.jobdeal.com/api/payment/klarna/checkout");
        klarnaCheckout.setSignalListener(new SignalListener() {
            @Override
            public void onSignal(String event, JSONObject jsonObject) {
                Timber.e(jsonObject.toString());
                if(event.equals("complete")) {
                    try {
                        confirmationUri = Uri.parse(jsonObject.getString("uri"));
                        ApiRestClient.getInstance().klarnaComplete(confirmationUri.getLastPathSegment());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        job = getIntent().getParcelableExtra("job");
        type = getIntent().getIntExtra("type", -1);
        applicant = getIntent().getParcelableExtra("applicant");

        if(type == -1)
            finish();

        if(type == Consts.PAY_BOOST || type == Consts.PAY_SPEEDY || type == Consts.PAY_BOOST_SPEEDY || type == Consts.PAY_LIST || type == Consts.PAY_UNDERBIDDER){
            ApiRestClient.getInstance().klarnaPayJob(job, type);
        } else if (type == Consts.PAY_CHOOSE){
            ApiRestClient.getInstance().klarnaPayChoose(applicant);
        } else if (type == Consts.PAY_SUBSCRIBE) {
            ApiRestClient.getInstance().klarnaPaySubscribe();
        } else if (type == Consts.CANCEL_SUBSCRIBE) {
            ApiRestClient.getInstance().klarnaCancelSubscribe();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.e("On New Intent");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        klarnaCheckout.resume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKlarnaSnippet(KlarnaRequestEvent event){
        if(event.klarnaRequest != null){
            klarnaCheckout.setSnippet(event.klarnaRequest.getHtml());
            rlKlarna.addView(klarnaCheckout.getView());
            refId = event.klarnaRequest.getRefId();
        } else {
            Snackbar.make(toolbar, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKlarnaComplete(PaymentEvent event){
        if(event.payment!=null){
            if(event.payment.getStatus().equals("PAID")) {
                setResult(RESULT_OK);
                klarnaCheckout.setSnippet(event.payment.getHtmlSnippet());
            } else {
                new AlertDialog.Builder(KlarnaActivity.this).setMessage("Payment NOT successful!").setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        dialog.dismiss();
                        finish();
                    }
                }).show();
            }
        } else {
            if(paymentReqRetry < 3) {
                ApiRestClient.getInstance().klarnaComplete(refId);
                paymentReqRetry++;
            } else
                finish();
        }
    }
}
