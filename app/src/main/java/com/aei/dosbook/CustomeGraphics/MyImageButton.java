package com.aei.dosbook.CustomeGraphics;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatImageButton;

import androidx.annotation.Nullable;

public class MyImageButton extends AppCompatImageButton{
    private boolean animation = true;
    public MyImageButton(Context context) {
        super(context);
        setBackgroundResource(getBackgroundRes());
    }

    public MyImageButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(getBackgroundRes());
    }

    public MyImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(getBackgroundRes());
    }
    int getBackgroundRes(){
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
        return value.resourceId;
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
