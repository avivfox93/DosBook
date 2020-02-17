package com.aei.dosbook.Entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String _id;
    private UserProfile userProfile;
    private String body;
    private List<Picture> pictures = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private Date date;

    public Post(UserProfile userProfile, String body, List<Picture> pictures, List<Comment> comments) {
        this.userProfile = userProfile;
        this.body = body;
        this.pictures = pictures;
        this.comments = comments;
    }

    public Post(UserProfile userProfile, String body) {
        this.userProfile = userProfile;
        this.body = body;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
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

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
