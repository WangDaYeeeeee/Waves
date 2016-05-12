package com.wangdaye.waves.data.dirbbble.model;

import java.util.Date;

/**
 * An interface for model items that can be displayed as a list of players.
 */
public interface PlayerListable {

    User getPlayer();
    long getId();
    Date getDateCreated();

}
