package com.wangdaye.waves.utils;

import android.content.res.Resources;

/**
 * Display utils.
 * */

public class DisplayUtils {
    // data
    private static int dpi = 0;

    public static void setDisplayDpi(int dpi) {
        DisplayUtils.dpi = dpi;
    }

    public static float dpToPx(int dp) {
        if (dpi == 0) {
            return 0;
        }
        return (float) (dp * (dpi / 160.0));
    }

    public static int getNavigationBarHeight(Resources r) {
        int resourceId = r.getIdentifier("navigation_bar_height", "dimen", "android");
        return r.getDimensionPixelSize(resourceId);
    }

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen","android");
        return r.getDimensionPixelSize(resourceId);
    }
}
