package com.justraspberry.jobdeal.rest.service.event;

import com.google.android.gms.maps.model.LatLng;

public class GetLocationFromAddressEvent {
    public LatLng latLng;

    public GetLocationFromAddressEvent(LatLng latLng) {
        this.latLng = latLng;
    }
}
