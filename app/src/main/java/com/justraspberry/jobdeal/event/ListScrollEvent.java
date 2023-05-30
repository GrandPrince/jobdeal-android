package com.justraspberry.jobdeal.event;

public class ListScrollEvent {
    public boolean scrollDown = false;

    public ListScrollEvent(boolean scrollDown) {
        this.scrollDown = scrollDown;
    }
}
