package com.example.chatme.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatme.Models.Messages;
import com.example.chatme.R;
import com.example.chatme.chatDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class chatAdapter extends RecyclerView.Adapter {



    ArrayList<Messages> messagesModels;
    Context context;
    String recieverId;

    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;

    public chatAdapter(ArrayList<Messages> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }
    public chatAdapter(ArrayList<Messages> messagesModels, Context context, String recieverId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recieverId = recieverId;
    }
    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView recieverMsg,recieverTime,senderName;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName=itemView.findViewById(R.id.senderName);
            recieverMsg=itemView.findViewById(R.id.recieverText);
            recieverTime=itemView.findViewById(R.id.recieverTime);

        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.reciever_layout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messageModel=messagesModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure you want to delete this message").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        String senderRoom=FirebaseAuth.getInstance().getUid()+recieverId;
                        database.getReference().child("chats").child(senderRoom).child(messageModel.getMessageId()).setValue(null);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        });

        Date time = new Date (messageModel.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String date = dateFormat.format(time);
        if(context.getClass()==chatDetails.class) {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
                ((SenderViewHolder) holder).senderTime.setText(date);

            } else {
                ((RecieverViewHolder) holder).senderName.setVisibility(View.INVISIBLE);
                ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMessage());
                ((RecieverViewHolder) holder).recieverTime.setText(date);
            }
        }
        else{
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
                ((SenderViewHolder) holder).senderTime.setText(date);

            } else {
                ((RecieverViewHolder) holder).senderName.setText(messageModel.getName());
                ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMessage());
                ((RecieverViewHolder) holder).recieverTime.setText(date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECIEVER_VIEW_TYPE;
        }
    }


}
