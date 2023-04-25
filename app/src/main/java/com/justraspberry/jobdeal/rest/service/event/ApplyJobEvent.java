package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

public class ApplyJobEvent {
    public Job job;

    public ApplyJobEvent(Job job) {
        this.job = job;
    }
}
