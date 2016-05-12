package com.wangdaye.waves.data.item;

/**
 * Comment item.
 * */

public class CommentItem {
    // data
    public long playerId;
    public String iconUrl;
    public String title;
    public String content;

    public CommentItem(long playerId, String iconUrl, String title, String content) {
        this.playerId = playerId;
        this.iconUrl = iconUrl;
        this.title = title;
        this.content = content;
    }
}
