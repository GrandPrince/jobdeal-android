package com.justraspberry.jobdeal.view.register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Context.TELEPHONY_SERVICE;


public class RegisterFragment extends Fragment {

    User user;

    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    TextInputEditText etPasswordConfirm;
    @BindView(R.id.etAddress)
    TextInputEditText etAddress;
    @BindView(R.id.etZip)
    TextInputEditText etZip;
    @BindView(R.id.etCity)
    TextInputEditText etCity;
    @BindView(R.id.etBankId)
    TextInputEditText etBankId;
    @BindView(R.id.btnNext)
    AppCompatButton btnNext;

    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(User user) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etZip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Util.hideKeyboardFrom(getActivity(), etZip);
                    etZip.clearFocus();
                    onNextClick();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult");
        if(requestCode == Consts.REQ_BANKID) {
            if (resultCode == getActivity().RESULT_OK) {
                Timber.e(data.getStringExtra("bankId"));
                user.setBankId(data.getStringExtra("bankId"));
                btnNext.setText(getString(R.string.next));
                onNextClick();
            } else if (resultCode == getActivity().RESULT_FIRST_USER) {
                new AlertDialog.Builder(getActivity()).setMessage(data.getStringExtra("error")).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
      /*  if(user.getBankId() == null) {
            Intent i = new Intent(getActivity(), BankIdActivity.class);
            startActivityForResult(i, Consts.REQ_BANKID);
        } else {*/
            if (!isFieldsValid())
                return;

            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setAddress(etAddress.getText().toString());
            user.setZip(etZip.getText().toString());
            user.setCity(etCity.getText().toString());
            //user.setBankId(etBankId.getText().toString());

            SP.getInstance().setPassword(etPassword.getText().toString());

            AboutFragment aboutFragment = AboutFragment.newInstance(user);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flRoot, aboutFragment);
            transaction.addToBackStack("about");
            transaction.commit();
       // }
    }

    private boolean isFieldsValid(){
        boolean isValid = true;

        if (etEmail.getText().toString().isEmpty() || !etEmail.getText().toString().contains(".") || !etEmail.getText().toString().contains("@")){
            isValid = false;
            etEmail.setError(getString(R.string.error_email));
        }

        if(etPassword.getText().toString().length() < 6){
            isValid = false;
            etPassword.setError(getString(R.string.error_password));
        }

        if(!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){
            isValid = false;
            etPassword.setError(getString(R.string.error_password_no_match));
            etPasswordConfirm.setError(getString(R.string.error_password_no_match));
        }

        if(etAddress.getText().toString().length() < 2){
            isValid = false;
            etAddress.setError(getString(R.string.error_address));
        }

        if (etZip.getText().toString().length() == 0){
            isValid = false;
            etZip.setError(getString(R.string.error_zip));
        }

        if(etCity.getText().toString().length() < 2){
            isValid = false;
            etCity.setError(getString(R.string.error_city));
        }

        //disabled bank id
       /* if(etBankId.getText().toString().length() < 6){
            isValid = false;
            etBankId.setError(getString(R.string.bank_id_validate_error));
        }*/

        return isValid;
    }


}
