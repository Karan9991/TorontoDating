package com.example.torontodating;

import android.content.Context;
import android.net.ConnectivityManager;

public class CheckConnection {

    private static CheckConnection checkConnection = null;

    private CheckConnection(){

    }

    public static CheckConnection getInstance(){
        if (checkConnection == null){
            checkConnection = new CheckConnection();
        }
        return checkConnection;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
