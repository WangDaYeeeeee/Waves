package com.wangdaye.waves.application;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Waves application class.
 * */

public class Waves extends Application {
    // widget
    private RefWatcher refWatcher;

    // data
    public static final int WRITE_EXTERNAL_STORAGE = 2;

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        Waves waves = (Waves) context.getApplicationContext();
        return waves.refWatcher;
    }
}