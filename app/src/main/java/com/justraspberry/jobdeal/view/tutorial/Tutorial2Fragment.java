package com.justraspberry.jobdeal.view.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.justraspberry.jobdeal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tutorial2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tutorial2Fragment extends Fragment {

    public Tutorial2Fragment() {
        // Required empty public constructor
    }


    public static Tutorial2Fragment newInstance() {
        Tutorial2Fragment fragment = new Tutorial2Fragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial_2, container, false);
    }
}