package com.wangdaye.waves.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Typeface utils.
 * */

public class TypefaceUtils {

    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/LondonBetween.ttf");
    }

    public static Toolbar setToolbarTypeface(Context context, Toolbar toolbar) {
        Field field = null;
        try {
            field = Toolbar.class.getDeclaredField("mTitleTextView");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert field != null;
        field.setAccessible(true);
        TextView textView = null;
        try {
            textView = (TextView) field.get(toolbar);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Typeface typeface = getTypeface(context);
        assert textView != null;
        textView.setTypeface(typeface);
        try {
            field.set(toolbar, textView);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return toolbar;
    }
}
