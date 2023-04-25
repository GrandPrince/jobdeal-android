package com.justraspberry.jobdeal.view.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Api;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.ForgotPassEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPassActivity extends AppCompatActivity {

    @BindView(R.id.btnNext)
    MaterialButton btnNext;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etEmail)
    AppCompatEditText etEmail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void showLoading(boolean show) {
        if (show) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(this, 44);
            btnNext.setLayoutParams(layoutParams);
            btnNext.setCornerRadius(Util.dpToPx(this, 32));
            btnNext.setText("");
            btnNext.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            btnNext.setLayoutParams(layoutParams);
            btnNext.setText(getString(R.string.request_email));
            btnNext.setEnabled(true);
            btnNext.setCornerRadius(Util.dpToPx(this, 8));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
       /* if(etEmail.getText().toString().contains(".") && etEmail.getText().toString().contains("@")){
            etEmail.setError(getString(R.string.error_email));
            return;
        }*/

        showLoading(true);

        User user = new User();
        user.setEmail(etEmail.getText().toString());
        ApiRestClient.getInstance().forgotPass(user);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onForgotPassEvent(ForgotPassEvent forgotPassEvent){
        showLoading(false);

        if(forgotPassEvent.isOk){
            new AlertDialog.Builder(ForgotPassActivity.this).setMessage(getString(R.string.reset_instructions))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setTitle(getString(R.string.forgot_pass_title))
                    .show();
        } else {

        }
    }
}
