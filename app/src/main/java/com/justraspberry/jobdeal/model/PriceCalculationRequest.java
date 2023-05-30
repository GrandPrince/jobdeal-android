package com.justraspberry.jobdeal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PriceCalculationRequest {
    @SerializedName("job")
    @Expose
    Job job;
    @SerializedName("applicant")
    @Expose
    Applicant applicant;
    @SerializedName("type")
    @Expose
    Integer type;

    public PriceCalculationRequest() {
    }

    public PriceCalculationRequest(Job job, Applicant applicant, Integer type) {
        this.job = job;
        this.applicant = applicant;
        this.type = type;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
