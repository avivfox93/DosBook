package com.aei.dosbook.CustomeGraphics;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import androidx.annotation.Nullable;

public class ImageButton extends AppCompatImageView {
    private boolean animation = true;
    public ImageButton(Context context) {
        super(context);
    }

    public ImageButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setAnimationEnabled(boolean animation){
        this.animation = animation;
    }
    @Override
    public boolean performClick(){
        super.performClick();
        if(animation){
            float scaleX = getScaleX();
            float scaleY = getScaleY();
            this.animate().setDuration(250).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setScaleX(scaleX);
                    setScaleY(scaleY);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).scaleXBy(0.25f).scaleYBy(0.25f).start();
        }
        return true;
    }

}
