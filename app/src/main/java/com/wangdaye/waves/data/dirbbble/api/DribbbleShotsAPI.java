package com.wangdaye.waves.data.dirbbble.api;

import android.support.annotation.StringDef;

import com.wangdaye.waves.data.dirbbble.model.Shot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Dribble shots API.
 * */

public interface DribbbleShotsAPI {
    // data
    String SHOT_SORT_POPULAR = "popular";
    String SHOT_SORT_COMMENTS = "comments";
    String SHOT_SORT_RECENT = "recent";
    String SHOT_SORT_VIEWS = "views";

    String SHOT_LIST_ANY_TYPE = "any_type";
    String SHOT_LIST_ANIMATED = "animated";
    String SHOT_LIST_ATTACHMENTS = "attachments";
    String SHOT_LIST_DEBUTS = "debuts";
    String SHOT_LIST_PLAYOFFS = "playoffs";
    String SHOT_LIST_REBOUNDS = "rebounds";
    String SHOT_LIST_TEAMS = "teams";

    int PER_PAGE_DEFAULT = 30;

    /** <br> get shot list. */

    @GET("shots")
    Call<List<Shot>> getPopular(@Query("page") Integer page,
                                @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent")
    Call<List<Shot>> getRecent(@Query("page") Integer page,
                                    @Query("per_page") Integer pageSize);

    @GET("shots?list=animated")
    Call<List<Shot>> getPopularAnimated(@Query("page") Integer page,
                                 @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=animated")
    Call<List<Shot>> getRecentAnimated(@Query("page") Integer page,
                                       @Query("per_page") Integer pageSize);

    @GET("shots?list=attachments")
    Call<List<Shot>> getPopularAttachments(@Query("page") Integer page,
                                        @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=attachments")
    Call<List<Shot>> getRecentAttachments(@Query("page") Integer page,
                                       @Query("per_page") Integer pageSize);

    @GET("shots?list=debuts")
    Call<List<Shot>> getPopularDebuts(@Query("page") Integer page,
                                           @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=debuts")
    Call<List<Shot>> getRecentDebuts(@Query("page") Integer page,
                                          @Query("per_page") Integer pageSize);

    @GET("shots?list=playoffs")
    Call<List<Shot>> getPopularPlayoffs(@Query("page") Integer page,
                                      @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=playoffs")
    Call<List<Shot>> getRecentPlayoffs(@Query("page") Integer page,
                                     @Query("per_page") Integer pageSize);

    @GET("shots?list=rebounds")
    Call<List<Shot>> getPopularRebounds(@Query("page") Integer page,
                                        @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=rebounds")
    Call<List<Shot>> getRecentRebounds(@Query("page") Integer page,
                                       @Query("per_page") Integer pageSize);

    @GET("shots?list=teams")
    Call<List<Shot>> getPopularTeams(@Query("page") Integer page,
                                        @Query("per_page") Integer pageSize);

    @GET("shots?sort=recent&list=teams")
    Call<List<Shot>> getRecentTeams(@Query("page") Integer page,
                                       @Query("per_page") Integer pageSize);

    /** <br> get shot. */

    @GET("shots/{id}")
    Call<Shot> getShot(@Path("id") long shotId);

    @GET("user/following/shots")
    Call<List<Shot>> getFollowing(@Query("page") Integer page,
                                  @Query("per_page") Integer pageSize);

    // Shot type
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SHOT_LIST_ANIMATED,
            SHOT_LIST_ATTACHMENTS,
            SHOT_LIST_DEBUTS,
            SHOT_LIST_PLAYOFFS,
            SHOT_LIST_REBOUNDS,
            SHOT_LIST_TEAMS
    })
    @interface ShotType {}

    // Short sort order
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SHOT_SORT_COMMENTS,
            SHOT_SORT_RECENT,
            SHOT_SORT_VIEWS
    })
    @interface ShotSort {}
}