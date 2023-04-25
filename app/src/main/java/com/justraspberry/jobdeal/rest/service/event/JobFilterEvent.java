package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.model.JobResponse;

import java.util.ArrayList;

public class JobFilterEvent {
    public JobResponse jobResponse;
    public boolean isSpeedy;
    public boolean isDelivery;
    public boolean isMap;

    public JobFilterEvent(JobResponse jobResponse, boolean isSpeedy, boolean isDelivery, boolean isMap) {
        this.jobResponse = jobResponse;
        this.isSpeedy = isSpeedy;
        this.isDelivery = isDelivery;
        this.isMap = isMap;
    }
}
