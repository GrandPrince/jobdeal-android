package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankIdAuth {
    @SerializedName("orderRef")
    @Expose
    String orderRef;

    @SerializedName("autoStartToken")
    @Expose
    String autoStartToken;

    public BankIdAuth() {
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getAutoStartToken() {
        return autoStartToken;
    }

    public void setAutoStartToken(String autoStartToken) {
        this.autoStartToken = autoStartToken;
    }
}
