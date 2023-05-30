package com.justraspberry.jobdeal.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.justraspberry.jobdeal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmChooseActivity extends AppCompatActivity {

    @BindView(R.id.smHelp)
    SwitchMaterial smHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_choose);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));

    }

    @OnClick(R.id.btnChose)
    public void onChooseClick(){
        Intent intent = new Intent();
        intent.putExtra("helpOnWay", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.ivClose)
    public void onCloseClick(){
        finish();
    }
}