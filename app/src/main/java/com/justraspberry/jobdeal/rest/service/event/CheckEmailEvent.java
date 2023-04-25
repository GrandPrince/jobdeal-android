package com.justraspberry.jobdeal.rest.service.event;

public class CheckEmailEvent {
    public Boolean isExist;

    public CheckEmailEvent(Boolean isExist) {
        this.isExist = isExist;
    }
}
