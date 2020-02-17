package com.aei.dosbook.Entities;

import java.util.Date;

public class Comment {
    private String _id;
    private UserProfile userProfile;
    private String body;
    private Date date;

    public Comment(String _id, UserProfile userProfile, String body, Date date) {
        this._id = _id;
        this.userProfile = userProfile;
        this.body = body;
        this.date = date;
    }

    public Comment(String _id, String body) {
        this._id = _id;
        this.body = body;
        this.date = new Date(System.currentTimeMillis());
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
