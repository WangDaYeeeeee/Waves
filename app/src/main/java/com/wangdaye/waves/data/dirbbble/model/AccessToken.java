package com.wangdaye.waves.data.dirbbble.model;

/**
 * Access token.
 * */

public class AccessToken {

    public final String access_token;
    public final String token_type;
    public final String scope;

    public AccessToken(String access_token, String token_type, String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.scope = scope;
    }
}
