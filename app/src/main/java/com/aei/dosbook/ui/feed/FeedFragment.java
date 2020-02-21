package com.aei.dosbook.ui.feed;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aei.dosbook.CustomeGraphics.ImageButton;
import com.aei.dosbook.Entities.Picture;
import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.ImageUtils;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.ui.NavigationDataManager;
import com.aei.dosbook.ui.adapters.PostAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment {

    private FeedViewModel feedViewModel;
    private Context cntx;
    private PostAdapter postAdapter;
    private ListView postListView;
    private Button postButton;
    private ImageButton pictureButton;
    private Dialog loadingDialog;
    private EditText postText;
    private List<Picture> postPictures = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private View view;

    public FeedFragment(){
        this.cntx = MyApp.getContext();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        view = inflater.inflate(R.layout.fragment_feed, container, false);
        postListView = view.findViewById(R.id.feed_post_list);
        postButton = view.findViewById(R.id.feed_post_btn);
        pictureButton = view.findViewById(R.id.feed_picture_btn);
        loadingDialog = MyApp.getLoadingView(this.getContext());
        postText = view.findViewById(R.id.feed_post_body);
        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        postButton.setEnabled(false);
        postButton.setOnClickListener(e->{
            loadingDialog.show();
            Post p = new Post(MyApp.getMyUserProfile(),postText.getText().toString());
            p.setPictures(postPictures);
            Database.getInstance().createPost((err, result) -> {
                refresh();
                postPictures.clear();
                loadingDialog.dismiss();
                if(err)
                    Toast.makeText(this.getContext(), "Unexpected Error!", Toast.LENGTH_SHORT).show();
                else
                    postText.getText().clear();
            },p);
        });
        pictureButton.setAnimationEnabled(true);
        pictureButton.setOnClickListener(e-> pickPicture());
        refreshLayout = view.findViewById(R.id.feed_swipe_layout);
        refreshLayout.setOnRefreshListener(this::updateFeed);
        return view;
    }

    private void updateFeed(){
        Database.getInstance().getPostsUntilDate((err, result) -> {
            refreshLayout.setRefreshing(false);
            if(err) {
                Log.e("posts","ERRORRRR");
                return;
            }
            postAdapter = new PostAdapter(cntx,R.layout.post_item,result,onSendComment, onProfileClick);
            postListView.setAdapter(postAdapter);
        },new Date(System.currentTimeMillis()));
    }

    private PostAdapter.CommentCallback onSendComment = (post,comment)->{
        comment.setUserProfile(MyApp.getMyUserProfile());
            Database.getInstance().sendComment((err, result) -> {
                if(err)
                    return;
                refresh();
            },comment,post.get_id());
        };

    private PostAdapter.ProfileCallback onProfileClick = profile ->{
        NavigationDataManager.setCurrentProfile(profile);
        if(view != null)
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
    };

    private void refresh(){
        Handler handler = new Handler();
        handler.postDelayed(this::updateFeed,1500);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateFeed();
    }

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
            if(pic != null)
                uploadProfile(pic);
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
            postPictures.add(result);
            Toast.makeText(getContext(),result.getUrl(),Toast.LENGTH_SHORT).show();
        },pic);
    }
}