package com.justraspberry.jobdeal.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.CloseLoginEvent;
import com.justraspberry.jobdeal.view.main.MainActivity;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinishFragment extends Fragment {

    public FinishFragment() {
        // Required empty public constructor
    }


    public static FinishFragment newInstance() {
        FinishFragment fragment = new FinishFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @OnClick(R.id.btnFinish)
    public void onRegisterClick(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
        EventBus.getDefault().postSticky(new CloseLoginEvent());
    }


}
