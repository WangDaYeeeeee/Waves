package com.wangdaye.waves.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.item.ShotItem;
import com.wangdaye.waves.ui.widget.imageView.CircleImageView;
import com.wangdaye.waves.ui.widget.imageView.ShotView;
import com.wangdaye.waves.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Shot adapter (recycler view).
 * */

public class ShotsAdapter extends RecyclerView.Adapter<ShotsAdapter.ViewHolder> {
    // widget
    private Context context;
    private MyItemClickListener myItemClickListener;
    private Typeface typeface;

    // data
    public List<ShotItem> itemList;
    private int itemType;

    private final int LEFT = 1;
    private final int RIGHT = 2;

    public static final int MINI_TILE = 0;
    public static final int LARGE_TILE = 1;
    public static final int MINI_CARD_WITH_TITLE = 2;
    public static final int MINI_CARD_WITHOUT_TITLE = 3;
    public static final int LARGE_CARD_WITH_TITLE = 4;
    public static final int LARGE_CARD_WITHOUT_TITLE = 5;

    public ShotsAdapter(Context context, List<ShotItem> itemList, int itemType) {
        this.context = context;
        this.itemList = itemList;
        this.itemType = itemType;
        this.typeface = TypefaceUtils.getTypeface(context);
    }

    /** <br> parent methods. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (itemType == MINI_TILE || itemType == LARGE_TILE) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_tile, parent, false);
            return new ViewHolder(view, LEFT);

        } else if (itemType == LARGE_CARD_WITH_TITLE || itemType == LARGE_CARD_WITHOUT_TITLE) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_card_large, parent, false);
            return new ViewHolder(view, LEFT);

        } else {

            if (viewType == LEFT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_card_left, parent, false);
                return new ViewHolder(view, LEFT);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_card_right, parent, false);
                return new ViewHolder(view, RIGHT);
            }
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (itemType == MINI_CARD_WITH_TITLE) {
            holder.likeNum.setText(String.valueOf(itemList.get(position).likes));
            holder.commentNum.setText(String.valueOf(itemList.get(position).comments));
        } else if (itemType == LARGE_CARD_WITH_TITLE) {
            holder.title.setText(String.valueOf(itemList.get(position).title));
            holder.subtitle.setText(String.valueOf(itemList.get(position).subTitle));
            holder.viewNum.setText(String.valueOf(itemList.get(position).views));
            holder.likeNum.setText(String.valueOf(itemList.get(position).likes));
            holder.commentNum.setText(String.valueOf(itemList.get(position).comments));
            Glide.with(context)
                    .load(itemList.get(position).playerIconUri)
                    .crossFade(300)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.icon);
        }

        holder.gifFlag.setVisibility(itemList.get(position).isGif ? View.VISIBLE : View.GONE);
        int i = new Random().nextInt(2);
        Glide.clear(holder.shotView);
        Glide.with(context)
                .load(itemList.get(position).imageUri)
                .placeholder(i == 0 ? R.drawable.shot_background_1 : R.drawable.shot_background_2)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.shotView);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemClickListener.onItemClick(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemType == MINI_TILE || itemType == LARGE_TILE
                || itemType == LARGE_CARD_WITH_TITLE || itemType == LARGE_CARD_WITHOUT_TITLE) {
            return 0;
        } else {
            if (position % 2 == 0) {
                return LEFT;
            } else {
                return RIGHT;
            }
        }
    }

    public void insertData(ShotItem item, int adapterPosition) {
        this.itemList.add(adapterPosition, item);
        this.notifyItemInserted(adapterPosition);
    }

    /** <br> interface. */

