package com.justraspberry.jobdeal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.Job;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    ArrayList<Job> jobs;
    Context context;
    OnJobClickListener onJobClickListener;

    public interface OnJobClickListener {
        public void onJobClick(View v, int position, Job job);
    }

    public void setOnJobClickListener(OnJobClickListener onJobClickListener) {
        this.onJobClickListener = onJobClickListener;
    }

    public JobAdapter(Context context, ArrayList<Job> jobs) {
        this.jobs = jobs;
        this.context = context;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);


        Glide.with(context).load(job.getMainImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_job)
                        .transforms(new CenterCrop(), new RoundedCorners(12)))
                .into(holder.ivImage);


        holder.tvTitle.setText(job.getName());
        holder.tvDuration.setText(job.getExpireAt());

        if (job.getSpeedy()) {
            holder.tvPrice.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_price));
            holder.tvPrice.setCompoundDrawables(null, null, ContextCompat.getDrawable(context, R.drawable.ic_av_timer_small), null);


            ImageViewCompat.setImageTintList(holder.ivDuration, ContextCompat.getColorStateList(context, R.color.colorRed));

            holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        } else {
            holder.tvPrice.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_price_green));
            holder.tvPrice.setCompoundDrawables(null, null, null, null);
            holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));

            ImageViewCompat.setImageTintList(holder.ivDuration, ContextCompat.getColorStateList(context, R.color.colorTextSecondary));
        }

        if(job.getHelpOnTheWay()){
            boolean isEnglish = context.getString(R.string.locale).equalsIgnoreCase("en");
            if(job.getChoosed()) {
                if (isEnglish) {
                    holder.ivRibbonChosen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ribbon_chosen_en));

                } else {
                    holder.ivRibbonChosen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ribbon_chosen_sv));

                }
                holder.ivRibbonChosen.setVisibility(View.VISIBLE);

            }
            if(context.getString(R.string.locale).equalsIgnoreCase("en"))
                holder.ivRibbon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ribbon_en));
            else
                holder.ivRibbon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ribbon_se));

             holder.ivRibbon.setVisibility(View.VISIBLE);
        } else {
            holder.ivRibbon.setVisibility(View.GONE);
            holder.ivRibbonChosen.setVisibility(View.GONE);
        }

        if (job.getBoost())
            holder.ivBoost.setVisibility(View.VISIBLE);
        else
            holder.ivBoost.setVisibility(View.GONE);

        holder.tvPrice.setText(Util.formatNumber(job.getPrice()) + " " + job.getCurrency());

        if (job.getDistance() != null) {
            if (job.getDistance() > 1000)
                holder.tvLocation.setText(String.valueOf(job.getDistance() / 1000) + "km");
            else
                holder.tvLocation.setText(String.valueOf(job.getDistance()) + "m");
        }

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        AppCompatImageView ivImage;
        @BindView(R.id.ivBookmark)
        ImageView ivBookmark;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvDuration)
        TextView tvDuration;
        @BindView(R.id.tvLocation)
        TextView tvLocation;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.cvRoot)
        MaterialCardView cvRoot;
        @BindView(R.id.ivBoost)
        ImageView ivBoost;
        @BindView(R.id.ivDuration)
        ImageView ivDuration;
        @BindView(R.id.ivRibbon)
        ImageView ivRibbon;
        @BindView(R.id.ivRibbonChosen)
        ImageView ivRibbonChosen;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cvRoot)
        public void onRootClick() {
            if (onJobClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onJobClickListener.onJobClick(itemView, getAdapterPosition(), jobs.get(getAdapterPosition()));
            }
        }
    }
}
