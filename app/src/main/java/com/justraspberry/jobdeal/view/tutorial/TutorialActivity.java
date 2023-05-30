package com.justraspberry.jobdeal.view.tutorial;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.view.main.MainActivity;
import com.justraspberry.jobdeal.view.main.MainListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.btnNext)
    MaterialButton btnNext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        viewPager.setAdapter(new TutorialViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

    }

    @OnClick(R.id.btnClose)
    public void onCloseClick(){
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        if(viewPager.getCurrentItem() == 3){
            setResult(RESULT_OK);
            finish();
            return;
        }

        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private static class TutorialViewPagerAdapter extends FragmentStatePagerAdapter {

        public TutorialViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:  return Tutorial1Fragment.newInstance();
                case 1: return Tutorial2Fragment.newInstance();
                case 2: return Tutorial3Fragment.newInstance();
                case 3: return Tutorial4Fragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


}
