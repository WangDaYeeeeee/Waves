package com.wangdaye.waves.data.dirbbble.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Models a a follow of a dribbble user
 */
public class Follow {

    public long id;
    public Date created_at;
    @SerializedName("follower")
    public User user;
}
