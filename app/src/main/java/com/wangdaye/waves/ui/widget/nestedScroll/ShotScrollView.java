package com.wangdaye.waves.ui.widget.nestedScroll;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.wangdaye.waves.utils.DisplayUtils;

/**
 * Shot scroll view.
 * */

public class ShotScrollView extends NestedScrollView {
    // data
    private float scrollLength;
    private float BOOST_LENGTH;

    private float oldY;
    private float touchSlop;

    /** <br> life cycle. */

    public ShotScrollView(Context context) {
        super(context);
        this.initialize();
    }

    public ShotScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public ShotScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.scrollLength = 0;
        this.BOOST_LENGTH = DisplayUtils.dpToPx(80);

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /** <br> data's getter & setter. */

    public float getScrollLength() {
        return scrollLength;
    }

    public void setScrollLength(float scrollLength) {
        this.scrollLength = scrollLength;
    }

    public float getBOOST_LENGTH() {
        return BOOST_LENGTH;
    }

    /** <br> parent methods. */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            oldY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev)
                || (ev.getAction() == MotionEvent.ACTION_MOVE && Math.abs(ev.getY() - oldY) > touchSlop);
    }
}
