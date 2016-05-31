package com.wangdaye.waves.ui.widget.imageView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Shot view,
 * it can keep the proportion by 4:3.
 * */

public class ShotView extends ImageView {
    // data
    private boolean mask = false;

    /** <br> life cycle. */

    public ShotView(Context context) {
        super(context);
    }

    public ShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMask(boolean mask) {
        this.mask = mask;
        invalidate();
    }

    /** <br> parent methods. */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() / 4.0 * 3.0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mask) {
            canvas.drawColor(Color.argb((int) (255 * 0.7), 33, 33, 33));
        }
    }
}
