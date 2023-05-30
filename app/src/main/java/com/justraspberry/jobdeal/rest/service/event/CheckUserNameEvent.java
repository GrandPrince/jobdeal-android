package com.justraspberry.jobdeal.rest.service.event;

public class CheckUserNameEvent {

    public Boolean isExist;

    public CheckUserNameEvent(Boolean isExist) {
        this.isExist = isExist;
    }
}
