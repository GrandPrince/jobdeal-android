package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Report;

public class ReportEvent {
    public Report report;

    public ReportEvent(Report report) {
        this.report = report;
    }
}
