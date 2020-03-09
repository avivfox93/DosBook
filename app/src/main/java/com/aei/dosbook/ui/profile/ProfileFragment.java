package com.aei.dosbook.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
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
import com.aei.dosbook.Utils.ImageUtils;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.aei.dosbook.ui.adapters.PostAdapter;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private UserProfile profile;
    private SimpleDateFormat dateFormat;
    private ListView posts;
    private View view;
    private ImageView profileImage;
    private Dialog loadingDialog;

    public ProfileFragment(){
        dateFormat = new SimpleDateFormat("dd/MM/YYYY",Locale.ENGLISH);
        profile = NavigationDataManager.getCurrentProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        profileImage = view.findViewById(R.id.profile_frag_pic);
        loadingDialog = MyApp.getLoadingView(this.getContext());
        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(profile.getProfilePic().getUrl()));
        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);
        TextView profileName = view.findViewById(R.id.profile_frag_name);
        profileName.setText(String.format(Locale.ENGLISH, "%s %s", profile.getfName(), profile.getlName()));
        TextView profileDOB = view.findViewById(R.id.profile_frag_dob);
        profileDOB.setText(dateFormat.format(profile.getDob()));
        TextView profileGender = view.findViewById(R.id.profile_frag_gender);
        profileGender.setText(toCamelCase(profile.getGender().toString()));
        posts = view.findViewById(R.id.profile_frag_postsList);
        TextView friendsList = view.findViewById(R.id.profile_frag_friendsList);
        MyImageButton profileSwapButton = view.findViewById(R.id.profile_swap_button);
        profileSwapButton.setVisibility(profile.get_id().equals(MyApp.getMyUserProfile().get_id()) ? View.VISIBLE : View.GONE);
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
            Database.getInstance().getUsersProfile((err, result) -> {
                if(err)
                    result = new ArrayList<>();
                profile.setFriends(result);
                NavigationDataManager.setCurrentSearch(profile.getFriends());
                if(view != null)
                    Navigation.findNavController(view).navigate(R.id.navigation_find);
            },profile.getFriendsId());
        });
        profileImage.setOnClickListener(e-> onImageClick.onClick(Database.getPhotoURL(profile.getProfilePic().getUrl())));
        profileSwapButton.setOnClickListener(e-> pickPicture());
        updatePosts();
        return view;
    }

    private String toCamelCase(String str){
        char[] chars = str.toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private void updatePosts(){
        mViewModel.refresh(profile,newPosts ->
                posts.setAdapter(new PostAdapter(Objects.requireNonNull(getActivity()),
                        R.layout.post_item,newPosts,onSendComment,onProfileClick,onImageClick)));
    }

    private PostAdapter.ImageCallback onImageClick = url -> MyApp.getImageDialog(getContext(),url).show();

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

    private void pickPicture(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Bitmap pic = null;
        if(resultCode != RESULT_CANCELED) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            pic = (Bitmap) data.getExtras().get("data");
                            pic = ImageUtils.setOriented(pic,data.getData().getPath());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                pic = (BitmapFactory.decodeFile(picturePath));
                                try {
                                    pic = ImageUtils.setOriented(pic, picturePath);
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                                cursor.close();
                            }
                        }
                    }
                    break;
                default: break;
            }
            if(pic != null) {
                uploadProfile(pic);
            }
        }
    }
    private void uploadProfile(Bitmap pic){
        loadingDialog.show();
        Database.getInstance().uploadPicture((err, result) -> {
            loadingDialog.dismiss();
            if(err){
                Toast.makeText(getContext(),"Your photo is not authorized!",Toast.LENGTH_LONG).show();
                return;
            }
            RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                    .load(pic);
            requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);
            profile.setProfilePic(result);
            Toast.makeText(getContext(),result.getUrl(),Toast.LENGTH_SHORT).show();
            Database.getInstance().setProfilePicture((cErr, result1) -> {
                if(!cErr)updatePosts();
                Log.e("Profile","GOT: " + (cErr ? "True" : "False"));
                },result);
        },pic);
    }
}
