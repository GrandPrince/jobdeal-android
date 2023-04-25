
package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Applicant implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("choosed")
    @Expose
    private Boolean choosed;
    @SerializedName("price")
    @Expose
    private Double price;
    public final static Parcelable.Creator<Applicant> CREATOR = new Creator<Applicant>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Applicant createFromParcel(Parcel in) {
            return new Applicant(in);
        }

        public Applicant[] newArray(int size) {
            return (new Applicant[size]);
        }

    };

    protected Applicant(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.job = ((Job) in.readValue((Job.class.getClassLoader())));
        this.choosed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.price = ((Double) in.readValue((Integer.class.getClassLoader())));
    }


    public Applicant() {
    }

    public Applicant(Integer id, User user, Job job, Boolean choosed, Double price) {
        super();
        this.id = id;
        this.user = user;
        this.job = job;
        this.choosed = choosed;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Boolean getChoosed() {
        return choosed;
    }

    public void setChoosed(Boolean choosed) {
        this.choosed = choosed;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(user);
        dest.writeValue(job);
        dest.writeValue(choosed);
        dest.writeValue(price);
    }

    public int describeContents() {
        return 0;
    }

}