package com.justraspberry.jobdeal.view.contact;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

import android.os.Bundle;

import com.justraspberry.jobdeal.R;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);
    }
}
