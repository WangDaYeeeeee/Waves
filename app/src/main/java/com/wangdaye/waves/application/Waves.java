package com.wangdaye.waves.application;

import android.app.Application;

/**
 * Waves application class.
 * */

public class Waves extends Application {
    // widget
    private static Waves instance;

    // data
    public static final int WRITE_EXTERNAL_STORAGE = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Waves getInstance() {
        return instance;
    }
}