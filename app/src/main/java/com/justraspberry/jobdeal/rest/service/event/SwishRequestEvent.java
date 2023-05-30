package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.SwishRequest;

public class SwishRequestEvent {
    public SwishRequest swishRequest;

    public SwishRequestEvent(SwishRequest swishRequest) {
        this.swishRequest = swishRequest;
    }
}
