package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

public class AddJobEvent {
    public Job job;
    public String errorMessage;

    public AddJobEvent(Job job) {
        this.job = job;
    }

    public AddJobEvent(Job job, String errorMessage) {
        this.job = job;
        this.errorMessage = errorMessage;
    }
}
