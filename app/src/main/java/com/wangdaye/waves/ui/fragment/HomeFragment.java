package com.wangdaye.waves.ui.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.adapter.ShotsAdapter;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.data.dirbbble.model.Shot;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.MyRecyclerView;
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
        implements View.OnClickListener, ShotsAdapter.MyItemClickListener, MyRecyclerView.OnLoadMoreListener,
        DribbbleService.GetShotsListener, MySwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener,
        FiltrateFragment.OnFiltrateListener, SearchFragment.OnSearchListener, SafeHandler.HandlerContainer {
    // widget
    private Toolbar toolbar;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private MyRecyclerView recyclerView;

    private ShotsAdapter.LoadFinishCallback loadFinishCallback;
    private SafeHandler<HomeFragment> handler;

    // data
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
        DribbbleService.instance.getDribbbleShots(shotSort, shotList, 1, false, this);

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

        FrameLayout statusBar = (FrameLayout) view.findViewById(R.id.fragment_home_statusBar);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusBar.getLayoutParams();
        params.height = ((MainActivity) getActivity()).getStatusBarHeight();
        statusBar.setLayoutParams(params);
        statusBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        this.toolbar = (Toolbar) view.findViewById(R.id.fragment_home_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_burger_menu);
        toolbar.inflateMenu(R.menu.menu_home);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOnClickListener(this);

        MyFloatingActionButton fab = (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab);

        this.swipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.fragment_home_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new ShotsAdapter(getActivity(), new ArrayList<ShotItem>());
        this.adapter.setOnItemClickListener(this);

        this.recyclerView = (MyRecyclerView) view.findViewById(R.id.fragment_home_recyclerView);
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
        ImageView imageView = (ImageView) view.findViewById(R.id.item_shot_mini_image);
        int x = (int) (recyclerView.touchX % imageView.getMeasuredWidth());
        int y = (int) (recyclerView.touchY % imageView.getMeasuredHeight());
        Bitmap bitmap = ImageUtils.drawableToBitmap(imageView.getDrawable());

        int revealColor;
        if (x < bitmap.getWidth() && y < bitmap.getHeight()) {
            revealColor = bitmap.getPixel(x, y);
        } else {
            revealColor = ContextCompat.getColor(getActivity(), R.color.colorRoot);
        }

        ShotFragment fragment = new ShotFragment();
        fragment.setData(getActivity(), shot, revealColor, recyclerView.touchX, recyclerView.touchY);
        ((MainActivity) getActivity()).insertFragment(fragment);
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
        DribbbleService.instance.getDribbbleShots(shotSort, shotList, ++ page, true, this);
    }

    // my swipe refresh layout listener.

    @Override
    public void refreshNew() {
        if (isSearching) {
            DribbbleService.instance.searchDribbbleShots(text, shotSort, 1, false, this);
        } else {
            DribbbleService.instance.getDribbbleShots(shotSort, shotList, 1, false, this);
        }
    }

    @Override
    public void loadMore() {
        if (isSearching) {
            DribbbleService.instance.searchDribbbleShots(text, shotSort, ++ page, true, this);
        } else {
            DribbbleService.instance.getDribbbleShots(shotSort, shotList, ++ page, true, this);
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