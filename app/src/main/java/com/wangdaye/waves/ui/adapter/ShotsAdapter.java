package com.wangdaye.waves.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.item.ShotItem;

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

    // data
    public List<ShotItem> itemList;

    public ShotsAdapter(Context context, List<ShotItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    /** <br> parent methods. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_mini, parent, false);
        return new ViewHolder(view, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.gifFlag.setVisibility(itemList.get(position).isGif ? View.VISIBLE : View.GONE);
        int i = new Random().nextInt(2);
        Glide.clear(holder.shotView);
        Glide.with(context)
                .load(itemList.get(position).imageUri)
                .placeholder(i == 0 ? R.drawable.shot_background_1 : R.drawable.shot_background_2)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.shotView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void insertData(ShotItem item, int adapterPosition) {
        this.itemList.add(adapterPosition, item);
        this.notifyItemInserted(adapterPosition);
    }

    public void removeData(int adapterPosition) {
        this.itemList.remove(adapterPosition);
        this.notifyItemRemoved(adapterPosition);
    }

    /** <br> interface. */

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    public interface LoadFinishCallback {
        void loadFinish(Object obj);
    }

    /** <br> holder. */

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public RelativeLayout container;
        public ImageView shotView;
        public Button gifFlag;

        private MyItemClickListener myItemClickListener;

        public ViewHolder(View itemView, MyItemClickListener itemClickListener) {
            super(itemView);
            this.myItemClickListener = itemClickListener;
            this.container = (RelativeLayout) itemView.findViewById(R.id.item_shot_mini_container);
            shotView = (ImageView) itemView.findViewById(R.id.item_shot_mini_image);
            shotView.setOnClickListener(this);
            gifFlag = (Button) itemView.findViewById(R.id.item_shot_mini_gifFlag);
        }

        @Override
        public void onClick(View v) {
            myItemClickListener.onItemClick(v, getAdapterPosition());
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