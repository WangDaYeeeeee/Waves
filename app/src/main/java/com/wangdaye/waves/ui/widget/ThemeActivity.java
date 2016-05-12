package com.wangdaye.waves.ui.widget;

import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.wangdaye.waves.R;
import com.wangdaye.waves.utils.ImageUtils;

/**
 * Theme activity, extends AppCompatActivity class.
 * */

public abstract class ThemeActivity extends AppCompatActivity {

    public void initColorTheme(FrameLayout statusBar, String name, int color) {
        this.setWindowTop(name, color);
        this.initStatusBar(statusBar);
    }

    public void setWindowTop(String name, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = ImageUtils.readBitmapFormSrc(this,
                    R.drawable.ic_launcher, 144, 144, getResources().getDisplayMetrics().widthPixels);
            ActivityManager.TaskDescription taskDescription
                    = new ActivityManager.TaskDescription(name, icon, ContextCompat.getColor(this, color));
            setTaskDescription(taskDescription);
            icon.recycle();
        }
    }

    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void initStatusBar(FrameLayout statusBar) {
        if (statusBar == null) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) statusBar.getLayoutParams();
        layoutParams.height = getStatusBarHeight();
        statusBar.setLayoutParams(layoutParams);
        statusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    public int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
