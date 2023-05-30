package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.BankIdAuth;

public class BankIdEvent {
    public BankIdAuth bankIdAuth;

    public BankIdEvent(BankIdAuth bankIdAuth) {
        this.bankIdAuth = bankIdAuth;
    }
}
