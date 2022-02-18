package com.example.chatme.Models;

public class Users {
    String profilePic,userName,mail,password,userId,lastMessage,about,phoneNumber;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }




    //empty constructor for firebase
    public Users(){}

    public Users(String phoneNumber, String userName, String userId,String profilePic) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    //signup constructor
    public Users(String userName,String mail,String password){
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
