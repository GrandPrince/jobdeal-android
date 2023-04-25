package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.User;

public class GetUserEvent {

    public User user;

    public GetUserEvent(User user) {
        this.user = user;
    }
}
