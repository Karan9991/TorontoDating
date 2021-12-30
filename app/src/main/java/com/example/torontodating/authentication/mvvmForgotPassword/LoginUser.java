package com.example.torontodating.authentication.mvvmForgotPassword;

import android.util.Patterns;


public class LoginUser {

    private String strEmailAddress;

    public LoginUser(String strEmailAddress) {
        this.strEmailAddress = strEmailAddress;
    }

    public String getStrEmailAddress() {
        return strEmailAddress;
    }


    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }
}
