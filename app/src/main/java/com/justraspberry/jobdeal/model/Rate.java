package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rate implements Parcelable {

    @SerializedName("from")
    @Expose
    private User user;
    @SerializedName("buyer")
    @Expose
    private User buyer;
    @SerializedName("doer")
    @Expose
    private User doer;
    @SerializedName("rate")
    @Expose
    private Double rate;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    public final static Parcelable.Creator<Rate> CREATOR = new Creator<Rate>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Rate createFromParcel(Parcel in) {
            return new Rate(in);
        }

        public Rate[] newArray(int size) {
            return (new Rate[size]);
        }

    };

    protected Rate(Parcel in) {
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.buyer = ((User) in.readValue((User.class.getClassLoader())));
        this.doer = ((User) in.readValue((User.class.getClassLoader())));
        this.rate = ((Double) in.readValue((Double.class.getClassLoader())));
        this.comment = ((String) in.readValue((String.class.getClassLoader())));
        this.job = ((Job) in.readValue((Job.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Rate() {
    }

    public Rate(User user, User buyer, User doer, Double rate, String comment, Job job) {
        this.user = user;
        this.buyer = buyer;
        this.doer = doer;
        this.rate = rate;
        this.comment = comment;
        this.job = job;
    }

    public User getFrom() {
        return user;
    }

    public void setFrom(User user) {
        this.user = user;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getDoer() {
        return doer;
    }

    public void setDoer(User doer) {
        this.doer = doer;
    }
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(user);
        dest.writeValue(buyer);
        dest.writeValue(doer);
        dest.writeValue(rate);
        dest.writeValue(comment);
        dest.writeValue(job);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return 0;
    }

}