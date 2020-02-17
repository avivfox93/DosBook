package com.aei.dosbook.ui;

import com.aei.dosbook.Entities.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class NavigationDataManager {

    private static UserProfile currentProfile = null;
    private static List<UserProfile> currentSearch = null;

    public static UserProfile getCurrentProfile(){
        return currentProfile;
    }

    public static void setCurrentProfile(UserProfile profile){
        currentProfile = profile;
    }

    public static List<UserProfile> getCurrentSearch() {
        return currentSearch;
    }

    public static void setCurrentSearch(List<UserProfile> currentSearch) {
        NavigationDataManager.currentSearch = currentSearch;
    }

    public static void clearCurrentSearch(){
        currentSearch = null;
    }
}
