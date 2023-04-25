package com.justraspberry.jobdeal.view.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.misc.OnTimerFinishedCallback;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.rest.service.event.VerifyVerificationEvent;
import com.justraspberry.jobdeal.view.login.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Context.TELEPHONY_SERVICE;


public class SmsFragment extends Fragment {

    User user;

    @BindView(R.id.etPin)
    PinView etPin;
    @BindView(R.id.pbNext)
    ProgressBar pbNext;
    @BindView(R.id.tvTimer)
    TextView tvTimer;
    @BindView(R.id.btnNext)
    MaterialButton btnNext;

    CountDownTimer countDownTimer;

    boolean isFromActivity = false;

    public SmsFragment() {
        // Required empty public constructor
    }


    public static SmsFragment newInstance(User user) {
        SmsFragment fragment = new SmsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public static SmsFragment newInstance(User user, boolean isFromActivity) {
        SmsFragment fragment = new SmsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putBoolean("isFromActivity", isFromActivity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            isFromActivity = getArguments().getBoolean("isFromActivity", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countDownTimer = Util.reverseTimer(120, tvTimer, getContext(), new OnTimerFinishedCallback() {
            @Override
            public void onTimerFinished() {
                ((RegisterActivity) getActivity()).enableBackButton(true);
            }
        });
        countDownTimer.start();
        etPin.requestFocus();
        etPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Util.hideKeyboardFrom(getActivity(), etPin);
                    etPin.clearFocus();
                    onNextClick();
                    return true;
                }
                return false;
            }
        });

        ((RegisterActivity) getActivity()).enableBackButton(false);
    }

    public void showLoading(boolean show){
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(getContext(), 44);
            btnNext.setLayoutParams(layoutParams);
            btnNext.setCornerRadius(Util.dpToPx(getContext(), 32));
            btnNext.setText("");
            btnNext.setEnabled(false);
            pbNext.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnNext.setLayoutParams(layoutParams);
            btnNext.setText(getString(R.string.next));
            btnNext.setEnabled(true);
            btnNext.setCornerRadius(Util.dpToPx(getContext(), 8));
            pbNext.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
            user.setCode(etPin.getText().toString());

            showLoading(true);
            ApiRestClient.getInstance().verifyVerification(user);
    }

    @OnClick(R.id.tvTimer)
    public void onResendClick(){
        tvTimer.setEnabled(false);
        ((RegisterActivity) getActivity()).enableBackButton(false);
        countDownTimer = Util.reverseTimer(120, tvTimer, getContext(), new OnTimerFinishedCallback() {
            @Override
            public void onTimerFinished() {
                ((RegisterActivity) getActivity()).enableBackButton(true);
            }
        });
        countDownTimer.start();

        ApiRestClient.getInstance().requestVerification(user);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyVerificationEvent(VerifyVerificationEvent event){
        showLoading(false);
        if(event.user != null){
            user.setUid(event.user.getUid());

            if(isFromActivity){
                Intent intent = new Intent();
                intent.putExtra("user", user);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            } else {
                ((RegisterActivity) getActivity()).enableBackButton(true);
                SP.getInstance().setRegistrationInProgress(false);
                RegisterFragment registerFragment = RegisterFragment.newInstance(user);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flRoot, registerFragment);
                transaction.addToBackStack("register");
                transaction.commit();
            }
        } else {
            Util.displaySnackBar(Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG)
                    , Util.dpToPx(getContext(), 0)
                    , Util.dpToPx(getContext(), 48));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestVerificationEvent(SendVerificationEvent event){
        showLoading(false);
        if(event.user != null){
            user.setUid(event.user.getUid());
            user.setId(event.user.getId());
        } else {
            Util.displaySnackBar(Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG)
                    , Util.dpToPx(getContext(), 0)
                    , Util.dpToPx(getContext(), 48));
        }
    }


}
