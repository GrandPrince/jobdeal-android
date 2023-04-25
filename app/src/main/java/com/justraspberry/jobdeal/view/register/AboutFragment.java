package com.justraspberry.jobdeal.view.register;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.rest.service.event.VerifyVerificationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutFragment extends Fragment {

    User user;

    @BindView(R.id.etAbout)
    EditText etAbout;
    @BindView(R.id.btnNext)
    MaterialButton btnNext;

    CountDownTimer countDownTimer;

    public AboutFragment() {
        // Required empty public constructor
    }


    public static AboutFragment newInstance(User user) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etAbout.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    etAbout.clearFocus();
                    Util.hideKeyboardFrom(getActivity(), etAbout);
                    btnNext.callOnClick();
                    return true;
                }
                return false;
            }
        });

    }


    @OnClick(R.id.btnNext)
    public void onNextClick(){
        Util.hideKeyboardFrom(getContext(), etAbout);
        user.setAboutMe(etAbout.getText().toString());

        AvatarFragment avatarFragment = AvatarFragment.newInstance(user);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flRoot, avatarFragment);
        transaction.addToBackStack("avatar");
        transaction.commit();
    }
}
