package com.wangdaye.waves.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.wangdaye.waves.ui.adapter.ShotsAdapter;

/**
 * My recycler view.
 * */

public class HomeRecyclerView extends RecyclerView
        implements ShotsAdapter.LoadFinishCallback {
    // widget
    private MyFloatingActionButton fab;

    private OnLoadMoreListener loadMoreListener;

    // data
    private int scrollState;
    private boolean isLoadingMore;

    public float touchX, touchY;

    /** <br> life cycle. */

    public HomeRecyclerView(Context context) {
        super(context);
        this.initialize();
    }

    public HomeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public HomeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize();
    }

    private void initialize() {
        this.scrollState = SCROLL_STATE_IDLE;
        this.isLoadingMore = false;

        this.fab = null;
    }

    /** <br> setter. */

    public void setFab(MyFloatingActionButton fab) {
        this.fab = fab;
    }

    /** <br> UI. */

    public void scrollToTop() {
        getLayoutManager().scrollToPosition(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchX = ev.getX();
        touchY = ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    /** <br> interface. */

    // on load more listener.

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    @Override
    public void loadFinish(Object obj) {
        isLoadingMore = false;
    }

    // my on scroll listener.

    public void setOnMyOnScrollListener() {
        this.addOnScrollListener(new MyOnScrollListener());
    }

    private class MyOnScrollListener extends OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (fab != null) {
                if ((scrollState == SCROLL_STATE_DRAGGING || scrollState == SCROLL_STATE_SETTLING)
                        && dy < 0) {
                    fab.show();
                } else if ((scrollState == SCROLL_STATE_DRAGGING || scrollState == SCROLL_STATE_SETTLING)
                        && dy > 0) {
                    fab.hide();
                }
            }

            if (getLayoutManager() instanceof LinearLayoutManager) {
                int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = HomeRecyclerView.this.getAdapter().getItemCount();

                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
                    loadMoreListener.onLoadMore();
                    isLoadingMore = true;
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            scrollState = newState;
        }
    }
}