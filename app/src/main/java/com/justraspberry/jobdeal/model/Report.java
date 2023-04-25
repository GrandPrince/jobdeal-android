package com.justraspberry.jobdeal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {
    @SerializedName("job")
    @Expose
    Job job;

    @SerializedName("reportText")
    @Expose
    String reportText;

    @SerializedName("user")
    @Expose
    User user;

    public Report() {
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
