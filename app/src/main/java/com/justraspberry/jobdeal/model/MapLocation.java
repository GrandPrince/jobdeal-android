package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapLocation implements Parcelable
{

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    public final static Parcelable.Creator<MapLocation> CREATOR = new Creator<MapLocation>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MapLocation createFromParcel(Parcel in) {
            return new MapLocation(in);
        }

        public MapLocation[] newArray(int size) {
            return (new MapLocation[size]);
        }

    }
            ;

    protected MapLocation(Parcel in) {
        this.lat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lng = ((Double) in.readValue((Double.class.getClassLoader())));
        this.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public MapLocation() {
    }

    public MapLocation(Double lat, Double lng, Integer distance) {
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public MapLocation withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public MapLocation withLng(Double lng) {
        this.lng = lng;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public MapLocation withDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
        dest.writeValue(distance);
    }

    public int describeContents() {
        return 0;
    }

}