package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.Payment;

public class PaymentEvent {
    public Payment payment;

    public PaymentEvent(Payment payment) {
        this.payment = payment;
    }
}
