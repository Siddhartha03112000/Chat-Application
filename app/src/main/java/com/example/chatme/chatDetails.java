package com.example.chatme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatme.Adapters.chatAdapter;
import com.example.chatme.Models.Messages;
import com.example.chatme.Models.Users;
import com.example.chatme.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class chatDetails extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Users user=new Users();
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        final String senderId=auth.getUid();
        String recieverId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("name");
        String profilePic=getIntent().getStringExtra("profilePic");
        String about=getIntent().getStringExtra("about");
        binding.Name.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.user).into(binding.profileImage);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chatDetails.this,MainActivity.class);
                startActivity(intent);
            }
        });

        binding.Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chatDetails.this,UserProfile.class);
                intent.putExtra("userId",recieverId);
                intent.putExtra("profilePic",profilePic);
                intent.putExtra("name",userName);
                intent.putExtra("about",about);
                startActivity(intent);
            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chatDetails.this,UserProfile.class);
                intent.putExtra("userId",recieverId);
                intent.putExtra("profilePic",profilePic);
                intent.putExtra("name",userName);
                intent.putExtra("about",about);
                startActivity(intent);
            }
        });
        final ArrayList<Messages> messageModels=new ArrayList<>();
        final chatAdapter chatadapter=new chatAdapter(messageModels,this,recieverId);
        binding.chatDetailsRecyclerView.setAdapter(chatadapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatDetailsRecyclerView.setLayoutManager(layoutManager);


        final String senderRoom= senderId+recieverId;
        final String recieverRoom= recieverId+senderId;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();

                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Messages model=snapshot1.getValue(Messages.class);
                    model.setMessageId(snapshot1.getKey());
                    messageModels.add(model);
                }
                chatadapter.notifyDataSetChanged();
                binding.chatDetailsRecyclerView.scrollToPosition(chatadapter.getItemCount() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatDetails.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(binding.typeMessage.getText().toString().isEmpty())) {
                    String message = binding.typeMessage.getText().toString();
                    final Messages model = new Messages(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    model.setName(auth.getCurrentUser().getDisplayName());
                    binding.typeMessage.setText("");
                    database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference().child("chats").child(recieverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    });

                }
            }
        });
    }
}