package com.wangdaye.waves.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.waves.R;

/**
 * Reveal view v2.0.
 * */

public class RevealView extends View {
    // widget
    private Paint paint;
    private OnRevealingListener onRevealingListener;

    // data
    private int circleColor;
    private int backgroundColor;

    private int touchPosition = 0;
    private float touchX = 0;
    private float touchY = 0;

    private int radius;

    private int stateNow;
    private boolean isLayout;

    private int drawTime;

    private static int DRAW_TIME = 20;

    public static final int ONE_TIME_SPEED = 10;
    public static final int TWO_TIMES_SPEED = 20;

    public static final int INITIAL_STATE = 0;
    public static final int REVEALING = 1;
    public static final int GRADIENT_TO_BACKGROUND = 2;
    public static final int SHOWING = 3;
    public static final int GRADIENT_TO_REVEAL = 4;
    public static final int HIDING = 5;
    public static final int DIE = 6;

    public static final int LEFT_TOP = 1;
    public static final int RIGHT_TOP = 2;
    public static final int LEFT_BOTTOM = 3;
    public static final int RIGHT_BOTTOM = 4;

    /**
     * <br> life cycle.
     * */

    public RevealView(Context context) {
        super(context);
        initialize();
    }

    public RevealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        stateNow = INITIAL_STATE;
        drawTime = 0;

        isLayout = false;

        this.paint = new Paint();
        this.backgroundColor = ContextCompat.getColor(getContext(), R.color.cardview_light_background);
        this.circleColor = ContextCompat.getColor(getContext(), R.color.colorRoot);
    }

    /**
     * <br> parent methods.
     * */

    // view.

    @Override
    protected void onDraw(Canvas canvas) {
        switch (stateNow) {

            case INITIAL_STATE:
                break;

            case REVEALING:
                drawTime ++;
                canvas.drawCircle(touchX, touchY, radius / DRAW_TIME * drawTime, paint);
                if (drawTime <= DRAW_TIME) {
                    invalidate();
                } else {
                    setState(GRADIENT_TO_BACKGROUND);
                }
                break;

            case GRADIENT_TO_BACKGROUND:
                drawTime ++;
                canvas.drawColor(circleColor);

                this.paint.reset();
                paint.setColor(backgroundColor);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAlpha(120);
                canvas.drawCircle(touchX, touchY, radius / DRAW_TIME * 2 * drawTime, paint);

                if (drawTime > DRAW_TIME / 2) {
                    this.paint.reset();
                    paint.setColor(backgroundColor);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setAntiAlias(true);
                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setAlpha(255);
                    canvas.drawCircle(touchX, touchY, radius / DRAW_TIME * 2 * (drawTime - DRAW_TIME / 2), paint);
                }

                if (drawTime <= DRAW_TIME) {
                    invalidate();
                } else {
                    setState(SHOWING);
                }
                break;

            case SHOWING:
                canvas.drawColor(backgroundColor);
                break;

            case GRADIENT_TO_REVEAL:
                drawTime ++;
                canvas.drawColor(circleColor);

                this.paint.reset();
                paint.setColor(backgroundColor);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAlpha(120);
                canvas.drawCircle(touchX, touchY, radius * 2 / DRAW_TIME * (DRAW_TIME - drawTime), paint);

                if (drawTime <= DRAW_TIME / 2) {
                    this.paint.reset();
                    paint.setColor(backgroundColor);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setAntiAlias(true);
                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setAlpha(255);
                    canvas.drawCircle(touchX, touchY, radius / DRAW_TIME * 2 * (DRAW_TIME / 2 - drawTime), paint);
                }

                if (drawTime <= DRAW_TIME) {
                    invalidate();
                } else {
                    setState(HIDING);
                }
                break;

            case HIDING:
                drawTime ++;

                canvas.drawCircle(touchX, touchY, radius / DRAW_TIME * (DRAW_TIME - drawTime), paint);
                if (drawTime <= DRAW_TIME) {
                    invalidate();
                } else {
                    setState(DIE);
                }
                break;

            case DIE:
                break;
        }
    }

    // layout.

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isLayout) {
            radius = (int) Math.pow(Math.pow(getMeasuredWidth(), 2) + Math.pow(getMeasuredHeight(), 2), 0.5) + 10;

            switch (touchPosition) {
                case 0:
                    break;
                case LEFT_TOP:
                    touchX = 0;
                    touchY = 0;
                    break;
                case RIGHT_TOP:
                    touchX = getMeasuredWidth();
                    touchY = 0;
                    break;
                case LEFT_BOTTOM:
                    touchX = 0;
                    touchY = getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM:
                    touchX = getMeasuredWidth();
                    touchY = getMeasuredHeight();
                    break;
            }
        }
    }

    /** <br> data. */

    public void setState(int stateTo) {
        stateNow = stateTo;

        switch (stateNow) {

            case INITIAL_STATE:
                initialize();
                invalidate();
                break;

            case REVEALING:
                drawTime = 0;
                paint.reset();
                paint.setColor(circleColor);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);

                invalidate();
                break;

            case GRADIENT_TO_BACKGROUND:
                drawTime = 0;

                if (circleColor == backgroundColor) {
                    setState(SHOWING);
                } else {
                    invalidate();
                }
                break;

            case SHOWING:
                drawTime = 0;

                invalidate();
                if (onRevealingListener != null) {
                    onRevealingListener.revealFinish();
                }
                break;

            case GRADIENT_TO_REVEAL:
                drawTime = 0;

                if (circleColor == backgroundColor) {
                    setState(HIDING);
                } else {
                    invalidate();
                }
                break;

            case HIDING:
                drawTime = 0;
                paint.reset();
                paint.setColor(circleColor);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);

                invalidate();
                break;

            case DIE:
                drawTime = 0;
                if (onRevealingListener != null) {
                    onRevealingListener.hideFinish();
                }
                break;
        }
    }

    public void setColor(int circleColor, int backgroundColor) {
        this.circleColor = circleColor;
        this.backgroundColor = backgroundColor;
    }

    public void setTouchPosition(int position, float x, float y) {
        touchPosition = position;
        if (position == 0) {
            touchX = x;
            touchY = y;
            return;
        }

        switch (position) {
            case LEFT_TOP:
                touchX = 0;
                touchY= 0;
                break;
            case RIGHT_TOP:
                touchX = getMeasuredWidth();
                touchY= 0;
                break;
            case LEFT_BOTTOM:
                touchX = 0;
                touchY = getMeasuredHeight();
                break;
            case RIGHT_BOTTOM:
                touchX = getMeasuredWidth();
                touchY = getMeasuredHeight();
                break;
        }
    }

    public void setDrawTime(int time) {
        DRAW_TIME = time;
    }

    /** <br> interface. */

    public interface OnRevealingListener {
        void revealFinish();
        void hideFinish();
    }

    public void setOnRevealingListener(OnRevealingListener listener) {
        this.onRevealingListener = listener;
    }

    public void cleanOnRevealingListener() {
        this.onRevealingListener = null;
    }
}