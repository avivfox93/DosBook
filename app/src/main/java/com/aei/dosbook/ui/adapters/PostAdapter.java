package com.aei.dosbook.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aei.dosbook.CustomeGraphics.MyImageButton;
import com.aei.dosbook.Entities.Comment;
import com.aei.dosbook.Entities.Picture;
import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.Entities.UserProfile;
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

    public static class ViewHolder{
        ImageView image;
        TextView quantity,name,postText,postDate,commentsText;
        ImageView profileImage;
        ListView comments;
        MyImageButton commentSendButton;
        EditText commentText;
    }

    public interface CommentCallback{
        void onSend(Post post, Comment comment);
    }

    public interface ProfileCallback{
        void onClick(UserProfile profile);
    }

    public interface ImageCallback{
        void onClick(List<Picture> urls);
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
        ViewHolder viewHolder;
        Post currentPost = postList.get(position);
        if(listItem == null) {
            listItem = LayoutInflater.from(cntx).inflate(R.layout.post_item, parent, false);
            viewHolder = new ViewHolder();
            listItem.setTag(viewHolder);
            viewHolder.image = listItem.findViewById(R.id.post_pic);
            viewHolder.quantity = listItem.findViewById(R.id.post_pic_quantity);
            viewHolder.profileImage = listItem.findViewById(R.id.post_profile_pic);
            viewHolder.name = listItem.findViewById(R.id.post_profile_name);
            viewHolder.postText = listItem.findViewById(R.id.post_text);
            viewHolder.comments = listItem.findViewById(R.id.post_comment_list);
            viewHolder.commentsText = listItem.findViewById(R.id.post_num_of_comments);
            viewHolder.postDate = listItem.findViewById(R.id.post_date);
            viewHolder.commentSendButton = listItem.findViewById(R.id.post_comment_send);
            viewHolder.commentText = listItem.findViewById(R.id.post_send_comment_text);
        }else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.quantity.setVisibility(View.GONE);
        viewHolder.profileImage.setOnClickListener(e->profileCallback.onClick(currentPost.getUserProfile()));
        if(!currentPost.getPictures().isEmpty()) {
            viewHolder.image.setVisibility(View.VISIBLE);
            RequestBuilder requestBuilder = MyApp.getRequestManager()
                    .load(Database.getPhotoURL(currentPost.getPictures().get(0).getUrl()))
                    .placeholder(R.drawable.ic_image_place_holder);

            viewHolder.image.setOnClickListener(e->
                    imageCallback.onClick(currentPost.getPictures()));
            requestBuilder.into(viewHolder.image);
            if(currentPost.getPictures().size() > 1){
                viewHolder.quantity.setVisibility(View.VISIBLE);
                viewHolder.quantity.setText(String.format(Locale.ENGLISH,"+%d", currentPost.getPictures().size() - 1));
            }
        }else
            viewHolder.image.setVisibility(View.GONE);
        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(currentPost.getUserProfile().getProfilePic().getUrl()));

        requestBuilder.apply(RequestOptions.circleCropTransform()).into(viewHolder.profileImage);

        viewHolder.name.setText(String.format(Locale.ENGLISH,"%s %s",
                currentPost.getUserProfile().getfName(), currentPost.getUserProfile().getlName()));

        viewHolder.postText.setText(currentPost.getBody());

        CommentAdapter commentAdapter = new CommentAdapter(MyApp.getContext(),R.layout.comment_item,
                currentPost.getComments());
        viewHolder.comments.setAdapter(commentAdapter);

        viewHolder.commentsText.setText(String.format(Locale.ENGLISH,"%d comments",currentPost.getComments().size()));

        viewHolder.commentsText.setOnClickListener(e-> viewHolder.comments.setVisibility(viewHolder.comments.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

        viewHolder.postDate.setText(dateFormat.format(currentPost.getDate()));

        viewHolder.commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewHolder.commentSendButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewHolder.commentSendButton.setEnabled(false);
        viewHolder.commentSendButton.setOnClickListener(e->{
            commentCallback.onSend(currentPost, new Comment(null,viewHolder.commentText.getText().toString()));
            viewHolder.commentText.setText("");
        });
        return listItem;
    }
}
