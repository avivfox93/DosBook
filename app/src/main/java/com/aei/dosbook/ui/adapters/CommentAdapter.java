package com.aei.dosbook.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aei.dosbook.Entities.Comment;
import com.aei.dosbook.Entities.Post;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private Context cntx;
    private List<Comment> commentList;
    private SimpleDateFormat dateFormat;


    public CommentAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        cntx = context;
        commentList = objects;
        dateFormat = new SimpleDateFormat("HH:mm dd/MM/YYYY",Locale.ENGLISH);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(cntx).inflate(R.layout.comment_item,parent,false);

        Comment currentComment = commentList.get(position);
        if(!MyApp.getMyUserProfile().isShowOppositeGender() && currentComment.getUserProfile().getGender() != MyApp.getMyUserProfile().getGender()){
            listItem.setVisibility(View.GONE);
            return listItem;
        }
        ImageView profileImage = listItem.findViewById(R.id.comment_profile_pic);

        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(currentComment.getUserProfile().getProfilePic().getUrl()));
        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);

        TextView name = listItem.findViewById(R.id.comment_profile_name);
        name.setText(String.format(Locale.ENGLISH,"%s %s",
                currentComment.getUserProfile().getfName(), currentComment.getUserProfile().getlName()));

        TextView postText = listItem.findViewById(R.id.comment_text);
        postText.setText(currentComment.getBody());

        TextView commentDate = listItem.findViewById(R.id.comment_date);
        commentDate.setText(dateFormat.format(currentComment.getDate()));

        return listItem;
    }
}
