package com.aei.dosbook.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class ProfileAdapter extends ArrayAdapter<UserProfile> {

    private Context cntx;
    private List<UserProfile> profileList;
    private ProfileCallback callback;

    public interface ProfileCallback{
        void onClick(UserProfile profile);
    }

    public ProfileAdapter(@NonNull Context context, int resource, @NonNull List<UserProfile> objects, ProfileCallback callback) {
        super(context, resource, objects);
        cntx = context;
        profileList = objects;
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(cntx).inflate(R.layout.profile_item,parent,false);

        UserProfile currentProfile = profileList.get(position);

        ImageView profileImage = listItem.findViewById(R.id.profile_item_pic);

        RequestBuilder<Drawable> requestBuilder = MyApp.getRequestManager()
                .load(Database.getPhotoURL(currentProfile.getProfilePic().getUrl()));
        requestBuilder.apply(RequestOptions.circleCropTransform()).into(profileImage);

        TextView name = listItem.findViewById(R.id.profile_item_name);
        name.setText(String.format(Locale.ENGLISH,"%s %s",
                currentProfile.getfName(), currentProfile.getlName()));
        listItem.setOnClickListener(e->callback.onClick(currentProfile));
        return listItem;
    }
}
