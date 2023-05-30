package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.UnreadCount;
import com.justraspberry.jobdeal.model.User;

public class GetUnreadNotificationCountEvent {

    public UnreadCount unreadCount;

    public GetUnreadNotificationCountEvent(UnreadCount unreadCount) {
        this.unreadCount = unreadCount;
    }
}
