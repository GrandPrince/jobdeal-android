package com.justraspberry.jobdeal.event;

import com.justraspberry.jobdeal.model.PriceCalculation;

public class PriceCalculationEvent {
    public PriceCalculation priceCalculation;

    public PriceCalculationEvent(PriceCalculation priceCalculation) {
        this.priceCalculation = priceCalculation;
    }
}
