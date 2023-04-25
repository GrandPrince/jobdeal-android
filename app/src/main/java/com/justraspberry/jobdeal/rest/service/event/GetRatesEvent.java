package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Rate;

import java.util.ArrayList;

public class GetRatesEvent {
    public ArrayList<Rate> rates;

    public GetRatesEvent(ArrayList<Rate> rates) {
        this.rates = rates;
    }
}
