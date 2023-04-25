package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fullUrl")
    @Expose
    private String fullUrl;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("path")
    @Expose
    private String path;

    private boolean uploaded = false;
    public final static Parcelable.Creator<Image> CREATOR = new Creator<Image>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return (new Image[size]);
        }

    }
            ;

    protected Image(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.fullUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.position = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.path = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Image() {
    }

    public Image(Integer id, String fullUrl, Integer position) {
        this.id = id;
        this.fullUrl = fullUrl;
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Image withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public Image withFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Image withPosition(Integer position) {
        this.position = position;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(fullUrl);
        dest.writeValue(position);
        dest.writeValue(path);
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public int describeContents() {
        return 0;
    }

}