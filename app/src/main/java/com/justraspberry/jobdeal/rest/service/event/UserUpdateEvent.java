package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.User;

public class UserUpdateEvent {

    public User user;

    public UserUpdateEvent(User user) {
        this.user = user;
    }
}
