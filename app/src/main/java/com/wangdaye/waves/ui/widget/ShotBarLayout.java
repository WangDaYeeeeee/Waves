package com.wangdaye.waves.ui.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Shot bar layout.
 * */

public class ShotBarLayout extends AppBarLayout {
    // widget
    private ImageView shot;

    // data
    private float maxiHeight;
    private float miniHeight;
    private boolean isLayout;

    /** <br> life cycle. */

    public ShotBarLayout(Context context) {
        super(context);
        this.initialize();
    }

    public ShotBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    private void initialize() {
        this.isLayout = false;
    }

    /** <br> parent methods. */

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!isLayout) {
            isLayout = true;
            this.shot = (ImageView) getChildAt(1);
        }
    }

    /** <br> data. */

    public float getMiniHeight() {
        return miniHeight;
    }

    public void setMiniHeight(float miniHeight) {
        this.miniHeight = miniHeight;
    }

    public float getMaxiHeight() {
        return maxiHeight;
    }

    public void setMaxiHeight(float maxiHeight) {
        this.maxiHeight = maxiHeight;
    }

    public float getHeightDifference() {
        return maxiHeight - miniHeight;
    }

    public void setShotAlpha(float height) {
        if (shot != null) {
            shot.setAlpha((float) (0.3 + 0.7 * height / maxiHeight));
        }
    }
}
