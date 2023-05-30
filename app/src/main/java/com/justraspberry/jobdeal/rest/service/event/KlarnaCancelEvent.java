package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.User;

public class KlarnaCancelEvent {

    public User user;

    public KlarnaCancelEvent(User user) {
        this.user = user;
    }
}
