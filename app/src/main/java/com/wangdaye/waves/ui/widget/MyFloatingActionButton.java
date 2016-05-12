package com.wangdaye.waves.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.wangdaye.waves.R;

/**
 * My floating action button.
 * */

public class MyFloatingActionButton extends FloatingActionButton {
    // data
    public boolean showing;

    /** <br> life cycle. */

    public MyFloatingActionButton(Context context) {
        super(context);
        this.initialize(context);
    }

    public MyFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
    }

    public MyFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context);
    }

    private void initialize(Context context) {
        this.showing = false;
    }

    /** <br> UI. */

    @Override
    public void show() {
        if (!showing) {
            showing = true;
            AnimatorSet fabShow = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.fab_show);
            fabShow.setTarget(this);
            fabShow.start();
        }
    }

    @Override
    public void hide() {
        if (showing) {
            showing = false;
            AnimatorSet fabHide = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.fab_hide);
            fabHide.setTarget(this);
            fabHide.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            fabHide.start();
        }
    }

    public int calcMargin(float shotBarMaxiHeight, float scrollLength, float stopLength) {
        if (stopLength == 0) {
            return (int) (shotBarMaxiHeight + 52 * (getContext().getResources().getDisplayMetrics().densityDpi / 160.0));
        } else if (scrollLength > stopLength) {
            return (int) (shotBarMaxiHeight + 52 * (getContext().getResources().getDisplayMetrics().densityDpi / 160.0) - stopLength);
        } else {
            return (int) (shotBarMaxiHeight + 52 * (getContext().getResources().getDisplayMetrics().densityDpi / 160.0) - scrollLength);
        }
    }
}
