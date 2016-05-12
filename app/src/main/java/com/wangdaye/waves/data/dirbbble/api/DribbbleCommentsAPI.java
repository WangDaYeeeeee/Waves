package com.wangdaye.waves.data.dirbbble.api;

import com.wangdaye.waves.data.dirbbble.model.Comment;
import com.wangdaye.waves.data.dirbbble.model.Like;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Dribbble comments API.
 * */

public interface DribbbleCommentsAPI {

    @GET("shots/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") long shotId,
                                    @Query("page") Integer page,
                                    @Query("per_page") Integer pageSize);

    @GET("shots/{shot}/comments/{id}/likes")
    Call<List<Like>> getCommentLikes(@Path("shot") long shotId,
                                     @Path("id") long commentId);

    @POST("shots/{shot}/comments")
    Call<Comment> postComment(@Path("shot") long shotId,
                              @Query("body") String body);


    @DELETE("shots/{shot}/comments/{id}")
    Call<Void> deleteComment(@Path("shot") long shotId,
                             @Path("id") long commentId);

    @GET("shots/{shot}/comments/{id}/like")
    Call<Like> likedComment(@Path("shot") long shotId,
                            @Path("id") long commentId);

    @POST("shots/{shot}/comments/{id}/like")
    Call<Like> likeComment(@Path("shot") long shotId,
                           @Path("id") long commentId);

    @DELETE("shots/{shot}/comments/{id}/like")
    Call<Void> unlikeComment(@Path("shot") long shotId,
                             @Path("id") long commentId);
}
