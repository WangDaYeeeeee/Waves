package com.wangdaye.waves.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.wangdaye.waves.R;
import com.wangdaye.waves.application.Waves;
import com.wangdaye.waves.data.dirbbble.model.Comment;
import com.wangdaye.waves.data.dirbbble.tools.DribbbleService;
import com.wangdaye.waves.data.item.CommentItem;
import com.wangdaye.waves.ui.adapter.CommentsAdapter;
import com.wangdaye.waves.ui.widget.CircleImageView;
import com.wangdaye.waves.ui.widget.MyFloatingActionButton;
import com.wangdaye.waves.ui.widget.RevealView;
import com.wangdaye.waves.ui.widget.ShotBarLayout;
import com.wangdaye.waves.ui.widget.ShotScrollParent;
import com.wangdaye.waves.ui.widget.ShotScrollView;
import com.wangdaye.waves.ui.widget.SwipeBackLayout;
import com.wangdaye.waves.ui.widget.ThemeActivity;
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
 * Shot details activity.
 * */

public class ShotActivity extends ThemeActivity
        implements SwipeBackLayout.OnSwipeListener, View.OnClickListener, RevealView.OnRevealingListener,
        DribbbleService.GetCommentsListener, CommentsAdapter.MyItemClickListener {
    // widget
    private ShotBarLayout shotBar;
    private FrameLayout statusBar;
    private ImageView shotImage;

    private ShotScrollView scrollView;
    private CircleImageView playerIcon;
    private TextView likesNum;

    private MyFloatingActionButton fab;
    private RevealView revealView;
    private WavesLoadingView wavesLoadingView;
    private RecyclerView recyclerView;

    // data
    private boolean started;
    private boolean isGif;
    //private String[] tags;

    private Typeface typeface;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        this.setStatusBarTransparent();
        setContentView(R.layout.activity_shot);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (started) {
            return;
        }
        this.started = true;

        this.initWidget();
        this.initColorTheme(null, getString(R.string.app_name), R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        revealView.setState(RevealView.REVEALING);
        wavesLoadingView.setState(WavesLoadingView.SHOWING);
        DribbbleService.instance.getDribbbleComments(getIntent().getLongExtra(getString(R.string.key_shot_id), 0), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Util.isOnMainThread()) {
            Glide.get(this).clearMemory();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    /** <br> UI. */

    private void initWidget() {
        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_shot_swipeBackLayout);
        assert swipeBackLayout != null;
        swipeBackLayout.setBackground(
                findViewById(R.id.activity_shot_statusBar),
                findViewById(R.id.activity_shot_shadow));
        swipeBackLayout.setOnSwipeListener(this,
                findViewById(R.id.activity_shot_scrollView),
                findViewById(R.id.activity_shot_container));

        ShotScrollParent scrollParent = (ShotScrollParent) findViewById(R.id.activity_shot_container);
        assert scrollParent != null;
        scrollParent.setPadding(0, getStatusBarHeight(), 0, 0);

        this.revealView = (RevealView) findViewById(R.id.activity_shot_revealView);
        assert revealView != null;
        revealView.setDrawTime(RevealView.TWO_TIMES_SPEED);
        revealView.setColor(
                getIntent().getIntExtra(
                                getString(R.string.key_reveal_color),
                                ContextCompat.getColor(this, R.color.colorTextGrey2nd)),
                ContextCompat.getColor(this, R.color.colorRoot));
        revealView.setOnRevealingListener(this);
        revealView.setTouchPosition(0,
                getIntent().getFloatExtra(getString(R.string.key_touch_x), 0),
                getIntent().getFloatExtra(getString(R.string.key_touch_y), 0));

        this.fab = (MyFloatingActionButton) findViewById(R.id.activity_shot_fab);
        assert fab != null;
        fab.setOnClickListener(this);

        this.initShotPart();
        this.initTitlePart();
        this.initDataPart();
    }

    private void initShotBar(float height) {
        FrameLayout.LayoutParams shotBarParams = (FrameLayout.LayoutParams) shotBar.getLayoutParams();
        shotBarParams.height = (int) height;
        shotBarParams.width = getResources().getDisplayMetrics().widthPixels;
        shotBar.setLayoutParams(shotBarParams);

        FrameLayout.LayoutParams statusBarParams = (FrameLayout.LayoutParams) statusBar.getLayoutParams();
        statusBarParams.height = getStatusBarHeight();

        scrollView.setPadding(0, (int) height, 0, 0);

        FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        fabParams.topMargin = fab.calcMargin(height, 0, 0);
        fab.setLayoutParams(fabParams);
    }

    private void initShotPart() {
        this.shotBar = (ShotBarLayout) findViewById(R.id.activity_shot_shotBar);
        shotBar.setMaxiHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0 * 3.0));
        shotBar.setMiniHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0));

        this.statusBar = (FrameLayout) findViewById(R.id.activity_shot_statusBar);

        this.shotImage = (ImageView) findViewById(R.id.activity_shot_shot);
        assert shotImage != null;
        shotImage.setOnClickListener(this);

        this.scrollView = (ShotScrollView) findViewById(R.id.activity_shot_scrollView);
    }

    private void initTitlePart() {
        this.playerIcon = (CircleImageView) findViewById(R.id.container_shot_player);

        TextView title = (TextView) findViewById(R.id.container_shot_title);
        assert title != null;
        title.setTypeface(typeface);
        title.setText(getIntent().getStringExtra(getString(R.string.key_title)));

        TextView subTitle = (TextView) findViewById(R.id.container_shot_subtitle);
        assert subTitle != null;
        subTitle.setText(getIntent().getStringExtra(getString(R.string.key_sub_title)));
    }

    @SuppressLint("SetTextI18n")
    private void initDataPart() {
        HtmlTextView content = (HtmlTextView) findViewById(R.id.container_shot_content);
        assert content != null;
        content.setHtmlFromString(
                getIntent().getStringExtra(getString(R.string.key_content)),
                new HtmlTextView.RemoteImageGetter());

        this.likesNum = (TextView) findViewById(R.id.container_shot_likes_num);
        assert likesNum != null;
        likesNum.setTypeface(typeface);
        likesNum.setText(String.valueOf(getIntent().getLongExtra(getString(R.string.key_likes), 0)) + " Likes");

        TextView viewsNum = (TextView) findViewById(R.id.container_shot_views_num);
        assert viewsNum != null;
        viewsNum.setTypeface(typeface);
        viewsNum.setText(String.valueOf(getIntent().getLongExtra(getString(R.string.key_views), 0)) + " Views");

        TextView bucketsNum = (TextView) findViewById(R.id.container_shot_buckets_num);
        assert bucketsNum != null;
        bucketsNum.setTypeface(typeface);
        bucketsNum.setText(String.valueOf(getIntent().getLongExtra(getString(R.string.key_buckets), 0)) + " Buckets");

        findViewById(R.id.container_shot_likes_container).setOnClickListener(this);
        findViewById(R.id.container_shot_views_container).setOnClickListener(this);
        findViewById(R.id.container_shot_buckets_container).setOnClickListener(this);

        this.wavesLoadingView = (WavesLoadingView) findViewById(R.id.container_shot_wavesLoadingView);
        wavesLoadingView.setNullString(getString(R.string.no_comments));

        this.recyclerView = (RecyclerView) findViewById(R.id.container_details_commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    /** <br> data. */

    // init data.

    private void initData() {
        this.started = false;
        /*
        this.tags = getIntent()
                .getBundleExtra(getString(R.string.key_values))
                .getStringArray(getString(R.string.key_tags));*/

        String uri = getIntent().getStringExtra(getString(R.string.key_image_uri));
        Log.d("ShotActivity", uri.substring(uri.length() - 3));
        this.isGif = uri.substring(uri.length() - 3).equals("gif");

        this.typeface = TypefaceUtils.getTypeface(this);
    }

    // download.

    private void downloadShot() {

        if (isGif) {
            Glide.with(getApplicationContext())
                    .load(getIntent().getStringExtra(getString(R.string.key_image_uri)))
                    .asGif()
                    .toBytes()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(download);
        } else {
            Glide.with(getApplicationContext())
                    .load(getIntent().getStringExtra(getString(R.string.key_image_uri)))
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
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.activity_shot_shot:
                Uri uri = Uri.parse(getIntent().getStringExtra(getString(R.string.key_web_uri)));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;

            case R.id.activity_shot_fab:
                Toast.makeText(this, getString(R.string.download_start), Toast.LENGTH_SHORT).show();

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
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.view_in_1),
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.view_in_1),
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.view_in_2)
        };
        viewIn[0].setTarget(findViewById(R.id.activity_shot_shotBar));
        viewIn[1].setTarget(findViewById(R.id.container_shot_title_container));
        viewIn[2].setTarget(findViewById(R.id.container_shot_data_container));

        viewIn[0].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                findViewById(R.id.container_shot_title_container).setVisibility(View.VISIBLE);
                viewIn[1].start();
            }
        });
        viewIn[1].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                findViewById(R.id.container_shot_data_container).setVisibility(View.VISIBLE);
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
                    Glide.with(getApplicationContext()) // shot.
                            .load(getIntent().getStringExtra(getString(R.string.key_image_uri)))
                            .crossFade(300)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(palette)
                            .into(shotImage);
                    Glide.with(getApplicationContext()) // icon.
                            .load(getIntent().getStringExtra(getString(R.string.key_player_icon_uri)))
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
        CommentsAdapter adapter = new CommentsAdapter(this, itemList, typeface);
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
                DribbbleService.instance.getDribbbleComments(getIntent().getLongExtra(getString(R.string.key_shot_id), 0), ShotActivity.this);
                wavesLoadingView.setOnClickListener(null);
            }
        });
    }

    // glide request listener.
    // use to set status style.

    private RequestListener palette = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Bitmap bitmap = ImageUtils.drawableToBitmap(resource);
            int color = ColorUtils.calcBackgroundColor(ShotActivity.this, bitmap, 6);

            if (ColorUtils.isLightColor(ShotActivity.this, color)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
            statusBar.setBackgroundColor(color);
            shotBar.setBackgroundColor(color);
            return false;
        }
    };

    // glide simple target.
    // use to download the shot image.

    private SimpleTarget download = new SimpleTarget<byte[]>() {

        @Override
        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
            try {
                ImageUtils.writeImageToFile(
                        ShotActivity.this,
                        resource,
                        Long.toString(getIntent().getLongExtra(getString(R.string.key_shot_id), 0)),
                        isGif);
                Toast.makeText(
                        ShotActivity.this,
                        getString(R.string.download_success),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
