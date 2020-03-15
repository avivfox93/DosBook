package com.aei.dosbook.Utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aei.dosbook.Entities.MyUserProfile;
import com.aei.dosbook.Entities.Picture;
import com.aei.dosbook.R;
import com.aei.dosbook.ui.OnSwipeTouchListener;
import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyApp extends Application {

    public static final String SERVER_ADDRESS = "https://dosbook.tk:3000";

    private static HttpManager httpManager;
    private static MySharedPrefs mySharedPrefs;
    private static MyVibrator myVibrator;
    private static SimpleDateFormat formatter;
    private static Context context;
    private static MyUserProfile myUserProfile;
    private static MyExecutor myExecutor;
    private static RequestManager requestManager;
    private static Context cntx;
    private static String uid;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(this);
        mySharedPrefs = new MySharedPrefs(this);
        myVibrator = new MyVibrator(this);
        formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        httpManager =  HttpManager.getInstance(this);
        context = this;
        myExecutor = new MyExecutor();
        requestManager = Glide.with(this);
        cntx = this;
    }

    public static void setUid(String uid){
        MyApp.uid = uid;
    }

    public static String getUid(){
        return uid;
    }

    public static Dialog getLoadingView(Context activity){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_dialog_layout);
        return dialog;
    }

    public static Context getContext(){
        return cntx;
    }

    public static RequestManager getRequestManager() {
        return requestManager;
    }

    public static MyExecutor getMyExecutor() {
        return myExecutor;
    }

    public static MyUserProfile getMyUserProfile(){return myUserProfile;}

    public static void setMyUserProfile(MyUserProfile profile){myUserProfile = profile;}

    public static HttpManager getHttpManager(){return httpManager;}

    public static MySharedPrefs getPrefs(){
        return mySharedPrefs;
    }

    public static MyVibrator getVibrator(){
        return myVibrator;
    }

    public static SimpleDateFormat getDateFormatter(){
        return formatter;
    }

    public static Dialog getImageDialog(Context cntx, String url){
        Dialog settingsDialog = new Dialog(cntx, android.R.style.Theme_Light);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View inf = LayoutInflater.from(cntx).inflate(R.layout.activity_image,null);
        ImageView image = inf.findViewById(R.id.activity_image_image);
        getRequestManager().load(url).into(image);
        inf.findViewById(R.id.activity_image_close).setOnClickListener(l->settingsDialog.dismiss());
        settingsDialog.setContentView(inf);
        return settingsDialog;
    }

    public static Dialog getImageDialog(Context cntx, List<Picture> url){
        Dialog settingsDialog = new Dialog(cntx, android.R.style.Theme_Light);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View inf = LayoutInflater.from(cntx).inflate(R.layout.activity_image,null);

        ImageView image = inf.findViewById(R.id.activity_image_image);
        getRequestManager().load(Database.getPhotoURL((url.get(0).getUrl()))).into(image);
        inf.setOnTouchListener(new OnSwipeTouchListener(cntx){
            int index = 0;
            public void onSwipeRight() {
                if(index > 0)
                    index--;
                else
                    return;
                getRequestManager().load(Database.getPhotoURL((url.get(index).getUrl()))).into(image);
            }
            public void onSwipeLeft() {
                if(index < url.size()-1)
                    index++;
                else
                    return;
                getRequestManager().load(Database.getPhotoURL((url.get(index).getUrl()))).into(image);
            }
            public void onSwipeBottom(){
                settingsDialog.dismiss();
            }
            public void onSwipeTop(){
                settingsDialog.dismiss();
            }
        });
        inf.findViewById(R.id.activity_image_close).setOnClickListener(l->settingsDialog.dismiss());
        settingsDialog.setContentView(inf);
        return settingsDialog;
    }
}

