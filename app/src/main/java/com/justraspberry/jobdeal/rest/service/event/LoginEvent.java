package com.justraspberry.jobdeal.rest.service.event;


import com.justraspberry.jobdeal.rest.service.model.LoginRegisterResponse;

public class LoginEvent {
    public LoginRegisterResponse loginResponse;
    public String errorMessage;
    public int statusCode = 200;

    public LoginEvent(LoginRegisterResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    public LoginEvent(LoginRegisterResponse loginResponse, String errorMessage, int statusCode) {
        this.loginResponse = loginResponse;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
}
