package com.wangdaye.waves.ui.widget.nestedScroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.wangdaye.waves.ui.widget.MyFloatingActionButton;

/**
 * Shot scroll parent.
 * */

public class ShotScrollParent extends
        FrameLayout implements NestedScrollView.OnScrollChangeListener {
    // widget
    private ShotBarLayout shotBarLayout;
    private ShotScrollView shotScrollView;
    private MyFloatingActionButton fab;

    // data
    private boolean isLayout;

    public final int NORMAL_STATE = 1;
    public final int BOOST_STATE = 2;
    public final int FINAL_STATE = 3;

    public ShotScrollParent(Context context) {
        super(context);
        this.initialize();
    }

    public ShotScrollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public ShotScrollParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShotScrollParent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
            this.shotBarLayout = (ShotBarLayout) getChildAt(1);
            this.shotScrollView = (ShotScrollView) getChildAt(2);
            this.fab = (MyFloatingActionButton) getChildAt(3);

            shotScrollView.setOnScrollChangeListener(this);
        }
    }

    /** <br> data. */

    private int getState(float dy) { // dy = 上移距离
        if (shotScrollView.getScrollLength() + dy < shotScrollView.getBOOST_LENGTH()) {
            return NORMAL_STATE;
        } else if (shotScrollView.getBOOST_LENGTH() <= shotScrollView.getScrollLength() + dy
                && shotScrollView.getScrollLength() + dy <= shotScrollView.getBOOST_LENGTH() + shotBarLayout.getHeightDifference()) {
            return BOOST_STATE;
        } else {
            return FINAL_STATE;
        }
    }

    private void setShotBarHeight() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) shotBarLayout.getLayoutParams();
        switch (getState(0)) {

            case NORMAL_STATE:
                params.height = (int) (shotBarLayout.getMaxiHeight());
                shotBarLayout.setLayoutParams(params);
                break;

            case BOOST_STATE:
                params.height = (int) (shotBarLayout.getMaxiHeight()
                        - (shotScrollView.getScrollLength() - shotScrollView.getBOOST_LENGTH()));
                shotBarLayout.setLayoutParams(params);
                break;

            case FINAL_STATE:
                params.height = (int) (shotBarLayout.getMiniHeight());
                shotBarLayout.setLayoutParams(params);
                break;
        }
        shotBarLayout.setShotAlpha(params.height);
    }

    private void setFabPosition() {
        FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        fabParams.topMargin = fab.calcMargin(
                shotBarLayout.getMaxiHeight(),
                shotScrollView.getScrollLength(),
                shotScrollView.getBOOST_LENGTH() + shotBarLayout.getHeightDifference());
        fab.setLayoutParams(fabParams);
    }

    /** <br> interface. */

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        shotScrollView.setScrollLength(shotScrollView.getScrollLength() + (scrollY - oldScrollY));
        this.setShotBarHeight();
        if (fab != null) {
            this.setFabPosition();
        }
    }
}
