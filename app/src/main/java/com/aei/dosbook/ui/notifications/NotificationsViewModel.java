package com.aei.dosbook.ui.notifications;

import android.util.Log;

import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    interface OnRequestsCallback{
        void onFinish(List<UserProfile> profiles);
    }

    public NotificationsViewModel() {}

    public void getRequests(OnRequestsCallback callback){
        Database.getInstance().getUsersProfile((err, result) -> {
            Log.e("WALLAKKKK","" + err);
            callback.onFinish(err ? new ArrayList<>() : result);
        },MyApp.getMyUserProfile().getInFriendReq());
    }

    public void approveRequest(UserProfile profile){
        Database.getInstance().approveFriendRequest((err, result) -> {},profile);
    }
}