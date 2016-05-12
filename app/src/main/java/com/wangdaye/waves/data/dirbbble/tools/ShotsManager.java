package com.wangdaye.waves.data.dirbbble.tools;

import com.wangdaye.waves.data.dirbbble.api.DribbbleShotsAPI;
import com.wangdaye.waves.data.dirbbble.model.Shot;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Shot get manager, help DribbbleService to get shots.
 * */

public class ShotsManager {
    // data
    private String sort;
    private String list;

    public ShotsManager(String sort, String list) {
        this.sort = sort;
        this.list = list;
    }

    /** <br> select. */

    public void selectApi(DribbbleShotsAPI api, int page, final boolean loadMore, DribbbleService.GetShotsListener listener) {
        switch (sort) {

            case DribbbleShotsAPI.SHOT_SORT_POPULAR:
                switch (list) {

                    case DribbbleShotsAPI.SHOT_LIST_ANY_TYPE:
                        this.getPopularShots(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_ANIMATED:
                        this.getPopularAnimated(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_ATTACHMENTS:
                        this.getPopularAttachments(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_DEBUTS:
                        this.getPopularDebuts(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_PLAYOFFS:
                        this.getPopularPlayoffs(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_REBOUNDS:
                        this.getPopularRebounds(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_TEAMS:
                        this.getPopularTeams(api, page, loadMore, listener);
                        break;
                }
                break;

            case DribbbleShotsAPI.SHOT_SORT_RECENT:
                switch (list) {

                    case DribbbleShotsAPI.SHOT_LIST_ANY_TYPE:
                        this.getRecentShots(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_ANIMATED:
                        this.getRecentAnimated(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_ATTACHMENTS:
                        this.getRecentAttachments(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_DEBUTS:
                        this.getRecentDebuts(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_PLAYOFFS:
                        this.getRecentPlayoffs(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_REBOUNDS:
                        this.getRecentRebounds(api, page, loadMore, listener);
                        break;

                    case DribbbleShotsAPI.SHOT_LIST_TEAMS:
                        this.getRecentTeams(api, page, loadMore, listener);
                        break;
                }
                break;
        }
    }

    /** <br> api. */

    // any type.

    private void getPopularShots(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopular(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentShots(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecent(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // animated.

    private void getPopularAnimated(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularAnimated(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentAnimated(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentAnimated(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // attachments.

    private void getPopularAttachments(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularAttachments(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentAttachments(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentAttachments(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // debuts.

    private void getPopularDebuts(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularDebuts(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentDebuts(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentDebuts(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // playoffs.

    private void getPopularPlayoffs(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularPlayoffs(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentPlayoffs(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentPlayoffs(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // rebounds.

    private void getPopularRebounds(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularRebounds(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentRebounds(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentRebounds(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    // teams.

    private void getPopularTeams(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getPopularTeams(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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

    private void getRecentTeams(DribbbleShotsAPI api, int page, final boolean loadMore, final DribbbleService.GetShotsListener listener) {
        Call<List<Shot>> getShotsList = api.getRecentTeams(page, DribbbleShotsAPI.PER_PAGE_DEFAULT);
        getShotsList.enqueue(new Callback<List<Shot>>() {
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
