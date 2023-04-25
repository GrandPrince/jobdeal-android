package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

import java.util.ArrayList;

public class PostedAppliedJobsEvent {
    public ArrayList<Job> jobs;
    public int type;

    public PostedAppliedJobsEvent(ArrayList<Job> jobs, int type) {
        this.jobs = jobs;
        this.type = type;
    }
}
