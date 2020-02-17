package com.aei.dosbook.ui.profile;

import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.Utils.Database;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ProfileViewModel extends ViewModel {

    public interface OnPostsCallback{
        void onFinish(List<Post> posts);
    }

    public void refresh(UserProfile profile, OnPostsCallback callback){
        Database.getInstance().getPostsFromUser((err, result) -> {
            if(err)
                return;
            callback.onFinish(result);
        },profile);
    }
}


