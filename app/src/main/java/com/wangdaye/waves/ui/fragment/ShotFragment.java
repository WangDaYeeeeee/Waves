package com.wangdaye.waves.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
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
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.waves.R;
import com.wangdaye.waves.application.Waves;
import com.wangdaye.waves.data.dirbbble.model.Comment;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.CommentItem;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.ui.activity.MainActivity;
import com.wangdaye.waves.ui.adapter.CommentsAdapter;
import com.wangdaye.waves.ui.widget.imageView.CircleImageView;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.container.RevealFragment;
import com.wangdaye.waves.ui.widget.RevealView;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotBarLayout;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotScrollParent;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotScrollView;
import com.wangdaye.waves.ui.widget.imageView.ShotView;
import com.wangdaye.waves.ui.widget.SwipeBackLayout;
import com.wangdaye.waves.utils.ColorUtils;
import com.wangdaye.waves.utils.DisplayUtils;
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
    private SwipeBackLayout swipeBackLayout;
    private RevealView revealView;

    private FrameLayout statusBar;
    private ShotBarLayout shotBar;
    private ShotView shotImage;

    private ShotScrollView scrollView;

    private CircleImageView playerIcon;
    private TextView likesNum;

    private MyFloatingActionButton fab;

    private FrameLayout loadingContainer;
    private RelativeLayout airBallContainer;
    private CircularProgressView circularProgressView;
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
        revealView.setState(RevealView.REVEALING);

        this.getComments();
        if (Util.isOnMainThread()) {
            if (Util.isOnMainThread()) {
                Glide.with(getActivity()) // shot.
                        .load(shotItem.imageUri)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .listener(palette)
                        .into(shotImage);
            }
            Glide.with(getActivity()) // icon.
                    .load(shotItem.imageUri)
                    .crossFade(300)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(playerIcon);
        }

        return view;
    }

    @Override
    public void hide() {
        fab.hide();
        Animation viewOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        viewOut.setAnimationListener(new ViewOutListener());
        swipeBackLayout.startAnimation(viewOut);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Util.isOnMainThread()) {
            Glide.get(getActivity()).clearMemory();
        }
    }

    private void finish() {
        MainActivity container = (MainActivity) getActivity();
        MyFloatingActionButton fab = (MyFloatingActionButton) container.findViewById(R.id.container_main_fab);
        assert fab != null;
        fab.show();
        container.setStatusBarTransparent();
        container.popFragment();
    }

    /** <br> UI. */

    private void initWidget(View view) {
        this.swipeBackLayout = (SwipeBackLayout) view.findViewById(R.id.fragment_shot_swipeBackLayout);
        swipeBackLayout.setEnabled(false);
        swipeBackLayout.setBackground(view.findViewById(R.id.fragment_shot_statusBar));
        swipeBackLayout.setOnSwipeListener(this,
                view.findViewById(R.id.fragment_shot_scrollView),
                view.findViewById(R.id.fragment_shot_container));

        ShotScrollParent scrollParent = (ShotScrollParent) view.findViewById(R.id.fragment_shot_container);
        scrollParent.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);

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
        this.setWidgetSizeForShot();
    }

    private void initShotPart(View view) {
        this.statusBar = (FrameLayout) view.findViewById(R.id.fragment_shot_statusBar);

        this.shotBar = (ShotBarLayout) view.findViewById(R.id.fragment_shot_shotBar);
        shotBar.setMaxiHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0 * 3.0));
        shotBar.setMiniHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0));

        this.shotImage = (ShotView) view.findViewById(R.id.fragment_shot_shot);
        shotImage.setOnClickListener(this);

        this.scrollView = (ShotScrollView) view.findViewById(R.id.fragment_shot_scrollView);
    }

    private void initTitlePart(View view) {
        this.playerIcon = (CircleImageView) view.findViewById(R.id.container_shot_player);

        TextView title = (TextView) view.findViewById(R.id.container_shot_title);
        title.setTypeface(typeface);
        title.setText(shotItem.title);

        TextView subTitle = (TextView) view.findViewById(R.id.container_shot_subtitle);
        subTitle.setText(shotItem.subTitle);
    }

    @SuppressLint("SetTextI18n")
    private void initDataPart(View view) {
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

        this.loadingContainer = (FrameLayout) view.findViewById(R.id.container_shot_loadingContainer);

        this.airBallContainer = (RelativeLayout) view.findViewById(R.id.container_airball_view_mini);
        airBallContainer.setVisibility(View.GONE);
        airBallContainer.setOnClickListener(this);

        TextView airBall = (TextView) view.findViewById(R.id.container_airball_view_mini_airBall);
        airBall.setTypeface(TypefaceUtils.getTypeface(getActivity()));

        this.circularProgressView = (CircularProgressView) view.findViewById(R.id.container_loading_view_circularProgressView);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.container_details_commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void setWidgetSizeForShot() {
        int height = (int) (getResources().getDisplayMetrics().widthPixels / 4.0 * 3.0);

        scrollView.setPadding(0, height, 0, 0);

        FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        fabParams.topMargin = fab.calcMargin(height, 0, 0);
        fab.setLayoutParams(fabParams);
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

    private void getComments() {
        ((MainActivity) getActivity())
                .getDribbbleService()
                .getDribbbleComments(shotItem.shotId, this);
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
        ((MainActivity) getActivity()).popFragmentList();
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

            case R.id.container_airball_view_mini:
                circularProgressView.setVisibility(View.VISIBLE);
                airBallContainer.setVisibility(View.GONE);
                getComments();
        }
    }

    // on item click.

    @Override
    public void onItemClick(View view, int position) {

    }

    // on reveal listener.

    @Override
    public void revealFinish() {
        Animation[] viewIn = new Animation[] {
                AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in),
                AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in)
        };

        shotBar.setVisibility(View.VISIBLE);
        shotBar.startAnimation(viewIn[0]);
        scrollView.setVisibility(View.VISIBLE);
        scrollView.startAnimation(viewIn[1]);

        fab.setVisibility(View.VISIBLE);
        fab.show();
        swipeBackLayout.setEnabled(true);
    }

    @Override
    public void hideFinish() {
        this.finish();
    }

    // get comments listener.

    @Override
    public void getCommentsSuccess(Call<List<Comment>> call, retrofit2.Response<List<Comment>> response) {
        loadingContainer.setVisibility(View.GONE);

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
        airBallContainer.setVisibility(View.VISIBLE);
        circularProgressView.setVisibility(View.GONE);
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
            int color = ColorUtils.calcBackgroundColor(bitmap);

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
