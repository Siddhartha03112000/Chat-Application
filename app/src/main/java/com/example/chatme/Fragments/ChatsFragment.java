package com.example.chatme.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatme.Adapters.UsersAdapter;
import com.example.chatme.Contacts;
import com.example.chatme.MainActivity;
import com.example.chatme.Models.Users;
import com.example.chatme.R;
import com.example.chatme.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    UsersAdapter adapter;
    ArrayList<Users> list=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    String currentUserId;
    public ChatsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentChatsBinding.inflate(inflater, container, false);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();

        adapter=new UsersAdapter(list,getContext());
        binding.chatsRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.chatsRecyclerView.setLayoutManager(layoutManager);

        binding.allContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),Contacts.class);
                startActivity(intent);
            }
        });

        database.getReference().child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().startsWith(currentUserId)) {
                        String id = dataSnapshot.getKey().replace(currentUserId, "");
                        database.getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                users.setUserId(snapshot.getKey());
                                if (users.getUserId().equals(id)) {
                                    list.add(users);
                                }
                                adapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }


}