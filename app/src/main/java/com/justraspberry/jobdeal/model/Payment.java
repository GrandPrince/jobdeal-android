package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("refId")
    @Expose
    private String refId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("amount")
    @Expose
    private Float amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("htmlSnippet")
    @Expose
    private String htmlSnippet;

    public final static Parcelable.Creator<Payment> CREATOR = new Creator<Payment>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        public Payment[] newArray(int size) {
            return (new Payment[size]);
        }

    }
            ;

    protected Payment(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.refId = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.provider = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.amount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
        this.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.job = ((Job) in.readValue((Job.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.htmlSnippet = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Payment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Payment withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Payment withRefId(String refId) {
        this.refId = refId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Payment withProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Payment withType(Integer type) {
        this.type = type;
        return this;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Payment withAmount(Float amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Payment withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Payment withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Payment withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Payment withJob(Job job) {
        this.job = job;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Payment withUser(User user) {
        this.user = user;
        return this;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public void setHtmlSnippet(String htmlSnippet) {
        this.htmlSnippet = htmlSnippet;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(refId);
        dest.writeValue(status);
        dest.writeValue(provider);
        dest.writeValue(type);
        dest.writeValue(amount);
        dest.writeValue(currency);
        dest.writeValue(updatedAt);
        dest.writeValue(createdAt);
        dest.writeValue(job);
        dest.writeValue(user);
        dest.writeValue(htmlSnippet);
    }

    public int describeContents() {
        return 0;
    }

}
