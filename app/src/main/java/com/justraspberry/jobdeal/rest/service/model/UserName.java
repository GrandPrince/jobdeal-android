package com.justraspberry.jobdeal.rest.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserName {

    @SerializedName("username")
    @Expose
    private String username;

   public UserName(){

   }

   public UserName(String username){
       this.username=username;
   }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
