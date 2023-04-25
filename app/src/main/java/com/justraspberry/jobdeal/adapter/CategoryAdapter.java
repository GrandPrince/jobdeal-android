package com.justraspberry.jobdeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.model.Category;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> {

    ArrayList<Category> categories;
    Context context;
    OnCategoryClickListener onCategoryClickListener;

    public interface OnCategoryClickListener {
        void onMoreClick(View v, int position, Category category);
        void onSelectClick(View v, int position, Category category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());

        if(category.getSubCategory() != null && category.getSubCategory().size() > 0)
            holder.ivArrow.setVisibility(View.VISIBLE);
        else
            holder.ivArrow.setVisibility(View.GONE);

        if(category.getSelected() == null){//sub selected
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_sub_selected));
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done));
        } else if (category.getSelected()){//selected
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_selected));
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done));
        } else if (!category.getSelected()) {//deselected
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_empty));
            holder.ivSelect.setImageDrawable(null);
        }

        //check if category have same sub category selected
        /*int selectedSubs = 0;

        for (Integer selectedCategoriyId : App.getInstance().getFilterBody().getFilter().getCategories()){
            for (Category c : category.getSubCategory()){
                if(c.getId().equals(selectedCategoriyId)){
                    selectedSubs++;
                }
                for (Category cx : c.getSubCategory()){
                    if(cx.getId().equals(selectedCategoriyId))
                        selectedSubs++;
                }
            }
        }*/

        /*if(selectedSubs > 0){
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_sub_selected));
            holder.ivSelect.setActivated(true);
        } else if (selectedSubs == 0 && category.getSubCategory().size() > 0){
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_empty));
            holder.ivSelect.setActivated(false);
        }

        if (selectedSubs == category.getTotalSubs() && category.getSubCategory().size() > 0) {
            holder.ivSelect.setBackground(ContextCompat.getDrawable(context, R.drawable.selection_selected));
            //holder.ivSelect.setActivated(true);
        }*/


        //Timber.e("SelectedSubs ID " + String.valueOf(category.getId()) + " - " + String.valueOf(selectedSubs) + " - TOTAL SUBS: " + String.valueOf(category.getTotalSubs()));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryVH extends RecyclerView.ViewHolder {
        @BindView(R.id.rlRoot)
        RelativeLayout rlRoot;
        @BindView(R.id.ivArrow)
        ImageView ivArrow;
        @BindView(R.id.ivSelect)
        ImageView ivSelect;
        @BindView(R.id.tvText)
        TextView tvCategoryName;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.tvText, R.id.ivArrow})
        public void onMoreClick(){
            if(onCategoryClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                onCategoryClickListener.onMoreClick(itemView, getAdapterPosition(), categories.get(getAdapterPosition()));
            }
        }

        @OnClick(R.id.ivSelect)
        public void onSelectClick(){
            if(onCategoryClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                onCategoryClickListener.onSelectClick(itemView, getAdapterPosition(), categories.get(getAdapterPosition()));
            }
        }
    }
}
