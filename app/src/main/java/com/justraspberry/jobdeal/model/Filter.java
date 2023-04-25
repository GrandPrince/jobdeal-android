package com.justraspberry.jobdeal.model;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.justraspberry.jobdeal.App;

import timber.log.Timber;

public class Filter implements Parcelable
{

    @SerializedName("fromPrice")
    @Expose
    private Integer fromPrice;
    @SerializedName("toPrice")
    @Expose
    private Integer toPrice;
    @SerializedName("categories")
    @Expose
    private List<Integer> categories = new ArrayList<>();
    @SerializedName("fromDistance")
    @Expose
    private Integer fromDistance;
    @SerializedName("toDistance")
    @Expose
    private Integer toDistance;
    @SerializedName("location")
    @Expose
    private MapLocation mapLocation;
    @SerializedName("helpOnTheWay")
    @Expose
    private Boolean helpOnTheWay = true;


    public Filter() {
        this.categories = new ArrayList<>();
    }

    protected Filter(Parcel in) {
        if (in.readByte() == 0) {
            fromPrice = null;
        } else {
            fromPrice = in.readInt();
        }
        if (in.readByte() == 0) {
            toPrice = null;
        } else {
            toPrice = in.readInt();
        }
        if (in.readByte() == 0) {
            fromDistance = null;
        } else {
            fromDistance = in.readInt();
        }
        if (in.readByte() == 0) {
            toDistance = null;
        } else {
            toDistance = in.readInt();
        }
        mapLocation = in.readParcelable(MapLocation.class.getClassLoader());
        byte tmpHelpOnTheWay = in.readByte();
        helpOnTheWay = tmpHelpOnTheWay == 0 ? null : tmpHelpOnTheWay == 1;
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    public Integer getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(Integer fromPrice) {
        this.fromPrice = fromPrice;
    }

    public Filter withFromPrice(Integer fromPrice) {
        this.fromPrice = fromPrice;
        return this;
    }

    public Integer getToPrice() {
        return toPrice;
    }

    public void setToPrice(Integer toPrice) {
        this.toPrice = toPrice;
    }

    public Filter withToPrice(Integer toPrice) {
        this.toPrice = toPrice;
        return this;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public Filter withCategories(List<Integer> categories) {
        this.categories = categories;
        return this;
    }

    public void addCategory(Integer categoryId) {
        this.categories.add(categoryId);
    }

    public void clearCategories(){
        this.categories.clear();
    }

    public void removeCategory(Integer categoryId) {
        this.categories.remove(categoryId);
    }

    public Integer getFromDistance() {
        return fromDistance;
    }

    public void setFromDistance(Integer fromDistance) {
        this.fromDistance = fromDistance;
    }

    public Filter withFromDistance(Integer fromDistance) {
        this.fromDistance = fromDistance;
        return this;
    }

    public Boolean getHelpOnTheWay() {
        return helpOnTheWay;
    }

    public void setHelpOnTheWay(Boolean helpOnTheWay) {
        this.helpOnTheWay = helpOnTheWay;
    }

    public Filter withHelpOnTheWay(Boolean helpOnTheWay) {
        this.helpOnTheWay = helpOnTheWay;
        return this;
    }

    public Integer getToDistance() {
        return toDistance;
    }

    public void setToDistance(Integer toDistance) {
        this.toDistance = toDistance;
    }

    public Filter withToDistance(Integer toDistance) {
        this.toDistance = toDistance;
        return this;
    }

    public MapLocation getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(MapLocation mapLocation) {
        this.mapLocation = mapLocation;
    }

    public int getCategoriesCount(){
        if(getCategories() == null)
            return 0;

        return getCategories().size();
    }


    public boolean isFilterSet(){
        if(fromPrice != null || toPrice != null || categories.size() > 0 || fromDistance != null || toDistance != null)
            return true;

        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (fromPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fromPrice);
        }
        if (toPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(toPrice);
        }
        if (fromDistance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fromDistance);
        }
        if (toDistance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(toDistance);
        }
        dest.writeParcelable(mapLocation, flags);
        dest.writeByte((byte) (helpOnTheWay == null ? 0 : helpOnTheWay ? 1 : 2));
    }
}