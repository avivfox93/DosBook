package com.aei.dosbook.CustomeGraphics;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

public class MyImageButton extends AppCompatImageButton{
    private boolean animation = true;
    private boolean backgroundSet = false;
    public MyImageButton(Context context) {
        super(context);
        if(!backgroundSet)setBackgroundResource(getBackgroundRes());
    }

    public MyImageButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(!backgroundSet)setBackgroundResource(getBackgroundRes());
    }

    public MyImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!backgroundSet)setBackgroundResource(getBackgroundRes());
    }
    private int getBackgroundRes(){
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);
        backgroundSet = true;
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
