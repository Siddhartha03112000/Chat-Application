package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.Adapters.chatAdapter;
import com.example.chatme.Models.Messages;
import com.example.chatme.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChat extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(GroupChat.this,MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final ArrayList<Messages> messages= new ArrayList<>();
        final String senderId= FirebaseAuth.getInstance().getUid();
        binding.Name.setText("Friends Group");
        final chatAdapter adapter=new chatAdapter(messages,this);
        binding.chatDetailsRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatDetailsRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Messages model=snapshot1.getValue(Messages.class);
                    messages.add(model);
                }
                adapter.notifyDataSetChanged();
                binding.chatDetailsRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChat.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.typeMessage.getText().toString().isEmpty()) {
                    String message = binding.typeMessage.getText().toString();
                    final Messages model = new Messages(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    model.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    binding.typeMessage.setText("");
                    database.getReference().child("Group Chat").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }
            }
        });
    }
}