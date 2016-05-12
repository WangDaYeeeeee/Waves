package com.wangdaye.waves.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.fragment.SettingsFragment;
import com.wangdaye.waves.ui.widget.ThemeActivity;

/**
 * Settings activity.
 * */

public class SettingsActivity extends ThemeActivity
        implements View.OnClickListener {

    // data
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (started) {
            return;
        }
        started = true;

        this.initWidget();
        this.initColorTheme(null, getString(R.string.nav_settings), R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_fragment, settingsFragment)
                .commit();
    }

    /** <br> widget. */

    private void initWidget() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(getString(R.string.nav_settings));
        toolbar.setNavigationOnClickListener(this);
    }

    /** <br> data. */

    private void initData() {
        this.started = false;
    }

    /** <br> interface. */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                overridePendingTransition(0, R.anim.activity_slide_out);
        }
    }
}
