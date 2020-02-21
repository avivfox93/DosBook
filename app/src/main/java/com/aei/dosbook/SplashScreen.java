package com.aei.dosbook;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView splashScreenImage = findViewById(R.id.splash_screen_image);
        splashScreenImage.animate().scaleX(0.95f).scaleY(0.95f).setInterpolator(new BounceInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        splashScreenImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Handler handler = new Handler();
                        Runnable r = ()-> startVerificationActivity();
                        handler.postDelayed(r,1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).setDuration(2000).start();
    }

    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startVerificationActivity(){
        Intent intent = new Intent(this,VerificationActivity.class);
        startActivity(intent);
        finish();
    }
}
