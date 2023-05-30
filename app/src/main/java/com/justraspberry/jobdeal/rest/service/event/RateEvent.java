package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Rate;

public class RateEvent {

    Rate rate;

    public RateEvent(Rate rate) {
        this.rate = rate;
    }
}
