package com.wangdaye.waves.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.waves.R;
import com.wangdaye.waves.utils.ImageUtils;

/**
 * Waves loading view.
 * */

public class WavesLoadingView extends View {
    // widget
    private Paint paint;
    private Path path;
    private OnLoadingListener onLoadingListener;

    // data
    private int stateNow;
    private boolean isLayout;

    private float swipeY;
    private float maxSwipeY;

    private int drawTime;
    private final int DRAW_CYCLE_TIMES = 80;
    private final int REC_CHANGE_CYCLE_TIMES = 84;

    private final int WAVES_COLOR = R.color.colorPrimary;
    private final int BACKGROUND_COLOR = R.color.colorRoot;
    private final int BACKGROUND_ALPHA = 180;

    public static final int INITIAL_STATE = 1;
    public static final int SWIPING = 2;
    public static final int SHOWING = 3;
    public static final int REFRESHING = 4;
    public static final int DONE = 5;
    public static final int NULL = 6;
    public static final int FAILED = -1;

    private int width;
    private float recWidth = 15;
    private float recSpace = 15;
    private float recHeight = 90;
    private float recRadius = 5;
    private float textSize = 40;

    private WaveRec[] waveRecs;
    private final int WAVE_NUM = 5;

    private String nullString;

    /** <br> life cycle. */

    public WavesLoadingView(Context context) {
        super(context);
        this.initialize();
    }

    public WavesLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public WavesLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WavesLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        this.stateNow = INITIAL_STATE;
        this.isLayout = false;

        this.width = getResources().getDisplayMetrics().widthPixels;
        this.recWidth = (float) (recWidth / 1080.0 * width);
        this.recSpace = (float) (recSpace / 1080.0 * width);
        this.recHeight = (float) (recHeight / 1080.0 * width);
        this.recRadius = (float) (recRadius / 1080.0 * width);
        this.textSize = (float) (textSize / 1080.0 * width);

        this.nullString = getContext().getString(R.string.no_data);

