package com.example.torontodating.authentication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.torontodating.R;
import com.example.torontodating.Validate;
import com.example.torontodating.authentication.Model.User;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Register extends AppCompatActivity implements ValueEventListener, Validate {
TextView tvsignin;
EditText etName, etAge, etEmail, etPassword;
Button btnRegister;
private ProgressBar progressBar;
AuthenticationPresenterLayer registerPresenterLayer;
 Register register;
 ImageView imageView;
    String Storage_Path = "Dating/";
    Uri FilePathUri;
    int Image_Request_Code = 7;
    Uri downlduri;
    ProgressDialog progressDialog ;
    StorageReference storageReference;
    User user;
    DatabaseReference dfUpdate;
    FirebaseDatabase database;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    String[] ITEMS = {"Customer", "Seller"};
    Boolean isValid, isSpinnerValid;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//UI
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvsignin = findViewById(R.id.tvsignin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar_cyclic);
        imageView = findViewById(R.id.profilePic);
        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();

        registerPresenterLayer = new AuthenticationPresenterLayer(this);
        register = this;

        isValid = false;
        isSpinnerValid = false;
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        dfUpdate = database.getReference();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Profile Picture"), Image_Request_Code);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (validations()) {
                UploadImageFileToFirebaseStorage(etName.getText().toString(),etAge.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString());
                }
            }
});
        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPresenterLayer.navigateToAhead(SignIn.class);
            }
        });
    }

    @Override
    public boolean validations() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etAge.getText())) {
            etAge.setError("Age is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("E-Mail is required!");
            isValid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Password is required!");
            isValid = false;
        }
        else if(!PASSWORD_PATTERN.matcher(etPassword.getText().toString().trim()).matches()){
            etPassword.setError( "Password must between 8 and 20 characters; must contain at least one lowercase letter, one uppercase letter, one numeric digit, and one special character, but cannot contain whitespace" );
            isValid = false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();
            imageView.setImageURI(FilePathUri);

            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplication().getApplicationContext().getContentResolver(), FilePathUri);
                // Setting up bitmap selected image into ImageView.
                //    ivUploadimg.setImageBitmap(bitmap);
                // After selecting image change choose button above text.
                //  ChooseButton.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplication().getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    public void UploadImageFileToFirebaseStorage(String name, String age, String email, String pass) {

        if (FilePathUri != null) {
            progressDialog.setTitle("Signing Up...");
            progressDialog.show();
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downlduri = uri;

                                    registerPresenterLayer.progressbarShow(progressBar);
                                    firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            registerPresenterLayer.progressbarHide(progressBar);
                                            if (task.isSuccessful()) {
                                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //user = new User(name, age, email, pass, downlduri.toString());
                                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                            String userid = firebaseUser.getUid();
                                                            writeNewUser(etName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(),  downlduri.toString(), "offline", firebaseAuth.getCurrentUser().getUid(), etEmail.getText().toString(), etEmail.getText().toString().toLowerCase(), etAge.getText().toString());


                                                            //mDatabase.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                                                            progressDialog.dismiss();
                                                            registerPresenterLayer.toast(getApplicationContext(), "Registered Successfully Please check your E-Mail for verification");

                                                            etName.setText("");
                                                            etAge.setText("");
                                                            etEmail.setText("");
                                                            etPassword.setText("");

                                                        } else {
                                                            progressDialog.dismiss();
                                                            registerPresenterLayer.toast(getApplicationContext(), task.getException().getMessage());
                                                        }
                                                    }
                                                });

                                            } else {
                                                progressDialog.dismiss();
                                                registerPresenterLayer.toast(getApplicationContext(), "SignUp Unsuccessful " + task.getException().getMessage());
                                            }
                                        }
                                    });
                                  }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Signing Up...");
                        }
                    });
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(Register.this, "Please Select Profile Picture", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(Register.this, databaseError.toException().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private void writeNewUser(String name, String email, String password, String imageURL, String status, String id, String username, String search, String age) {

        User user = new User(name, email, password, imageURL, status, id, username, search, age);
        //  String key = mDatabase.getDatabase().getReference().push().getKey();
        String uid = firebaseAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(uid).setValue(user);
    }
}
