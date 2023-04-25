package com.example.logify.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation {
    private final int mTargetHeight;
    private final View mView;
    private final boolean mIsExpand;

    public ExpandAnimation(View view, int targetHeight, boolean isExpand) {
        mView = view;
        mTargetHeight = targetHeight;
        mIsExpand = isExpand;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = mIsExpand ? (int) (mTargetHeight * interpolatedTime) : (int) (mTargetHeight * (1 - interpolatedTime));
        mView.getLayoutParams().height = newHeight;
        mView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
