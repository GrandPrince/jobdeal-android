package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class BankIdCollect {
    @SerializedName("orderRef")
    @Expose
    String orderRef;

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("hint")
    @Expose
    String hint;

    @SerializedName("data")
    @Expose
    BankIdUser user;

    public BankIdCollect() {
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public BankIdUser getUser() {
        return user;
    }

    public void setUser(BankIdUser user) {
        this.user = user;
    }
}
