package com.wangdaye.waves.ui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wangdaye.waves.R;

/**
 * <br> tag view.
 * */

public class TagView extends ViewGroup {
    // data
    private int margin;
    private int tagHeight;

    /** <br> life cycle. */

    public TagView(Context context) {
        super(context);
        this.initialize();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.margin = (int) (8 * (getResources().getDisplayMetrics().densityDpi / 160.0));
        this.tagHeight = (int) (48 * (getResources().getDisplayMetrics().densityDpi / 160.0));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> parent methods. */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            Button button = (Button) child.findViewById(R.id.item_tag);
            child.measure((int) button.getPaint().measureText(button.getText().toString()), heightMeasureSpec);
        }

        int x = 0, y = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            if (x + 2 * margin + child.getMeasuredWidth() > getResources().getDisplayMetrics().widthPixels) {
                x = 0;
                y += 2 * margin + child.getMeasuredHeight();
            }
            x += 2 * margin + child.getMeasuredWidth();
        }

        this.setMeasuredDimension(
                getResources().getDisplayMetrics().widthPixels,
                getChildCount() > 0 ? y + 2 * margin + getChildAt(0).getMeasuredHeight() : 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = 0, y = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            if (x + 2 * margin + child.getMeasuredWidth() > getMeasuredWidth()) {
                x = 0;
                y += 2 * margin + child.getMeasuredHeight();
            }
            child.layout(
                    x + margin,
                    y + margin,
                    x + margin + child.getMeasuredWidth(),
                    y + margin + child.getMeasuredHeight());
            x += 2 * margin + child.getMeasuredWidth();
        }
    }

    /** <br> data. */

    @SuppressLint("InflateParams")
    public void setTags(String[] tags) {
        for (String t : tags) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_tag, null);
            Button button = (Button) v.findViewById(R.id.item_tag);
            button.setText(t);
            LayoutParams params = new LayoutParams(
                    (int) button.getPaint().measureText(t),
                    LayoutParams.WRAP_CONTENT);
            addView(v, params);
        }
    }
}
