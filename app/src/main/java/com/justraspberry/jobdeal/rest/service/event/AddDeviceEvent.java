package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Device;

public class AddDeviceEvent {
    public Device device;

    public AddDeviceEvent(Device device) {
        this.device = device;
    }
}