    public interface MyItemClickListener {
        void onItemClick(ViewHolder holder, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    /** <br> holder. */

    public class ViewHolder extends RecyclerView.ViewHolder {
        // widget
        public CardView card;

        public ShotView shotView;
        public Button gifFlag;

        public RelativeLayout titlebar;
        public CircleImageView icon;
        public TextView title;
        public TextView subtitle;

        public RelativeLayout databar;
        public TextView likeNum;
        public TextView commentNum;
        public TextView viewNum;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            if (itemType == MINI_TILE || itemType == LARGE_TILE) {

                card = (CardView) itemView.findViewById(R.id.item_shot_tile_container);
                shotView = (ShotView) itemView.findViewById(R.id.item_shot_tile_image);
                gifFlag = (Button) itemView.findViewById(R.id.item_shot_tile_large_gifFlag);

            } else if (itemType == LARGE_CARD_WITH_TITLE || itemType == LARGE_CARD_WITHOUT_TITLE) {

                card = (CardView) itemView.findViewById(R.id.item_shot_card_large_container);
                shotView = (ShotView) itemView.findViewById(R.id.item_shot_card_large_image);
                gifFlag = (Button) itemView.findViewById(R.id.item_shot_card_large_gifFlag);
                titlebar = (RelativeLayout) itemView.findViewById(R.id.item_shot_card_large_titlebar);
                icon = (CircleImageView) itemView.findViewById(R.id.item_shot_card_large_icon);
                title = (TextView) itemView.findViewById(R.id.item_shot_card_large_title);
                subtitle = (TextView) itemView.findViewById(R.id.item_shot_card_large_subtitle);
                databar = (RelativeLayout) itemView.findViewById(R.id.item_shot_card_large_databar);
                likeNum = (TextView) itemView.findViewById(R.id.item_shot_card_large_likeNum);
                likeNum.setTypeface(typeface);
                commentNum = (TextView) itemView.findViewById(R.id.item_shot_card_large_commentNum);
                commentNum.setTypeface(typeface);
                viewNum = (TextView) itemView.findViewById(R.id.item_shot_card_large_viewNum);
                viewNum.setTypeface(typeface);
                if (itemType == LARGE_CARD_WITHOUT_TITLE) {
                    titlebar.setVisibility(View.GONE);
                    databar.setVisibility(View.GONE);
                } else {
                    title.setTypeface(typeface);
                }

            } else {
                switch (type) {

                    case LEFT:
                        card = (CardView) itemView.findViewById(R.id.item_shot_card_left_container);
                        shotView = (ShotView) itemView.findViewById(R.id.item_shot_card_left_image);
                        gifFlag = (Button) itemView.findViewById(R.id.item_shot_card_left_gifFlag);
                        databar = (RelativeLayout) itemView.findViewById(R.id.item_shot_card_left_databar);
                        likeNum = (TextView) itemView.findViewById(R.id.item_shot_card_left_likeNum);
                        commentNum = (TextView) itemView.findViewById(R.id.item_shot_card_left_commentNum);
                        if (itemType == MINI_CARD_WITHOUT_TITLE) {
                            databar.setVisibility(View.GONE);
                        }
                        break;

                    case RIGHT:
                        card = (CardView) itemView.findViewById(R.id.item_shot_card_right_container);
                        shotView = (ShotView) itemView.findViewById(R.id.item_shot_card_right_image);
                        gifFlag = (Button) itemView.findViewById(R.id.item_shot_card_right_gifFlag);
                        databar = (RelativeLayout) itemView.findViewById(R.id.item_shot_card_right_databar);
                        likeNum = (TextView) itemView.findViewById(R.id.item_shot_card_right_likeNum);
                        commentNum = (TextView) itemView.findViewById(R.id.item_shot_card_right_commentNum);
                        if (itemType == MINI_CARD_WITHOUT_TITLE) {
                            databar.setVisibility(View.GONE);
                        }
                        break;
                }
                likeNum.setTypeface(typeface);
                commentNum.setTypeface(typeface);
            }
        }
    }

    /** <br> item animator. */

    public static class ShotItemAnimator extends RecyclerView.ItemAnimator {
        // data
        private List<RecyclerView.ViewHolder> addHolderList;
        private List<RecyclerView.ViewHolder> removeHolderList;
        private List<RecyclerView.ViewHolder> addAnimationList;
        private List<RecyclerView.ViewHolder> removeAnimationList;

        public ShotItemAnimator() {
            this.addHolderList = new ArrayList<>();
            this.removeHolderList = new ArrayList<>();
            this.addAnimationList = new ArrayList<>();
            this.removeAnimationList = new ArrayList<>();
        }

        @Override // add
        public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                         @Nullable ItemHolderInfo preLayoutInfo,
                                         @NonNull ItemHolderInfo postLayoutInfo) {
            viewHolder.itemView.setVisibility(View.GONE);
            addHolderList.add(viewHolder);
            return true;
        }

        @Override // remove
        public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                            @NonNull ItemHolderInfo preLayoutInfo,
                                            @Nullable ItemHolderInfo postLayoutInfo) {
            removeHolderList.add(viewHolder);
            return false;
        }

        @Override // move
        public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull ItemHolderInfo preLayoutInfo,
                                          @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override // change
        public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                     @NonNull RecyclerView.ViewHolder newHolder,
                                     @NonNull ItemHolderInfo preLayoutInfo,
                                     @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public void runPendingAnimations() {
            boolean isAdd = !addHolderList.isEmpty();
            boolean isRemove = !removeHolderList.isEmpty();

            if (!isAdd && !isRemove) {
                return;
            }

            if (isAdd) {
                List<RecyclerView.ViewHolder> tempList = new ArrayList<>();
                tempList.addAll(addHolderList);
                addHolderList.clear();
                for (RecyclerView.ViewHolder holder : tempList) {
                    this.showAddAnimator(holder);
                }
                tempList.clear();
            }

            if (isRemove) {
                for (RecyclerView.ViewHolder holder : removeHolderList) {
                    this.showRemoveAnimator(holder);
                }
                removeHolderList.clear();
            }
    }

        @Override
        public void endAnimation(RecyclerView.ViewHolder item) {
            item.itemView.setVisibility(View.VISIBLE);
        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return !addHolderList.isEmpty()
                    && !removeHolderList.isEmpty()
                    && !addAnimationList.isEmpty()
                    && !removeAnimationList.isEmpty();
        }

        private void showAddAnimator(final RecyclerView.ViewHolder holder) {
            this.addAnimationList.add(holder);
            final View itemView = holder.itemView;
            ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f);
            animator.setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    itemView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    itemView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    addAnimationList.remove(holder);
                    if (!isRunning()) {
                        dispatchAnimationsFinished();
                    }
                }
            });
            animator.start();
        }

        private void showRemoveAnimator(final RecyclerView.ViewHolder holder) {
            this.removeAnimationList.add(holder);
            final View itemView = holder.itemView;
            ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f);
            animator.setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    itemView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    itemView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    removeAnimationList.remove(holder);
                    itemView.setVisibility(View.VISIBLE);
                    if (!isRunning()) {
                        dispatchAnimationsFinished();
                    }
                }
            });
            animator.start();
        }
    }
}