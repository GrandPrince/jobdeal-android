package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

public class ChooseApplicantEvent {
    public Job job;

    public ChooseApplicantEvent(Job job) {
        this.job = job;
    }
}
