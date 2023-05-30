package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.ImageUploadResponse;

import java.util.ArrayList;

public class UploadJobImageEvent {
    public ArrayList<ImageUploadResponse> imageUploadResponseList;

    public UploadJobImageEvent(ArrayList<ImageUploadResponse> imageUploadResponseList) {
        this.imageUploadResponseList = imageUploadResponseList;
    }
}
