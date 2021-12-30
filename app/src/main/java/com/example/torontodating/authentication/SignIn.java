package com.example.torontodating.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.torontodating.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.torontodating.R;
import com.example.torontodating.Validate;

import java.util.Map;

public class SignIn extends AppCompatActivity implements Validate {
EditText etEmailLogin, etPasswordLogin;
Button btnLogin;
TextView tvSignUp, tvForgotPassword;
private ProgressBar progressBar;

SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
     Boolean isValid;

    AuthenticationPresenterLayer authenticationPresenterLayer;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mFirebasedataRefSell;
    DatabaseReference mFirebasedataRefCust;
    Query querySeller, queryCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(this);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar_cyclicSignin);
        sharedPref = getSharedPreferences("UserType", Context.MODE_PRIVATE);
        editor =  sharedPref.edit();
        etEmailLogin.setText("999karansandhu@gmail.com");
        etPasswordLogin.setText("Karan6$");

        authenticationPresenterLayer = new AuthenticationPresenterLayer(this);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        isValid = false;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mFirebasedataRefSell  = FirebaseDatabase.getInstance().getReference();
        mFirebasedataRefCust  = FirebaseDatabase.getInstance().getReference();


//If User already signed in
                if (firebaseUser!=null&&firebaseAuth.getCurrentUser().isEmailVerified()){

                    startActivity(new Intent(SignIn.this, com.example.torontodating.Chat.MainActivity.class));
                    finish();
                    Log.e("User","Signed In");
                    }

                btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()){
                    authenticationPresenterLayer.progressbarShow(progressBar);
                    firebaseAuth.signInWithEmailAndPassword(etEmailLogin.getText().toString(), etPasswordLogin.getText().toString()).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            authenticationPresenterLayer.progressbarHide(progressBar);
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                    Log.e("User","Authenticated");
                               startActivity(new Intent(SignIn.this, com.example.torontodating.Chat.MainActivity.class));
                               finish();
                                }else {
                                    authenticationPresenterLayer.toast(getApplicationContext(), "Please verify your E-Mail address");

                                }
                            } else {
                                authenticationPresenterLayer.toast(getApplicationContext(), "SignIn Failed " + task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

                tvSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        authenticationPresenterLayer.navigateToAhead(Register.class);
                    }
                });

           tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticationPresenterLayer.navigateToAhead(ForgotPassword.class);
            }
        });
    }


    @Override
    public boolean validations() {
        if (TextUtils.isEmpty(etEmailLogin.getText())) {
            etEmailLogin.setError("E-Mail is required!");
            isValid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(etEmailLogin.getText().toString().trim()).matches()) {
            etEmailLogin.setError("Please enter a valid email address");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etPasswordLogin.getText())) {
            etPasswordLogin.setError("Password is required!");
            isValid = false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }
}
