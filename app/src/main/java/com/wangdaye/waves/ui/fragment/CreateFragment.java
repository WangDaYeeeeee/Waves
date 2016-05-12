package com.wangdaye.waves.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wangdaye.waves.R;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;

/**
 * Create fragment,
 * used to create a shot for Dribbble.
 * */

public class CreateFragment extends RevealFragment implements RevealView.OnRevealingListener {
    // widget
    public RevealView revealView;
    private LinearLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        this.setColor(R.color.colorAccent, R.color.colorRoot);
        this.initWidget(view);
        revealView.setState(RevealView.REVEALING);

        return view;
    }

    /** <br> UI. */

    private void initWidget(View view) {
        MyFloatingActionButton fab = (MyFloatingActionButton) getActivity().findViewById(R.id.container_main_fab);

        this.revealView = (RevealView) view.findViewById(R.id.fragment_create_revealView);
        revealView.setColor(
                ContextCompat.getColor(getActivity(), circleColor),
                ContextCompat.getColor(getActivity(), backgroundColor));
        revealView.setOnRevealingListener(this);
        revealView.setDrawTime(RevealView.TWO_TIMES_SPEED);
        revealView.setTouchPosition(0,
                (float) (fab.getX() + fab.getMeasuredWidth() / 2.0),
                (float) (fab.getY() + fab.getMeasuredHeight() / 2.0));

        this.container = (LinearLayout) view.findViewById(R.id.fragment_create_container);
    }

    @Override
    public void hide() {
        AnimatorSet viewOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_out);
        viewOut.setTarget(container);
        viewOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                revealView.setState(RevealView.GRADIENT_TO_REVEAL);
            }
        });

        viewOut.start();
    }

    /** <br> interface. */

    // on reveal listener.

    @Override
    public void revealFinish() {
        AnimatorSet viewIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_in_1);
        viewIn.setTarget(container);
        container.setVisibility(View.VISIBLE);
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
            //fab.down(getActivity());
        }
    }
}
