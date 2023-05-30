package com.justraspberry.jobdeal.event;

public class TotalJobCountEvent {
    public int total = 0;
    public int shown = 0;
    public boolean isSpeedy = false;
    public boolean isMap = false;
    public boolean isDelivery = false;

    public TotalJobCountEvent(int total, int shown, boolean isSpeedy, boolean isDelivery, boolean isMap) {
        this.total = total;
        this.shown = shown;
        this.isSpeedy = isSpeedy;
        this.isMap = isMap;
        this.isDelivery = isDelivery;
    }
}
