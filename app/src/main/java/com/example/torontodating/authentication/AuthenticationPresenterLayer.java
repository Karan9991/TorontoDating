package com.example.torontodating.authentication;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthenticationPresenterLayer extends AppCompatActivity implements AuthenticationIntractor {

    Context mcontext;

    AuthenticationPresenterLayer(Context context) {
        this.mcontext = context;
    }

    @Override
    public void progressbarShow(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressbarHide(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToAhead(Class<?> cls) {
        mcontext.startActivity(new Intent(mcontext, cls));
    }

}