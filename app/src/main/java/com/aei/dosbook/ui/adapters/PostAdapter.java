package com.aei.dosbook.ui.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aei.dosbook.CustomeGraphics.MyImageButton;
import com.aei.dosbook.Entities.Comment;
import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.ImageActivity;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PostAdapter extends ArrayAdapter<Post> {

    private Context cntx;
    private List<Post> postList;
    private SimpleDateFormat dateFormat;
    private CommentCallback commentCallback;
    private ProfileCallback profileCallback;
    private ImageCallback imageCallback;

    public interface CommentCallback{
        void onSend(Post post, Comment comment);
    }

    public interface ProfileCallback{
        void onClick(UserProfile profile);
    }

    public interface ImageCallback{
        void onClick(String url);
    }

    public PostAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects,
                       CommentCallback commentCallback, ProfileCallback profileCallback,
                       ImageCallback imageCallback) {
        super(context, resource, objects);
        cntx = context;
        if(!MyApp.getMyUserProfile().isShowOppositeGender()) {
            postList = objects.stream()
                    .filter(post -> post.getUserProfile().getGender() == MyApp.getMyUserProfile().getGender())
                    .collect(Collectors.toList());
            clear();
            addAll(postList);
        }
        else
            postList = objects;
        dateFormat = new SimpleDateFormat("HH:mm dd/MM/YYYY",Locale.ENGLISH);
        this.commentCallback = commentCallback;
        this.profileCallback = profileCallback;
        this.imageCallback = imageCallback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(cntx).inflate(R.layout.post_item,parent,false);
        Post currentPost = postList.get(position);

        ImageView image = listItem.findViewById(R.id.post_pic);
        ImageView profileImage = listItem.findViewById(R.id.post_profile_pic);
        profileImage.setOnClickListener(e->profileCallback.onClick(currentPost.getUserProfile()));
        if(!currentPost.getPictures().isEmpty()) {
            RequestBuilder requestBuilder = MyApp.getRequestManager()
                    .load(Database.getPhotoURL(currentPost.getPictures().get(0).getUrl()));
            image.setOnClickListener(e->
                    imageCallback.onClick(Database.getPhotoURL(currentPost.getPictures().get(0).getUrl())));
            requestBuilder.into(image);
            image.setVisibility(View.VISIBLE);
        }else
            image.setVisibility(View.GONE);
        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(currentPost.getUserProfile().getProfilePic().getUrl()));

        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);

        TextView name = listItem.findViewById(R.id.post_profile_name);
        name.setText(String.format(Locale.ENGLISH,"%s %s",
                currentPost.getUserProfile().getfName(), currentPost.getUserProfile().getlName()));

        TextView postText = listItem.findViewById(R.id.post_text);
        postText.setText(currentPost.getBody());

        ListView comments = listItem.findViewById(R.id.post_comment_list);
        CommentAdapter commentAdapter = new CommentAdapter(MyApp.getContext(),R.layout.comment_item,
                currentPost.getComments());
        comments.setAdapter(commentAdapter);

        TextView commentsText = listItem.findViewById(R.id.post_num_of_comments);
        commentsText.setText(String.format(Locale.ENGLISH,"%d comments",currentPost.getComments().size()));

        commentsText.setOnClickListener(e-> comments.setVisibility(comments.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        TextView postDate = listItem.findViewById(R.id.post_date);
        postDate.setText(dateFormat.format(currentPost.getDate()));

        MyImageButton commentSendButton = listItem.findViewById(R.id.post_comment_send);
        EditText commentText = listItem.findViewById(R.id.post_send_comment_text);
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                commentSendButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        commentSendButton.setEnabled(false);
        commentSendButton.setOnClickListener(e->{
            commentCallback.onSend(currentPost, new Comment(null,commentText.getText().toString()));
            commentText.setText("");
        });
        return listItem;
    }
}
