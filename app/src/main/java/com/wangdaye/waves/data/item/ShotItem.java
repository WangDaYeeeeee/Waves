package com.wangdaye.waves.data.item;

import com.wangdaye.waves.data.dirbbble.model.Shot;

/**
 * Shot item.
 * */

public class ShotItem {
    // data
    public String imageUri;
    public boolean isGif;

    public long shotId;
    public String webUri;
    public String title;
    public String subTitle;
    public String playerIconUri;
    public String content;
    public long likes;
    public long views;
    public long buckets;
    public long comments;
    public String[] tags;

    public ShotItem(Shot shot) {
        if (shot.images.hidpi != null && !shot.images.hidpi.equals("")) {
            this.imageUri = shot.images.hidpi;
        } else {
            this.imageUri = shot.images.normal;
        }
        this.isGif = shot.animated;

        this.shotId = shot.id;
        this.webUri = shot.html_url;
        this.title = shot.title;
        if (shot.created_at != null) {
            this.subTitle = shot.user.username + ", " + shot.created_at.toString().split("T")[0];
        } else {
            this.subTitle = shot.user.username;
        }
        this.playerIconUri = shot.user.avatar_url;
        this.content = shot.description == null ? "" : shot.description;
        this.likes = shot.likes_count;
        this.views = shot.views_count;
        this.buckets = shot.buckets_count;
        this.comments = shot.comments_count;
        if (shot.tags != null) {
            this.tags = shot.tags.toArray(new String[shot.tags.size()]);
        } else {
            this.tags = new String[0];
        }
    }
}
