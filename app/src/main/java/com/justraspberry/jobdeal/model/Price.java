package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Price implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("list")
    @Expose
    private Float list;
    @SerializedName("boost")
    @Expose
    private Float boost;
    @SerializedName("choose")
    @Expose
    private Float choose;
    @SerializedName("difference")
    @Expose
    private Float difference;
    @SerializedName("subscribe")
    @Expose
    private Float subscribe;
    @SerializedName("speedy")
    @Expose
    private Float speedy;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    public final static Parcelable.Creator<Price> CREATOR = new Creator<Price>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        public Price[] newArray(int size) {
            return (new Price[size]);
        }

    }
            ;

    protected Price(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.list = ((Float) in.readValue((Float.class.getClassLoader())));
        this.boost = ((Float) in.readValue((Float.class.getClassLoader())));
        this.choose = ((Float) in.readValue((Float.class.getClassLoader())));
        this.difference = ((Float) in.readValue((Float.class.getClassLoader())));
        this.subscribe = ((Float) in.readValue((Float.class.getClassLoader())));
        this.speedy = ((Float) in.readValue((Float.class.getClassLoader())));
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
        this.fromDate = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Price() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Price withId(Integer id) {
        this.id = id;
        return this;
    }

    public Float getList() {
        return list;
    }

    public void setList(Float list) {
        this.list = list;
    }

    public Price withList(Float list) {
        this.list = list;
        return this;
    }

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float boost) {
        this.boost = boost;
    }

    public Price withBoost(Float boost) {
        this.boost = boost;
        return this;
    }

    public Float getChoose() {
        return choose;
    }

    public void setChoose(Float choose) {
        this.choose = choose;
    }

    public Price withChoose(Float choose) {
        this.choose = choose;
        return this;
    }

    public Float getDifference() {
        return difference;
    }

    public void setDifference(Float difference) {
        this.difference = difference;
    }

    public Price withDifference(Float difference) {
        this.difference = difference;
        return this;
    }

    public Float getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Float subscribe) {
        this.subscribe = subscribe;
    }

    public Price withSubscribe(Float subscribe) {
        this.subscribe = subscribe;
        return this;
    }

    public Float getSpeedy() {
        return speedy;
    }

    public void setSpeedy(Float speedy) {
        this.speedy = speedy;
    }

    public Price withSpeedy(Float speedy) {
        this.speedy = speedy;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Price withCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Price withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public Price withFromDate(String fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Price withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(list);
        dest.writeValue(boost);
        dest.writeValue(choose);
        dest.writeValue(difference);
        dest.writeValue(subscribe);
        dest.writeValue(speedy);
        dest.writeValue(country);
        dest.writeValue(currency);
        dest.writeValue(fromDate);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return 0;
    }

}