package com.aei.dosbook.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.R;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.MyApp;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FriendRequestAdapter extends ArrayAdapter<UserProfile> {

    private Context cntx;
    private List<UserProfile> profileList;
    private OnApprove onApprove;
    private PostAdapter.ProfileCallback profileCallback;

    public interface OnApprove{
        void onClick(UserProfile profile);
    }

    public FriendRequestAdapter(@NonNull Context context, int resource,
                                @NonNull List<UserProfile> objects, OnApprove onApprove, PostAdapter.ProfileCallback profileCallback) {
        super(context, resource, objects);
        cntx = context;
        profileList = objects;
        this.onApprove = onApprove;
        this.profileCallback = profileCallback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(cntx).inflate(R.layout.request_item,parent,false);

        UserProfile currentProfile = profileList.get(position);

        ImageView profileImage = listItem.findViewById(R.id.profile_item_pic);

        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(currentProfile.getProfilePic().getUrl()));
        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);
        Button approve = listItem.findViewById(R.id.request_confirm);
        approve.setOnClickListener(e->{
            onApprove.onClick(currentProfile);
        });
        TextView name = listItem.findViewById(R.id.profile_item_name);
        name.setText(String.format(Locale.ENGLISH,"%s %s",
                currentProfile.getfName(), currentProfile.getlName()));
        listItem.setOnClickListener(e-> profileCallback.onClick(currentProfile));
        return listItem;
    }
}

