package com.justraspberry.jobdeal.rest.service.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info implements Parcelable
{

    @SerializedName("minPrice")
    @Expose
    private Float minPrice;
    @SerializedName("maxPrice")
    @Expose
    private Float maxPrice;
    @SerializedName("minAndroidVersion")
    @Expose
    private Integer minAndroidVersion;
    @SerializedName("minIosVersion")
    @Expose
    private Integer minIosVersion;
    @SerializedName("currency")
    @Expose
    private String currency;
    public final static Parcelable.Creator<Info> CREATOR = new Creator<Info>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        public Info[] newArray(int size) {
            return (new Info[size]);
        }

    }
            ;

    protected Info(Parcel in) {
        this.minPrice = ((Float) in.readValue((Float.class.getClassLoader())));
        this.maxPrice = ((Float) in.readValue((Float.class.getClassLoader())));
        this.minAndroidVersion = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.minIosVersion = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Info() {
        this.currency = "";
        this.maxPrice = 10000f;
        this.minPrice = 0f;
        this.minAndroidVersion = 1;
        this.minIosVersion = 1;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Info withMinPrice(Float minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Info withMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public Integer getMinAndroidVersion() {
        return minAndroidVersion;
    }

    public void setMinAndroidVersion(Integer minAndroidVersion) {
        this.minAndroidVersion = minAndroidVersion;
    }

    public Info withMinAndroidVersion(Integer minAndroidVersion) {
        this.minAndroidVersion = minAndroidVersion;
        return this;
    }

    public Integer getMinIosVersion() {
        return minIosVersion;
    }

    public void setMinIosVersion(Integer minIosVersion) {
        this.minIosVersion = minIosVersion;
    }

    public Info withMinIosVersion(Integer minIosVersion) {
        this.minIosVersion = minIosVersion;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Info withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(minPrice);
        dest.writeValue(maxPrice);
        dest.writeValue(minAndroidVersion);
        dest.writeValue(minIosVersion);
        dest.writeValue(currency);
    }

    public int describeContents() {
        return 0;
    }

}