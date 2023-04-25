package com.justraspberry.jobdeal.view.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.uCropEvent;
import com.justraspberry.jobdeal.model.User;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FragmentTransaction transaction;
    User user;
    Boolean canGoBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        user = getIntent().getParcelableExtra("user");

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.registration));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        getSupportActionBar().setTitle(getString(R.string.registration));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        SmsFragment smsFragment = SmsFragment.newInstance(user);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flRoot, smsFragment);
        transaction.addToBackStack("sms");
        transaction.commit();

    }

    public void enableBackButton(boolean enable){
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        canGoBack = enable;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(canGoBack) {
            onBackPressed();
            return super.onSupportNavigateUp();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if(canGoBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
            if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            EventBus.getDefault().postSticky(new uCropEvent(resultUri));
        } else {
            EventBus.getDefault().postSticky(new uCropEvent(null));
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            EventBus.getDefault().postSticky(new uCropEvent(null));
        } else {
            EventBus.getDefault().postSticky(new uCropEvent(null));
        }
    }
}
