package com.justraspberry.jobdeal.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Job implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("expireAt")
    @Expose
    private String expireAt;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("property")
    @Expose
    private String property = "";
    @SerializedName("images")
    @Expose
    private List<Image> images = new ArrayList<>();
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("isBoost")
    @Expose
    private Boolean isBoost;
    @SerializedName("isSpeedy")
    @Expose
    private Boolean isSpeedy;
    @SerializedName("isDelivery")
    @Expose
    private Boolean isDelivery;
    @SerializedName("isBookmarked")
    @Expose
    private Boolean isBookmarked;
    @SerializedName("isListed")
    @Expose
    private Boolean isListed;
    @SerializedName("isApplied")
    @Expose
    private Boolean isApplied;
    @SerializedName("isExpired")
    @Expose
    private Boolean isExpired;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("mainImage")
    @Expose
    private String mainImage;
    @SerializedName("bidCount")
    @Expose
    private Integer bidCount;
    @SerializedName("choosedCount")
    @Expose
    private Integer choosedCount;
    @SerializedName("applicantCount")
    @Expose
    private Integer applicantCount;
    @SerializedName("isChoosed")
    @Expose
    private Boolean isChoosed;
    @SerializedName("helpOnTheWay")
    @Expose
    private Boolean isHelpOnTheWay;
    @SerializedName("isUnderbidderListed")
    @Expose
    private Boolean isUnderbidderListed;

    public final static Parcelable.Creator<Job> CREATOR = new Creator<Job>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        public Job[] newArray(int size) {
            return (new Job[size]);
        }

    };

    protected Job(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.expireAt = ((String) in.readValue((String.class.getClassLoader())));
        this.price = ((Double) in.readValue((Double.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.property = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.images, (com.justraspberry.jobdeal.model.Image.class.getClassLoader()));
        this.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
        this.isBoost = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isSpeedy = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isBookmarked = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.mainImage = ((String) in.readValue((String.class.getClassLoader())));
        this.bidCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.isListed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isApplied = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.choosedCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.applicantCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.isChoosed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isDelivery = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isHelpOnTheWay = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isExpired = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isUnderbidderListed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public Job() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Job withId(Integer id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job withUser(User user) {
        this.user = user;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Job withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Job withDescription(String description) {
        this.description = description;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Job withPrice(Double price) {
        this.price = price;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Job withAddress(String address) {
        this.address = address;
        return this;
    }


    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Job withCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        return this;
    }


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Job withProperty(String property) {
        this.property = property;
        return this;
    }


    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Job withImages(List<Image> images) {
        this.images = images;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Job withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Job withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getBoost() {
        return isBoost;
    }

    public void setBoost(Boolean boost) {
        isBoost = boost;
    }

    public Boolean getSpeedy() {
        return isSpeedy;
    }

    public void setSpeedy(Boolean speedy) {
        isSpeedy = speedy;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Integer getBidCount() {
        return bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }

    public Boolean getListed() {
        return isListed;
    }

    public void setListed(Boolean listed) {
        isListed = listed;
    }

    public Boolean getApplied() {
        return isApplied;
    }

    public void setApplied(Boolean applied) {
        isApplied = applied;
    }

    public Image getFirstNotUploadedImage() {
        for (Image image : getImages()) {
            if (!image.isUploaded())
                return image;
        }

        return null;
    }

    public Integer getChoosedCount() {
        return choosedCount;
    }

    public void setChoosedCount(Integer choosedCount) {
        this.choosedCount = choosedCount;
    }

    public Integer getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(Integer applicantCount) {
        this.applicantCount = applicantCount;
    }

    public Boolean getChoosed() {
        return isChoosed;
    }

    public void setChoosed(Boolean choosed) {
        isChoosed = choosed;
    }

    public Boolean getDelivery() {
        return isDelivery;
    }

    public void setDelivery(Boolean delivery) {
        isDelivery = delivery;
    }

    public Job withIsDelivery(Boolean isDelivery) {
        this.isDelivery = isDelivery;
        return this;
    }

    public Boolean getHelpOnTheWay() {
        return isHelpOnTheWay;
    }

    public void setHelpOnTheWay(Boolean helpOnTheWay) {
        isHelpOnTheWay = helpOnTheWay;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    public Boolean getUnderbidderListed() {
        return isUnderbidderListed;
    }

    public void setUnderbidderListed(Boolean underbidderListed) {
        isUnderbidderListed = underbidderListed;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(user);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(expireAt);
        dest.writeValue(price);
        dest.writeValue(address);
        dest.writeValue(categoryId);
        dest.writeValue(property);
        dest.writeList(images);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(currency);
        dest.writeValue(isBoost);
        dest.writeValue(isSpeedy);
        dest.writeValue(isBookmarked);
        dest.writeValue(country);
        dest.writeValue(distance);
        dest.writeValue(mainImage);
        dest.writeValue(bidCount);
        dest.writeValue(isListed);
        dest.writeValue(isApplied);
        dest.writeValue(choosedCount);
        dest.writeValue(applicantCount);
        dest.writeValue(isChoosed);
        dest.writeValue(isDelivery);
        dest.writeValue(isHelpOnTheWay);
        dest.writeValue(isExpired);
        dest.writeValue(isUnderbidderListed);
    }

    public int describeContents() {
        return 0;
    }

}