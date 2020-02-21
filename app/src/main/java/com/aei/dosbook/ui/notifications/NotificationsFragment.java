package com.aei.dosbook.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.aei.dosbook.ui.adapters.FriendRequestAdapter;
import com.aei.dosbook.ui.adapters.PostAdapter;
import com.aei.dosbook.ui.feed.FeedViewModel;

import java.util.List;
import java.util.Objects;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private View view;
    private ListView notificationsListView;
    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationsListView = view.findViewById(R.id.notification_list_view);
        refresh();
        refreshLayout = view.findViewById(R.id.notification_swipe);
        refreshLayout.setOnRefreshListener(this::refresh);
        return view;
    }

    private void refresh(){
        notificationsViewModel.getRequests(this::onProfiles);
    }

    private void onProfiles(List<UserProfile> profiles){
        notificationsListView.setAdapter(new FriendRequestAdapter(Objects.requireNonNull(getContext()),
                R.layout.profile_item,profiles,onRequestApprove,onProfileClick));
        refreshLayout.setRefreshing(false);
    }

    private PostAdapter.ProfileCallback onProfileClick = profile -> {
        NavigationDataManager.setCurrentProfile(profile);
        if(view != null)
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
    };

    private FriendRequestAdapter.OnApprove onRequestApprove = profile -> {
        notificationsViewModel.approveRequest(profile);
        FriendRequestAdapter adapter = (FriendRequestAdapter)notificationsListView.getAdapter();
        MyApp.getMyUserProfile().getFriendsId().add(profile.get_id());
        adapter.remove(profile);
        MyApp.getMyUserProfile().getInFriendReq().remove(profile.get_id());
        adapter.notifyDataSetChanged();
    };
}