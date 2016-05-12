package com.wangdaye.waves.data.dirbbble.tools;

import com.wangdaye.waves.data.dirbbble.api.DribbbleSearchAPI;
import com.wangdaye.waves.data.dirbbble.api.DribbbleShotsAPI;
import com.wangdaye.waves.data.dirbbble.model.Shot;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Shots search manager, help DribbbleService to search shot list.
 * */

public class SearchManager {
    // data
    private String sort;

    public SearchManager(String sort) {
        this.sort = sort;
    }

    public void selectApi(DribbbleSearchAPI api, String text, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> searchShotsList;
        if (sort.equals(DribbbleShotsAPI.SHOT_SORT_POPULAR)) {
            searchShotsList = api.searchShots(
                    text, page, DribbbleSearchAPI.PER_PAGE_DEFAULT, DribbbleSearchAPI.SORT_POPULAR);
        } else {
            searchShotsList = api.searchShots(
                    text, page, DribbbleSearchAPI.PER_PAGE_DEFAULT, DribbbleSearchAPI.SORT_RECENT);
        }

        searchShotsList.enqueue(new Callback<List<Shot>>() {
            @Override
            public void onResponse(Call<List<Shot>> call, retrofit2.Response<List<Shot>> response) {
                if (listener != null) {
                    listener.getShotsSuccess(call, response, loadMore);
                }
            }

            @Override
            public void onFailure(Call<List<Shot>> call, Throwable t) {
                if (listener != null) {
                    listener.getShotsFailed(call, t, loadMore);
                }
            }
        });
    }
}
