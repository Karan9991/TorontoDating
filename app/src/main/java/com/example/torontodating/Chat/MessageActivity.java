package com.example.torontodating.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.torontodating.CheckConnection;
import com.example.torontodating.EditProfile;
import com.example.torontodating.authentication.ForgotPassword;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.torontodating.Chat.Adapter.MessageAdapter;
import com.example.torontodating.Chat.Fragments.APIService;
import com.example.torontodating.Chat.Model.Chat;
import com.example.torontodating.Chat.Notifications.Client;
import com.example.torontodating.Chat.Notifications.Data;
import com.example.torontodating.Chat.Notifications.MyResponse;
import com.example.torontodating.Chat.Notifications.Sender;
import com.example.torontodating.Chat.Notifications.Token;
import com.example.torontodating.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    Intent intent;
    ValueEventListener seenListener;
    String userid;
    APIService apiService;
    boolean notify = false;
    SharedPreferences sharedPref;
    com.example.torontodating.authentication.Model.User user;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String blockedUser, someoneBlocked = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPref = getSharedPreferences("UserType", Context.MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBlockedUser();
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    if (CheckConnection.getInstance().isNetworkAvailable(MessageActivity.this)) {
                        if (blockedUser != null) {
                            if (!blockedUser.equals(user.getId())) {
                                    sendMessage(fuser.getUid(), userid, msg);
                                    text_send.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), "You blocked this user", Toast.LENGTH_LONG).show();
                            }
                        }else {
                                sendMessage(fuser.getUid(), userid, msg);
                                text_send.setText("");
                            }
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });
       // if (sharedPref.getString("UT",null).equals("Seller")){
            Customerr();
       // }else if (sharedPref.getString("UT",null).equals("Customer")){
           // Sellerr();
        //}

//        reference = FirebaseDatabase.getInstance().getReference("Seller").child(userid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                com.tiff.tiffinbox.Authentication.Model.User user = dataSnapshot.getValue(com.tiff.tiffinbox.Authentication.Model.User.class);
//                username.setText(user.getUsername());
//                if (user.getImageURL().equals("default")){
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    //and this
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//                }
//
//                readMesagges(fuser.getUid(), userid, user.getImageURL());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        seenMessage(userid);

        getBlockedUser();
      // getSomoneBlockedYou();

        Log.e(" vvvb"," "+someoneBlocked+fuser.getUid());
//        if(someoneBlocked != null) {
//            if (someoneBlocked.equals(fuser.getUid())) {
//                text_send.setEnabled(false);
//                btn_send.setEnabled(false);
//            }
//        }

    }
