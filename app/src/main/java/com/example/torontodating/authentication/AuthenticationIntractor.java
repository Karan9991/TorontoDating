package com.example.torontodating.authentication;

import android.content.Context;
import android.widget.ProgressBar;

public interface AuthenticationIntractor {
    void progressbarShow(ProgressBar progressBar);
    void progressbarHide(ProgressBar progressBar);
    void toast(Context context, String message);
    void navigateToAhead(Class<?> cls);
}
