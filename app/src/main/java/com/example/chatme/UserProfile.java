package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.databinding.ActivityFindFirendsBinding;
import com.example.chatme.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    private String Current_state,SenderUserId,ReceiverUserId;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Profile");
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        SenderUserId=auth.getCurrentUser().getUid();
        String profile=getIntent().getStringExtra("profilePic");
        String name=getIntent().getStringExtra("name");
        ReceiverUserId=getIntent().getStringExtra("userId");
        String status=getIntent().getStringExtra("about");

        Current_state="new";
        Picasso.get().load(profile).placeholder(R.drawable.user).into(binding.image);
        binding.name.setText(name);
        binding.status.setText(status);

        manageRequests();

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfile.this,chatDetails.class);
                intent.putExtra("userId",ReceiverUserId);
                intent.putExtra("profilePic",profile);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }

    private void manageRequests() {
        database.getReference().child("Chat Requests").child(SenderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ReceiverUserId)){
                    String request_type=snapshot.child(ReceiverUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent")) {
                        Current_state = "request_sent";
                        binding.sendRequest.setText("Cancel Request");
                    }
                    else if(request_type.equals("received")){
                        Current_state="request_received";
                        binding.sendRequest.setText("Accept Request");
                        binding.declineRequest.setVisibility(View.VISIBLE);
                        binding.declineRequest.setEnabled(true);

                        binding.declineRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();

                            }
                        });
                    }
                }
                else{
                    database.getReference().child("Contacts").child(SenderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(ReceiverUserId)){
                                Current_state="friends";
                                binding.isfriend.setVisibility(View.VISIBLE);
                                binding.sendMessage.setVisibility(View.VISIBLE);
                                binding.sendRequest.setText("Remove Friend");
                            }
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

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sendRequest.setEnabled(false);
                if(Current_state.equals("new")){
                    sendChatRequest();
                }
                if(Current_state.equals("request_sent")){
                    cancelChatRequest();
                }
                if(Current_state.equals("request_received")){
                    acceptRequest();
                }
                if(Current_state.equals("friends")){
                    removeFriend();
                }

            }
        });
    }

    private void removeFriend() {
        database.getReference().child("Contacts").child(SenderUserId).child(ReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    database.getReference().child("Contacts").child(ReceiverUserId).child(SenderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Current_state="new";
                                binding.sendRequest.setEnabled(true);
                                binding.sendRequest.setText("Send Request");
                                binding.declineRequest.setVisibility(View.INVISIBLE);
                                binding.declineRequest.setEnabled(false);
                                binding.isfriend.setVisibility(View.INVISIBLE);
                                binding.sendMessage.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptRequest() {
        database.getReference().child("Contacts").child(SenderUserId).child(ReceiverUserId).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    database.getReference().child("Contacts").child(ReceiverUserId).child(SenderUserId).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                database.getReference().child("Chat Requests").child(SenderUserId).child(ReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            database.getReference().child("Chat Requests").child(ReceiverUserId).child(SenderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    binding.sendRequest.setEnabled(true);
                                                    Current_state="friends";
                                                    binding.isfriend.setVisibility(View.VISIBLE);
                                                    binding.sendMessage.setVisibility(View.VISIBLE);
                                                    binding.sendRequest.setText("Remove Friend");
                                                    binding.declineRequest.setVisibility(View.INVISIBLE);
                                                    binding.declineRequest.setEnabled(false);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    private void cancelChatRequest() {
        database.getReference().child("Chat Requests").child(SenderUserId).child(ReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    database.getReference().child("Chat Requests").child(ReceiverUserId).child(SenderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                binding.sendRequest.setEnabled(true);
                                Current_state="new";
                                binding.sendRequest.setText("Send Request");
                                binding.declineRequest.setVisibility(View.INVISIBLE);
                                binding.declineRequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendChatRequest() {
        database.getReference().child("Chat Requests").child(SenderUserId).child(ReceiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    database.getReference().child("Chat Requests").child(ReceiverUserId).child(SenderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                binding.sendRequest.setEnabled(true);
                                Current_state="request_sent";
                                binding.sendRequest.setText("Cancel Request");
                            }
                        }
                    });
                }
            }
        });
    }
}