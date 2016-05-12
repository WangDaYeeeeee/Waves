package com.wangdaye.waves.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.wangdaye.waves.R;

/**
 * Settings fragment.
 * */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
