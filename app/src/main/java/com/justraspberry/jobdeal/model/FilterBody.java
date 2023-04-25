package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterBody implements Parcelable
{

    @SerializedName("sortBy")
    @Expose
    private String sortBy;
    @SerializedName("sortDirection")
    @Expose
    private String sortDirection;
    @SerializedName("currentLocation")
    @Expose
    private CurrentLocation currentLocation;
    @SerializedName("filter")
    @Expose
    private Filter filter = new Filter();

    @SerializedName("myLocation")
    @Expose
    private Boolean isMyLocation = true;
    @SerializedName("address")
    @Expose
    private String address = null;

    public final static Parcelable.Creator<FilterBody> CREATOR = new Creator<FilterBody>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FilterBody createFromParcel(Parcel in) {
            return new FilterBody(in);
        }

        public FilterBody[] newArray(int size) {
            return (new FilterBody[size]);
        }

    }
            ;

    protected FilterBody(Parcel in) {
        this.sortBy = ((String) in.readValue((String.class.getClassLoader())));
        this.sortDirection = ((String) in.readValue((String.class.getClassLoader())));
        this.filter = ((Filter) in.readValue((Filter.class.getClassLoader())));
        this.isMyLocation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));

    }

    public FilterBody() {
        this.sortBy = "published";
        this.sortDirection = "DESC";
        this.currentLocation = new CurrentLocation();
        this.filter = new Filter();
        this.isMyLocation = true;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public FilterBody withSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public FilterBody withSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
        return this;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public FilterBody withFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public Boolean getMyLocation() {
        return isMyLocation;
    }

    public void setMyLocation(Boolean myLocation) {
        isMyLocation = myLocation;
    }

    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(CurrentLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sortBy);
        dest.writeValue(sortDirection);
        dest.writeValue(filter);
        dest.writeValue(isMyLocation);
        dest.writeValue(address);
    }

    public int describeContents() {
        return 0;
    }

}