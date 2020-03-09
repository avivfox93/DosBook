package com.aei.dosbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.aei.dosbook.CustomeGraphics.MyImageButton;
import com.aei.dosbook.Utils.MyApp;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView image = findViewById(R.id.activity_image_image);
        MyApp.getRequestManager().load(getIntent().getStringExtra("url")).into(image);
        MyImageButton close = findViewById(R.id.activity_image_close);
        close.setOnClickListener(e->finish());
    }
}
