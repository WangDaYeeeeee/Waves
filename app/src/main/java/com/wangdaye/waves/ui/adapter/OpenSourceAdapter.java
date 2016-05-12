package com.wangdaye.waves.ui.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdaye.waves.R;
import com.wangdaye.waves.data.item.OpenSourceItem;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * Open source adapter (recycler view).
 * */

public class OpenSourceAdapter extends RecyclerView.Adapter<OpenSourceAdapter.ViewHolder> {
    // data
    public List<OpenSourceItem> itemList;
    private Typeface typeface;

    public OpenSourceAdapter(List<OpenSourceItem> itemList, Typeface typeface) {
        this.itemList = itemList;
        this.typeface = typeface;
    }

    /** <br> parent methods. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_open_source, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setTypeface(typeface);
        holder.title.setText(itemList.get(position).title);
        holder.subtitle.setText(itemList.get(position).subtitle);
        holder.web.setHtmlFromString(itemList.get(position).web, new HtmlTextView.RemoteImageGetter());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /** <br> holder. */

    public class ViewHolder extends RecyclerView.ViewHolder {
        // widget
        public TextView title;
        public TextView subtitle;
        public HtmlTextView web;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.item_open_source_title);
            this.subtitle = (TextView) itemView.findViewById(R.id.item_open_source_subtitle);
            this.web = (HtmlTextView) itemView.findViewById(R.id.item_open_source_web);
        }
    }
}
