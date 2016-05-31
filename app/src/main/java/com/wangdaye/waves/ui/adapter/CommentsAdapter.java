package com.wangdaye.waves.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.waves.R;
import com.wangdaye.waves.data.item.CommentItem;
import com.wangdaye.waves.ui.widget.imageView.CircleImageView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * Comments adapter (recycler view).
 * */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    // widget
    private MyItemClickListener myItemClickListener;
    private Context context;

    // data
    public List<CommentItem> itemList;
    private Typeface typeface;

    public CommentsAdapter(Context context, List<CommentItem> itemList, Typeface typeface) {
        this.context = context;
        this.itemList = itemList;
        this.typeface = typeface;
    }

    /** <br> parent methods. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(itemList.get(position).title);
        holder.title.setTypeface(typeface);
        holder.content.setHtmlFromString(itemList.get(position).content, new HtmlTextView.RemoteImageGetter());

        Glide.with(context)
                .load(itemList.get(position).iconUrl)
                .crossFade(300)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.icon);
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
        public RelativeLayout container;
        public CircleImageView icon;
        public TextView title;
        public HtmlTextView content;

        private MyItemClickListener listener;

        public ViewHolder(View itemView, MyItemClickListener listener) {
            super(itemView);
            this.listener = listener;

            this.container = (RelativeLayout) itemView.findViewById(R.id.item_comment);
            container.setOnClickListener(this);
            this.icon = (CircleImageView) itemView.findViewById(R.id.item_comment_icon);
            this.title = (TextView) itemView.findViewById(R.id.item_comment_title);
            this.content = (HtmlTextView) itemView.findViewById(R.id.item_comment_content);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
