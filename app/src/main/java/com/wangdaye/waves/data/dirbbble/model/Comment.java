package com.wangdaye.waves.data.dirbbble.model;

import android.text.Spanned;

import java.util.Date;

/**
 * Models a commend on a Dribbble shot.
 */
public class Comment {

    public long id;
    public String body;
    public String likes_url;
    public Date created_at;
    public Date updated_at;
    public User user;
    public long likes_count;
    // todo move this into a decorator
    public Boolean liked;
    public Spanned parsedBody;
}
