package com.justraspberry.jobdeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.User;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ApplicantsViewHolder> {

    ArrayList<Applicant> applicants;
    Context context;
    Job job;
    public onDoerClickListener onDoerClickListener;

    public interface onDoerClickListener {
        void onDoerClick(View view, int position, Applicant applicant);
        void onAvatarClick(View view, int position, Applicant applicant);
    }

    public ApplicantsAdapter(Context context, ArrayList<Applicant> applicants, Job job) {
        this.applicants = applicants;
        this.context = context;
        this.job = job;
    }

    public void setOnDoerClickListener(ApplicantsAdapter.onDoerClickListener onDoerClickListener) {
        this.onDoerClickListener = onDoerClickListener;
    }

    @NonNull
    @Override
    public ApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_applicant_item, parent, false);
        return new ApplicantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantsViewHolder holder, int position) {
        Applicant applicant = applicants.get(position);

        Glide.with(context).load(applicant.getUser().getAvatar()).into(holder.civProfilePicture);
        holder.tvUserFullName.setText(applicant.getUser().getFullName());
        holder.tvLocation.setText(applicant.getUser().getCity());
        holder.tvBidPrice.setText(Util.formatNumber(applicant.getPrice()) + " " + App.getInstance().getInfo().getCurrency());
        holder.rbProfileRating.setRating(applicant.getUser().getRateInfo().getAvgDoer());
        holder.tvRating.setText(context.getString(R.string.rating_params, Util.formatNumber(applicant.getUser().getRateInfo().getAvgDoer()), applicant.getUser().getRateInfo().getCountDoer()));

        if (applicant.getChoosed()) {
            holder.ivJobAgreed.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_jobdeal_logo_gradient));
        } else {
            holder.ivJobAgreed.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_hands));
            ImageViewCompat.setImageTintList(holder.ivJobAgreed, ContextCompat.getColorStateList(context, R.color.colorGrey));
        }

        if(applicant.getPrice() < job.getPrice()){
            holder.tvLowerBid.setVisibility(View.VISIBLE);
        } else {
            holder.tvLowerBid.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return applicants.size();
    }

    public void updateJob(Job job) {
        this.job = job;
        notifyDataSetChanged();
    }

    public class ApplicantsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civProfilePicture)
        CircleImageView civProfilePicture;
        @BindView(R.id.tvUserFullName)
        TextView tvUserFullName;
        @BindView(R.id.tvLocation)
        TextView tvLocation;
        @BindView(R.id.rbProfileRating)
        BaseRatingBar rbProfileRating;
        @BindView(R.id.tvRating)
        TextView tvRating;
        @BindView(R.id.tvBidPrice)
        TextView tvBidPrice;
        @BindView(R.id.ivJobAgreed)
        ImageView ivJobAgreed;
        @BindView(R.id.rlProfileInfo)
        RelativeLayout rlProfileInfo;
        @BindView(R.id.tvLowerBid)
        TextView tvLowerBid;


        public ApplicantsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rlProfileInfo)
        public void onApplicantClick() {
            if (onDoerClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onDoerClickListener.onDoerClick(itemView, getAdapterPosition(), applicants.get(getAdapterPosition()));
            }
        }

        @OnClick(R.id.civProfilePicture)
        public void onAvatarClick(){
            if (onDoerClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onDoerClickListener.onAvatarClick(itemView, getAdapterPosition(), applicants.get(getAdapterPosition()));
            }
        }

    }
}
