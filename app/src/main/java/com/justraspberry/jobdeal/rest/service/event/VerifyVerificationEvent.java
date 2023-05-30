package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.User;

public class VerifyVerificationEvent {
    public User user;

    public VerifyVerificationEvent(User user) {
        this.user = user;
    }
}
