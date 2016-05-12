package com.wangdaye.waves.data.dirbbble.api;

import android.support.annotation.StringDef;

import com.wangdaye.waves.data.dirbbble.model.Shot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Dribbble search API.
 * */

public interface DribbbleSearchAPI {
    // data
    String ENDPOINT = "https://dribbble.com/";
    String SORT_POPULAR = "";
    String SORT_RECENT = "latest";
    int PER_PAGE_DEFAULT = 30;

    @GET("search")
    Call<List<Shot>> searchShots(@Query("q") String query,
                            @Query("page") Integer page,
                            @Query("per_page") Integer pageSize,
                            @Query("s") @SortOrder String sort);


    /** magic constants **/

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SORT_POPULAR,
            SORT_RECENT
    })
    @interface SortOrder { }
}
