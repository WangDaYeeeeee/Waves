package com.wangdaye.waves.ui.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.dirbbble.api.DribbbleShotsAPI;
import com.wangdaye.waves.data.dirbbble.model.Shot;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.adapter.MyPagerAdapter;
import com.wangdaye.waves.ui.adapter.ShotsAdapter;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.MyRecyclerView;
import com.wangdaye.waves.ui.widget.imageView.ShotView;
import com.wangdaye.waves.ui.widget.swipeRefreshLayout.BothWaySwipeRefreshLayout;
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
 * Material Home fragment.
 * */

public class HomeFragment extends Fragment
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SafeHandler.HandlerContainer,
        SearchFragment.OnSearchListener, FiltrateFragment.OnFiltrateListener, ViewPager.OnPageChangeListener {
    // widget
    private Toolbar toolbar;
    private BothWaySwipeRefreshLayout[] swipeRefreshLayouts;
    private MyRecyclerView[] recyclerViews;
    private RelativeLayout[] airBallContainers;

    private ShotsAdapter.LoadFinishCallback loadFinishCallback;
    private SafeHandler<HomeFragment> handler;

    // data
    private int viewPage;
    private ShotsAdapter[] shotsAdapters;

    private String defaultSort;
    private String shotList;
    private int shotPage;
    private int[] shotItemType;

    public boolean isSearching;
    private String searchText;

    private final int CREATE_FRAGMENT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.initData();
        this.initWidget(view);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = CREATE_FRAGMENT;
                handler.sendMessage(msg);
            }
        }, 100);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).setStatusBarTransparent();
        handler.removeCallbacksAndMessages(null);
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.handler = new SafeHandler<>(this);

        this.toolbar = (Toolbar) view.findViewById(R.id.fragment_home_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_burger_menu);
        toolbar.inflateMenu(R.menu.menu_home);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOnClickListener(this);

        this.initPage(view);

        this.shotsAdapters = new ShotsAdapter[] {
                new ShotsAdapter(getActivity(), new ArrayList<ShotItem>(), shotItemType[0]),
                new ShotsAdapter(getActivity(), new ArrayList<ShotItem>(), shotItemType[1])
        };
        shotsAdapters[0].setOnItemClickListener(new MyItemClickListener(0));
        shotsAdapters[1].setOnItemClickListener(new MyItemClickListener(1));

        if (shotItemType[0] == ShotsAdapter.MINI_TILE
                || shotItemType[0] == ShotsAdapter.MINI_CARD_WITH_TITLE
                || shotItemType[0] == ShotsAdapter.MINI_CARD_WITHOUT_TITLE) {
            recyclerViews[0].setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerViews[0].setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
        recyclerViews[0].setRelateView(
                swipeRefreshLayouts[0],
                (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab));
        recyclerViews[0].setOnMyOnScrollListener();
        recyclerViews[0].setItemAnimator(new ShotsAdapter.ShotItemAnimator());
        recyclerViews[0].setOnLoadMoreListener(new MyOnLoadMoreListener(0));
        this.loadFinishCallback = recyclerViews[0];
        recyclerViews[0].setAdapter(shotsAdapters[0]);

        if (shotItemType[1] == ShotsAdapter.MINI_TILE
                || shotItemType[1] == ShotsAdapter.MINI_CARD_WITH_TITLE
                || shotItemType[1] == ShotsAdapter.MINI_CARD_WITHOUT_TITLE) {
            recyclerViews[1].setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            recyclerViews[1].setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
        recyclerViews[1].setRelateView(
                swipeRefreshLayouts[1],
                (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab));
        recyclerViews[1].setOnMyOnScrollListener();
        recyclerViews[1].setItemAnimator(new ShotsAdapter.ShotItemAnimator());
        recyclerViews[1].setOnLoadMoreListener(new MyOnLoadMoreListener(1));
        this.loadFinishCallback = recyclerViews[1];
        recyclerViews[1].setAdapter(shotsAdapters[1]);
    }

    @SuppressLint("InflateParams")
    private void initPage(View view) {
        View[] pages = new View[] {
                LayoutInflater.from(getActivity()).inflate(R.layout.container_home_page, null),
                LayoutInflater.from(getActivity()).inflate(R.layout.container_home_page, null)
        };

        this.swipeRefreshLayouts = new BothWaySwipeRefreshLayout[] {
                (BothWaySwipeRefreshLayout) pages[0].findViewById(R.id.container_home_page_swipeRefreshLayout),
                (BothWaySwipeRefreshLayout) pages[1].findViewById(R.id.container_home_page_swipeRefreshLayout)
        };
        swipeRefreshLayouts[0].setOnRefreshAndLoadListener(new MyOnRefreshListener(0));
        swipeRefreshLayouts[0].setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayouts[1].setOnRefreshAndLoadListener(new MyOnRefreshListener(1));
        swipeRefreshLayouts[1].setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        this.recyclerViews = new MyRecyclerView[] {
                (MyRecyclerView) pages[0].findViewById(R.id.container_home_page_recyclerView),
                (MyRecyclerView) pages[1].findViewById(R.id.container_home_page_recyclerView)
        };

        this.airBallContainers = new RelativeLayout[] {
                (RelativeLayout) pages[0].findViewById(R.id.container_airball_view_large),
                (RelativeLayout) pages[1].findViewById(R.id.container_airball_view_large)
        };
        airBallContainers[0].setVisibility(View.GONE);
        airBallContainers[1].setVisibility(View.GONE);
        airBallContainers[0].setOnClickListener(this);
        airBallContainers[1].setOnClickListener(this);

        ImageView[] airBallImages = new ImageView[]{
                (ImageView) pages[0].findViewById(R.id.container_airball_view_large_image),
                (ImageView) pages[1].findViewById(R.id.container_airball_view_large_image)
        };
        Glide.with(this)
                .load(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(airBallImages[0]);
        Glide.with(this)
                .load(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(airBallImages[1]);

        TextView[] airBallTexts = new TextView[]{
                (TextView) pages[0].findViewById(R.id.container_airball_view_large_airBall),
                (TextView) pages[1].findViewById(R.id.container_airball_view_large_airBall)
        };
        airBallTexts[0].setTypeface(TypefaceUtils.getTypeface(getActivity()));
        airBallTexts[1].setTypeface(TypefaceUtils.getTypeface(getActivity()));

        List<View> pageList = new ArrayList<>();
        List<String> tabList = new ArrayList<>();
        if (defaultSort.equals(DribbbleShotsAPI.SHOT_SORT_POPULAR)) {
            pageList.add(pages[0]);
            pageList.add(pages[1]);
            tabList.add("Popular");
            tabList.add("Recent");
        } else {
            pageList.add(pages[1]);
            pageList.add(pages[0]);
            tabList.add("Recent");
            tabList.add("Popular");
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fragment_home_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(pageList, tabList);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fragment_home_viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void cleanShots() {
        for (ShotsAdapter a : shotsAdapters) {
            a.itemList = new ArrayList<>();
            a.notifyDataSetChanged();
        }
    }

    public void searchFinish() {
        this.isSearching = false;
        this.searchText = null;
        this.cleanShots();
        this.refreshNew(viewPage);
        ((MainActivity) getActivity()).setWindowTop(getString(R.string.app_name), R.color.colorPrimary);
        toolbar.setTitle(getString(R.string.app_name));
    }

    /** <br> data. */

    private void initData() {
        MainActivity container = (MainActivity) getActivity();
        this.defaultSort = container.getFiltrateData()[0];
        this.shotList = container.getFiltrateData()[1];
        this.shotPage = 0;
        this.getShotItemType();

        this.isSearching = false;
        this.searchText = null;
    }

    private void getShotItemType() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (defaultSort.equals(DribbbleShotsAPI.SHOT_SORT_POPULAR)) {
            this.shotItemType = new int[] {
                    sharedPreferences.getInt(getString(R.string.key_shots_item_type_popular), ShotsAdapter.MINI_TILE),
                    sharedPreferences.getInt(getString(R.string.key_shots_item_type_recent), ShotsAdapter.MINI_TILE)
            };
        } else {
            this.shotItemType = new int[] {
                    sharedPreferences.getInt(getString(R.string.key_shots_item_type_recent), ShotsAdapter.MINI_TILE),
                    sharedPreferences.getInt(getString(R.string.key_shots_item_type_popular), ShotsAdapter.MINI_TILE)
            };
        }
    }

    private String getShotSortNow(int position) {
        String[] tabs = new String[2];
        if (defaultSort.equals(DribbbleShotsAPI.SHOT_SORT_POPULAR)) {
            tabs[0] = DribbbleShotsAPI.SHOT_SORT_POPULAR;
            tabs[1] = DribbbleShotsAPI.SHOT_SORT_RECENT;
        } else {
            tabs[0] = DribbbleShotsAPI.SHOT_SORT_RECENT;
            tabs[1] = DribbbleShotsAPI.SHOT_SORT_POPULAR;
        }
        return tabs[position];
    }

    private void refreshNew(int position) {
        airBallContainers[position].setVisibility(View.GONE);

        swipeRefreshLayouts[position].setRefreshing(true);
        if (isSearching) {
            ((MainActivity) getActivity())
                    .getDribbbleService()
                    .searchDribbbleShots(
                            searchText,
                            getShotSortNow(position),
                            1,
                            false,
                            new MyGetShotsListener(position));
        } else {
            ((MainActivity) getActivity())
                    .getDribbbleService()
                    .getDribbbleShots(
                            getShotSortNow(position),
                            shotList,
                            1,
                            false,
                            new MyGetShotsListener(position));
        }
    }

    private void loadMore(int position) {
        if (isSearching) {
            ((MainActivity) getActivity())
                    .getDribbbleService()
                    .searchDribbbleShots(
                            searchText,
                            getShotSortNow(position),
                            ++ shotPage,
                            true,
                            new MyGetShotsListener(position));
        } else {
            ((MainActivity) getActivity())
                    .getDribbbleService()
                    .getDribbbleShots(
                            getShotSortNow(position),
                            shotList,
                            ++ shotPage,
                            true,
                            new MyGetShotsListener(position));
        }
    }

    /** <br> interface. */

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.fragment_home_toolbar:
                recyclerViews[viewPage].smoothScrollToPosition(0);
                break;

            case R.id.container_airball_view_large:
                refreshNew(viewPage);
                break;
        }
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
                searchFragment.setData(isSearching ? searchText : null);
                searchFragment.setOnSearchListener(this);
                container.insertFragment(searchFragment);
                break;

            case R.id.action_filtrate:
                if (fab != null) {
                    fab.hide();
                }

                FiltrateFragment filtrateFragment = new FiltrateFragment();
                filtrateFragment.setData(shotList);
                filtrateFragment.setOnFiltrateListener(this);
                container.insertFragment(filtrateFragment);
                break;

            case R.id.action_shot_item_type:
                if (fab != null) {
                    fab.hide();
                }

                ShotItemTypeFragment shotItemTypeFragment = new ShotItemTypeFragment();
                shotItemTypeFragment.setOnShotItemTypeSelectListener(new MyOnShotItemTypeSelectListener(viewPage));
                container.insertFragment(shotItemTypeFragment);
                break;
        }
        return false;
    }

    @Override
    public void changeFiltrateData(String shotList) {
        this.shotList = shotList;
        this.cleanShots();
        refreshNew(viewPage);
    }

    @Override
    public void onSearch(String text) {
        this.isSearching = true;
        this.searchText = text;
        this.cleanShots();
        this.refreshNew(viewPage);

        toolbar.setTitle(getString(R.string.search) + " " + text);
        ((MainActivity) getActivity()).setWindowTop(getString(R.string.search) + " " + text, R.color.colorPrimary);
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        viewPage = position;
        if (shotsAdapters[position].getItemCount() <= 0) {
            refreshNew(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing.
    }

    /** <br> listener. */

    // on refresh listener.

    private class MyOnRefreshListener implements BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
        private int position;

        public MyOnRefreshListener(int position) {
            this.position = position;
        }

        @Override
        public void onRefresh() {
            refreshNew(position);
        }

        @Override
        public void onLoad() {
            if (!recyclerViews[position].isLoadingMore()) {
                recyclerViews[position].setLoadingMore(true);
                loadMore(position);
            }
        }
    }

    // on load more listener.

    private class MyOnLoadMoreListener implements MyRecyclerView.OnLoadMoreListener {
        private int position;

        public MyOnLoadMoreListener(int position) {
            this.position = position;
        }

        @Override
        public void onLoadMore() {
            loadMore(position);
        }
    }

    // on item click listener.

    private class MyItemClickListener implements ShotsAdapter.MyItemClickListener {
        private int position;

        public MyItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onItemClick(ShotsAdapter.ViewHolder holder, int position) {
            MyFloatingActionButton fab = (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab);
            fab.hide();

            ShotItem shot = shotsAdapters[this.position].itemList.get(position);
            ShotView shotView = holder.shotView;

            int x = (int) (recyclerViews[this.position].touchX % shotView.getMeasuredWidth());
            int y = (int) (recyclerViews[this.position].touchY % shotView.getMeasuredHeight());
            Bitmap bitmap = ImageUtils.drawableToBitmap(shotView.getDrawable());

            int revealColor;
            if (x < bitmap.getWidth() && y < bitmap.getHeight()) {
                revealColor = bitmap.getPixel(x, y);
            } else {
                revealColor = ContextCompat.getColor(getActivity(), R.color.colorRoot);
            }

            ShotFragment fragment = new ShotFragment();
            fragment.setData(getActivity(), shot, revealColor,
                    recyclerViews[this.position].touchX, recyclerViews[this.position].touchY);
            ((MainActivity) getActivity()).insertFragment(fragment);
        }
    }

    private class MyOnShotItemTypeSelectListener implements ShotItemTypeFragment.OnShotItemTypeSelectListener {
        private int position;

        public MyOnShotItemTypeSelectListener(int position) {
            this.position = position;
        }

        @Override
        public void onShotItemTypeSelected(int type) {
            if (shotItemType[position] == type) {
                return;
            }

            shotItemType[position] = type;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            if (defaultSort.equals(DribbbleShotsAPI.SHOT_SORT_POPULAR)) {
                if (position == 0) {
                    editor.putInt(getString(R.string.key_shots_item_type_popular), type).apply();
                } else {
                    editor.putInt(getString(R.string.key_shots_item_type_recent), type).apply();
                }
            } else {
                if (position == 0) {
                    editor.putInt(getString(R.string.key_shots_item_type_recent), type).apply();
                } else {
                    editor.putInt(getString(R.string.key_shots_item_type_popular), type).apply();
                }
            }
            ShotsAdapter adapter = new ShotsAdapter(getActivity(), shotsAdapters[position].itemList, type);
            adapter.setOnItemClickListener(new MyItemClickListener(position));
            shotsAdapters[position] = adapter;

            if (type == ShotsAdapter.MINI_TILE
                    || type == ShotsAdapter.MINI_CARD_WITH_TITLE || type == ShotsAdapter.MINI_CARD_WITHOUT_TITLE) {
                recyclerViews[position].setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerViews[position].setAdapter(adapter);
            } else {
                recyclerViews[position].setLayoutManager(new GridLayoutManager(getActivity(), 1));
                recyclerViews[position].setAdapter(adapter);
            }
        }
    }

    // dirbbble shot api.

    private class MyGetShotsListener implements DribbbleService.GetShotsListener {
        private int position;

        public MyGetShotsListener(int position) {
            this.position = position;
        }

        @Override
        public void getShotsSuccess(Call<List<Shot>> call, Response<List<Shot>> response, boolean loadMore) {
            if (response.isSuccessful()) {
                if (loadMore) {
                    shotPage++;
                } else {
                    shotPage = 1;
                    shotsAdapters[position].itemList = new ArrayList<>();
                    shotsAdapters[position].notifyDataSetChanged();
                }

                for (int i = 0; i < response.body().size(); i ++) {
                    shotsAdapters[position].insertData(new ShotItem(response.body().get(i)), shotsAdapters[position].itemList.size());
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.get_shots_data_failed), Toast.LENGTH_SHORT).show();
            }

            if (loadMore) {
                loadFinishCallback.loadFinish(this);
                swipeRefreshLayouts[position].setLoading(false);
            } else {
                swipeRefreshLayouts[position].setRefreshing(false);
            }
        }

        @Override
        public void getShotsFailed(Call<List<Shot>> call, Throwable t, boolean loadMore) {
            Toast.makeText(getActivity(),
                    getString(R.string.get_shots_data_failed) + "\n" + t.getMessage(),
                    Toast.LENGTH_SHORT).show();

            if (loadMore) {
                loadFinishCallback.loadFinish(this);
                swipeRefreshLayouts[position].setLoading(false);
            } else {
                swipeRefreshLayouts[position].setRefreshing(false);
            }

            if (shotsAdapters[position].getItemCount() == 0) {
                airBallContainers[position].setVisibility(View.VISIBLE);
            }
        }
    }

    /** <br> handler. */

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CREATE_FRAGMENT:
                TypefaceUtils.setToolbarTypeface(getActivity(), toolbar);
                refreshNew(0);
                break;
        }
    }
}
