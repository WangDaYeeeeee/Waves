package com.wangdaye.waves.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * Swipe refresh layout.
 * */

public class MySwipeRefreshLayout extends RelativeLayout
        implements WavesLoadingView.OnLoadingListener {
    // widget
    private HomeRecyclerView target;
    private WavesLoadingView wavesLoadingView;

    private OnRefreshListener onRefreshListener;

    // data
    private float oldY;
    private float swipeDownY;
    private float swipeUpY;
    private float SWIPE_DIST = 200;
    private final float RADIO = 2;
    private float touchSlop;

    private boolean downSwiping;
    private boolean upSwiping;

    private boolean isLayout;

    private int stateNow;
    public static final int INITIAL_STATE = 1;
    public static final int RELEASE_TO_REFRESH = 2;
    public static final int REFRESHING = 3;
    public static final int RELEASE_TO_LOAD = 4;
    public static final int LOADING = 5;
    public static final int ALL_DONE = 6;
    public static final int NO_DATA = -1;

    /** <br> life cycle. */

    public MySwipeRefreshLayout(Context context) {
        super(context);
        this.initialize();
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        this.stateNow = INITIAL_STATE;

        this.swipeDownY = 0;
        this.swipeUpY = 0;
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.downSwiping = false;
        this.upSwiping = false;

        this.isLayout = false;
    }

    /** <br> parent methods. */

    // touch.

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            oldY = ev.getY();
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);

        if (stateNow == REFRESHING || stateNow == LOADING) {
            return false;
        }

        switch (ev.getAction()) {

            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY && ev.getY() - oldY > touchSlop) {
                    // 下滑
                    if ((!canChildSwipeDown() || upSwiping)
                            && ( stateNow == INITIAL_STATE || stateNow == RELEASE_TO_REFRESH || stateNow == RELEASE_TO_LOAD)) {
                        return true;
                    }
                } else if (ev.getY() < oldY && oldY - ev.getY() > touchSlop) {
                    // 上滑
                    if ((!canChildSwipeUp() || downSwiping)
                            && ( stateNow == INITIAL_STATE || stateNow == RELEASE_TO_REFRESH || stateNow == RELEASE_TO_LOAD)) {
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (stateNow == RELEASE_TO_REFRESH
                        || stateNow == RELEASE_TO_LOAD
                        || (stateNow == INITIAL_STATE && (upSwiping || downSwiping))) {
                    upSwiping = false;
                    downSwiping = false;
                    return true;
                }
                if (stateNow == NO_DATA && Math.abs(ev.getY() - oldY) < touchSlop) {
                    wavesLoadingView.setState(WavesLoadingView.SHOWING);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY) {
                    // 下滑
                    if (!canChildSwipeDown() && stateNow == INITIAL_STATE) {
                        downSwiping = true;
                        swipeDownY += (ev.getY() - oldY) / RADIO;
                        oldY = ev.getY();
                        if (swipeDownY >= SWIPE_DIST) {
                            setState(RELEASE_TO_REFRESH);
                        } else {
                            setState(INITIAL_STATE);
                        }
                        return true;

                    } else if (upSwiping && stateNow == INITIAL_STATE) {
                        swipeUpY -= (ev.getY() - oldY) / RADIO;
                        oldY = ev.getY();
                        if (swipeUpY < 0) {
                            setState(INITIAL_STATE);
                            swipeUpY = 0;
                            upSwiping = false;
                        } else {
                            setState(INITIAL_STATE);
                        }
                        return true;

                    } else if (stateNow == RELEASE_TO_REFRESH) {
                        swipeDownY += (ev.getY() - oldY) / RADIO;
                        oldY = ev.getY();
                        setState(RELEASE_TO_REFRESH);
                        return true;

                    } else if (stateNow == RELEASE_TO_LOAD) {
                        swipeUpY -= (ev.getY() - oldY) / RADIO;
                        oldY = ev.getY();
                        if (swipeUpY < SWIPE_DIST) {
                            setState(INITIAL_STATE);
                        } else {
                            setState(RELEASE_TO_LOAD);
                        }
                        return true;
                    }

                } else if (ev.getY() < oldY) {
                    // 上滑
                    if (!canChildSwipeUp() && stateNow == INITIAL_STATE) {
                        upSwiping = true;
                        swipeUpY += (oldY - ev.getY()) / RADIO;
                        oldY = ev.getY();
                        if (swipeUpY >= SWIPE_DIST) {
                            setState(RELEASE_TO_LOAD);
                        } else {
                            setState(INITIAL_STATE);
                        }
                        return true;

                    } else if (downSwiping && stateNow == INITIAL_STATE) {
                        swipeDownY -= (oldY - ev.getY()) / RADIO;
                        oldY = ev.getY();
                        if (swipeDownY < 0) {
                            setState(INITIAL_STATE);
                            swipeDownY = 0;
                            downSwiping = false;
                        } else {
                            setState(INITIAL_STATE);
                        }
                        return true;

                    } else if (stateNow == RELEASE_TO_REFRESH) {
                        swipeDownY -= (oldY - ev.getY()) / RADIO;
                        oldY = ev.getY();
                        if (swipeDownY < SWIPE_DIST) {
                            setState(INITIAL_STATE);
                        } else {
                            setState(RELEASE_TO_REFRESH);
                        }
                        return true;

                    } else if (stateNow == RELEASE_TO_LOAD) {
                        swipeUpY += (oldY - ev.getY()) / RADIO;
                        oldY = ev.getY();
                        setState(RELEASE_TO_LOAD);
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (stateNow == RELEASE_TO_REFRESH) {
                    swipeDownY = SWIPE_DIST;
                    swipeUpY = 0;
                    downSwiping = false;
                    upSwiping = false;
                    setState(REFRESHING);
                    return true;
                } else if (stateNow == RELEASE_TO_LOAD) {
                    swipeDownY = 0;
                    swipeUpY = SWIPE_DIST;
                    downSwiping = false;
                    upSwiping = false;
                    setState(LOADING);
                    return true;
                } else if (stateNow == INITIAL_STATE) {
                    swipeDownY = 0;
                    swipeUpY = 0;
                    downSwiping = false;
                    upSwiping = false;
                    setState(INITIAL_STATE);
                    return true;
                }
                break;
        }
        return false;
    }

    // layout.

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!isLayout) {
            isLayout = true;

            target = (HomeRecyclerView) getChildAt(0);
            wavesLoadingView = (WavesLoadingView) getChildAt(1);
            wavesLoadingView.setOnLoadingListener(this);

            SWIPE_DIST = (float) (wavesLoadingView.getMeasuredHeight() / 8.0);
            wavesLoadingView.setState(WavesLoadingView.SHOWING);
        }
    }

    /** <br> data. */

    public void setState(int stateTo) {
        stateNow = stateTo;
        switch (stateNow) {

            case INITIAL_STATE:
                wavesLoadingView.setState(WavesLoadingView.SWIPING);
                wavesLoadingView.showSwiping(swipeDownY + swipeUpY, SWIPE_DIST);
                break;

            case RELEASE_TO_REFRESH:
                wavesLoadingView.showSwiping(swipeDownY, SWIPE_DIST);
                break;

            case REFRESHING:
                wavesLoadingView.setState(WavesLoadingView.REFRESHING);
                if (onRefreshListener != null) {
                    onRefreshListener.refreshNew();
                }
                break;

            case RELEASE_TO_LOAD:
                wavesLoadingView.showSwiping(swipeUpY, SWIPE_DIST);
                break;

            case LOADING:
                wavesLoadingView.setState(WavesLoadingView.REFRESHING);
                if (onRefreshListener != null) {
                    onRefreshListener.loadMore();
                }
                break;

            case ALL_DONE:
                swipeUpY = 0;
                swipeDownY = 0;
                wavesLoadingView.setState(WavesLoadingView.INITIAL_STATE);
                setState(INITIAL_STATE);
                break;

            case NO_DATA:
                swipeUpY = 0;
                swipeDownY = 0;
                wavesLoadingView.setState(WavesLoadingView.FAILED);
                break;
        }
    }

    public boolean canChildSwipeDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(target, -1) || target.getScrollY() > 0;
        } else {
            return ViewCompat.canScrollVertically(target, -1);
        }
    }

    public boolean canChildSwipeUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(target, 1) || target.getScrollY() < 0;
        } else {
            return ViewCompat.canScrollVertically(target, 1);
        }
    }

    /** <br> interface. */

    // waves loading view listener.

    @Override
    public void refresh() {
        stateNow = REFRESHING;
        if (onRefreshListener != null) {
            onRefreshListener.refreshNew();
        }
    }

    @Override
    public void hideFinish() {

    }

    // on refresh listener.

    public interface OnRefreshListener {
        void refreshNew();
        void loadMore();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}
