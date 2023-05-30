package com.justraspberry.jobdeal.view.categories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.CategorySearchEvent;
import com.justraspberry.jobdeal.event.FragmentStackChangedEvent;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.flRoot)
    FrameLayout flRoot;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etSearch)
    TextInputEditText etSearch;

    boolean isFilterMultiSelect = false; //if we start categories from filter than multi select shoud be enabled, and
    boolean isWishlist = false;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (App.getInstance().getCategories().size() == 0)
            ApiRestClient.getInstance().getCategories();

        isFilterMultiSelect = getIntent().getBooleanExtra("isFilterMultiSelect", false);
        isWishlist = getIntent().getBooleanExtra("isWishlist", false);

        transaction = getSupportFragmentManager().beginTransaction();

        CategoryFragment categoryFragment = CategoryFragment.newInstance(App.getInstance().getCategories(), isFilterMultiSelect, isWishlist);
        transaction.add(R.id.flRoot, categoryFragment, getString(R.string.categories));
        transaction.addToBackStack(getString(R.string.categories));
        transaction.commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fr = getSupportFragmentManager().findFragmentById(R.id.flRoot);
                if (fr != null) {
                    toolbar.setTitle(fr.getTag().toUpperCase());
                }

                EventBus.getDefault().post(new FragmentStackChangedEvent());
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment searchFragment = getSupportFragmentManager().findFragmentByTag("search");
                if (s.length() > 0) {
                    ArrayList<Category> filteredCategories = App.getInstance().searchCategories(s.toString().toLowerCase());

                    transaction = getSupportFragmentManager().beginTransaction();
                    CategoryFragment categoryFragment = CategoryFragment.newInstance(filteredCategories, isFilterMultiSelect, isWishlist);

                    if (searchFragment == null) {
                        //not exist
                        transaction.add(R.id.flRoot, categoryFragment, "search");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        if (searchFragment.isHidden()) {
                            transaction.show(searchFragment);
                            transaction.commit();
                        }
                        EventBus.getDefault().post(new CategorySearchEvent(filteredCategories));
                    }
                } else {
                    if (searchFragment != null) {
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.hide(searchFragment);
                        transaction.commit();
                        toolbar.setTitle(getString(R.string.categories).toUpperCase());
                        getSupportFragmentManager().popBackStack();
                    }

                    EventBus.getDefault().post(new FragmentStackChangedEvent());//refresh state of fragments
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
