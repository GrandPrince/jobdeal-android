package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyInfo implements Parcelable
{

    @SerializedName("doerJobsDone")
    @Expose
    private Integer doerJobsDone;
    @SerializedName("doerEarned")
    @Expose
    private Float doerEarned;
    @SerializedName("buyerSpent")
    @Expose
    private Float buyerSpent;
    @SerializedName("buyerContracts")
    @Expose
    private Integer buyerContracts;

    public final static Parcelable.Creator<MyInfo> CREATOR = new Creator<MyInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MyInfo createFromParcel(Parcel in) {
            return new MyInfo(in);
        }

        public MyInfo[] newArray(int size) {
            return (new MyInfo[size]);
        }

    }
            ;

    protected MyInfo(Parcel in) {
        this.doerJobsDone = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.doerEarned = ((Float) in.readValue((Float.class.getClassLoader())));
        this.buyerSpent = ((Float) in.readValue((Float.class.getClassLoader())));
        this.buyerContracts = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public MyInfo() {
    }

    public Integer getDoerJobsDone() {
        return doerJobsDone;
    }

    public void setDoerJobsDone(Integer doerJobsDone) {
        this.doerJobsDone = doerJobsDone;
    }

    public MyInfo withDoerJobsDone(Integer doerJobsDone) {
        this.doerJobsDone = doerJobsDone;
        return this;
    }

    public Float getDoerEarned() {
        return doerEarned;
    }

    public void setDoerEarned(Float doerEarned) {
        this.doerEarned = doerEarned;
    }

    public MyInfo withDoerEarned(Float doerEarned) {
        this.doerEarned = doerEarned;
        return this;
    }

    public Float getBuyerSpent() {
        return buyerSpent;
    }

    public void setBuyerSpent(Float buyerSpent) {
        this.buyerSpent = buyerSpent;
    }

    public MyInfo withBuyerSpent(Float buyerSpent) {
        this.buyerSpent = buyerSpent;
        return this;
    }

    public Integer getBuyerContracts() {
        return buyerContracts;
    }

    public void setBuyerContracts(Integer buyerContracts) {
        this.buyerContracts = buyerContracts;
    }

    public MyInfo withBuyerContracts(Integer buyerContracts) {
        this.buyerContracts = buyerContracts;
        return this;
    }




    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(doerJobsDone);
        dest.writeValue(doerEarned);
        dest.writeValue(buyerSpent);
        dest.writeValue(buyerContracts);
    }

    public int describeContents() {
        return 0;
    }

}