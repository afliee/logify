package com.example.logify.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.cardview.widget.CardView;

public class ExpandCollapseAnimation extends Animation {
    private final CardView mCardView;
    private final int mStartHeight;
    private final int mEndHeight;
    private final boolean mIsExpanding;

    public ExpandCollapseAnimation(CardView cardView, int startHeight, int endHeight, boolean isExpanding) {
        mCardView = cardView;
        mStartHeight = startHeight;
        mEndHeight = endHeight;
        mIsExpanding = isExpanding;

        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = mStartHeight + (int) ((mEndHeight - mStartHeight) * interpolatedTime);
        mCardView.getLayoutParams().height = newHeight;
        mCardView.requestLayout();

        if (interpolatedTime == 1 && !mIsExpanding) {
            mCardView.setVisibility(View.GONE);
        } else if (mCardView.getVisibility() == View.GONE) {
            mCardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
