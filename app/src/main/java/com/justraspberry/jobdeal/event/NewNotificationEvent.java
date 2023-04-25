package com.justraspberry.jobdeal.event;

import com.justraspberry.jobdeal.model.Notification;

public class NewNotificationEvent {
    public Notification notification;

    public NewNotificationEvent(Notification notification) {
        this.notification = notification;
    }
}
