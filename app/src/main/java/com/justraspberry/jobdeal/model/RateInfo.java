package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RateInfo implements Parcelable
{

    @SerializedName("avgBuyer")
    @Expose
    private Float avgBuyer = 0f;
    @SerializedName("avgDoer")
    @Expose
    private Float avgDoer = 0f;
    @SerializedName("countBuyer")
    @Expose
    private Integer countBuyer=0;
    @SerializedName("countDoer")
    @Expose
    private Integer countDoer=0;
    public final static Parcelable.Creator<RateInfo> CREATOR = new Creator<RateInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RateInfo createFromParcel(Parcel in) {
            return new RateInfo(in);
        }

        public RateInfo[] newArray(int size) {
            return (new RateInfo[size]);
        }

    }
            ;

    protected RateInfo(Parcel in) {
        this.avgBuyer = ((Float) in.readValue((Float.class.getClassLoader())));
        this.avgDoer = ((Float) in.readValue((Float.class.getClassLoader())));
        this.countBuyer = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.countDoer = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public RateInfo() {
    }

    public Float getAvgBuyer() {
        return avgBuyer;
    }

    public void setAvgBuyer(Float avgBuyer) {
        this.avgBuyer = avgBuyer;
    }

    public Float getAvgDoer() {
        return avgDoer;
    }

    public void setAvgDoer(Float avgDoer) {
        this.avgDoer = avgDoer;
    }

    public Integer getCountBuyer() {
        return countBuyer;
    }

    public void setCountBuyer(Integer countBuyer) {
        this.countBuyer = countBuyer;
    }

    public Integer getCountDoer() {
        return countDoer;
    }

    public void setCountDoer(Integer countDoer) {
        this.countDoer = countDoer;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(avgBuyer);
        dest.writeValue(avgDoer);
        dest.writeValue(countBuyer);
        dest.writeValue(countDoer);
    }

    public int describeContents() {
        return 0;
    }

}