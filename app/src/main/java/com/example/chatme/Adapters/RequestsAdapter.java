package com.example.chatme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatme.Models.Users;
import com.example.chatme.R;
import com.example.chatme.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Users> list;
    Context context;
    String currentUserId;

    public RequestsAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requestusers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, int position) {
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        Users users=list.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.user).into(holder.image);
        holder.userName.setText(users.getUserName());
        holder.about.setText("Wants to be friends with you");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfile.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("profilePic",users.getProfilePic());
                intent.putExtra("name",users.getUserName());
                intent.putExtra("about",users.getAbout());

                context.startActivity(intent);

            }
        });
        holder.Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.Accept.setEnabled(false);
                database.getReference().child("Contacts").child(currentUserId).child(users.getUserId()).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            database.getReference().child("Contacts").child(users.getUserId()).child(currentUserId).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        database.getReference().child("Chat Requests").child(currentUserId).child(users.getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    database.getReference().child("Chat Requests").child(users.getUserId()).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                holder.Accept.setText("Friends");
                                                                holder.Decline.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(context, "New Contact Added", Toast.LENGTH_SHORT).show();
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
                    }
                });
            }
        });

        holder.Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Chat Requests").child(currentUserId).child(users.getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            database.getReference().child("Chat Requests").child(users.getUserId()).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Request Declined", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName,about;
        Button Accept,Decline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            userName=itemView.findViewById(R.id.userName);
            about=itemView.findViewById(R.id.about);
            Accept=itemView.findViewById(R.id.Accept);
            Decline=itemView.findViewById(R.id.Decline);
        }
    }
}
