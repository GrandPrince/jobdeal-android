package com.justraspberry.jobdeal.rest.service.event;

import com.justraspberry.jobdeal.model.FilterBody;

public class WishlistEvent {
    public FilterBody filterBody;

    public WishlistEvent(FilterBody filterBody) {
        this.filterBody = filterBody;
    }
}
