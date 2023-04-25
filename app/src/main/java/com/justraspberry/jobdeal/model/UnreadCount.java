package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnreadCount implements Parcelable {

    @SerializedName("unreadCount")
    @Expose
    private Integer unreadCount;
    @SerializedName("unreadDoerCount")
    @Expose
    private Integer unreadDoerCount;
    @SerializedName("unreadBuyerCount")
    @Expose
    private Integer unreadBuyerCount;
    public final static Creator<UnreadCount> CREATOR = new Creator<UnreadCount>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UnreadCount createFromParcel(Parcel in) {
            return new UnreadCount(in);
        }

        public UnreadCount[] newArray(int size) {
            return (new UnreadCount[size]);
        }

    };

    protected UnreadCount(Parcel in) {
        this.unreadCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.unreadDoerCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.unreadBuyerCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public UnreadCount() {
    }


    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Integer getUnreadDoerCount() {
        return unreadDoerCount;
    }

    public void setUnreadDoerCount(Integer unreadDoerCount) {
        this.unreadDoerCount = unreadDoerCount;
    }

    public Integer getUnreadBuyerCount() {
        return unreadBuyerCount;
    }

    public void setUnreadBuyerCount(Integer unreadBuyerCount) {
        this.unreadBuyerCount = unreadBuyerCount;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(unreadCount);
        dest.writeValue(unreadDoerCount);
        dest.writeValue(unreadBuyerCount);
    }

    public int describeContents() {
        return 0;
    }

}