package com.example.torontodating.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.torontodating.CheckConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.example.torontodating.R;
import com.example.torontodating.authentication.mvvmForgotPassword.LoginUser;
import com.example.torontodating.authentication.mvvmForgotPassword.LoginViewModel;
import com.example.torontodating.databinding.ActivityForgotPasswordBinding;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
EditText etForgotPassword;
TextView tvForgotPassword;
Button btnForgotPassword;

private FirebaseAuth firebaseAuth;

private boolean isValid;

private LoginViewModel loginViewModel;
private ActivityForgotPasswordBinding binding;
AuthenticationPresenterLayer presenterLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        binding = DataBindingUtil.setContentView(ForgotPassword.this, R.layout.activity_forgot_password);
        //setContentView(R.layout.activity_forgot_password);
        binding.setLifecycleOwner(this);
        binding.setLoginViewModel(loginViewModel);

        etForgotPassword = findViewById(R.id.etForgotPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        presenterLayer = new AuthenticationPresenterLayer(this);

        isValid = false;
        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");

        loginViewModel.getUser().observe(this, new Observer<LoginUser>() {
            @Override
            public void onChanged(LoginUser loginUser) {
                if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrEmailAddress())) {
                    binding.etForgotPassword.setError("Enter an E-Mail Address");
                    binding.etForgotPassword.requestFocus();
                }
                else if (!loginUser.isEmailValid()) {
                    binding.etForgotPassword.setError("Enter a Valid E-mail Address");
                    binding.etForgotPassword.requestFocus();
                }
                else {
                    binding.etForgotPassword.setError(null);
                    binding.tvForgotPassword.setText(loginUser.getStrEmailAddress());
                    if (CheckConnection.getInstance().isNetworkAvailable(ForgotPassword.this)) {
                        sendPasswordLink();
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void sendPasswordLink(){
                            firebaseAuth.sendPasswordResetEmail(etForgotPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                etForgotPassword.setError(null);
                                presenterLayer.toast(getApplicationContext(), "Password Link sent to your E-Mail, Please check your E-Mail");
                            } else {
                                presenterLayer.toast(getApplicationContext(), task.getException().getMessage());
                            }
                        }
                    });
    }
    
    @Override
    public boolean onSupportNavigateUp() {

        startActivity(new Intent(ForgotPassword.this,SignIn.class));

        return super.onSupportNavigateUp();
    }
}
