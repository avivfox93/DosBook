package com.aei.dosbook.ui.find;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.aei.dosbook.ui.adapters.ProfileAdapter;

public class FindFragment extends Fragment {

    private FindViewModel findViewModel;
    private ListView profiles;
    private View view;
    private Dialog loadingDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        findViewModel = new ViewModelProvider(this).get(FindViewModel.class);
        view = inflater.inflate(R.layout.fragment_find, container, false);
        profiles = view.findViewById(R.id.find_profile_list);
        EditText searchText = view.findViewById(R.id.find_text_in);
        Button findBtn = view.findViewById(R.id.find_search_btn);
        findBtn.setEnabled(false);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findBtn.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadingDialog =  MyApp.getLoadingView(getContext());
        findBtn.setOnClickListener(e->{
            loadingDialog.show();
            findViewModel.search(searchText.getText().toString(),onProfilesCallback);
        });
        if(NavigationDataManager.getCurrentSearch() != null){
//            Log.e("Find", "found: " + NavigationDataManager.getCurrentSearch().size());
            onProfilesCallback.onResult(NavigationDataManager.getCurrentSearch());
            NavigationDataManager.clearCurrentSearch();
        }
        return view;
    }

    private ProfileAdapter.ProfileCallback onProfileClicked = profile->{
        NavigationDataManager.setCurrentProfile(profile);
        if(view != null)
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
    };

    private FindViewModel.OnProfilesCallback onProfilesCallback = newProfiles->{
        loadingDialog.dismiss();
        Context cntx = getContext();
        if(cntx != null)
            profiles.setAdapter(new ProfileAdapter(getContext(),R.layout.profile_item,
                    newProfiles,onProfileClicked));
    };
}