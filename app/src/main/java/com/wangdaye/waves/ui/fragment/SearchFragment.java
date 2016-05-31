package com.wangdaye.waves.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.container.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;

/**
 * Search fragment.
 * */

public class SearchFragment extends RevealFragment
        implements RevealView.OnRevealingListener, View.OnClickListener, TextView.OnEditorActionListener {
    // widget
    private CoordinatorLayout container;
    private RevealView revealView;
    private AppBarLayout appBarLayout;
    private EditText editText;

    private OnSearchListener onSearchListener;

    // data
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        this.setColorSrc(android.R.color.white, R.color.colorShadow);
        this.initWidget(view);
        revealView.setState(RevealView.REVEALING);

        return view;
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.container = (CoordinatorLayout) view.findViewById(R.id.fragment_search);

        float dpi = getResources().getDisplayMetrics().densityDpi;
        float x = (float) (getActivity().findViewById(R.id.container_main_container).getMeasuredWidth() - 62 * (dpi / 160.0));

        this.revealView = (RevealView) view.findViewById(R.id.fragment_search_background);
        revealView.setColor(
                ContextCompat.getColor(getActivity(), circleColor),
                ContextCompat.getColor(getActivity(), backgroundColor));
        revealView.setTouchPosition(0, x, 0);
        revealView.setDrawTime(RevealView.TWO_TIMES_SPEED);
        revealView.setOnRevealingListener(this);
        revealView.setOnClickListener(this);

        this.appBarLayout = (AppBarLayout) view.findViewById(R.id.fragment_search_appBarLayout);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_search_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(this);

        this.editText = (EditText) view.findViewById(R.id.container_searchbar_editText);
        editText.setOnEditorActionListener(this);
        editText.setText(text);
        editText.setFocusable(true);
        editText.requestFocus();
    }

    @Override
    public void hide() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        Animation viewOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        viewOut.setAnimationListener(new ViewOutListener());
        container.startAnimation(viewOut);
    }

    /** <br> data. */

    public void setData(String text) {
        this.text = text;
    }

    /** <br> interface. */

    // on click.

    @Override
    public void onClick(View v) {
        MainActivity container = (MainActivity) getActivity();

        switch (v.getId()) {
            case -1:
                container.removeFragment();
                break;

            case R.id.fragment_search_background:
                container.removeFragment();
                break;
        }
    }

    // on editor action.

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        text = v.getText().toString();
        MainActivity container = (MainActivity) getActivity();
        container.removeFragment();

        if (!text.equals("") && onSearchListener != null) {
            onSearchListener.onSearch(text);
        }
        return true;
    }

    // on reveal changed listener.

    @Override
    public void revealFinish() {
        Animation viewIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
        viewIn.setAnimationListener(new ViewInListener());
        appBarLayout.setVisibility(View.VISIBLE);
        appBarLayout.startAnimation(viewIn);
    }

    @Override
    public void hideFinish() {
        MainActivity container = (MainActivity) getActivity();
        MyFloatingActionButton fab = (MyFloatingActionButton) container.findViewById(R.id.container_main_fab);
        if (fab != null) {
            fab.show();
        }
        container.popFragment();
    }

    // animation listener.

    private class ViewInListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class ViewOutListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            hideFinish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    // on search listener.

    public interface OnSearchListener {
        void onSearch(String text);
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}
