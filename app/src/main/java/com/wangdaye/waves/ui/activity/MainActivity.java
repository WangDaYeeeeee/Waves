package com.wangdaye.waves.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.ui.fragment.HomeFragment;
import com.wangdaye.waves.ui.widget.container.RevealFragment;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.container.ThemeActivity;
import com.wangdaye.waves.utils.DisplayUtils;
import com.wangdaye.waves.utils.SafeHandler;
import com.wangdaye.waves.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ThemeActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        SafeHandler.HandlerContainer {
    // widget
    private MyFloatingActionButton fab;
    private FragmentTransaction tempTransaction;

    // data
    private DribbbleService dribbbleService;
    private String sort;
    private String list;

    private int fragmentNow;
    public List<Fragment> fragmentList;
    private boolean started;

    private final int HOME_FRAGMENT = 1;

    private final int CHANGE_FRAGMENT = 1;
    private final int SETTINGS_ACTIVITY = 5;
    private final int ABOUT_ACTIVITY = 6;
    private final int RESTART_APP = 0;

    // handler
    private SafeHandler<MainActivity> handler;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        this.setStatusBarTransparent();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (started) {
            return;
        }
        started = true;
        this.handler = new SafeHandler<>(this);
        this.initWidget();
        this.initColorTheme(getString(R.string.app_name), R.color.colorPrimary);
        this.changeFragment(HOME_FRAGMENT, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if(Util.isOnMainThread()) {
            Glide.get(this).clearMemory();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentList.size() > 1) {
            removeFragment();
        } else if (fragmentList.size() == 1 && fragmentNow == HOME_FRAGMENT && ((HomeFragment) fragmentList.get(0)).isSearching) {
            ((HomeFragment) fragmentList.get(0)).searchFinish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTINGS_ACTIVITY:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String newSort = sharedPreferences.getString(
                        getString(R.string.key_shots_sort),
                        getString(R.string.default_shots_sort));
                String newList = sharedPreferences.getString(
                        getString(R.string.key_shots_list),
                        getString(R.string.default_shots_list));

                if (!newSort.equals(sort) || !newList.equals(list)) {
                    this.sort = newSort;
                    this.list = newList;

                    if (fragmentList.get(0) instanceof HomeFragment) {
                        this.changeFragment(HOME_FRAGMENT, true);
                    }
                }
                break;
        }
    }

    public void reStartApp(int time) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = RESTART_APP;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, time);
    }

    /** <br> UI. */

    private void initWidget() {
        this.initFab();
        this.initDrawer();
    }

    private void initFab() {
        this.fab = (MyFloatingActionButton) findViewById(R.id.container_main_fab);
        assert fab != null;
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.bottomMargin = DisplayUtils.getNavigationBarHeight(getResources()) + layoutParams.bottomMargin;
        fab.setLayoutParams(layoutParams);
        fab.setOnClickListener(this);
    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_navView);
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem;
        menuItem= navigationView.getMenu().findItem(R.id.nav_myBuckets);
        menuItem.setVisible(false);
        menuItem= navigationView.getMenu().findItem(R.id.nav_myLikes);
        menuItem.setVisible(false);
        menuItem= navigationView.getMenu().findItem(R.id.nav_myShots);
        menuItem.setVisible(false);

        View navHeader = navigationView.getHeaderView(0);
        ImageView navHeaderBackground = (ImageView) navHeader.findViewById(R.id.container_nav_header_background);
        Glide.with(getApplicationContext())
                .load(R.drawable.nav_header_animated)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(navHeaderBackground);

        TextView userName = (TextView) navHeader.findViewById(R.id.container_nav_header_userName);
        userName.setTypeface(TypefaceUtils.getTypeface(this));
        userName.setText(getString(R.string.app_name));

        TextView userId = (TextView) navHeader.findViewById(R.id.container_nav_header_userId);
        userId.setText(getString(R.string.login_dribble));
    }

    /** <br> data. */

    private void initData() {
        this.dribbbleService = new DribbbleService();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sort = sharedPreferences.getString(getString(R.string.key_shots_sort), getString(R.string.default_shots_sort));
        this.list = sharedPreferences.getString(getString(R.string.key_shots_list), getString(R.string.default_shots_list));

        fragmentNow = HOME_FRAGMENT;
        fragmentList = new ArrayList<>();
        started = false;
        DisplayUtils.setDisplayDpi(getResources().getDisplayMetrics().densityDpi);
    }

    public String[] getFiltrateData() {
        return new String[] {
                sort, list
        };
    }

    public DribbbleService getDribbbleService() {
        return this.dribbbleService;
    }

    /** <br> fragment. */

    private void changeFragment(int fragmentTo, boolean init) {
        if (fragmentTo == fragmentNow && !init) {
            return;
        }

        for (Fragment f : fragmentList) {
            getSupportFragmentManager().popBackStack();
        }
        fragmentList.clear();

        this.fragmentNow = fragmentTo;

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment newFragment;
        NavigationView navView = (NavigationView) findViewById(R.id.activity_main_navView);
        switch (fragmentTo) {
            case HOME_FRAGMENT:
                this.setWindowTop(getString(R.string.app_name), R.color.colorPrimary);
                if (navView != null) {
                    navView.setCheckedItem(R.id.nav_home);
                }
                newFragment = new HomeFragment();
                fab.show();
                break;

            default:
                newFragment = new HomeFragment();
                break;
        }

        fragmentList.add(newFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container_main_container, newFragment);

        if (init) {
            transaction.commit();
        } else {
            tempTransaction = transaction;
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = CHANGE_FRAGMENT;
                    handler.sendMessage(msg);
                }
            };
            timer.schedule(timerTask, 400);
        }
    }

    public void insertFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragmentList.add(fragment);
        transaction.add(R.id.container_main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void removeFragment() {
        if (fragmentList.get(fragmentList.size() - 1) instanceof RevealFragment) {
            ((RevealFragment) fragmentList.get(fragmentList.size() - 1)).hide();
            this.popFragmentList();
        } else {
            this.popFragment();
            this.popFragmentList();
        }
    }

    public void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void popFragmentList() {
        fragmentList.remove(fragmentList.size() - 1);
    }

    /** <br> parent method. */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                changeFragment(HOME_FRAGMENT, false);
                break;

            case R.id.nav_settings:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = SETTINGS_ACTIVITY;
                        handler.sendMessage(msg);
                    }
                }, 400);
                break;

            case R.id.nav_about:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = ABOUT_ACTIVITY;
                        handler.sendMessage(msg);
                    }
                }, 400);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** <br> interface. */

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_main_fab:
                if (!fab.translating && fab.showing) {
                    Toast.makeText(this, "click fab", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {

            case RESTART_APP:
                int enter_anim = android.R.anim.fade_in;
                int exit_anim = android.R.anim.fade_out;
                finish();
                overridePendingTransition(enter_anim, exit_anim);
                startActivity(getIntent());
                overridePendingTransition(enter_anim, exit_anim);
                break;

            case CHANGE_FRAGMENT:
                if (tempTransaction != null) {
                    tempTransaction.commit();
                    tempTransaction = null;
                }
                break;

            case SETTINGS_ACTIVITY:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settings, SETTINGS_ACTIVITY);
                break;

            case ABOUT_ACTIVITY:
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
                break;
        }
    }
}
