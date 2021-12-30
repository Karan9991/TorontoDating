package com.example.torontodating.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.torontodating.EditProfile;
import com.example.torontodating.authentication.SignIn;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.torontodating.Chat.Fragments.ChatsFragment;
import com.example.torontodating.Chat.Fragments.UsersFragment;
import com.example.torontodating.Chat.Model.Chat;
//import com.tiff.tiffinbox.FindUserType;
import com.example.torontodating.R;
import com.example.torontodating.authentication.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView name, age;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference reference, refUserType;
   // FindUserType findUserType;
    SharedPreferences sharedPref;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    AlertDialog.Builder builder2;
    boolean logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);

        builder2 = new AlertDialog.Builder(this);
        init();
        logout = false;
        sharedPref = getSharedPreferences("UserType", Context.MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            Customerr();

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);


        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "("+unread+") Chat");
                }
                 viewPagerAdapter.addFragment(new UsersFragment(), "Date");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);
                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());

    }

public void Customerr(){
    reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            name.setText(user.getName());
            age.setText(user.getAge());

            if (user.getImageURL().equals("default")){
                profile_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                //change this
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if(t.onOptionsItemSelected(item))
        return true;
    return super.onOptionsItemSelected(item);
}
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){
            HashMap<String, Object> map = new HashMap<>();
            map.put("status", status);
            reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
       Log.i("OnResume","onresume");
         status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        Log.i("Onpause","onpause");

    }

    private void init(){
        getSupportActionBar().setTitle("Brampton Tiffins");

        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.descSeller, R.string.addressSeller);
        dl.addDrawerListener(t);
       // t.setDrawerIndicatorEnabled(true);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setItemIconTintList(null);
        View hView =  nv.getHeaderView(0);
         name = (TextView)hView.findViewById(R.id.tvhamburgername);
         age = (TextView)hView.findViewById(R.id.tvhamburgerage);
         profile_image = hView.findViewById(R.id.pi);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id)
                {
                    case R.id.account:
                         startActivity(new Intent(MainActivity.this, EditProfile.class));
                        break;

                    case R.id.mycart:
                        logout();
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });

    }
    private void logout(){
        builder2.setTitle("Logout");
        builder2.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout = true;
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(MainActivity.this, SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder2.create();
        alert.show();
    }
}
