package com.example.logify.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CollapseAnimation extends Animation {
    private final int mInitialHeight;
    private final View mView;

    public CollapseAnimation(View view) {
        mInitialHeight = view.getHeight();
        mView = view;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (mInitialHeight * (1 - interpolatedTime));
        mView.getLayoutParams().height = newHeight;
        mView.requestLayout();

        if (interpolatedTime == 1) {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

