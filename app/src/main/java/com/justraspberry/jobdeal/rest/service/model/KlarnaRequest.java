package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KlarnaRequest {
    @SerializedName("refId")
    @Expose
    String refId;

    @SerializedName("html")
    @Expose
    String html;

    public KlarnaRequest() {
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
