package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatme.Adapters.FragmentsAdapter;
import com.example.chatme.databinding.ActivityMainBinding;
import com.example.chatme.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        if(auth.getCurrentUser().getUid()!=null){
            database.getReference().child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!(snapshot.child("userName").exists())||!(snapshot.child("phoneNumber").exists())){
                        Intent intent=new Intent(MainActivity.this,Settings.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
                break;
            case R.id.groupChat:
                Intent intent2 = new Intent(MainActivity.this,GroupChat.class);
                startActivity(intent2);
                break;
            case R.id.logout:
                auth.signOut();
                Intent intent3 = new Intent(MainActivity.this,SignIn.class);
                startActivity(intent3 );
                break;
            case R.id.findFriends:
                Intent intent4=new Intent(MainActivity.this,FindFirends.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}