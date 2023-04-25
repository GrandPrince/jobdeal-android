package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Applicant;

import java.util.ArrayList;

public class GetJobApplicantsEvent {

    public ArrayList<Applicant> jobApplicants;

    public GetJobApplicantsEvent(ArrayList<Applicant> jobApplicants) {
        this.jobApplicants = jobApplicants;
    }
}
