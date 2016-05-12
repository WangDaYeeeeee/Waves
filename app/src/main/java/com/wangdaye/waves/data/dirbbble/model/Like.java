package com.wangdaye.waves.data.dirbbble.model;

import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Models a like of a Dribbble shot.
 */
public class Like {

    public long id;
    public Date created_at;
    public @Nullable User user; // some calls do not populate the user field
    public @Nullable Shot shot; // some calls do not populate the shot field
}
