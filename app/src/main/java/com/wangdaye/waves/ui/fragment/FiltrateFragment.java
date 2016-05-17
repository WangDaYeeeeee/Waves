package com.wangdaye.waves.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.wangdaye.waves.R;
import com.wangdaye.waves.data.dirbbble.api.DribbbleShotsAPI;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;

/**
 * Filtrate fragment,
 * used to filtrate shots.
 * */

public class FiltrateFragment extends RevealFragment
        implements View.OnClickListener, RevealView.OnRevealingListener {
    // widget
    private FrameLayout background;
    private CardView cardView;
    public RevealView revealView;
    private RelativeLayout infoContainer;

    private OnFiltrateListener onFiltrateListener;

    // data
    private String shotSort;
    private String shotList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtrate, container, false);

        this.setColorSrc(R.color.colorTextGrey2nd, R.color.cardview_light_background);
        this.initWidget(view);
        revealView.setState(RevealView.REVEALING);

        return view;
    }

    @Override
    public void hide() {
        AnimatorSet backgroundOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.background_out);
        backgroundOut.setTarget(background);
        AnimatorSet viewOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_out);
        viewOut.setTarget(infoContainer);
        viewOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                revealView.setState(RevealView.GRADIENT_TO_REVEAL);
            }
        });

        cardView.setCardElevation(0);
        backgroundOut.start();
        viewOut.start();
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.background = (FrameLayout) view.findViewById(R.id.fragment_filtrate_background);
        background.setOnClickListener(this);

        this.cardView = (CardView) view.findViewById(R.id.fragment_filtrate_card);

        this.revealView = (RevealView) view.findViewById(R.id.fragment_filtrate_revealView);
        revealView.setColor(
                ContextCompat.getColor(getActivity(), circleColor),
                ContextCompat.getColor(getActivity(), backgroundColor));
        revealView.setTouchPosition(RevealView.RIGHT_TOP, 0, 0);
        revealView.setDrawTime(RevealView.ONE_TIME_SPEED);
        revealView.setOnRevealingListener(this);

        this.infoContainer = (RelativeLayout) view.findViewById(R.id.fragment_filtrate_infoContainer);

        Spinner sortSpinner = (Spinner) view.findViewById(R.id.fragment_filtrate_sortSpinner);
        sortSpinner.setOnItemSelectedListener(new SortSpinnerItemSelectListener());
        sortSpinner.setSelection(shotSortAdapter(shotSort));
        Spinner listSpinner = (Spinner) view.findViewById(R.id.fragment_filtrate_listSpinner);
        listSpinner.setOnItemSelectedListener(new ListSpinnerItemSelectListener());
        listSpinner.setSelection(shotListAdapter(shotList));

        Button done = (Button) view.findViewById(R.id.fragment_filtrate_doneButton);
        done.setOnClickListener(this);
    }

    private int shotSortAdapter(String shotSort) {
        switch (shotSort) {
            case DribbbleShotsAPI.SHOT_SORT_POPULAR:
                return 0;
            case DribbbleShotsAPI.SHOT_SORT_RECENT:
                return 1;
        }
        return 0;
    }

    private int shotListAdapter(String shotList) {
        switch (shotList) {
            case DribbbleShotsAPI.SHOT_LIST_ANY_TYPE:
                return 0;
            case DribbbleShotsAPI.SHOT_LIST_ANIMATED:
                return 1;
            case DribbbleShotsAPI.SHOT_LIST_ATTACHMENTS:
                return 2;
            case DribbbleShotsAPI.SHOT_LIST_DEBUTS:
                return 3;
            case DribbbleShotsAPI.SHOT_LIST_PLAYOFFS:
                return 4;
            case DribbbleShotsAPI.SHOT_LIST_REBOUNDS:
                return 5;
            case DribbbleShotsAPI.SHOT_LIST_TEAMS:
                return 6;
        }
        return 0;
    }

    /** <br> data. */

    public void setData(String shotSort, String shotList) {
        this.shotSort = shotSort;
        this.shotList = shotList;
    }

    /** <br> interface. */

    // on click.

    @Override
    public void onClick(View v) {
        MainActivity container = (MainActivity) getActivity();
        switch (v.getId()) {

            case R.id.fragment_filtrate_doneButton:
                container.removeFragment();

                if (onFiltrateListener != null) {
                    onFiltrateListener.changeFiltrateData(shotSort, shotList);
                }
                break;

            case R.id.fragment_filtrate_background:
                container.removeFragment();
        }
    }

    // spinner item click listener.

    private class SortSpinnerItemSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    shotSort = DribbbleShotsAPI.SHOT_SORT_POPULAR;
                    break;
                case 1:
                    shotSort = DribbbleShotsAPI.SHOT_SORT_RECENT;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ListSpinnerItemSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    shotList = DribbbleShotsAPI.SHOT_LIST_ANY_TYPE;
                    break;
                case 1:
                    shotList = DribbbleShotsAPI.SHOT_LIST_ANIMATED;
                    break;
                case 2:
                    shotList = DribbbleShotsAPI.SHOT_LIST_ATTACHMENTS;
                    break;
                case 3:
                    shotList = DribbbleShotsAPI.SHOT_LIST_DEBUTS;
                    break;
                case 4:
                    shotList = DribbbleShotsAPI.SHOT_LIST_PLAYOFFS;
                    break;
                case 5:
                    shotList = DribbbleShotsAPI.SHOT_LIST_REBOUNDS;
                    break;
                case 6:
                    shotList = DribbbleShotsAPI.SHOT_LIST_TEAMS;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    // on reveal listener.

    @Override
    public void revealFinish() {
        AnimatorSet backgroundIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.background_in);
        backgroundIn.setTarget(background);
        AnimatorSet viewIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_in_1);
        viewIn.setTarget(infoContainer);

        cardView.setCardElevation(10);
        infoContainer.setVisibility(View.VISIBLE);
        background.setVisibility(View.VISIBLE);
        backgroundIn.start();
        viewIn.start();
    }

    @Override
    public void hideFinish() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.commit();

        MainActivity container = (MainActivity) getActivity();
        MyFloatingActionButton fab = (MyFloatingActionButton) container.findViewById(R.id.container_main_fab);
        if (fab != null) {
            fab.show();
        }
    }

    // on filtrate listener.

    public interface OnFiltrateListener {
        void changeFiltrateData(String shotSort, String shotList);
    }

    public void setOnFiltrateListener(OnFiltrateListener listener) {
        this.onFiltrateListener = listener;
    }

    public void cleanOnFiltrateListener() {
        this.onFiltrateListener = null;
    }
}
