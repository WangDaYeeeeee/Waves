package com.wangdaye.waves.ui.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.wangdaye.waves.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Swipe back layout.
 * */

public class SwipeBackLayout extends FrameLayout
        implements SafeHandler.HandlerContainer {
    // widget
    private View container;
    private View target;
    private View statusBar;
    private View shadow;
    private OnSwipeListener onSwipeListener;

    // data
    private float swipeDownY;
    private float swipeUpY;
    private float SWIPE_DIST = 200;
    private final float SWIPE_RADIO = 2.5F;
    private float touchSlop;

    private float oldY;

    private int stateNow;
    public static final int NORMAL_STATE = 0;
    public static final int MOVING = 1;

    private int swipeDir;
    public static final int UP = 1;
    public static final int DOWN = -1;

    private int swipeResult;
    private int sendMsgTime;
    private final int SWIPE_FINISH = 1;
    private final int SWIPE_CANCEL = -1;

    // handler
    private SafeHandler<SwipeBackLayout> handler;
    private Timer timer;

    /** <br> life cycle. */

    public SwipeBackLayout(Context context) {
        super(context);
        this.initialize();
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.swipeDownY = 0;
        this.swipeUpY = 0;
        this.swipeDir = 0;
        this.swipeResult = 0;
        this.sendMsgTime = 0;

        int width = getResources().getDisplayMetrics().widthPixels;
        this.SWIPE_DIST = (int) (SWIPE_DIST / 1080.0 * width);
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.setState(NORMAL_STATE);

        this.handler = new SafeHandler<>(this);
        this.timer = new Timer();
    }

    /** <br> parent methods. */

    // touch.

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            oldY = ev.getY();
        }
        return stateNow != MOVING;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY && ev.getY() - oldY > touchSlop) {
                    // 下滑
                    if (onSwipeListener != null) {
                        return target == null || onSwipeListener.canSwipeBack(target, DOWN);
                    }
                } else if (ev.getY() < oldY && oldY - ev.getY() > touchSlop) {
                    // 上滑
                    if (onSwipeListener != null) {
                        return target == null || onSwipeListener.canSwipeBack(target, UP);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                return swipeDir != 0
                        && (target == null || onSwipeListener.canSwipeBack(target, swipeDir))
                        && Math.abs(ev.getY() - oldY) > touchSlop;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY) {
                    // 下滑
                    if (swipeDir == 0) {
                        swipeDir = DOWN;
                        swipeDownY = (float) ((ev.getY() - oldY) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP) {
                        swipeUpY -= (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                        if (swipeUpY <= 0) {
                            swipeUpY = swipeDir = 0;
                        }
                    } else if (swipeDir == DOWN) {
                        swipeDownY += (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                    }
                    oldY = ev.getY();

                    setBackgroundAlpha(swipeDownY + swipeUpY);
                    requestLayout();
                    return true;
                } else if (ev.getY() < oldY) {
                    // 上滑
                    if (swipeDir == 0) {
                        swipeDir = UP;
                        swipeUpY = (float) ((oldY - ev.getY()) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP) {
                        swipeUpY += (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                    } else if (swipeDir == DOWN) {
                        swipeDownY -= (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                        if (swipeDownY <= 0) {
                            swipeDownY = swipeDir = 0;
                        }
                    }
                    oldY = ev.getY();

                    setBackgroundAlpha(swipeDownY + swipeUpY);
                    requestLayout();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                setState(MOVING);
                if (ev.getY() > oldY) {
                    // 下滑
                    if (swipeDir == 0) {
                        swipeDir = DOWN;
                        swipeDownY = (float) ((ev.getY() - oldY) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP) {
                        swipeUpY -= (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                        if (swipeUpY <= 0) {
                            swipeUpY = swipeDir = 0;
                        }
                    } else if (swipeDir == DOWN) {
                        swipeDownY += (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                    }
                    oldY = ev.getY();

                    setBackgroundAlpha(swipeDownY + swipeUpY);
                    requestLayout();
                } else if (ev.getY() < oldY) {
                    // 上滑
                    if (swipeDir == 0) {
                        swipeDir = UP;
                        swipeUpY = (float) ((oldY - ev.getY()) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP) {
                        swipeUpY += (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                    } else if (swipeDir == DOWN) {
                        swipeDownY -= (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                        if (swipeDownY <= 0) {
                            swipeDownY = swipeDir = 0;
                        }
                    }
                    oldY = ev.getY();

                    setBackgroundAlpha(swipeDownY + swipeUpY);
                    requestLayout();
                }

                if ((swipeDir == UP && swipeUpY >= SWIPE_DIST) || (swipeDir == DOWN && swipeDownY >= SWIPE_DIST)) {
                    swipeResult = SWIPE_FINISH;
                    statusBar.setVisibility(View.GONE);
                    shadow.setVisibility(GONE);
                    this.swipeOver();
                } else if (swipeDir != 0) {
                    swipeResult = SWIPE_CANCEL;
                    this.swipeOver();
                } else {
                    setState(NORMAL_STATE);
                }
                return true;
        }
        return false;
    }

    // layout.

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (container != null) {
            if (swipeDir == 0) {
                container.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
            } else if (swipeDir == UP) {
                container.layout(
                        0,
                        (int) -swipeUpY,
                        getMeasuredWidth(),
                        (int) (getMeasuredHeight() - swipeUpY));
            } else if (swipeDir == DOWN) {
                container.layout(
                        0,
                        (int) swipeDownY,
                        getMeasuredWidth(),
                        (int) (getMeasuredHeight() + swipeDownY));
            }
        }
    }

    /** <br> data */

    public void setState(int stateTo) {
        this.stateNow = stateTo;
    }

    private void swipeOver() {
        timer.schedule(new MyTimerTask(), 16, 16);
    }

    private void setBackgroundAlpha(float swipeLength) {
        if (statusBar == null || shadow == null) {
            return;
        }
        if (swipeLength < SWIPE_DIST) {
            statusBar.setAlpha((float) (1 - swipeLength * 1.0 / SWIPE_DIST));
            shadow.setAlpha((float) (1 - swipeLength * 1.0 / SWIPE_DIST));
        } else {
            statusBar.setAlpha(0);
            shadow.setAlpha(0);
        }
    }

    /** <br> interface. */

    public interface OnSwipeListener {
        boolean canSwipeBack(View target, int dir);
        void swipeFinish();
    }

    public void setOnSwipeListener(OnSwipeListener listener, View target, View container) {
        this.onSwipeListener = listener;
        this.target = target;
        this.container = container;
    }

    /** <br> setter. */

    public void setBackground(View statusBar, View shadow) {
        this.statusBar = statusBar;
        this.shadow = shadow;
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        sendMsgTime ++;

        if (container != null) {
            switch (message.what) {
                case SWIPE_FINISH:
                    float delta = (float) (getMeasuredHeight() / 20.0);
                    container.layout(
                            0,
                            (int) ((swipeUpY + swipeDownY + delta * sendMsgTime) * -swipeDir * 1.0),
                            getMeasuredWidth(),
                            (int) (getMeasuredHeight() + (swipeUpY + swipeDownY + delta * sendMsgTime) * -swipeDir * 1.0));
                    setBackgroundAlpha(0);
                    break;

                case SWIPE_CANCEL:
                    container.layout(
                            0,
                            (int) ((swipeUpY + swipeDownY) * -swipeDir / 20.0 * (20 - sendMsgTime)),
                            getMeasuredWidth(),
                            (int) ((swipeUpY + swipeDownY) * -swipeDir / 20.0 * (20 - sendMsgTime) + getMeasuredHeight()));
                    setBackgroundAlpha((float) ((swipeUpY + swipeDownY) / 20.0 * (20 - sendMsgTime)));
                    break;
            }
        }

        if (sendMsgTime >= 20) {
            timer.cancel();
            timer.purge();
            timer = new Timer();

            sendMsgTime = 0;
            swipeDownY = swipeUpY = swipeDir = 0;
            setState(NORMAL_STATE);
            if (swipeResult == SWIPE_FINISH && onSwipeListener != null) {
                container.setVisibility(GONE);
                onSwipeListener.swipeFinish();
            }
        }
    }

    /** <br> inner class. */

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = swipeResult;
            handler.sendMessage(message);
        }
    }
}
