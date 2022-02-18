package com.example.chatme.Models;

public class Messages {
    String uId,message,messageId,name;
    Long timestamp;

    public Messages(String uId, String message, Long timestamp,String name) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.name=name;
    }

    public Messages(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }
    public Messages(){}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
