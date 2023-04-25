package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("fromId")
    @Expose
    private Integer fromId;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("sender")
    @Expose
    private User sender;
    @SerializedName("timePass")
    @Expose
    private Integer timePass;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("isSeen")
    @Expose
    private Boolean isSeen;
    @SerializedName("unreadCount")
    @Expose
    private Integer unreadCount;
    @SerializedName("rate")
    @Expose
    private Rate rate;


    public final static Parcelable.Creator<Notification> CREATOR = new Creator<Notification>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return (new Notification[size]);
        }

    }
            ;

    protected Notification(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.userId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.fromId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.type = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.job = ((Job) in.readValue((Job.class.getClassLoader())));
        this.sender = ((User) in.readValue((User.class.getClassLoader())));
        this.timePass = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.body = ((String) in.readValue((String.class.getClassLoader())));
        this.isSeen = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.unreadCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.rate = ((Rate) in.readValue((Rate.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Notification() {
    }

    /**
     *
     * @param sender
     * @param id
     * @param fromId
     * @param timePass
     * @param userId
     * @param job
     * @param type
     */
    public Notification(Integer id, Integer userId, Integer fromId, Integer type, Job job, User sender, Integer timePass) {
        super();
        this.id = id;
        this.userId = userId;
        this.fromId = fromId;
        this.type = type;
        this.job = job;
        this.sender = sender;
        this.timePass = timePass;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Integer getTimePass() {
        return timePass;
    }

    public void setTimePass(Integer timePass) {
        this.timePass = timePass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userId);
        dest.writeValue(fromId);
        dest.writeValue(type);
        dest.writeValue(job);
        dest.writeValue(sender);
        dest.writeValue(timePass);
        dest.writeValue(title);
        dest.writeValue(body);
        dest.writeValue(isSeen);
        dest.writeValue(unreadCount);
        dest.writeValue(rate);
    }

    public int describeContents() {
        return 0;
    }

}
