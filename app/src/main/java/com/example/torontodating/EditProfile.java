package com.example.torontodating;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.torontodating.Chat.MainActivity;
import com.example.torontodating.authentication.ForgotPassword;
import com.example.torontodating.authentication.Model.User;
import com.example.torontodating.authentication.Register;
import com.example.torontodating.authentication.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements ValueEventListener, Validate {

//UI
    ImageView imgRecipe,imgView,imgLeftArrow;
    EditText etPrice, etDesc;
    User user;
    Button btnEdit,btnUpdate, btnDeleteAccount;
    AlertDialog.Builder builder;

    String key;
    String Storage_Path = "Recipe/";
    Uri FilePathUri;
    int Image_Request_Code = 7;
    Uri downlduri;
    ProgressDialog progressDialog ;
    private boolean isValid;

    //Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference df,dfUpdate;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

//UI
        imgRecipe = findViewById(R.id.imgRecipe);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        btnEdit = findViewById(R.id.btnEdit);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgView = findViewById(R.id.imgView);
        imgLeftArrow = findViewById(R.id.imgLeftArrow);
        btnDeleteAccount = findViewById(R.id.btnDeleteProfile);

        user = new User();
         key = firebaseAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(EditProfile.this);
        isValid = false;

        dfUpdate = database.getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        builder = new AlertDialog.Builder(this);

        gettingIntent();

         imgLeftArrow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(EditProfile.this, MainActivity.class));
             }
         });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUpdate.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.VISIBLE);
                btnDeleteAccount.setVisibility(View.VISIBLE);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()) {
                    if (CheckConnection.getInstance().isNetworkAvailable(EditProfile.this)) {
                        UploadImageFileToFirebaseStorage(etDesc.getText().toString(), etPrice.getText().toString());
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckConnection.getInstance().isNetworkAvailable(EditProfile.this)) {
                    alertDialogdelete();
                }else {
                    Toast.makeText(getApplicationContext(),"No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot!=null) {
               user = dataSnapshot.getValue(User.class);
               if (user != null) {
                   etPrice.setText(user.getName());
                   etDesc.setText(user.getAge());
                   Glide.with(getApplicationContext()).load(user.getImageURL()).into(imgRecipe);
               }
           }
    }
    private void writeNewPost(String desc, String imageURL, String price) {
        user = new User(price,user.getEmail(), user.getPassword(), imageURL, user.getStatus(), user.getId(), user.getUsername(), user.getSearch(),desc, user.getGender());
        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(firebaseAuth.getCurrentUser().getUid(),postValues);
        dfUpdate.updateChildren(childUpdates);
        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_LONG).show();
    }
    private void gettingIntent(){
            df = database.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
            df.addValueEventListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

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
    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplication().getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

   //  Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage(String desc, String price) {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {
            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");
            // Showing progressDialog.
            progressDialog.show();
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downlduri = uri;
                               //     imageUploadInfo = new AddRecipe(downlduri.toString(),etSellerPrice.getText().toString(), etSellerDesc.getText().toString());
                                    user = new User(price,user.getEmail(), user.getPassword(), downlduri.toString(), user.getStatus(), user.getId(), user.getUsername(), user.getSearch(),desc, user.getGender());
                                    Map<String, Object> postValues = user.toMap();

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put(firebaseAuth.getCurrentUser().getUid(), postValues);
                                    dfUpdate.updateChildren(childUpdates);
                                 //   dfImgUpdate.child("Seller").child(firebaseAuth.getCurrentUser().getUid()).child("Recipe").child(etSellerTitle.getText().toString()).setValue(imageUploadInfo);
                                    progressDialog.dismiss();

                                    //  @SuppressWarnings("VisibleForTests")
                                    Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Hiding the progressDialog.
                            progressDialog.dismiss();
                            // Showing exception erro message.
                            Toast.makeText(EditProfile.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Setting progressDialog Title.
                            progressDialog.setTitle("Profile Updating...");
                        }
                    });
        }
        else {
            user = new User(price,user.getEmail(), user.getPassword(), user.getImageURL(), user.getStatus(), user.getId(), user.getUsername(), user.getSearch(),desc,user.getGender());

            writeNewPost(etDesc.getText().toString(), user.getImageURL(), etPrice.getText().toString());
            //Toast.makeText(EditProfile.this, "Please Select Image", Toast.LENGTH_LONG).show();

        }
    }
    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
      //  Toast.makeText(Recipe.this, databaseError.toException().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean validations() {
         if (TextUtils.isEmpty(etPrice.getText())) {
            etPrice.setError("Name is required!");
            isValid = false;
        }
        else if (TextUtils.isEmpty(etDesc.getText())) {
            etDesc.setError("Age is required!");
            isValid = false;
        }
        else {
            isValid = true;
        }
        return isValid;
    }

    private void alertDialogdelete(){
        builder.setTitle("Are You Sure?");
        builder.setMessage("Your All Data Will be Deleted Permanently")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delacc();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void delacc() {
        Log.e("ok","okkk"+user.getEmail()+firebaseAuth.getCurrentUser().getUid());
        FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();
        try {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), user.getPassword());
            if (userr != null) {
                userr.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        userr.delete().addOnCompleteListener(new OnCompleteListener < Void > () {
                            @Override
                            public void onComplete (@NonNull Task < Void > task) {
                                //user deleted successfully
                                Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), Register.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            //error
        }
    }

}
