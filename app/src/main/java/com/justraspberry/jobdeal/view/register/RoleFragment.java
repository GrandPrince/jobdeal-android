package com.justraspberry.jobdeal.view.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.RegisterEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoleFragment extends Fragment {
    User user;

    @BindView(R.id.tvDoer)
    TextView tvDoer;
    @BindView(R.id.tvBuyer)
    TextView tvBuyer;
    @BindView(R.id.btnRegister)
    MaterialButton btnRegister;
    @BindView(R.id.pbLogin)
    ProgressBar pbLogin;

    boolean roleSelected = false;

    public RoleFragment() {
        // Required empty public constructor
    }


    public static RoleFragment newInstance(User user) {
        RoleFragment fragment = new RoleFragment();
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
        View view = inflater.inflate(R.layout.fragment_role, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void showLoading(boolean show) {
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnRegister.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(getContext(), 44);
            btnRegister.setLayoutParams(layoutParams);
            btnRegister.setCornerRadius(Util.dpToPx(getContext(), 32));
            btnRegister.setText("");
            btnRegister.setEnabled(false);
            pbLogin.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnRegister.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnRegister.setLayoutParams(layoutParams);

            btnRegister.setText(getString(R.string.register));
            btnRegister.setEnabled(true);
            btnRegister.setCornerRadius(Util.dpToPx(getContext(), 8));
            pbLogin.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.btnRegister)
    public void onRegisterClick() {
        if (!roleSelected) {
            Snackbar.make(tvBuyer, getString(R.string.role_not_selected), Snackbar.LENGTH_LONG).show();
            return;
        }

        showLoading(true);
        ApiRestClient.getInstance().register(user);
    }

    @OnClick({R.id.tvDoer, R.id.tvBuyer})
    public void toggleForm(View view) {
        roleSelected = true;
        if (view.getId() == R.id.tvDoer) {
            tvDoer.setActivated(true);
            tvBuyer.setActivated(false);
            user.setRole(Consts.ROLE_DOER);
        } else {
            tvDoer.setActivated(false);
            tvBuyer.setActivated(true);
            user.setRole(Consts.ROLE_BUYER);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRegisterEvent(RegisterEvent event) {
        EventBus.getDefault().removeStickyEvent(RegisterEvent.class);
        showLoading(false);
        if (event.registerResponse != null) {
            SP.getInstance().setJWT(event.registerResponse.getJwt());
            SP.getInstance().setUser(event.registerResponse.getUser());
            App.getInstance().setCurrentUser(event.registerResponse.getUser());

            FinishFragment finishFragment = FinishFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flRoot, finishFragment);
            transaction.addToBackStack("finish");
            transaction.commit();
        } else {
            Snackbar.make(tvBuyer, event.errorMessage, Snackbar.LENGTH_LONG).setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRegisterClick();
                }
            }).show();
        }
    }
}
