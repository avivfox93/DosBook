package com.aei.dosbook.Entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyUserProfile extends UserProfile {

    private String phone;
    private String uid;
    private Settings settings = new Settings();
    private boolean showOppositeGender = true;


    private String token;

    public MyUserProfile(String phoneNumber, String _id, Gender gender, Picture profilePic, String fName, String lName, Date dob) {
        super(_id, gender, profilePic, fName, lName, dob);
        this.phone = phoneNumber;
    }


    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getUuid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }

    public void setProfilePic(Picture profilePic){
        this.profilePic = profilePic;
    }

    public void setFName(String fName){
        this.fName = fName;
    }

    public void setLName(String lName){
        this.lName = lName;
    }

    public void setDOB(Date dob){
        this.dob = dob;
    }

    public Settings getSettings(){return settings;}

    public boolean isShowOppositeGender() {
        return showOppositeGender;
    }

    public void setShowOppositeGender(boolean showOppositeGender) {
        this.showOppositeGender = showOppositeGender;
    }

    public class Settings{
        private String _id;
        private boolean showDifferentGender = true;
        private boolean receiveNotifications = false;
    }
}

