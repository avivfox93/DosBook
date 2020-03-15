package com.aei.dosbook.Entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfile {
    public enum Gender{
        MALE,FEMALE;
        public static List<Gender> getList(){
            List<Gender> list = new ArrayList<>();
            list.add(MALE);list.add(FEMALE);
            return list;
        }
    }
    private String _id;
    protected Gender gender;
    protected Picture profilePic;



    protected String fName, lName;
    protected Date dob;
    protected List<UserProfile> friends;
    protected List<String> friendsId, inFriendReq, outFriendReq;

    public UserProfile(String _id, Gender gender, Picture profilePic, String fName, String lName, Date dob) {
        this._id = _id;
        this.gender = gender;
        this.profilePic = profilePic;
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        friends = new ArrayList<>();
        friendsId = new ArrayList<>();
        inFriendReq = new ArrayList<>();
        outFriendReq = new ArrayList<>();
    }

    public Gender getGender() {
        return gender;
    }

    public Picture getProfilePic() {
        return profilePic;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public Date getDob() {
        return dob;
    }

    public List<UserProfile> getFriends() {
        return friends;
    }

    public List<String> getFriendsId() {
        return friendsId;
    }

    public void setFriends(List<UserProfile> friends) {
        this.friends = friends;
    }

    public String get_id(){
        return _id;
    }

    public List<String> getInFriendReq() {
        return inFriendReq;
    }

    public List<String> getOutFriendReq() {
        return outFriendReq;
    }

    public void setProfilePic(Picture profilePic) {
        this.profilePic = profilePic;
    }
    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    //    public String toJson(){
//        List<UserProfile> temp = new ArrayList<>();
//        friends.forEach(f->{
//            friendsId.add(f.get_id());
//            temp.add(f);
//        });
//        friends.clear();
//        Gson gson = new Gson();
//        String result = gson.toJson(this);
//        friends.addAll(temp);
//        return result;
//    }

}