//public void Sellerr(){
//                    if (!msg.equals("")) {
//                        Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
//                    }
//                    else if (CheckConnection.getInstance().isNetworkAvailable(MessageActivity.this)) {
//                        Toast.makeText(getApplicationContext(),"No Internet!", Toast.LENGTH_SHORT).show();
//                    }
//                        else if (blockedUser != null) {
//                    }
//                        else if (!blockedUser.equals(user.getId())) {
//                    }
//                    else if (someoneBlocked.equals(fuser.getUid())) {
//                        sendMessage(fuser.getUid(), userid, msg);
//                        text_send.setText("");
//                    }
//    else if (someoneBlocked != null && someoneBlocked.equals(fuser.getUid())) {
//
//    }else {
//                                    Toast.makeText(getApplicationContext(), "This user blocked you", Toast.LENGTH_LONG).show();
//                                }
//                            } else {
//                                Toast.makeText(getApplicationContext(), "You blocked this user", Toast.LENGTH_LONG).show();
//                            }
//                        }else {
//                                z
//                            }else {
//                                Toast.makeText(getApplicationContext(), "This user blocked you!", Toast.LENGTH_LONG).show();
//                            }
//                            sendMessage(fuser.getUid(), userid, msg);
//                            text_send.setText("");
//                        }
//                    }else {
//                    }
//                } else {
//                }
//}
public void Customerr(){
    reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(com.example.torontodating.authentication.Model.User.class);
            getSomoneBlockedYou();
            username.setText(user.getName());
            if (user.getImageURL().equals("default")){
                profile_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                //and this
             //   Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                Picasso.with(getApplicationContext()).
                        load(user.getImageURL()).into(profile_image);
            }

            readMesagges(fuser.getUid(), userid, user.getImageURL());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = message;
 //if (sharedPref.getString("UT",null).equals("Seller")){
    // reference = FirebaseDatabase.getInstance().getReference("Seller").child(fuser.getUid());
      //  }else if (sharedPref.getString("UT",null).equals("Customer")){
     reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
       // }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                com.example.torontodating.authentication.Model.User user = dataSnapshot.getValue(com.example.torontodating.authentication.Model.User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//public void SSeller(){
//    reference = FirebaseDatabase.getInstance().getReference("Seller").child(fuser.getUid());
//    reference.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            com.tiff.tiffinbox.Authentication.Model.User user = dataSnapshot.getValue(com.tiff.tiffinbox.Authentication.Model.User.class);
//            if (notify) {
//                sendNotifiaction(receiver, user.getUsername(), msg);
//            }
//            notify = false;
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    });
//}
//public void CCustomer(){
//    reference = FirebaseDatabase.getInstance().getReference("Seller").child(fuser.getUid());
//    reference.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            com.tiff.tiffinbox.Authentication.Model.User user = dataSnapshot.getValue(com.tiff.tiffinbox.Authentication.Model.User.class);
//            if (notify) {
//                sendNotifiaction(receiver, user.getUsername(), msg);
//            }
//            notify = false;
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    });
//}

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                           // Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.i("apiiiiiiiiiii"," "+t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
       // if (sharedPref.getString("UT",null).equals("Customer")){
            reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
//        }else if (sharedPref.getString("UT",null).equals("Seller")){
//            reference = FirebaseDatabase.getInstance().getReference("Seller").child(fuser.getUid());
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("status", status);
//            reference.updateChildren(hashMap);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.block_menu,menu);
        MenuItem item = menu.findItem(R.id.block);
        if (blockedUser != null) {
            if (blockedUser.equals(user.getId())) {
                item.setTitle("Unblock");
            } else {
                item.setTitle("Block");
            }
        }else {
            item.setTitle("Block");
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.block:
                Log.e("Block","User has been blocked!");
                if (blockedUser != null) {
                    if (blockedUser.equals(user.getId())) {
                        unBlock();
                        item.setTitle("Block");
                    } else {
                        block();
                        item.setTitle("UnBlock");
                    }
                }else {
                    block();
                    item.setTitle("UnBlock");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void block(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(fuser.getUid()).child("block").child("to").child(user.getId()).setValue(user.getId());
        reference.child("users").child(user.getId()).child("block").child("from").child(fuser.getUid()).setValue(fuser.getUid());
        Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_LONG).show();
        getBlockedUser();
    }
    private void unBlock(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(fuser.getUid()).child("block").child("to").child(user.getId()).removeValue();
        reference.child("users").child(user.getId()).child("block").child("from").child(fuser.getUid()).removeValue();
        Toast.makeText(getApplicationContext(),"UnBlocked",Toast.LENGTH_LONG).show();
        getBlockedUser();
        blockedUser = null;
    }
    private String getBlockedUser(){
        reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid()).child("block").child("to");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e("BlockedUserData"," "+snapshot.getValue());
                    if (snapshot.getValue().equals(user.getId())){
                         blockedUser = snapshot.getValue().toString();
                        Log.e("id"," "+snapshot.getValue());
                        Log.e("String"," "+blockedUser);
                    }else {
                        Log.e("id","no data");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    return blockedUser;
    }
    private String getSomoneBlockedYou(){
        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getId()).child("block").child("to");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e("SomeoneBlockedUserData"," "+snapshot.getValue());
                    if (snapshot.getValue().equals(fuser.getUid())){
                        someoneBlocked = snapshot.getValue().toString();
                        Log.e("id"," "+snapshot.getValue());
                        Log.e("String"," "+someoneBlocked);
                        text_send.setEnabled(false);
                        btn_send.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"This user Blocked you",Toast.LENGTH_LONG).show();
                    }else {
                        Log.e("id","no data");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return someoneBlocked;
    }
}
