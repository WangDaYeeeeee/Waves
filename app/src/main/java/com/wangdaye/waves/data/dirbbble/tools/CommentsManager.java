package com.wangdaye.waves.data.dirbbble.tools;

import com.wangdaye.waves.data.dirbbble.api.DribbbleCommentsAPI;
import com.wangdaye.waves.data.dirbbble.model.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Comments manager, help DribbbleService to get or post comments data.
 * */

public class CommentsManager {

    public void selectApi(DribbbleCommentsAPI api, long shotId, DribbbleService.GetCommentsListener listener) {
        this.getCommentsForOneShot(api, shotId, listener);
    }

    private void getCommentsForOneShot(DribbbleCommentsAPI api, long shotId, final DribbbleService.GetCommentsListener listener) {
        Call<List<Comment>> getComments = api.getComments(shotId, 1, 5);
        getComments.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (listener != null) {
                    listener.getCommentsSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                if (listener != null) {
                    listener.getCommentsFailed(call, t);
                }
            }
        });
    }
}
