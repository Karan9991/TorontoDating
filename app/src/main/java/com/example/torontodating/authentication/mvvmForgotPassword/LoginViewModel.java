package com.example.torontodating.authentication.mvvmForgotPassword;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> EmailAddress = new MutableLiveData<>();

    private MutableLiveData<LoginUser> userMutableLiveData;

    public MutableLiveData<LoginUser> getUser() {

        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;

    }

    public void onClick() {

        LoginUser loginUser = new LoginUser(EmailAddress.getValue());

        userMutableLiveData.setValue(loginUser);
        Log.i("onclick","oonnonnnonoonnonnnon");

    }

}
