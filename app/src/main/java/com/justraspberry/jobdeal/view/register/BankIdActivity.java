package com.justraspberry.jobdeal.view.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.BankIdCollectEvent;
import com.justraspberry.jobdeal.rest.service.event.BankIdEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BankIdActivity extends AppCompatActivity {

    @BindView(R.id.tvHint)
    TextView tvHint;
    @BindView(R.id.pbBankId)
    ProgressBar pbBank;
    @BindView(R.id.btnVerify)
    MaterialButton btnVerify;

    String bankIdRefId = null;
    boolean collectInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_id);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));

        setFinishOnTouchOutside(false);

        ApiRestClient.getInstance().authBankId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(bankIdRefId!=null && !collectInProgress){
            ApiRestClient.getInstance().collectBankId(bankIdRefId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @OnClick(R.id.btnVerify)
    public void onVerifyCancelClick(){
        //TODO call bank id cancel
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBankIdAuth(BankIdEvent event){
        if(event.bankIdAuth != null){
            bankIdRefId = event.bankIdAuth.getOrderRef();
            Intent intent = new Intent();
            intent.setPackage("com.bankid.bus");
            intent.setAction(Intent.ACTION_VIEW); intent.setData(Uri.parse("bankid:///?autostarttoken=" + event.bankIdAuth.getAutoStartToken() + "&redirect=null")) ;
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBankIdCollectEvent(BankIdCollectEvent event){
        collectInProgress = false;
        if(event.bankIdCollect != null){
            Timber.e(event.bankIdCollect.getStatus());
            if(event.bankIdCollect.getStatus().equals("pending")){ //pending status, send collect again
                ApiRestClient.getInstance().collectBankId(event.bankIdCollect.getOrderRef());
                collectInProgress = true;
            } else if (event.bankIdCollect.getStatus().equals("failed")) {//failed, show error message
                bankIdRefId = null;
                Intent i = new Intent();
                i.putExtra("error", event.bankIdCollect.getHint());
                setResult(RESULT_FIRST_USER, i);
                finish();
            } else if (event.bankIdCollect.getStatus().equals("complete")){//complete return bankid result
                bankIdRefId = null;
                Intent i = new Intent();
                i.putExtra("bankId", event.bankIdCollect.getUser().getPersonalNumber());
                setResult(RESULT_OK, i);
                finish();
            }
        } else {
            Intent i = new Intent();
            setResult(RESULT_CANCELED, i);
            finish();
        }
    }
}
