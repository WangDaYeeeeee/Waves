package com.wangdaye.waves.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.waves.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Shot item type adapter (recycler view).
 * */

public class ShotItemTypeAdapter extends RecyclerView.Adapter<ShotItemTypeAdapter.ViewHolder> {
    // widget
    private Context context;
    private MyItemClickListener myItemClickListener;

    // data
    private List<Integer> itemList;

    public ShotItemTypeAdapter(Context context) {
        super();
        this.context = context;
        this.itemList = new ArrayList<>();
        itemList.add(ShotsAdapter.MINI_TILE);
        itemList.add(ShotsAdapter.LARGE_TILE);
        itemList.add(ShotsAdapter.MINI_CARD_WITH_TITLE);
        itemList.add(ShotsAdapter.MINI_CARD_WITHOUT_TITLE);
        itemList.add(ShotsAdapter.LARGE_CARD_WITH_TITLE);
        itemList.add(ShotsAdapter.LARGE_CARD_WITHOUT_TITLE);
    }

    /** <br> parent methods. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shot_item_type, parent, false);
        return new ViewHolder(view, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (itemList.get(position)) {

            case ShotsAdapter.MINI_TILE:
                Glide.with(context)
                        .load(R.drawable.shot_item_tile_mini)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;

            case ShotsAdapter.LARGE_TILE:
                Glide.with(context)
                        .load(R.drawable.shot_item_tile_large)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;

            case ShotsAdapter.MINI_CARD_WITH_TITLE:
                Glide.with(context)
                        .load(R.drawable.shot_item_card_mini_with_title)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;

            case ShotsAdapter.MINI_CARD_WITHOUT_TITLE:
                Glide.with(context)
                        .load(R.drawable.shot_item_card_mini_without_title)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;

            case ShotsAdapter.LARGE_CARD_WITH_TITLE:
                Glide.with(context)
                        .load(R.drawable.shot_item_card_large_with_title)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;

            case ShotsAdapter.LARGE_CARD_WITHOUT_TITLE:
                Glide.with(context)
                        .load(R.drawable.shot_item_card_large_without_title)
                        .crossFade(300)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.image);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /** interface. */

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    /** <br> holder. */

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        private ImageView image;
        private MyItemClickListener listener;

        public ViewHolder(View itemView, MyItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.image = (ImageView) itemView.findViewById(R.id.item_shot_item_type_image);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
