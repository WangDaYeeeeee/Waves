package com.wangdaye.waves.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.adapter.ShotItemTypeAdapter;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.container.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;

/**
 * Shot item type fragment.
 * */

public class ShotItemTypeFragment extends RevealFragment
        implements RevealView.OnRevealingListener, View.OnClickListener, ShotItemTypeAdapter.MyItemClickListener {
    // widget
    private CoordinatorLayout container;
    private RevealView revealView;
    private LinearLayout viewContainer;

    private OnShotItemTypeSelectListener onShotItemTypeSelectListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot_item_type, container, false);

        this.setColorSrc(android.R.color.white, R.color.colorShadow);
        this.initWidget(view);
        revealView.setState(RevealView.REVEALING);

        return view;
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.container = (CoordinatorLayout) view.findViewById(R.id.fragment_shot_item_type);

        this.revealView = (RevealView) view.findViewById(R.id.fragment_shot_item_type_background);
        revealView.setColor(
                ContextCompat.getColor(getActivity(), circleColor),
                ContextCompat.getColor(getActivity(), backgroundColor));
        revealView.setTouchPosition(RevealView.RIGHT_TOP, 0, 0);
        revealView.setDrawTime(RevealView.TWO_TIMES_SPEED);
        revealView.setOnRevealingListener(this);
        revealView.setOnClickListener(this);

        this.viewContainer = (LinearLayout) view.findViewById(R.id.fragment_shot_item_type_view_container);

        ShotItemTypeAdapter adapter = new ShotItemTypeAdapter(getActivity());
        adapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_shot_item_type_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void hide() {
        Animation viewOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        viewOut.setAnimationListener(new ViewOutListener());
        container.startAnimation(viewOut);
    }

    /** <br> listener. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_shot_item_type_background:
                ((MainActivity) getActivity()).removeFragment();
                break;
        }
    }

    // my on item click listener.

    @Override
    public void onItemClick(View view, int position) {
        if (onShotItemTypeSelectListener != null) {
            onShotItemTypeSelectListener.onShotItemTypeSelected(position);
        }
        ((MainActivity) getActivity()).removeFragment();
    }

    // on reveal listener.

    @Override
    public void revealFinish() {
        Animation viewIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
        viewContainer.setVisibility(View.VISIBLE);
        viewContainer.startAnimation(viewIn);
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

    // on shot item type select listener.

    public interface OnShotItemTypeSelectListener {
        void onShotItemTypeSelected(int type);
    }

    public void setOnShotItemTypeSelectListener(OnShotItemTypeSelectListener l) {
        this.onShotItemTypeSelectListener = l;
    }
}
