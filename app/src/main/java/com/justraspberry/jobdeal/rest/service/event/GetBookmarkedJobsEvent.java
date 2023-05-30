package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Job;

import java.util.ArrayList;

public class GetBookmarkedJobsEvent {
    public ArrayList<Job> bookmarkedJobs;

    public GetBookmarkedJobsEvent(ArrayList<Job> bookmarkedJobs) {
        this.bookmarkedJobs = bookmarkedJobs;
    }
}
