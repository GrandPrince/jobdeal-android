package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SwishRequest {
    @SerializedName("refId")
    @Expose
    String refId;

    @SerializedName("paymentId")
    @Expose
    String paymentId;

    public SwishRequest() {
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
