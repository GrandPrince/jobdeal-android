package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.KlarnaRequest;

public class KlarnaRequestEvent {
    public KlarnaRequest klarnaRequest;

    public KlarnaRequestEvent(KlarnaRequest klarnaRequest) {
        this.klarnaRequest = klarnaRequest;
    }
}
