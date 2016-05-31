package com.wangdaye.waves.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.item.OpenSourceItem;
import com.wangdaye.waves.ui.adapter.OpenSourceAdapter;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotBarLayout;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotScrollParent;
import com.wangdaye.waves.ui.widget.nestedScroll.ShotScrollView;
import com.wangdaye.waves.ui.widget.imageView.ShotView;
import com.wangdaye.waves.ui.widget.StatusBarView;
import com.wangdaye.waves.ui.widget.SwipeBackLayout;
import com.wangdaye.waves.ui.widget.container.ThemeActivity;
import com.wangdaye.waves.utils.ColorUtils;
import com.wangdaye.waves.utils.DisplayUtils;
import com.wangdaye.waves.utils.ImageUtils;
import com.wangdaye.waves.utils.TypefaceUtils;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * About activity.
 * */

public class AboutActivity extends ThemeActivity
        implements SwipeBackLayout.OnSwipeListener, View.OnClickListener {
    // widget
    private StatusBarView statusBar;

    private ShotBarLayout shotBar;
    private ShotView shotImage;

    private ShotScrollView scrollView;
    private ImageView appIcon;

    // data
    private boolean started;
    private Typeface typeface;

    private String appDescription;
    private String[] title;
    private String[] subtitle;
    private String[] web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        this.setStatusBarTransparent();
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (started) {
            return;
        }
        started = true;

        this.initWidget();
        this.initColorTheme(getString(R.string.nav_about), R.color.colorPrimary);
        this.loadImage();

        scrollView.scrollTo(0, 0);
        scrollView.setPadding(0, (int) shotBar.getMaxiHeight(), 0, 0);
    }

    /** <br> UI. */

    private void initWidget() {
        this.statusBar = (StatusBarView) findViewById(R.id.activity_about_statusBar);

        SwipeBackLayout swipeBackLayout = (SwipeBackLayout) findViewById(R.id.activity_about_swipeBackLayout);
        assert swipeBackLayout != null;
        swipeBackLayout.setBackground(findViewById(R.id.activity_about_statusBar));
        swipeBackLayout.setOnSwipeListener(this,
                findViewById(R.id.activity_about_scrollView),
                findViewById(R.id.activity_about_container));

        ShotScrollParent scrollParent = (ShotScrollParent) findViewById(R.id.activity_about_container);
        assert scrollParent != null;
        scrollParent.setPadding(0, DisplayUtils.getStatusBarHeight(getResources()), 0, 0);

        this.initShotPart();
        this.initTitlePart();
        this.initDataPart();
    }

    private void initShotPart() {
        this.shotBar = (ShotBarLayout) findViewById(R.id.activity_about_shotBar);
        shotBar.setMaxiHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0 * 3.0));
        shotBar.setMiniHeight((float) (getResources().getDisplayMetrics().widthPixels / 4.0));

        this.shotImage = (ShotView) findViewById(R.id.activity_about_shot);

        this.scrollView = (ShotScrollView) findViewById(R.id.activity_about_scrollView);
    }

    @SuppressLint("SetTextI18n")
    private void initTitlePart() {
        this.appIcon = (ImageView) findViewById(R.id.container_about_app_icon);

        TextView title = (TextView) findViewById(R.id.container_about_app_name);
        assert title != null;
        title.setTypeface(typeface);
        title.setText(getString(R.string.app_name) + " -" + getString(R.string.version_code) + " beta");

        TextView subTitle = (TextView) findViewById(R.id.container_about_powered);
        assert subTitle != null;
        subTitle.setText(getString(R.string.powered));
    }

    private void initDataPart() {
        HtmlTextView description = (HtmlTextView) findViewById(R.id.container_about_app_description);
        assert description != null;
        description.setHtmlFromString(appDescription, new HtmlTextView.RemoteImageGetter());

        Button changelogButton = (Button) findViewById(R.id.container_about_changelogButton);
        assert changelogButton != null;
        changelogButton.setOnClickListener(this);

        Button githubButton = (Button) findViewById(R.id.container_about_githubButton);
        assert githubButton != null;
        githubButton.setOnClickListener(this);

        List<OpenSourceItem> itemList = new ArrayList<>();
        for (int i = 0; i < web.length; i ++) {
            itemList.add(new OpenSourceItem(title[i], subtitle[i], web[i]));
        }
        OpenSourceAdapter adapter = new OpenSourceAdapter(itemList, typeface);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.container_about_openSourceList);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /** <br> data. */

    private void initData() {
        this.started = false;
        this.typeface = TypefaceUtils.getTypeface(this);

        this.appDescription = ""
                + "<p>" + "<a href=\"" + getString(R.string.my_email) + "\">" + getString(R.string.my_email) + "</a>"
                + "<p>" + "<a href=\"" + getString(R.string.my_github) + "\" >" + getString(R.string.my_github) + "</a>";

        this.title = new String[] {
                "Dribbble",
                "Material Design Icons",
                "Retrofit",
                "Jsoup",
                "Junit4",
                "Glide",
                "Okhttp",
                "Html-TextView",
                "CircularProgressView"
        };

        this.subtitle = new String[] {
                "Show and tell for designers.",
                "Maintained by Austin Andrews.",
                "Square",
                "Jonathan Hedley",
                "junit-team",
                "Bump Technologies",
                "Square",
                "Sufficiently Secure",
                "Rahat Ahmed"
        };

        this.web = new String[] {
                "<a href=\"" + getString(R.string.dribbble_web) + "\">" + getString(R.string.dribbble_web) + "</a>",
                "<a href=\"" + getString(R.string.material_icon_web)  + "\">" + getString(R.string.material_icon_web) + "</a>",
                "<a href=\"https://github.com/square/retrofit\">https://github.com/square/retrofit</a>",
                "<a href=\"https://github.com/jhy/jsoup\">https://github.com/jhy/jsoup</a>",
                "<a href=\"https://github.com/junit-team/junit4\">https://github.com/junit-team/junit4</a>",
                "<a href=\"https://github.com/bumptech/glide\">https://github.com/bumptech/glide</a>",
                "<a href=\"https://github.com/square/okhttp\">https://github.com/square/okhttp</a>",
                "<a href=\"https://github.com/SufficientlySecure/html-textview\">https://github.com/SufficientlySecure/html-textview</a>",
                "<a href=\"https://github.com/rahatarmanahmed/CircularProgressView\">https://github.com/rahatarmanahmed/CircularProgressView</a>"
        };
    }

    private void loadImage() {
        Glide.with(getApplicationContext()) // shot.
                .load(R.drawable.nav_header_animated)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(palette)
                .into(shotImage);
        Glide.with(getApplicationContext()) // icon.
                .load(R.drawable.ic_launcher)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(appIcon);
    }

    /** interface. */

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.container_about_changelogButton:
                Uri changelog = Uri.parse("https://github.com/WangDaYeeeeee/Waves/releases");
                startActivity(new Intent(Intent.ACTION_VIEW, changelog));
                break;

            case R.id.container_about_githubButton:
                Uri github = Uri.parse("https://github.com/WangDaYeeeeee/Waves");
                startActivity(new Intent(Intent.ACTION_VIEW, github));
                break;
        }
    }

    // swipe back

    @Override
    public boolean canSwipeBack(View target, int dir) {
        return !ViewCompat.canScrollVertically(target, dir);
    }

    @Override
    public void swipeFinish() {
        finish();
    }

    // glide request listener.

    private RequestListener palette = new RequestListener<Integer, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Integer model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Bitmap bitmap = ImageUtils.drawableToBitmap(resource);
            int color = ColorUtils.calcBackgroundColor(bitmap);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            statusBar.setBackgroundColor(color);
            shotBar.setBackgroundColor(color);
            return false;
        }
    };
}