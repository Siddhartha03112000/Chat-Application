package com.example.chatme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatme.Adapters.AllUsersAdapter;
import com.example.chatme.Adapters.RequestsAdapter;
import com.example.chatme.Models.Users;
import com.example.chatme.R;
import com.example.chatme.databinding.FragmentRequestsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {
    FragmentRequestsBinding binding;
    RequestsAdapter requestsAdapter;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<Users> RequestList=new ArrayList<>();
    private String currentUserId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentRequestsBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        currentUserId=auth.getCurrentUser().getUid();

        requestsAdapter=new RequestsAdapter(RequestList,getContext());
        binding.requests.setAdapter(requestsAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.requests.setLayoutManager(layoutManager);

        database.getReference().child("Chat Requests").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("request_type").getValue().equals("received")) {
                        String id = dataSnapshot.getKey();
                        database.getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Users users = snapshot.getValue(Users.class);
                                users.setUserId(snapshot.getKey());
                                if (users.getUserId().equals(id)) {
                                    RequestList.add(users);
                                }
                                requestsAdapter.notifyDataSetChanged();
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