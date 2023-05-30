package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

public class GetJobByIdEvent {
    public Job job;

    public GetJobByIdEvent(Job job) {
        this.job = job;
    }
}
