package com.aei.dosbook.ui.find;

import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.Utils.Database;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public interface OnProfilesCallback{
        void onResult(List<UserProfile> profiles);
    }

    public void search(String text, OnProfilesCallback callback){
        String[] data = text.split(" ");
        String fName = data[0];
        String lName = data.length > 1 ? data[1] : ".*";
        Database.getInstance().findProfiles((err, result) -> {
            if(err)
                callback.onResult(new ArrayList<>());
            else
                callback.onResult(result);
        },fName,lName);
    }

    public FindViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}