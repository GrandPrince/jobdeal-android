package com.justraspberry.jobdeal.view.categories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.adapter.CategoryAdapter;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.CategorySearchEvent;
import com.justraspberry.jobdeal.event.FragmentStackChangedEvent;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.FilterBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class CategoryFragment extends Fragment {

    @BindView(R.id.rvCategories)
    RecyclerView rvCategories;

    ArrayList<Category> categories = new ArrayList<>();
    CategoryAdapter categoryAdapter;

    boolean isFilterMultiSelect = false;
    boolean isWishlist = false;

    FilterBody filterBody;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(ArrayList<Category> categories, boolean isFilterMultiSelect, boolean isWishlist) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("categories", categories);
        args.putBoolean("isFilterMultiSelect", isFilterMultiSelect);
        args.putBoolean("isWishlist", isWishlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categories = getArguments().getParcelableArrayList("categories");
            isFilterMultiSelect = getArguments().getBoolean("isFilterMultiSelect", false);
            isWishlist = getArguments().getBoolean("isWishlist", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(categoryAdapter);
        rvCategories.setHasFixedSize(true);
        rvCategories.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        if(isWishlist)
            filterBody = App.getInstance().getWishlistBody();
        else
            filterBody = App.getInstance().getFilterBody();

        if (isFilterMultiSelect) {//if from filter, select all categories that are in filter categories
            for (Integer categoryId : filterBody.getFilter().getCategories()) {
                int i = 0;
                for (Category c : categories) {
                    if (c.getId().equals(categoryId)) {
                        c.setSelected(true);
                        categoryAdapter.notifyItemChanged(i);
                    }
                    i++;
                }
            }
        }

        categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onMoreClick(View v, int position, Category category) {
                if (category.getSubCategory() == null || category.getSubCategory().size() == 0)
                    return;

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                CategoryFragment categoryFragment = CategoryFragment.newInstance(category.getSubCategory(), isFilterMultiSelect, isWishlist);
                transaction.add(R.id.flRoot, categoryFragment, category.getName());
                transaction.addToBackStack(category.getName());
                transaction.commit();
            }

            @Override
            public void onSelectClick(View v, int position, Category category) {
                if (!isFilterMultiSelect) { //if not filter-multi select first deselect all
                    int i = 0;
                    for (Category c : categories) {
                        if (c.getSelected() == null || c.getSelected()) {
                            c.setSelected(false);
                            categoryAdapter.notifyItemChanged(i);
                        }
                        i++;
                    }
                }

                if (category.getSelected() == null || category.getSelected())
                    category.setSelected(false);
                else
                    category.setSelected(true);

                if(!isFilterMultiSelect){//back result
                    Intent i = new Intent();
                    i.putExtra("category", category);
                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                }

                if (isFilterMultiSelect) { // if filter categories add/remove from filter categories, else if from job create thant do nothing with filter
                    if (category.getSelected()) {//if category is selected, select all sub categories of that category

                        filterBody.getFilter().addCategory(category.getId());

                        for (Category c : category.getSubCategory()) {
                            for (Category cx : c.getSubCategory()) {
                                cx.setSelected(true);
                                filterBody.getFilter().addCategory(cx.getId());
                            }
                            c.setSelected(true);
                            filterBody.getFilter().addCategory(c.getId());
                        }

                        if (category.getParent() != null)
                            category.getParent().setSelected(null);
                        if (category.getRoot() != null)
                            category.getRoot().setSelected(null);

                        //if all sub categories are selected select parentId also
                        if (category.getParent() != null) {
                            int selectedSubCategories = 0;


                            for (Category cx : category.getParent().getSubCategory()) {
                                if (cx.getSelected() != null && cx.getSelected())
                                    selectedSubCategories++;
                            }

                            if (selectedSubCategories == category.getParent().getSubCategory().size()) {
                                category.getParent().setSelected(true);
                                filterBody.getFilter().addCategory(category.getParent().getId());
                            }

                            //same for root OMG :D
                            if (category.getRoot() != null) {
                                int selectedParentSubCategories = 0;
                                for (Category cx : category.getRoot().getSubCategory()) {
                                    if (cx.getSelected() != null && cx.getSelected())
                                        selectedParentSubCategories++;
                                }

                                if (selectedParentSubCategories == category.getRoot().getSubCategory().size()) {
                                    category.getRoot().setSelected(true);
                                    filterBody.getFilter().addCategory(category.getRoot().getId());
                                }
                            }

                        }
                    } else {
                        filterBody.getFilter().removeCategory(category.getId());

                        for (Category c : category.getSubCategory()) {
                            for (Category cx : c.getSubCategory()) {
                                cx.setSelected(false);
                                filterBody.getFilter().removeCategory(cx.getId());
                            }
                            c.setSelected(false);
                            filterBody.getFilter().removeCategory(c.getId());
                        }

                        if (category.getParent() != null)
                            category.getParent().setSelected(null);

                        if (category.getRoot() != null)
                            category.getRoot().setSelected(null);

                        //if parent id exist remove all parent and root ids from selected
                        if (category.getParentId() != null) {

                            //category.getParent().setSelected(false);
                            filterBody.getFilter().removeCategory(category.getParent().getId());

                            if (category.getRoot() != null) {
                                filterBody.getFilter().removeCategory(category.getRoot().getId());
                                category.getRoot().setSelected(null);
                            }

                            //if all sub categories deselected, deselect parent also
                            int selectedSubCategories = 0;


                            for (Category cx : category.getParent().getSubCategory()) {
                                if (cx.getSelected() == null || cx.getSelected())
                                    selectedSubCategories++;
                            }

                            if (selectedSubCategories == 0) {
                                category.getParent().setSelected(false);
                            }

                            //same for root OMG :D
                            if (category.getRoot() != null) {
                                int selectedParentSubCategories = 0;
                                for (Category cx : category.getRoot().getSubCategory()) {
                                    if (cx.getSelected() == null || cx.getSelected())
                                        selectedParentSubCategories++;
                                }

                                if (selectedParentSubCategories == 0) {
                                    category.getRoot().setSelected(false);
                                }
                            }
                        }

                    }

                }
                categoryAdapter.notifyItemChanged(position);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentStackChangedEvent(FragmentStackChangedEvent event) {
        if (categoryAdapter != null)
            categoryAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchCategoriesEvent(CategorySearchEvent event){
        if(getTag() != null && getTag().equalsIgnoreCase("search")){
            categories.clear();
            categories.addAll(event.categories);
            categoryAdapter.notifyDataSetChanged();
        }
    }


}
