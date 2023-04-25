package com.justraspberry.jobdeal.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.register.RegisterActivity;
import com.justraspberry.jobdeal.view.register.RegisterFragment;
import com.justraspberry.jobdeal.view.register.SmsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmsVerifyActivity extends AppCompatActivity {
    User user;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verify);
        ButterKnife.bind(this);

        user = getIntent().getParcelableExtra("user");

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.registration));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        getSupportActionBar().setTitle(getString(R.string.registration));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SmsFragment smsFragment = SmsFragment.newInstance(user, true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flRoot, smsFragment);
        transaction.commit();
    }


}