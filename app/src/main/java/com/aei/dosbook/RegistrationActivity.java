package com.aei.dosbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aei.dosbook.Entities.MyUserProfile;
import com.aei.dosbook.Entities.Picture;
import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.Utils.Database;
import com.aei.dosbook.Utils.ImageUtils;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.Utils.Verification;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private String phoneNumber;
    private EditText fName,lName,DOB;
    private Spinner genderSelect;
    private Button profileBtn, registerBtn;
    private ImageView profileImage;
    private Bitmap profilePic;
    private Dialog loadingDialog;
    Picture picture;
    private GregorianCalendar dob = new GregorianCalendar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loadingDialog = MyApp.getLoadingView(this);
        fName = findViewById(R.id.register_fname_in);
        lName = findViewById(R.id.register_lname_in);
        DOB = findViewById(R.id.register_dob_in);
        genderSelect = findViewById(R.id.register_gender_spinner);
        profileBtn = findViewById(R.id.register_profile_btn);
        registerBtn = findViewById(R.id.register_register_btn);
        profileImage = findViewById(R.id.register_profile_img);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        SimpleDateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        genderSelect.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, UserProfile.Gender.values()));
        profileBtn.setOnClickListener(e->{pickPicture();registerBtn.setEnabled(false);});
        DOB.setClickable(true);
        DOB.setOnClickListener(e->{
            DatePickerDialog datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                int year = datePickerDialog.getDatePicker().getYear();
                int month = datePickerDialog.getDatePicker().getMonth();
                int day = datePickerDialog.getDatePicker().getDayOfMonth();
                dob.set(year,month,day);
                DOB.setText(formatter.format(dob.getTime()));
            });
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {

            });
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                dob.set(year,month,dayOfMonth);
                DOB.setText(formatter.format(dob.getTime()));
            });
            datePickerDialog.show();
        });
        registerBtn.setOnClickListener(e->{
            MyUserProfile profile = new MyUserProfile(phoneNumber,"0",
                    (UserProfile.Gender) genderSelect.getSelectedItem(),picture,
                    fName.getText().toString(),lName.getText().toString(),dob.getTime());
            profile.setPhone(MyApp.getPrefs().getString("phone",""));
            profile.setToken(Verification.getToken());
            MyApp.setMyUserProfile(profile);
            registerProfile(profile);
            registerBtn.setEnabled(false);
        });
    }

    void registerProfile(MyUserProfile profile){
        Database.getInstance().registerUserProfile((err, result) -> {
            if(err) {
                Log.e("DATABASE", "Error registering");
                registerBtn.setEnabled(true);
            }else {
                Toast.makeText(this, "Great Success!", Toast.LENGTH_SHORT).show();
                startMainActivity();
                finish();
            }
        },profile);
    }

    void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    void pickPicture(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto , 1);
    }

    void uploadProfilePicture(){
        Database.getInstance().uploadPicture((err, result) -> {
            registerBtn.setEnabled(true);
            if(err){
                Toast.makeText(this,"Your photo is not authorized!",Toast.LENGTH_LONG).show();
                return;
            }
            picture = result;
            Toast.makeText(this,result.getUrl(),Toast.LENGTH_SHORT).show();
        },profilePic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_CANCELED) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            profilePic = (Bitmap) data.getExtras().get("data");
                            profilePic = ImageUtils.setOriented(profilePic,data.getData().getPath());
                            profileImage.setImageBitmap(profilePic);
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
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                profilePic = (BitmapFactory.decodeFile(picturePath));
                                try {
                                    profilePic = ImageUtils.setOriented(profilePic, picturePath);
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                                profileImage.setImageBitmap(profilePic);
                                cursor.close();
                            }
                        }

                    }
                    break;
                    default:
                        registerBtn.setEnabled(true);
            }
            if(profilePic != null)
                uploadProfilePicture();
        }
    }
}
