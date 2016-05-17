package com.wangdaye.waves.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.squareup.leakcanary.RefWatcher;
import com.wangdaye.waves.R;
import com.wangdaye.waves.application.Waves;
import com.wangdaye.waves.data.dirbbble.model.Comment;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.CommentItem;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.adapter.CommentsAdapter;
import com.wangdaye.waves.ui.widget.CircleImageView;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;
import com.wangdaye.waves.ui.widget.ShotBarLayout;
import com.wangdaye.waves.ui.widget.ShotScrollParent;
import com.wangdaye.waves.ui.widget.ShotScrollView;
import com.wangdaye.waves.ui.widget.SwipeBackLayout;
import com.wangdaye.waves.ui.widget.WavesLoadingView;
import com.wangdaye.waves.utils.ColorUtils;
import com.wangdaye.waves.utils.ImageUtils;
import com.wangdaye.waves.utils.TypefaceUtils;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Shot fragment.
 * */

public class ShotFragment extends RevealFragment
        implements SwipeBackLayout.OnSwipeListener, View.OnClickListener, RevealView.OnRevealingListener,
        DribbbleService.GetCommentsListener, CommentsAdapter.MyItemClickListener {
    // widget
    private ShotScrollParent scrollParent;

    private ShotBarLayout shotBar;
    private FrameLayout statusBar;
    private ImageView shotImage;

    private ShotScrollView scrollView;

    private RelativeLayout titleContainer;
    private CircleImageView playerIcon;

    private AppBarLayout dataContainer;
    private TextView likesNum;

    private MyFloatingActionButton fab;
    private RevealView revealView;
    private WavesLoadingView wavesLoadingView;
    private RecyclerView recyclerView;

    // data
    private ShotItem shotItem;
    private float revealX, revealY;
    private boolean isGif;
    private String[] tags;

    private Typeface typeface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper
                = new ContextThemeWrapper(getActivity(), R.style.WavesTheme_Translucent_Dribbble);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_shot, container, false);

        this.initWidget(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }
        revealView.setState(RevealView.REVEALING);
        wavesLoadingView.setState(WavesLoadingView.SHOWING);
        ((MainActivity) getActivity())
                .getDribbbleService()
                .getDribbbleComments(shotItem.shotId, this);
        return view;
    }

    @Override
    public void hide() {
        fab.hide();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = Waves.getRefWatcher(getActivity());
        refWatcher.watch(this);
        if(Util.isOnMainThread()) {
            Glide.get(getActivity()).clearMemory();
        }
    }

    private void finish() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.remove(this);
        transaction.commit();

        MainActivity container = (MainActivity) getActivity();
        container.setStatusBarTransparent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            container.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        MyFloatingActionButton fab = (MyFloatingActionButton) container.findViewById(R.id.container_main_fab);
        if (fab != null) {
            fab.show();
        }
    }

    /** <br> UI. */

    private void initWidget(View view) {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) view.findViewById(R.id.fragment_shot_swipeBackLayout);
        swipeBackLayout.setBackground(view.findViewById(R.id.fragment_shot_statusBar));
        swipeBackLayout.setOnSwipeListener(this,
                view.findViewById(R.id.fragment_shot_scrollView),
                view.findViewById(R.id.fragment_shot_container));

        this.scrollParent = (ShotScrollParent) view.findViewById(R.id.fragment_shot_container);
        scrollParent.setPadding(0, ((MainActivity) getActivity()).getStatusBarHeight(), 0, 0);

        this.revealView = (RevealView) view.findViewById(R.id.fragment_shot_revealView);
        revealView.setDrawTime(RevealView.TWO_TIMES_SPEED);
        revealView.setColor(circleColor, backgroundColor);
        revealView.setOnRevealingListener(this);
        revealView.setTouchPosition(0, revealX, revealY);

        this.fab = (MyFloatingActionButton) view.findViewById(R.id.fragment_shot_fab);
        fab.setOnClickListener(this);

        this.initShotPart(view);
        this.initTitlePart(view);
        this.initDataPart(view);
    }

    private void initShotBar(float height) {
        FrameLayout.LayoutParams shotBarParams = (FrameLayout.LayoutParams) shotBar.getLayoutParams();
        shotBarParams.height = (int) height;
        shotBarParams.width = getResources().getDisplayMetrics().widthPixels;
        shotBar.setLayoutParams(shotBarParams);

        FrameLayout.LayoutParams statusBarParams = (FrameLayout.LayoutParams) statusBar.getLayoutParams();
        statusBarParams.height = ((MainActivity) getActivity()).getStatusBarHeight();

        scrollView.setPadding(0, (int) height, 0, 0);

        FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        fabParams.topMargin = fab.calcMargin(height, 0, 0);
        fab.setLayoutParams(fabParams);
    }

    private void initShotPart(View view) {
        this.shotBar = (ShotBarLayout) view.findViewById(R.id.fragment_shot_shotBar);
        shotBar.setMaxiHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0 * 3.0));
        shotBar.setMiniHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0));

        this.statusBar = (FrameLayout) view.findViewById(R.id.fragment_shot_statusBar);

        this.shotImage = (ImageView) view.findViewById(R.id.fragment_shot_shot);
        shotImage.setOnClickListener(this);

        this.scrollView = (ShotScrollView) view.findViewById(R.id.fragment_shot_scrollView);
    }

    private void initTitlePart(View view) {
        this.titleContainer = (RelativeLayout) view.findViewById(R.id.container_shot_title_container);

        this.playerIcon = (CircleImageView) view.findViewById(R.id.container_shot_player);

        TextView title = (TextView) view.findViewById(R.id.container_shot_title);
        title.setTypeface(typeface);
        title.setText(shotItem.title);

        TextView subTitle = (TextView) view.findViewById(R.id.container_shot_subtitle);
        subTitle.setText(shotItem.subTitle);
    }

    @SuppressLint("SetTextI18n")
    private void initDataPart(View view) {
        this.dataContainer = (AppBarLayout) view.findViewById(R.id.container_shot_data_container);

        HtmlTextView content = (HtmlTextView) view.findViewById(R.id.container_shot_content);
        content.setHtmlFromString(
                shotItem.content,
                new HtmlTextView.RemoteImageGetter());

        this.likesNum = (TextView) view.findViewById(R.id.container_shot_likes_num);
        likesNum.setTypeface(typeface);
        likesNum.setText(String.valueOf(shotItem.likes) + " Likes");

        TextView viewsNum = (TextView) view.findViewById(R.id.container_shot_views_num);
        viewsNum.setTypeface(typeface);
        viewsNum.setText(String.valueOf(shotItem.views) + " Views");

        TextView bucketsNum = (TextView) view.findViewById(R.id.container_shot_buckets_num);
        bucketsNum.setTypeface(typeface);
        bucketsNum.setText(String.valueOf(shotItem.buckets) + " Buckets");

        view.findViewById(R.id.container_shot_likes_container).setOnClickListener(this);
        view.findViewById(R.id.container_shot_views_container).setOnClickListener(this);
        view.findViewById(R.id.container_shot_buckets_container).setOnClickListener(this);

        this.wavesLoadingView = (WavesLoadingView) view.findViewById(R.id.container_shot_wavesLoadingView);
        wavesLoadingView.setNullString(getString(R.string.no_comments));

        this.recyclerView = (RecyclerView) view.findViewById(R.id.container_details_commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    /** <br> data. */

    // init data.

    public void setData(Context context, ShotItem shotItem, int revealColor, float x, float y) {
        this.shotItem = shotItem;

        String uri = shotItem.imageUri;
        this.isGif = uri.substring(uri.length() - 3).equals("gif");
        this.typeface = TypefaceUtils.getTypeface(context);

        this.setColorSrc(
                revealColor,
                ContextCompat.getColor(context, R.color.colorRoot));
        this.revealX = x;
        this.revealY = y;
    }

    // download.

    private void downloadShot() {

        if (isGif) {
            Glide.with(getActivity())
                    .load(shotItem.imageUri)
                    .asGif()
                    .toBytes()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(download);
        } else {
            Glide.with(getActivity())
                    .load(shotItem.imageUri)
                    .asBitmap()
                    .toBytes()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(download);
        }
    }

    /** <br> permission. */

    private void requestPermission(int permissionCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        switch (permissionCode) {
            case Waves.WRITE_EXTERNAL_STORAGE:
                if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    this.downloadShot();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        switch (requestCode) {
            case 1:
                this.downloadShot();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permission, grantResult);
                break;
        }
    }

    /** <br> interface. */

    // swipe back layout.

    @Override
    public boolean canSwipeBack(View target, int dir) {
        return !ViewCompat.canScrollVertically(target, dir);
    }

    @Override
    public void swipeFinish() {
        ((MainActivity) getActivity()).fragmentList.remove(((MainActivity) getActivity()).fragmentList.size() - 1);
        this.finish();
    }

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fragment_shot_shot:
                Uri uri = Uri.parse(shotItem.webUri);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;

            case R.id.fragment_shot_fab:
                Toast.makeText(getActivity(), getString(R.string.download_start), Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    this.downloadShot();
                } else {
                    this.requestPermission(Waves.WRITE_EXTERNAL_STORAGE);
                }
                break;
        }
    }

    // on item click.

    @Override
    public void onItemClick(View view, int position) {

    }

    // on reveal listener.

    @Override
    public void revealFinish() {
        final AnimatorSet[] viewIn = new AnimatorSet[] {
                (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_in_1),
                (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_in_1),
                (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.view_in_2)
        };
        viewIn[0].setTarget(shotBar);
        viewIn[1].setTarget(titleContainer);
        viewIn[2].setTarget(dataContainer);

        viewIn[0].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                titleContainer.setVisibility(View.VISIBLE);
                viewIn[1].start();
            }
        });
        viewIn[1].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dataContainer.setVisibility(View.VISIBLE);
                viewIn[2].start();
                fab.setVisibility(View.VISIBLE);
                fab.show();
            }
        });
        viewIn[2].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if(Util.isOnMainThread()) {
                    Glide.with(getActivity()) // shot.
                            .load(shotItem.imageUri)
                            .crossFade(300)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(palette)
                            .into(shotImage);
                    Glide.with(getActivity()) // icon.
                            .load(shotItem.imageUri)
                            .crossFade(300)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(playerIcon);
                }
            }
        });

        this.initShotBar(shotBar.getMaxiHeight());
        shotBar.setVisibility(View.VISIBLE);
        viewIn[0].start();
    }

    @Override
    public void hideFinish() {
        this.finish();
    }

    // get comments listener.

    @Override
    public void getCommentsSuccess(Call<List<Comment>> call, retrofit2.Response<List<Comment>> response) {
        if (response.body() == null || response.body().size() == 0) {
            wavesLoadingView.setState(WavesLoadingView.NULL);
            return;
        }

        List<CommentItem> itemList = new ArrayList<>();
        for (int i = 0; i < response.body().size(); i ++) {
            itemList.add(new CommentItem(
                    response.body().get(i).user.id,
                    response.body().get(i).user.avatar_url,
                    response.body().get(i).user.username,
                    response.body().get(i).body));
        }
        CommentsAdapter adapter = new CommentsAdapter(getActivity(), itemList, typeface);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getCommentsFailed(Call<List<Comment>> call, Throwable t) {
        wavesLoadingView.setState(WavesLoadingView.FAILED);
        wavesLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wavesLoadingView.setState(WavesLoadingView.SHOWING);
                ((MainActivity) getActivity())
                        .getDribbbleService()
                        .getDribbbleComments(shotItem.shotId, ShotFragment.this);
                wavesLoadingView.setOnClickListener(null);
            }
        });
    }

    // glide request listener.
    // used to set status style.

    private RequestListener palette = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Bitmap bitmap = ImageUtils.drawableToBitmap(resource);
            int color = ColorUtils.calcBackgroundColor(getActivity(), bitmap, 6);

            if (ColorUtils.isLightColor(getActivity(), color)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
            statusBar.setBackgroundColor(color);
            shotBar.setBackgroundColor(color);
            return false;
        }
    };

    // glide simple target.
    // used to download the shot image.

    private SimpleTarget download = new SimpleTarget<byte[]>() {

        @Override
        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
            try {
                ImageUtils.writeImageToFile(
                        getActivity(),
                        resource,
                        Long.toString(shotItem.shotId),
                        isGif);
                Toast.makeText(
                        getActivity(),
                        getString(R.string.download_success),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
