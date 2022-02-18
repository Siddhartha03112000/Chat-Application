package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.chatme.Adapters.AllUsersAdapter;
import com.example.chatme.Models.Users;
import com.example.chatme.databinding.ActivityContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity {
    ActivityContactsBinding binding;
    AllUsersAdapter adapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Users> Contactlist=new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Contacts");

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        currentUserId=auth.getCurrentUser().getUid();

        adapter=new AllUsersAdapter(Contactlist,this);
        binding.Contacts.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.Contacts.setLayoutManager(layoutManager);

        database.getReference().child("Contacts").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contactlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.getKey();
                    database.getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            users.setUserId(snapshot.getKey());
                            if (users.getUserId().equals(id)) {
                                Contactlist.add(users);

                            }
                            adapter.notifyDataSetChanged();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}