        this.paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);

        this.path = new Path();
    }

    /** <br> draw. */

    private void drawBackground(Canvas canvas, int alpha) {
        paint.setColor(ContextCompat.getColor(getContext(), BACKGROUND_COLOR));
        paint.setAlpha(alpha);

        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
    }

    private void drawSwipingRec(Canvas canvas, float swipeY, float maxSwipeY) {
        float x = (float) (1.0 * swipeY / maxSwipeY * DRAW_CYCLE_TIMES);
        paint.setColor(ContextCompat.getColor(getContext(), WAVES_COLOR));
        paint.setPathEffect(new CornerPathEffect(recRadius));

        for (int i = 0; i < waveRecs.length; i ++) {
            WaveRec swipeWave = waveRecs[i].getSwipeWave(x - 4 * i);
            if (swipeWave == null) {
                continue;
            }

            path.reset();
            path.moveTo(swipeWave.xNow, swipeWave.yNow);
            path.lineTo(swipeWave.xNow + swipeWave.widthNow, swipeWave.yNow);
            path.lineTo(swipeWave.xNow + swipeWave.widthNow, swipeWave.yNow + swipeWave.heightNow);
            path.lineTo(swipeWave.xNow, swipeWave.yNow + swipeWave.heightNow);
            path.close();

            paint.setAlpha((int) (255.0 / REC_CHANGE_CYCLE_TIMES * 2 * swipeWave.time));
            canvas.drawPath(path, paint);
        }
        paint.setPathEffect(null);
    }

    private void drawRefreshingRec(Canvas canvas) {
        paint.setColor(ContextCompat.getColor(getContext(), WAVES_COLOR));
        paint.setPathEffect(new CornerPathEffect(recRadius));

        for (WaveRec aWaveRec : waveRecs) {
            path.reset();
            path.moveTo(aWaveRec.xNow, aWaveRec.yNow);
            path.lineTo(aWaveRec.xNow + aWaveRec.widthNow, aWaveRec.yNow);
            path.lineTo(aWaveRec.xNow + aWaveRec.widthNow, aWaveRec.yNow + aWaveRec.heightNow);
            path.lineTo(aWaveRec.xNow, aWaveRec.yNow + aWaveRec.heightNow);
            path.close();

            paint.setAlpha(aWaveRec.alpha);
            canvas.drawPath(path, paint);
        }
        paint.setPathEffect(null);
    }

    /** <br> math. */

    private void calcRec(boolean init) {
        if (init) {
            waveRecs = new WaveRec[WAVE_NUM];
            for (int i = 0; i < waveRecs.length; i ++) {
                waveRecs[i] = new WaveRec(
                        (float) (getMeasuredWidth() / 2.0 - (2.5 - i) * recWidth - (2 - i) * recSpace),
                        (float) (getMeasuredHeight() / 2.0 - recHeight / 2.0 * getHeightScale(i)),
                        recWidth,
                        recHeight * getHeightScale(i),
                        -12 * i);
            }
        } else {
            for (WaveRec waveRec : waveRecs) {
                waveRec.changState();
            }
        }
    }

    /** <br> parent methods. */

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (stateNow) {
            case INITIAL_STATE:
                this.calcRec(true);
                setAlpha(1);
                break;

            case SWIPING:
                this.drawBackground(canvas, (int) (BACKGROUND_ALPHA * 1.0 / maxSwipeY * swipeY));
                this.drawSwipingRec(canvas, swipeY, maxSwipeY);
                break;

            case SHOWING:
                drawTime ++;
                this.drawBackground(canvas, (int) (BACKGROUND_ALPHA * 1.0 / DRAW_CYCLE_TIMES * drawTime));
                this.drawSwipingRec(canvas, drawTime, DRAW_CYCLE_TIMES);

                if (drawTime >= DRAW_CYCLE_TIMES) {
                    setState(REFRESHING);
                    drawTime = 0;
                } else {
                    invalidate();
                }
                break;

            case REFRESHING:
                drawTime ++;
                this.drawBackground(canvas, BACKGROUND_ALPHA);
                this.calcRec(false);
                this.drawRefreshingRec(canvas);

                invalidate();
                break;

            case DONE:
                drawTime ++;
                this.drawBackground(canvas, BACKGROUND_ALPHA);
                this.calcRec(false);
                this.drawRefreshingRec(canvas);

                if (drawTime >= DRAW_CYCLE_TIMES) {
                    if (onLoadingListener != null) {
                        onLoadingListener.hideFinish();
                    }
                    setState(INITIAL_STATE);
                } else {
                    setAlpha((float) (1.0 * (DRAW_CYCLE_TIMES - drawTime) / DRAW_CYCLE_TIMES));
                    invalidate();
                }
                break;

            case NULL:
                paint.setAlpha(255);
                paint.setColor(ContextCompat.getColor(getContext(), WAVES_COLOR));
                canvas.drawText(nullString, getMeasuredWidth() / 2, getMeasuredHeight() / 2, paint);
                break;

            case FAILED:
                paint.setAlpha(255);
                paint.setColor(ContextCompat.getColor(getContext(), WAVES_COLOR));

                this.calcRec(true);
                if (getMeasuredHeight() > 300.0 * (getResources().getDisplayMetrics().densityDpi / 160.0)) {
                    canvas.drawBitmap(
                            ImageUtils.readBitmapFormSrc(getContext(), R.drawable.error, 400, 400, width),
                            null,
                            new RectF(
                                    (float) (getMeasuredWidth() / 4.0),
                                    (float) (getMeasuredHeight() / 2.0 - getMeasuredWidth() / 2.0),
                                    (float) (getMeasuredWidth() / 4.0 * 3),
                                    (float) (getMeasuredHeight() / 2.0)),
                            paint);
                    canvas.drawText(getContext().getString(R.string.air_ball) + " " + getContext().getString(R.string.touch_to_retry),
                            getMeasuredWidth() / 2, getMeasuredHeight() / 2 + textSize * 2, paint);
                } else {
                    canvas.drawText(getContext().getString(R.string.air_ball) + " " + getContext().getString(R.string.touch_to_retry),
                            getMeasuredWidth() / 2, getMeasuredHeight() / 2, paint);
                }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!isLayout) {
            isLayout = true;
            this.calcRec(true);
        }
    }


    /** <br> data. */

    public void setState(int stateTo) {
        stateNow = stateTo;
        if (stateNow == INITIAL_STATE) {
            drawTime = 0;
        }
        if (stateNow == SHOWING || stateNow == REFRESHING) {
            if (onLoadingListener != null) {
                onLoadingListener.refresh();
            }
            swipeY = 0;
        }
        invalidate();
    }

    public void showSwiping(float swipeY, float maxSwipeY) {
        this.maxSwipeY = maxSwipeY;

        if (swipeY < 0) {
            swipeY = 0;
        } else if (swipeY > maxSwipeY) {
            swipeY = maxSwipeY;
        }
        this.swipeY = swipeY;

        invalidate();
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    /** <br> inner class. */

    private class WaveRec {
        // data
        public float xNow;
        public float yNow;
        public float xInit;
        public float yInit;
        public float widthNow;
        public float heightNow;
        public float widthMax;
        public float heightMax;
        public int alpha;
        public int time;

        public WaveRec(float x, float y, float width, float height, int time) {
            this.xInit = x;
            this.yInit = y;
            this.xNow = x;
            this.yNow = y;
            this.widthMax = width;
            this.widthNow = width;
            this.heightMax = height;
            this.heightNow = height;
            this.alpha = 225;
            this.time = time;
        }

        public void changState() {
            time ++;
            if (0 < time && time <= REC_CHANGE_CYCLE_TIMES / 2) {
                this.hide();
            } else if (time > REC_CHANGE_CYCLE_TIMES / 2) {
                this.show();
                if (time >= REC_CHANGE_CYCLE_TIMES) {
                    time = 0;
                }
            }
        }

        private void show() {
            alpha += 30;
            if (alpha > 255) {
                alpha = 255;
            }
            widthNow = widthMax;
            heightNow = (float) (heightMax / REC_CHANGE_CYCLE_TIMES * 2.0 * (time - REC_CHANGE_CYCLE_TIMES / 2));
            xNow = xInit;
            yNow = (float) (yInit + (heightMax - heightNow) / 2.0);
        }

        private void hide() {
            alpha -= 17;
            if (alpha < 0) {
                alpha = 0;
            }
            widthNow = (float) (1.0 * widthMax * (1 - time / REC_CHANGE_CYCLE_TIMES));
            heightNow = (float) (1.0 * heightMax * (1 - time / REC_CHANGE_CYCLE_TIMES));
            if (xInit < getMeasuredWidth()) {
                // left.
                xNow = (float) (xInit + (widthMax - widthNow) / 2.0);
            } else {
                xNow = (float) (xInit - (widthMax - widthNow) / 2.0);
            }
            yNow = (float) (yInit + (heightMax - heightNow) / 2.0);
        }

        public WaveRec getSwipeWave(float x) {
            if (x < 1) {
                return null;
            }
            if (x > REC_CHANGE_CYCLE_TIMES / 2) {
                x = REC_CHANGE_CYCLE_TIMES / 2;
            }

            float height = (float) (heightMax / REC_CHANGE_CYCLE_TIMES * 2.0 * x);
            return new WaveRec(xInit, (float) (yInit + (heightMax - height) / 2.0), widthMax, height, (int) x);
        }
    }

    public float getHeightScale(int position) {
        switch (position) {
            case 0:
                return 0.9F;
            case 1:
                return 0.95F;
            case 2:
                return 1;
            case 3:
                return 0.95F;
            case 4:
                return 0.9F;
            default:
                return 1;
        }
    }

    /** <br> interface. */

    public interface OnLoadingListener {
        void refresh();
        void hideFinish();
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }
}
