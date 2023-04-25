package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.justraspberry.jobdeal.model.Job;

import java.util.ArrayList;

public class JobResponse  {
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("jobs")
    @Expose
    private ArrayList<Job> jobs;

    public JobResponse() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }
}
