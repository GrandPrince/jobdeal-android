package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Notification;

import java.util.ArrayList;

public class GetNotificationEvent {
    public ArrayList<Notification> notifications;
    public int type;

    public GetNotificationEvent(ArrayList<Notification> notifications, int type) {
        this.notifications = notifications;
        this.type = type;
    }
}
