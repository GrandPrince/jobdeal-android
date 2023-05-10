package com.justraspberry.jobdeal.view.payment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.event.PriceCalculationEvent;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.PaymentOption;
import com.justraspberry.jobdeal.model.Price;
import com.justraspberry.jobdeal.model.PriceCalculationRequest;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.PaymentEvent;
import com.justraspberry.jobdeal.rest.service.event.SwishRequestEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

public class PaymentActivity extends AppCompatActivity {

    @BindView(R.id.rlKlarnaPayment)
    RelativeLayout rlKlarna;
    @BindView(R.id.rlSwishPayment)
    RelativeLayout rlSwish;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvPriceDesc)
    TextView tvPriceDesc;
    @BindView(R.id.pbPayment)
    ProgressBar pbPayment;

    Job job;
    Applicant applicant;
    int type = -1;

    String refId;
    int paymentTryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));

        setFinishOnTouchOutside(false);

        job = getIntent().getParcelableExtra("job");
        type = getIntent().getIntExtra("type", -1);
        applicant = getIntent().getParcelableExtra("applicant");

        if (type == -1)
            finish();

        paymentTryCount = 0;

        PriceCalculationRequest priceCalculationRequest = new PriceCalculationRequest(job, applicant, type);
        ApiRestClient.getInstance().getPriceCalculation(priceCalculationRequest);
    }



    public void calculatePrice() {
        Price prices = App.getInstance().getPrices();
        if (type == Consts.PAY_BOOST) {
            Double price = Util.roundNumber(prices.getBoost().doubleValue(), 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        } else if (type == Consts.PAY_BOOST_SPEEDY) {
            Double price = Util.roundNumber(prices.getBoost().doubleValue() + prices.getSpeedy().doubleValue(), 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        } else if (type == Consts.PAY_SPEEDY) {
            Double price = Util.roundNumber(prices.getSpeedy().doubleValue(), 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        } else if (type == Consts.PAY_CHOOSE) {
            Double diff = 0d;
            if (job.getPrice() > applicant.getPrice())
                diff = (job.getPrice() - applicant.getPrice()) * (prices.getDifference() / 100);

            Double price = Util.roundNumber(job.getPrice() * (prices.getChoose() / 100) + diff, 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        } else if (type == Consts.PAY_LIST) {
            Double price = Util.roundNumber(job.getPrice() * (prices.getList() / 100), 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        } else if (type == Consts.PAY_SUBSCRIBE) {
            Double price = Util.roundNumber(prices.getSubscribe().doubleValue(), 2);
            tvPrice.setText(Util.formatNumber(price) + " " + App.getInstance().getInfo().getCurrency());
        }
    }

    @OnClick(R.id.rlSwishPayment)
    public void onSwishClick() {
        rlKlarna.setVisibility(View.INVISIBLE);
        rlSwish.setVisibility(View.INVISIBLE);
        pbPayment.setVisibility(View.VISIBLE);

        /*if (!Util.isSwishAppInstalled(this)) {
            Snackbar.make(tvPrice, getString(R.string.swish_not_installed), Snackbar.LENGTH_LONG).show();
            rlKlarna.setVisibility(View.INVISIBLE);
            rlSwish.setVisibility(View.INVISIBLE);
            pbPayment.setVisibility(View.VISIBLE);
            return;
        }*/

        if (type == Consts.PAY_BOOST || type == Consts.PAY_SPEEDY || type == Consts.PAY_BOOST_SPEEDY || type == Consts.PAY_LIST || type == Consts.PAY_UNDERBIDDER) {
            ApiRestClient.getInstance().swishPayJob(job, type);
        } else if (type == Consts.PAY_CHOOSE) {
            ApiRestClient.getInstance().swishPayChoose(applicant);
        }
    }

    @OnClick(R.id.rlKlarnaPayment)
    public void onKlarnaClick() {
        rlKlarna.setVisibility(View.INVISIBLE);
        rlSwish.setVisibility(View.INVISIBLE);
        pbPayment.setVisibility(View.VISIBLE);

        Intent i = new Intent(this, KlarnaActivity.class);
        i.putExtra("job", job);
        i.putExtra("type", type);
        i.putExtra("applicant", applicant);
        startActivityForResult(i, Consts.REQ_KLARNA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.REQ_KLARNA) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent();
                i.putExtra("job", job);
                i.putExtra("applicant", applicant);
                setResult(RESULT_OK, i);
                finish();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (refId != null) {
            ApiRestClient.getInstance().swishPayComplete(refId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (refId != null) {
            ApiRestClient.getInstance().swishPayComplete(refId);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwishRequestEvent(SwishRequestEvent event) {
        if (event.swishRequest != null) {
            refId = event.swishRequest.getRefId();
            Uri scheme = Uri.parse("swish://paymentrequest?token=" + event.swishRequest.getRefId() + "&callbackurl=" + "http://jobdeal.com/swish/" + event.swishRequest.getPaymentId());
            Intent intent = new Intent(Intent.ACTION_VIEW, scheme);
            intent.setPackage("se.bankgirot.swish");
            startActivityForResult(intent, Consts.REQ_SWISH);
        } else {
            Snackbar.make(tvPrice, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
            rlKlarna.setVisibility(View.VISIBLE);
            rlSwish.setVisibility(View.VISIBLE);
            pbPayment.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPaymentCompleteEvent(PaymentEvent event){
        EventBus.getDefault().removeStickyEvent(PaymentEvent.class);
        if(event.payment != null){
            if(event.payment.getStatus().equalsIgnoreCase("PAID") || event.payment.getStatus().equalsIgnoreCase("PENDING")){
                setResult(RESULT_OK);
                finish();
            }/* else if (event.payment.getStatus().equalsIgnoreCase("PENDING")){
                if(paymentTryCount < 30) {
                    rlSwish.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ApiRestClient.getInstance().swishPayComplete(refId);
                                paymentTryCount++;
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, 1000);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }*/ else {
                setResult(RESULT_CANCELED);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPriceCalculationEvent(PriceCalculationEvent event){
        if(event.priceCalculation != null){
            tvPrice.setText(Util.formatNumber(event.priceCalculation.getPrice()) + " " + event.priceCalculation.getCurrency());
            tvPriceDesc.setText(event.priceCalculation.getDescriptionText());

            for (PaymentOption paymentOption : event.priceCalculation.getPaymentOptions()){
                if(paymentOption.getId().equals(1)){
                    rlSwish.setVisibility(View.VISIBLE);
                } else if (paymentOption.getId().equals(2)){
                    rlKlarna.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Toast.makeText(PaymentActivity.this, getString(R.string.default_error), Toast.LENGTH_LONG).show();
            finish();
        }
    }


}
