package com.justraspberry.jobdeal.rest.service.event;


import com.justraspberry.jobdeal.rest.service.model.LoginRegisterResponse;

public class RegisterEvent {
    public LoginRegisterResponse registerResponse;
    public String errorMessage;
    public int statusCode=200;

    public RegisterEvent(LoginRegisterResponse registerResponse){this.registerResponse=registerResponse;}

    public RegisterEvent(LoginRegisterResponse registerResponse, String errorMessage, int statusCode){
        this.registerResponse=registerResponse;
        this.errorMessage=errorMessage;
        this.statusCode=statusCode;
    }

}
