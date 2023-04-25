package com.justraspberry.jobdeal.event;

import android.location.Location;

public class LocationEvent {
    public Location location;

    public LocationEvent(Location location) {
        this.location = location;
    }
}
