package com.aei.dosbook.ui.profile;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aei.dosbook.CustomeGraphics.MyImageButton;
import com.aei.dosbook.R;

import androidx.annotation.NonNull;

public class ProfileSettingsDialog extends Dialog {
    public interface ProfileDialogCallback{
        boolean onOK(String fName, String lName);
    }
    private EditText fName,lName;
    public ProfileSettingsDialog(@NonNull Context context, ProfileDialogCallback callback) {
        super(context);
        View view = View.inflate(context,R.layout.profile_settings_dialog,null);
        setContentView(view);

        fName = view.findViewById(R.id.profile_dialog_fname);
        lName = view.findViewById(R.id.profile_dialog_lname);

        MyImageButton okButton = view.findViewById(R.id.profile_dialog_ok);
        MyImageButton cancelButton = view.findViewById(R.id.profile_dialog_cancel);

        cancelButton.setOnClickListener(e-> dismiss());
        okButton.setOnClickListener(e->{
            if(callback.onOK(fName.getText().toString(),lName.getText().toString()))
                dismiss();
        });
    }
}
