package com.wangdaye.waves.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.wangdaye.waves.utils.DisplayUtils;

/**
 * Status bar view.
 * */

public class StatusBarView extends FrameLayout {
    // data
    public int statusBarHeight;

    public StatusBarView(Context context) {
        super(context);
        this.initialize();
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.statusBarHeight = DisplayUtils.getStatusBarHeight(getResources());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), statusBarHeight);
    }
}
