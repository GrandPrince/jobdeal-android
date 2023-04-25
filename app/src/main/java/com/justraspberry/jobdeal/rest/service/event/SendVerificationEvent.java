package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.User;

public class SendVerificationEvent {
    public User user;

    public SendVerificationEvent(User user) {
        this.user = user;
    }
}
