package com.wangdaye.waves.data.dirbbble.tools;

import com.google.gson.GsonBuilder;
import com.wangdaye.waves.data.dirbbble.api.Constants;
import com.wangdaye.waves.data.dirbbble.api.DataLoadingSubject;
import com.wangdaye.waves.data.dirbbble.api.DribbbleCommentsAPI;
import com.wangdaye.waves.data.dirbbble.api.DribbbleSearchAPI;
import com.wangdaye.waves.data.dirbbble.api.DribbbleSearchConverter;
import com.wangdaye.waves.data.dirbbble.api.DribbbleShotsAPI;
import com.wangdaye.waves.data.dirbbble.model.Comment;
import com.wangdaye.waves.data.dirbbble.model.Shot;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Shots getter, used to get shots list from Dribbble.
 * */

public class DribbbleService {
    // widget
    private List<DataLoadingSubject.DataLoadingCallbacks> loadingCallbacks;

    private GetShotsListener getShotsListener;
    private GetOneShotListener getOneShotListener;
    private GetCommentsListener getCommentsListener;

    /** <br> shot. */

    public void getDribbbleShots(String shotSort, String shotList, int page, boolean loadMore) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(Constants.CLIENT_TOKEN))
                .build();

        DribbbleShotsAPI api = new Retrofit.Builder()
                .baseUrl(Constants.DRIBBBLE_API)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Constants.DATE_FORMAT)
                                        .create()))
                .build()
                .create((DribbbleShotsAPI.class));

        new ShotsManager(shotSort, shotList).selectApi(api, page, loadMore, getShotsListener);
    }

    /** <br> search. */

    public void searchDribbbleShots(String text, String sort, int page, final boolean loadMore) {

        DribbbleSearchAPI api = new Retrofit.Builder()
                .baseUrl(DribbbleSearchAPI.ENDPOINT)
                .addConverterFactory(new DribbbleSearchConverter.Factory())
                .build()
                .create((DribbbleSearchAPI.class));

        new SearchManager(sort).selectApi(api, text, page, loadMore, getShotsListener);
    }

    /** <br> comments */

    public void getDribbbleComments(long shotId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(Constants.CLIENT_TOKEN))
                .build();

        DribbbleCommentsAPI api = new Retrofit.Builder()
                .baseUrl(Constants.DRIBBBLE_API)
                .client(client)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setDateFormat(Constants.DATE_FORMAT)
                                        .create()))
                .build()
                .create((DribbbleCommentsAPI.class));

        new CommentsManager().selectApi(api, shotId, getCommentsListener);
    }

    /** <br> getters & setters. */

    public void cleanGetShotsListener() {
        this.getShotsListener = null;
    }

    public void setGetShotsListener(GetShotsListener getShotsListener) {
        this.getShotsListener = getShotsListener;
    }

    public void cleanGetOneShotListener() {
        this.getOneShotListener = null;
    }

    public void setGetOneShotListener(GetOneShotListener getOneShotListener) {
        this.getOneShotListener = getOneShotListener;
    }

    public void cleanGetCommentsListener() {
        this.getCommentsListener = null;
    }

    public void setGetCommentsListener(GetCommentsListener getCommentsListener) {
        this.getCommentsListener = getCommentsListener;
    }

    /** <br> interface. */

    public interface GetShotsListener {
        void getShotsSuccess(Call<List<Shot>> call, retrofit2.Response<List<Shot>> response, boolean loadMore);
        void getShotsFailed(Call<List<Shot>> call, Throwable t, boolean loadMore);
    }

    public interface GetOneShotListener {
        void getOneShotSuccess();
        void getOneShotFailed();
    }

    public interface GetCommentsListener {
        void getCommentsSuccess(Call<List<Comment>> call, retrofit2.Response<List<Comment>> response);
        void getCommentsFailed(Call<List<Comment>> call, Throwable t);
    }
}
