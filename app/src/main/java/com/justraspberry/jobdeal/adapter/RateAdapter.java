package com.justraspberry.jobdeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.model.Rate;
import com.willy.ratingbar.BaseRatingBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {

    Context context;
    ArrayList<Rate> rates;

    public RateAdapter(Context context, ArrayList<Rate> rates) {
        this.context = context;
        this.rates = rates;
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_rating, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        Rate rate = rates.get(position);

        holder.ratingBar.setRating(rate.getRate().floatValue());
        holder.tvComment.setText(rate.getComment());
        holder.tvJobByUser.setText(rate.getFrom().getFullName());
        holder.tvJobDoneDate.setText(rate.getCreatedAt());
        holder.tvJobName.setText(rate.getJob().getName());
    }

    @Override
    public int getItemCount() {
        return rates.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ratingBar)
        BaseRatingBar ratingBar;
        @BindView(R.id.tvJobName)
        TextView tvJobName;
        @BindView(R.id.tvJobByUser)
        TextView tvJobByUser;
        @BindView(R.id.tvJobDoneDate)
        TextView tvJobDoneDate;
        @BindView(R.id.tvComment)
        TextView tvComment;


        public RateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
