package com.aei.dosbook.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aei.dosbook.CustomeGraphics.MyImageButton;
import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.aei.dosbook.ui.adapters.PostAdapter;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private UserProfile profile;
    private SimpleDateFormat dateFormat;
    private ListView posts;
    private View view;

    public ProfileFragment(){
        dateFormat = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        profile = NavigationDataManager.getCurrentProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        ImageView profileImage = view.findViewById(R.id.profile_frag_pic);
        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(profile.getProfilePic().getUrl()));
        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);
        TextView profileName = view.findViewById(R.id.profile_frag_name);
        profileName.setText(String.format(Locale.ENGLISH, "%s %s", profile.getfName(), profile.getlName()));
        TextView profileDOB = view.findViewById(R.id.profile_frag_dob);
        profileDOB.setText(dateFormat.format(profile.getDob()));
        TextView profileGender = view.findViewById(R.id.profile_frag_gender);
        profileGender.setText(profile.getGender().toString());
        posts = view.findViewById(R.id.profile_frag_postsList);
        TextView friendsList = view.findViewById(R.id.profile_frag_friendsList);
        MyImageButton addFriend = view.findViewById(R.id.profile_frag_add_friend);
        if(profile.get_id().equals(MyApp.getMyUserProfile().get_id()) ||
                MyApp.getMyUserProfile().getFriendsId().contains(profile.get_id()) ||
                MyApp.getMyUserProfile().getOutFriendReq().contains(profile.get_id())) {
            addFriend.setImageResource(R.drawable.ic_friend);
            addFriend.setEnabled(false);
        }
        else
            addFriend.setImageResource(R.drawable.ic_add_friend);
        addFriend.setOnClickListener(e->{
            addFriend.setImageResource(R.drawable.ic_friend);
            Database.getInstance().sendFriendRequest(profile.get_id());
            MyApp.getMyUserProfile().getOutFriendReq().add(profile.get_id());
        });
        friendsList.setText(String.format(Locale.ENGLISH,"%d Friends",profile.getFriendsId().size()));
        friendsList.setOnClickListener(e->{
            Toast.makeText(getContext(),"He got " + profile.getFriendsId().size() + " friends",
                    Toast.LENGTH_SHORT).show();
            Database.getInstance().getUsersProfile((err, result) -> {
                if(err)
                    result = new ArrayList<>();
                profile.setFriends(result);
                NavigationDataManager.setCurrentSearch(profile.getFriends());
                if(view != null)
                    Navigation.findNavController(view).navigate(R.id.navigation_find);
            },profile.getFriendsId());
        });
        updatePosts();
        return view;
    }

    private void updatePosts(){
        mViewModel.refresh(profile,newPosts ->
                posts.setAdapter(new PostAdapter(Objects.requireNonNull(getActivity()),
                        R.layout.post_item,newPosts,onSendComment,onProfileClick)));
    }

    private PostAdapter.ProfileCallback onProfileClick = profile ->{
        NavigationDataManager.setCurrentProfile(profile);
        if(view != null)
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
    };

    private PostAdapter.CommentCallback onSendComment = (post,comment)->{
        comment.setUserProfile(MyApp.getMyUserProfile());
        Database.getInstance().sendComment((err, result) -> {
            if(err)
                return;
            updatePosts();
        },comment,post.get_id());
    };
}
