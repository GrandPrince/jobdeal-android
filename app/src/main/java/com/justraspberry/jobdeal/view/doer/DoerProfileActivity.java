package com.justraspberry.jobdeal.view.doer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.model.Rate;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;

public class DoerProfileActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.civProfilePicture)
    CircleImageView civProfilePicture;
    @BindView(R.id.tvUserFullName)
    TextView tvDoerFullName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.rbProfileRating)
    BaseRatingBar rbProfileRating;
    @BindView(R.id.tvRating)
    TextView tvRating;
    @BindView(R.id.ivPremiumUser)
    ImageView ivPremiumUser;
    @BindView(R.id.tvBidPrice)
    TextView tvBidPrice;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;

    ArrayList<Rate> rateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doer_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);



    }
}
