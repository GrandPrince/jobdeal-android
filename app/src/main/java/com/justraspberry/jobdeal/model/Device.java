package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device implements Parcelable
{

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("osVersion")
    @Expose
    private String osVersion;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("appVersion")
    @Expose
    private String appVersion;
    public final static Creator<Device> CREATOR = new Creator<Device>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return (new Device[size]);
        }

    }
            ;

    protected Device(Parcel in) {
        this.token = ((String) in.readValue((String.class.getClassLoader())));
        this.osVersion = ((String) in.readValue((String.class.getClassLoader())));
        this.model = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.appVersion = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Device() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Device withToken(String token) {
        this.token = token;
        return this;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Device withOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return this;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Device withModel(String model) {
        this.model = model;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Device withType(String type) {
        this.type = type;
        return this;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Device withAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(token);
        dest.writeValue(osVersion);
        dest.writeValue(model);
        dest.writeValue(type);
        dest.writeValue(appVersion);
    }

    public int describeContents() {
        return 0;
    }

}