package com.wangdaye.waves.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.adapter.ShotsAdapter;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.data.dirbbble.model.Shot;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.activity.ShotActivity;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.HomeRecyclerView;
import com.wangdaye.waves.ui.widget.MySwipeRefreshLayout;
import com.wangdaye.waves.utils.ImageUtils;
import com.wangdaye.waves.utils.SafeHandler;
import com.wangdaye.waves.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Home fragment
 * */

public class HomeFragment extends Fragment
        implements View.OnClickListener, ShotsAdapter.MyItemClickListener, HomeRecyclerView.OnLoadMoreListener,
        DribbbleService.GetShotsListener, MySwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener,
        FiltrateFragment.OnFiltrateListener, SearchFragment.OnSearchListener, SafeHandler.HandlerContainer {
    // widget
    private Toolbar toolbar;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private HomeRecyclerView recyclerView;

    private ShotsAdapter.LoadFinishCallback loadFinishCallback;
    private SafeHandler<HomeFragment> handler;

    // data
    private DribbbleService dribbbleService;
    private ShotsAdapter adapter;

    private String shotSort;
    private String shotList;
    private int page;

    public boolean isSearching;
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.initData();
        this.initWidget(view);
        dribbbleService.getDribbbleShots(shotSort, shotList, 1, false);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }, 10);
        return view;
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.handler = new SafeHandler<>(this);

        this.toolbar = (Toolbar) view.findViewById(R.id.fragment_home_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_burger_menu);
        toolbar.inflateMenu(R.menu.menu_fragment_home);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOnClickListener(this);

        MyFloatingActionButton fab = (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab);

        this.swipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.fragment_home_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new ShotsAdapter(getActivity(), new ArrayList<ShotItem>());
        this.adapter.setOnItemClickListener(this);

        this.recyclerView = (HomeRecyclerView) view.findViewById(R.id.fragment_home_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setFab(fab);
        recyclerView.setOnMyOnScrollListener();
        recyclerView.setItemAnimator(new ShotsAdapter.ShotItemAnimator());
        recyclerView.setOnLoadMoreListener(this);
        this.loadFinishCallback = recyclerView;
        recyclerView.setAdapter(adapter);
    }

    public void searchFinish() {
        this.isSearching = false;
        this.text = null;
        toolbar.setTitle(getString(R.string.app_name));
        swipeRefreshLayout.setState(MySwipeRefreshLayout.REFRESHING);
        ((MainActivity) getActivity()).setWindowTop(getString(R.string.app_name), R.color.colorPrimary);
    }

    /** <br> data. */

    private void initData() {
        this.dribbbleService = new DribbbleService();
        dribbbleService.setGetShotsListener(this);

        MainActivity container = (MainActivity) getActivity();
        this.shotSort = container.getFiltrateData()[0];
        this.shotList = container.getFiltrateData()[1];
        this.page = 0;

        this.isSearching = false;
        this.text = null;
    }

    /** <br> interface. */

    // on filtrate chang listener.

    @Override
    public void changeFiltrateData(String shotSort, String shotList) {
        this.shotSort = shotSort;
        this.shotList = shotList;
        Log.d("Home", shotSort + " " + shotList);
        swipeRefreshLayout.setState(MySwipeRefreshLayout.REFRESHING);
    }

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.fragment_home_toolbar:
                recyclerView.scrollToTop();
                break;
        }
    }

    // on item click.

    @Override
    public void onItemClick(View view, int position) {
        ShotItem shot = adapter.itemList.get(position);
        Intent intent = new Intent(getActivity(), ShotActivity.class);
        intent.putExtra(getString(R.string.key_shot_id), shot.shotId);
        intent.putExtra(getString(R.string.key_image_uri), shot.imageUri);
        intent.putExtra(getString(R.string.key_player_icon_uri), shot.playerIconUri);
        intent.putExtra(getString(R.string.key_web_uri), shot.webUri);
        intent.putExtra(getString(R.string.key_title), shot.title);
        intent.putExtra(getString(R.string.key_sub_title), shot.subTitle);
        intent.putExtra(getString(R.string.key_content), shot.content);
        intent.putExtra(getString(R.string.key_likes), shot.likes);
        intent.putExtra(getString(R.string.key_views), shot.views);
        intent.putExtra(getString(R.string.key_buckets), shot.buckets);
        Bundle values = new Bundle();
        values.putStringArray(getString(R.string.key_tags), shot.tags);
        intent.putExtra(getString(R.string.key_values), values);
        intent.putExtra(getString(R.string.key_touch_x), recyclerView.touchX);
        intent.putExtra(getString(R.string.key_touch_y), recyclerView.touchY);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_shot_mini_image);
        intent.putExtra(
                getString(R.string.key_reveal_color),
                ImageUtils.drawableToBitmap(imageView.getDrawable())
                        .getPixel(
                                (int) (recyclerView.touchX % imageView.getMeasuredWidth()),
                                (int) (recyclerView.touchY % imageView.getMeasuredHeight())));

        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    // on menu item click.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        MainActivity container = (MainActivity) getActivity();
        MyFloatingActionButton fab = (MyFloatingActionButton) container.findViewById(R.id.container_main_fab);

        switch (item.getItemId()) {

            case R.id.action_search:
                if (fab != null) {
                    fab.hide();
                }

                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setData(isSearching ? text : null);
                searchFragment.setOnSearchListener(this);
                container.insertFragment(searchFragment);
                break;

            case R.id.action_filtrate:
                if (fab != null) {
                    fab.hide();
                }

                FiltrateFragment filtrateFragment = new FiltrateFragment();
                filtrateFragment.setData(shotSort, shotList);
                filtrateFragment.setOnFiltrateListener(this);
                container.insertFragment(filtrateFragment);
                break;
        }
        return false;
    }

    // my recycler view load more data listener.

    @Override
    public void onLoadMore() {
        dribbbleService.getDribbbleShots(shotSort, shotList, ++ page, true);
    }

    // my swipe refresh layout listener.

    @Override
    public void refreshNew() {
        if (isSearching) {
            dribbbleService.searchDribbbleShots(text, shotSort, 1, false);
        } else {
            dribbbleService.getDribbbleShots(shotSort, shotList, 1, false);
        }
    }

    @Override
    public void loadMore() {
        if (isSearching) {
            dribbbleService.searchDribbbleShots(text, shotSort, ++ page, true);
        } else {
            dribbbleService.getDribbbleShots(shotSort, shotList, ++ page, true);
        }
    }

    // on search listener.

    @Override
    public void onSearch(String text) {
        this.isSearching = true;
        this.text = text;

        toolbar.setTitle(getString(R.string.search) + " " + text);
        swipeRefreshLayout.setState(MySwipeRefreshLayout.REFRESHING);

        ((MainActivity) getActivity()).setWindowTop(getString(R.string.search) + " " + text, R.color.colorPrimary);
    }

    // dribbble shots api.

    @Override
    public void getShotsSuccess(Call<List<Shot>> call, Response<List<Shot>> response, boolean loadMore) {
        if (response.isSuccessful()) {
            if (loadMore) {
                page ++;
            } else {
                page = 1;
                adapter.itemList = new ArrayList<>();
                adapter.notifyDataSetChanged();
            }

            for (int i = 0; i < response.body().size(); i ++) {
                adapter.insertData(new ShotItem(response.body().get(i)), adapter.itemList.size());
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.get_shots_data_failed), Toast.LENGTH_SHORT).show();
        }

        if (loadMore) {
            loadFinishCallback.loadFinish(this);
        }

        if (adapter.getItemCount() > 0) {
            swipeRefreshLayout.setState(MySwipeRefreshLayout.ALL_DONE);
        } else {
            swipeRefreshLayout.setState(MySwipeRefreshLayout.NO_DATA);
        }
    }

    @Override
    public void getShotsFailed(Call<List<Shot>> call, Throwable t, boolean loadMore) {
        Toast.makeText(getActivity(),
                getString(R.string.get_shots_data_failed) + "\n" + t.getMessage(),
                Toast.LENGTH_SHORT).show();
        if (loadMore) {
            loadFinishCallback.loadFinish(this);
        }

        if (adapter.getItemCount() > 0) {
            swipeRefreshLayout.setState(MySwipeRefreshLayout.ALL_DONE);
        } else {
            swipeRefreshLayout.setState(MySwipeRefreshLayout.NO_DATA);
        }
    }

    /** <br> handler. */

    @Override
    public void handleMessage(Message message) {
        this.toolbar = TypefaceUtils.setToolbarTypeface(getActivity(), toolbar);
    }
}