package com.justraspberry.jobdeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Notification;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    ArrayList<Notification> notifications;
    Context context;
    onNotificationClickListener onNotificationClickListener;

    public interface onNotificationClickListener {
        public void onNotificationClick(View itemView, int position, Job job, Notification notification);
    }

    public void setOnNotificationClickListener(onNotificationClickListener onNotificationClickListener) {
        this.onNotificationClickListener = onNotificationClickListener;
    }

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvNotificationHeader.setText(notification.getTitle());
        holder.tvNotificationMsg.setText(notification.getBody());
        holder.tvDate.setText(Util.getTimePass(context, notification.getTimePass()));

        if (notification.getJob() != null) {
            holder.rlNotificationImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(notification.getJob().getMainImage())
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(Util.dpToPx(context, 4))))
                    .into(holder.ivImage);
        } else {
            holder.rlNotificationImage.setVisibility(View.GONE);
        }

        if(notification.getSeen())
            holder.vIndicator.setVisibility(View.INVISIBLE);
        else
            holder.vIndicator.setVisibility(View.VISIBLE);

        switch (notification.getType()) {
            case Consts.DOER_BID:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_gradient_orange));
                break;
            case Consts.BUYER_ACCEPTED:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.RATE_DOER:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.RATE_BUYER:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.WISHLIST_JOB:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_gradient_orange));
                break;
            case Consts.PAYMENT_SUCCESS:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_grey));
                break;
            case Consts.PAYMENT_ERROR:
                holder.rlNotificationIcon.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_shape_grey));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rlNotificationIcon)
        RelativeLayout rlNotificationIcon;
        @BindView(R.id.tvNotificationHeader)
        TextView tvNotificationHeader;
        @BindView(R.id.tvNotificationMsg)
        TextView tvNotificationMsg;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.rlNotificationImage)
        RelativeLayout rlNotificationImage;
        @BindView(R.id.cvNotification)
        MaterialCardView cvNotification;
        @BindView(R.id.vIndicator)
        View vIndicator;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cvNotification)
        public void onNotificationClick() {
            if (onNotificationClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                onNotificationClickListener.onNotificationClick(itemView, getAdapterPosition(), notifications.get(getAdapterPosition()).getJob(), notifications.get(getAdapterPosition()));
            }
        }
    }
}
