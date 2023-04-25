package com.justraspberry.jobdeal.event;

import com.justraspberry.jobdeal.model.Category;

import java.util.ArrayList;

public class CategorySearchEvent {
    public ArrayList<Category> categories;

    public CategorySearchEvent(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
