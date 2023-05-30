package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.model.FilterBody;
import com.justraspberry.jobdeal.model.Price;
import com.justraspberry.jobdeal.model.User;


public class LoginRegisterResponse {

    @SerializedName("jwt")
    @Expose
    String jwt;

    @SerializedName("user")
    @Expose
    User user;

    @SerializedName("info")
    @Expose
    Info info;

    @SerializedName("prices")
    @Expose
    Price prices;

    @SerializedName("wishlist")
    @Expose
    FilterBody wishlist;

    public LoginRegisterResponse() {
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Price getPrices() {
        return prices;
    }

    public void setPrices(Price prices) {
        this.prices = prices;
    }

    public FilterBody getWishlist() {
        return wishlist;
    }

    public void setWishlist(FilterBody wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public String toString() {
        return App.getInstance().getGson().toJson(this);
    }
}
