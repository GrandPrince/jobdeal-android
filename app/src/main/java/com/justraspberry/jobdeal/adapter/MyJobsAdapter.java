package com.justraspberry.jobdeal.adapter;

import android.content.Context;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MyJobsAdapter extends RecyclerView.Adapter<MyJobsAdapter.MyJobsViewHolder> {

    ArrayList<Job> postedJobs;
    Context context;
    private onJobClickListener onJobClickListener;

    public interface onJobClickListener {
        public void onJobClicked(View view, int position, Job job);
    }

    public void setOnJobClickListener(onJobClickListener onJobClickListener) {
        this.onJobClickListener = onJobClickListener;
    }

    public MyJobsAdapter(Context context, ArrayList<Job> postedJobs) {
        this.context = context;
        this.postedJobs = postedJobs;
    }


    @NonNull
    @Override
    public MyJobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_job, parent, false);
        return new MyJobsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyJobsViewHolder holder, int position) {
        Job postedJob = postedJobs.get(position);

        Glide.with(context).load(postedJob.getMainImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_job).transforms(new CenterCrop(), new RoundedCorners(16)))
                .into(holder.ivJobImage);

        holder.tvJobName.setText(postedJob.getName());
        if (postedJob.getExpired()) {
            holder.tvJobTime.setText(context.getString(R.string.job_expired));
            holder.cvJob.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGreyLight));
        } else {
            holder.tvJobTime.setText(postedJob.getExpireAt());
            holder.cvJob.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }
        holder.tvJobDescription.setText(postedJob.getDescription());
        holder.tvNumOfBiders.setText(String.valueOf(postedJob.getBidCount()));
        holder.tvJobPrice.setText(String.valueOf(Util.formatNumber(postedJob.getPrice())) + " " + postedJob.getCurrency());

        if (postedJob.getListed())
            holder.ivLock.setVisibility(View.INVISIBLE);
        else
            holder.ivLock.setVisibility(View.VISIBLE);

        if (postedJob.getChoosedCount().equals(postedJob.getApplicantCount())) {
            holder.tvNumOfBiders.setVisibility(View.GONE);
            holder.ivJobAgreed.setVisibility(View.VISIBLE);
        } else {
            holder.tvNumOfBiders.setVisibility(View.VISIBLE);
            holder.ivJobAgreed.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postedJobs.size();
    }

    public class MyJobsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivJobImage)
        ImageView ivJobImage;
        @BindView(R.id.tvJobName)
        TextView tvJobName;
        @BindView(R.id.tvJobTime)
        TextView tvJobTime;
        @BindView(R.id.tvJobDescription)
        TextView tvJobDescription;
        @BindView(R.id.tvNumOfBiders)
        TextView tvNumOfBiders;
        @BindView(R.id.ivJobAgreed)
        ImageView ivJobAgreed;
        @BindView(R.id.tvJobPrice)
        TextView tvJobPrice;
        @BindView(R.id.cvJob)
        MaterialCardView cvJob;
        @BindView(R.id.ivLock)
        ImageView ivLock;


        public MyJobsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cvJob)
        public void onJobClicked() {
            if (onJobClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onJobClickListener.onJobClicked(itemView, getAdapterPosition(), postedJobs.get(getAdapterPosition()));
            }
        }
    }
}
