package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.Models.Users;
import com.example.chatme.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("My Profile");

        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=binding.etUsername.getText().toString();
                String about=binding.etAbout.getText().toString();
                String phoneNumber=binding.etPhone.getText().toString();
                HashMap<String,Object> obj=new HashMap<>();
                if(TextUtils.isEmpty(username)){
                    binding.etUsername.setError("user name cannot be empty");
                    return;
                }
                storage.getReference().child("Profile Pictures").child(auth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        database.getReference().child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("profilePic").setValue(uri.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                obj.put("userName",username);
                obj.put("about",about);
                obj.put("phoneNumber",phoneNumber);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).updateChildren(obj);
                Toast.makeText(Settings.this, "Profile Updated", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(Settings.this,MainActivity.class);
                startActivity(intent);
            }
        });

        database.getReference().child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if(snapshot.hasChild("userName")){
                        binding.etUsername.setText(users.getUserName());
                }
                if(snapshot.hasChild("about")){
                    binding.etAbout.setText(users.getAbout());
                }
                if (snapshot.hasChild("profilePic")){
                    Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.user).into(binding.ProfilePic);
                }
                if(snapshot.hasChild("phoneNumber")){
                    binding.etPhone.setText(users.getPhoneNumber());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null){
            Uri file= data.getData();
            binding.ProfilePic.setImageURI(file);

            final StorageReference reference=storage.getReference().child("Profile Pictures").child(auth.getCurrentUser().getUid());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });
        }
    }
}