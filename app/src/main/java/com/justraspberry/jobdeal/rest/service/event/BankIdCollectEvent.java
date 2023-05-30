package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.rest.service.model.BankIdCollect;

public class BankIdCollectEvent {
    public BankIdCollect bankIdCollect;

    public BankIdCollectEvent(BankIdCollect bankIdCollect) {
        this.bankIdCollect = bankIdCollect;
    }
}
