package com.wangdaye.waves.ui.widget;

import android.app.Fragment;

/**
 * Reveal fragment, coordinate reveal view to show reveal animation.
 * */

public abstract class RevealFragment extends Fragment {
    // data
    public int circleColor;
    public int backgroundColor;

    public void setColorSrc(int circleColor, int backgroundColor) {
        this.circleColor = circleColor;
        this.backgroundColor = backgroundColor;
    }

    public abstract void hide();
}
