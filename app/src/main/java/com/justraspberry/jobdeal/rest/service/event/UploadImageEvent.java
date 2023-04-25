package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.ImageUploadResponse;

public class UploadImageEvent {
    public ImageUploadResponse imageUploadResponse;

    public UploadImageEvent(ImageUploadResponse imageUploadResponse) {
        this.imageUploadResponse = imageUploadResponse;
    }
}
