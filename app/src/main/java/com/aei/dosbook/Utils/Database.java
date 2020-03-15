package com.aei.dosbook.Utils;

import android.graphics.Bitmap;
import android.util.Base64;

import com.aei.dosbook.Entities.Comment;
import com.aei.dosbook.Entities.MyUserProfile;
import com.aei.dosbook.Entities.Picture;
import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.Entities.UserProfile;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {

    public interface Callback<T>{
        void onFinish(boolean err, T result);
    }

    private static final String FRIEND_REQUEST_PATH = "/api/request";
    private static final String MY_PROFILE_PATH = "/api/profile";
    private static final String USER_PROFILE_PATH = "/api/profiles";
    private static final String APPROVE_REQUEST_PATH = "/api/approve_request";
    private static final String GET_PROFILES_PATH = "/api/get_profiles";
    private static final String GET_FRIEND_REQ_PATH = "/api/get_friends_req";
    private static final String FIND_PROFILES_PATH = "/api/find_profiles";
    private static final String POSTS_PROFILE_PATH = "/api/get_profile_posts";
    private static final String POSTS_PATH = "/api/get_posts";
    private static final String SEND_POST_PATH = "/api/post";
    private static final String PICTURES_UPLOAD_PATH = "/api/upload";
    private static final String SET_PROFILE_PICTURE = "/api/set_profile";
    private static final String PICTURES_DOWNLOAD_PATH = "/api/photo";
    private static final String PICTURES_COMMENT_PATH = "/api/comment";
    private static final String PROFILE_SET_SHOW_GENDER = "/api/set_gender_filter";
    private static final String PROFILE_SET_NAME = "/api/set_profile_name";

    private static Database database;

    public static Database getInstance(){
        if(database == null)
            database = new Database();
        return database;
    }

    public void approveFriendRequest(Callback<JSONObject> callback, UserProfile profile){
        String address = MyApp.SERVER_ADDRESS + APPROVE_REQUEST_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("profile",profile.get_id());
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address, json,
                response -> callback.onFinish(false,response), error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void findProfiles(Callback<List<UserProfile>> callback, String fName, String lName){
        String address = MyApp.SERVER_ADDRESS + FIND_PROFILES_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("fName",fName);
            json.put("lName",lName);
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try {
                JSONArray profiles = response.getJSONArray("profiles");
                callback.onFinish(false, new Gson()
                        .fromJson(profiles.toString(),new TypeToken<List<UserProfile>>(){}.getType()));
            }catch (JSONException e){
                e.printStackTrace();
                callback.onFinish(false, new ArrayList<>());
            }
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void getMyUserProfiles(Callback<MyUserProfile> callback){
        String address = MyApp.SERVER_ADDRESS + MY_PROFILE_PATH;
        JSONObject json = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, address,
                json, response -> {
            try {
                JSONObject profile = json.getJSONObject("profile");
                callback.onFinish(false, new Gson()
                        .fromJson(profile.toString(),MyUserProfile.class));
            }catch (JSONException e){
                callback.onFinish(true,null);
            }
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void getUsersProfile(Callback<List<UserProfile>> callback, List<String> ids){
        String address = MyApp.SERVER_ADDRESS + GET_PROFILES_PATH;
        JSONObject json = new JSONObject();
        try {
            JSONArray arr = new JSONArray();
            ids.forEach(arr::put);
            json.put("profiles",arr);
            json.put("token",Verification.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try {
                JSONArray profiles = response.getJSONArray("profiles");
                callback.onFinish(false, new Gson()
                        .fromJson(profiles.toString(),new TypeToken<List<UserProfile>>(){}.getType()));
            }catch (JSONException e){
                e.printStackTrace();
                callback.onFinish(true,null);
            }
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void getUserProfile(Callback<List<UserProfile>> callback, String id){
        List<String> ids = new ArrayList<>();
        ids.add(id);
        getUsersProfile(callback,ids);
    }

    public void getPostsFromUser(Callback<List<Post>> callback, UserProfile profile){
        String address = MyApp.SERVER_ADDRESS + POSTS_PROFILE_PATH;
        JSONObject json = new JSONObject();
        try {
            json.put("date",new Date(System.currentTimeMillis()));
            json.put("token",Verification.getToken());
            json.put("profile",new Gson().toJson(profile));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try {
//                Log.e("POSTS",response.toString());
                JSONArray posts = response.getJSONArray("posts");
                callback.onFinish(false, new Gson()
                        .fromJson(posts.toString(),new TypeToken<List<Post>>(){}.getType()));
            }catch (JSONException e){
                e.printStackTrace();
                callback.onFinish(false, new ArrayList<>());
            }
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void getPostsUntilDate(Callback<List<Post>> callback, Date date){
        String address = MyApp.SERVER_ADDRESS + POSTS_PATH;
        JSONObject json = new JSONObject();
            try {
            json.put("date",date);
            json.put("token",Verification.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try {
//                Log.e("POSTS",response.toString());
                JSONArray posts = response.getJSONArray("posts");
                callback.onFinish(false, new Gson()
                        .fromJson(posts.toString(),new TypeToken<List<Post>>(){}.getType()));
            }catch (JSONException e){
                e.printStackTrace();
                callback.onFinish(false, new ArrayList<>());
            }
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void createPost(Callback<JSONObject> callback, Post post){
        String address = MyApp.SERVER_ADDRESS + SEND_POST_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("post",new Gson().toJson(post));
            json.put("token",Verification.getToken());
        }catch (JSONException err){
            err.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> callback.onFinish(false, json),
                error ->{ callback.onFinish(true,null);error.printStackTrace();});
        MyApp.getHttpManager().sendRequest(request);
    }

    public void setProfilePicture(Callback<JSONObject> callback, Picture pic){
        String address = MyApp.SERVER_ADDRESS + SET_PROFILE_PICTURE;
        JSONObject json = new JSONObject();
        try{
            json.put("picture",new Gson().toJson(pic));
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> callback.onFinish(true, response),
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void uploadPicture(Callback<Picture> callback, Bitmap image){
        String address = MyApp.SERVER_ADDRESS + PICTURES_UPLOAD_PATH;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        JSONObject json = new JSONObject();
        try {
            json.put("data",encodedImage);
            json.put("token",Verification.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
//                Log.e("UPLOAD","got " + response.toString());
                Picture picture = new Gson().fromJson(response.toString(),Picture.class);
                if(picture != null)
                    callback.onFinish(false, picture);
                else
                    callback.onFinish(true,null);
        },
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void sendFriendRequest(String id){
        String address = MyApp.SERVER_ADDRESS + FRIEND_REQUEST_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("token",Verification.getToken());
            json.put("profile",id);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {},
                Throwable::printStackTrace);
        MyApp.getHttpManager().sendRequest(request);
    }

    public void getFriendRequests(Callback<List<UserProfile>> callback){
        String address = MyApp.SERVER_ADDRESS + GET_FRIEND_REQ_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {
            try{
                JSONArray array = response.getJSONArray("req");

                ArrayList<UserProfile> profiles = new Gson().fromJson(array.toString(),
                        new TypeToken<List<UserProfile>>(){}.getType());
//                Log.e("DATABASE","SIZE: " + array.toString());
                callback.onFinish(false,profiles);
            }catch (Exception e){
                e.printStackTrace();
                callback.onFinish(true,null);

            }
        },
                Throwable::printStackTrace);
        MyApp.getHttpManager().sendRequest(request);
    }

    public void registerUserProfile(Callback<JSONObject> callback,MyUserProfile profile){
        String address = MyApp.SERVER_ADDRESS + USER_PROFILE_PATH;
        JSONObject json = new JSONObject();
        try {
            json.put("profile",new JSONObject(new Gson().toJson(profile)));
            json.put("token",Verification.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> callback.onFinish(false, json),
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void sendComment(Callback<JSONObject> callback, Comment comment, String postId){
        String address = MyApp.SERVER_ADDRESS + PICTURES_COMMENT_PATH;
        JSONObject json = new JSONObject();
        try{
            json.put("post_id",postId);
            json.put("comment",new JSONObject(new Gson().toJson(comment)));
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> callback.onFinish(false,json),
                error -> callback.onFinish(true,null));
        MyApp.getHttpManager().sendRequest(request);
    }

    public void updateShowOpposingGender(){
        String address = MyApp.SERVER_ADDRESS + PROFILE_SET_SHOW_GENDER;
        JSONObject json = new JSONObject();
        try{
            json.put("show",MyApp.getMyUserProfile().isShowOppositeGender());
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {},error -> {});
        MyApp.getHttpManager().sendRequest(request);
    }

    public static String getPhotoURL(String fileName){
        return MyApp.SERVER_ADDRESS + PICTURES_DOWNLOAD_PATH + "/" + fileName;
    }

    public void updateProfileName(UserProfile profile){
        String address = MyApp.SERVER_ADDRESS + PROFILE_SET_NAME;
        JSONObject json = new JSONObject();
        try{
            json.put("fname",profile.getfName());
            json.put("lname",profile.getlName());
            json.put("token",Verification.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address,
                json, response -> {},
                error -> {});
        MyApp.getHttpManager().sendRequest(request);
    }

    private Database(){}
}
