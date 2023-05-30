package com.justraspberry.jobdeal.view.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.CloseLoginEvent;
import com.justraspberry.jobdeal.misc.PhoneValidator;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.LoginEvent;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.main.MainActivity;
import com.justraspberry.jobdeal.view.register.RegisterActivity;
import com.justraspberry.jobdeal.view.tutorial.TutorialActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.cvLoginForm)
    CardView cvLoginForm;
    @BindView(R.id.cvRegisterForm)
    CardView cvRegisterForm;
    @BindView(R.id.btnNext)
    MaterialButton btnNext;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etSurname)
    EditText etSurname;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.pbLogin)
    ProgressBar pbLogin;
    @BindView(R.id.rlSplash)
    RelativeLayout rlSplash;
    @BindView(R.id.tvEnterAsGuest)
    TextView tvEnterAsGuest;
    @BindView(R.id.tvForgot)
    TextView tvForgot;

    @BindView(R.id.vvSplash)
    VideoView vvSplash;
    @BindView(R.id.placeholder)
    View splashPlaceholder;
    @BindView(R.id.ivSplashLogo)
    ImageView ivSplashLogo;

    boolean isRegister = false, isLoginFinished = false;

    String country = "se";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        tvLogin.setActivated(true);

        TelephonyManager telman = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (telman != null && telman.getNetworkCountryIso() != null && !telman.getNetworkCountryIso().equalsIgnoreCase("")) {
            country = telman.getNetworkCountryIso();
            etMobile.setHint(getString(R.string.mobile_number));
            etMobile.setText("+" + Util.getPrefixNumberForCountry(LoginActivity.this));
        }

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onNextClick();
                    return true;
                }
                return false;
            }
        });

        etMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    onNextClick();
                    return true;
                }
                return false;
            }
        });

        if (SP.getInstance().getUser() != null) {
            playSplashVideo(false);

            User user = new User();
            user.setPassword(SP.getInstance().getPassword());
            user.setEmail(SP.getInstance().getUser().getEmail());
            user.setCountry(Util.getCountry(this));
            user.setLocale(getString(R.string.locale));

            ApiRestClient.getInstance().login(user);
            return;
        } else if (getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase("logout")) {
            rlSplash.setVisibility(View.GONE);
        } else {
           playSplashVideo(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Consts.REQ_TUTORIAL){
            SP.getInstance().setFirstStart(false);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void playSplashVideo(boolean guestMode){
         int loginCount = SP.getInstance().getLoginCount();
         boolean isRegistrationInProgress = SP.getInstance().isRegistrationInProgress();

            if (loginCount < 4) {
                ivSplashLogo.setVisibility(View.GONE);
                vvSplash.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video));
                vvSplash.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorWhite));
                vvSplash.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Timber.e("vvSplash onCompletion");
                        splashPlaceholder.setVisibility(View.GONE);
                        vvSplash.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorWhite));

                        if(SP.getInstance().isFirstStart()){
                            Intent i = new Intent(LoginActivity.this, TutorialActivity.class);
                            startActivityForResult(i, Consts.REQ_TUTORIAL);
                        } else if(guestMode || isLoginFinished){
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
                vvSplash.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        //vvSplash.setZOrderOnTop(true);
                        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                    vvSplash.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, android.R.color.transparent));
                                    splashPlaceholder.setVisibility(View.GONE);
                                    return true;
                                }
                                return false;
                            }
                    });
                }});

                vvSplash.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Timber.e("vvSplash Error: " + what);
                        vvSplash.setVisibility(View.GONE);
                        ivSplashLogo.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                vvSplash.start();
            } else {
                if(isRegistrationInProgress){
                    Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else if(guestMode){
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    User user = new User();
                    user.setPassword(SP.getInstance().getPassword());
                    user.setEmail(SP.getInstance().getUser().getEmail());
                    user.setCountry(Util.getCountry(LoginActivity.this));
                    user.setLocale(getString(R.string.locale));

                    ApiRestClient.getInstance().login(user);
                }
            }

            SP.getInstance().setLoginCount(loginCount + 1);
    }

    @OnClick({R.id.tvRegister, R.id.tvLogin})
    public void toggleForm(View view) {
        if (view.getId() == R.id.tvLogin) {
            tvLogin.setActivated(true);
            tvRegister.setActivated(false);
            cvLoginForm.setVisibility(View.VISIBLE);
            cvRegisterForm.setVisibility(View.GONE);
            btnNext.setText(getString(R.string.login));
            isRegister = false;
            tvForgot.setVisibility(View.VISIBLE);
            tvEnterAsGuest.setVisibility(View.VISIBLE);
        } else {
            tvLogin.setActivated(false);
            tvRegister.setActivated(true);
            cvRegisterForm.setVisibility(View.VISIBLE);
            cvLoginForm.setVisibility(View.GONE);
            btnNext.setText(getString(R.string.next));
            isRegister = true;
            tvForgot.setVisibility(View.GONE);
            tvEnterAsGuest.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tvForgot)
    public void onForgotClick() {
        Intent i = new Intent(LoginActivity.this, ForgotPassActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tvEnterAsGuest)
    public void onEnterAsGuest() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btnNext)
    public void onNextClick() {
        user = new User();

        if (isRegister) {
            if (!isRegisterValid())
                return;

            user.setName(etName.getText().toString());
            user.setSurname(etSurname.getText().toString());
            user.setMobile(PhoneValidator.getPhoneNumberWithRegion(country, etMobile.getText().toString(), country));
            user.setCountry(country);
            user.setLocale(getString(R.string.locale));

            showLoading(true);

            SP.getInstance().setRegistrationInProgress(true);
            ApiRestClient.getInstance().requestVerification(user);
        } else {
            if (!isLoginValid())
                return;

            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());

            SP.getInstance().setPassword(etPassword.getText().toString());

            showLoading(true);

            ApiRestClient.getInstance().login(user);
        }
    }

    public void showLoading(boolean show) {
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(this, 44);
            btnNext.setLayoutParams(layoutParams);
            btnNext.setCornerRadius(Util.dpToPx(this, 32));
            btnNext.setText("");
            btnNext.setEnabled(false);
            pbLogin.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnNext.setLayoutParams(layoutParams);
            if (isRegister)
                btnNext.setText(getString(R.string.next));
            else
                btnNext.setText(getString(R.string.login));
            btnNext.setEnabled(true);
            btnNext.setCornerRadius(Util.dpToPx(this, 8));
            pbLogin.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isRegisterValid() {
        boolean isValid = true;

        if (etName.getText().toString().length() < 2) {
            isValid = false;
            etName.setError(getString(R.string.error_name));
        }

        if (etMobile.getText().toString().length() < 6) {
            isValid = false;
            etMobile.setError(getString(R.string.error_mobile));
        }

        if(PhoneValidator.validate(country, etMobile.getText().toString(), country) == null){
            isValid = false;
            etMobile.setError(getString(R.string.error_mobile));
        }

        if (etSurname.getText().toString().length() < 2) {
            isValid = false;
            etSurname.setError(getString(R.string.error_surname));
        }

        return isValid;
    }

    public boolean isLoginValid() {
        boolean isValid = true;

        if (etEmail.getText().toString().length() < 2 || !etEmail.getText().toString().contains(".") || !etEmail.getText().toString().contains("@")) {
            isValid = false;
            etEmail.setError(getString(R.string.error_email));
        }

        if (etPassword.getText().toString().length() < 6) {
            isValid = false;
            etPassword.setError(getString(R.string.error_password));
        }

        return isValid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                }
                if (deepLink != null) {
                    String jobIdString = deepLink.getLastPathSegment();
                    if (jobIdString != null) {
                        try {
                            App.getInstance().setJobId(Integer.parseInt(jobIdString));
                            Intent i = new Intent(LoginActivity.this, JobDetailsActivity.class);
                            i.putExtra("jobId", App.getInstance().getJobId());
                            startActivityForResult(i, Consts.REQ_JOB_DETAIL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Timber.e("deepLink is null");
                }
            }

        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e("GetDynamicLink " + e.toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLoginEvent(LoginEvent event) {
        EventBus.getDefault().removeStickyEvent(LoginEvent.class);
        //Timber.e(App.getInstance().getGson().toJson(event.loginResponse));
        showLoading(false);
        if (event.loginResponse != null) {
            SP.getInstance().setUser(event.loginResponse.getUser());
            SP.getInstance().setJWT(event.loginResponse.getJwt());
            App.getInstance().setCurrentUser(event.loginResponse.getUser());

            if(!vvSplash.isPlaying()){
                Intent i = new Intent(LoginActivity.this, MainActivity.class);

                if(getIntent().getExtras() != null) {
                    i.putExtras(getIntent().getExtras());
                    i.setAction(getIntent().getAction());
                }

                startActivity(i);
                finish();
            }

            isLoginFinished = true;
        } else {
            Util.hideKeyboardFrom(LoginActivity.this, etPassword);
            if ((event.statusCode == 470 || event.statusCode == 401) && event.errorMessage != null) {
                Util.displaySnackBar(Snackbar.make(tvLogin, event.errorMessage, Snackbar.LENGTH_LONG), Util.dpToPx(this, 0), Util.dpToPx(this, 48));
            } else if (event.statusCode == 500) {
                Util.displaySnackBar(Snackbar.make(tvLogin, getString(R.string.default_error), Snackbar.LENGTH_LONG), Util.dpToPx(this, 0), Util.dpToPx(this, 48));
            } else {
                rlSplash.setVisibility(View.GONE);
                SP.getInstance().logout();
                App.getInstance().setCurrentUser(null);
                Util.displaySnackBar(Snackbar.make(tvLogin, getString(R.string.default_error), Snackbar.LENGTH_LONG), Util.dpToPx(this, 0), Util.dpToPx(this, 48));

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCloseLoginEvent(CloseLoginEvent event) {
        EventBus.getDefault().removeStickyEvent(CloseLoginEvent.class);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestVerificationEvent(SendVerificationEvent event){
        showLoading(false);
        if(event.user != null){
            user.setUid(event.user.getUid());
            user.setId(event.user.getId());

            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        } else {
            Util.displaySnackBar(Snackbar.make(tvLogin, getString(R.string.default_error), Snackbar.LENGTH_LONG), Util.dpToPx(this, 0), Util.dpToPx(this, 48));
        }
    }
}
