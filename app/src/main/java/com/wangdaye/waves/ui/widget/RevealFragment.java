package com.wangdaye.waves.ui.widget;

import android.app.Fragment;

/**
 * Reveal fragment, coordinate reveal view to show reveal animation.
 * */

public class RevealFragment extends Fragment {
    // data
    public int circleColor;
    public int backgroundColor;

    public void setColor(int circleColor, int backgroundColor) {
        this.circleColor = circleColor;
        this.backgroundColor = backgroundColor;
    }

    public void hide() {

    }
}
